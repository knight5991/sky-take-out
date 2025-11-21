package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderPageVo;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final AddressBookMapper addressBookMapper;


    public OrderServiceImpl(OrderMapper orderMapper, ShoppingCartMapper shoppingCartMapper, OrderDetailMapper orderDetailMapper, AddressBookMapper addressBookMapper) {
        this.orderMapper = orderMapper;
        this.shoppingCartMapper = shoppingCartMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.addressBookMapper = addressBookMapper;
    }

    /**
     * 用户提交订单
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        //1.处理各种业务异常(地址簿为空,购物车数据为空)

        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.getById(Math.toIntExact(ordersSubmitDTO.getAddressBookId()));
        if (addressBook == null){
            //地址簿为空，抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        ShoppingCart cart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(cart);
        if (list == null || list.size() == 0){
            //购物车为空,抛出异常
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //2.向订单表插入1条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);

        orders.setUserId(userId);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);

        orders.setUserName(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());

        orderMapper.insert(orders);

        //3.向订单详细表插入多条数据
        List<OrderDetail> details = new ArrayList<>();
        for (ShoppingCart shoppingCart : list) {
            OrderDetail detail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart,detail);
            detail.setOrderId(orders.getId());
            detail.setId(null);
            details.add(detail);
        }
        orderDetailMapper.insertBatch(details);


        //4.清空当前用户购物车
        shoppingCartMapper.delete(cart);

        //5.封装vo对象返回结果
        OrderSubmitVO vo = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
        return vo;
    }

    /**
     * 用户支付订单
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        Long userId = BaseContext.getCurrentId();
        //这里的正常流程是查询数据库表将对应用户的openid取出，然后填充请求数据，对微信的支付接口发出请求，得到需要的数据
 /*        User user = user.getById(userId);
       //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }
*/
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));
        Integer OrderPaidStatus = Orders.PAID;//支付状态，已支付
        Integer OrderStatus = Orders.TO_BE_CONFIRMED;  //订单状态，待接单
        LocalDateTime check_out_time = LocalDateTime.now();//更新支付时间
        Orders orders = Orders.builder()
                .payStatus(OrderPaidStatus)
                .status(OrderStatus)
                .checkoutTime(check_out_time)
                .number(ordersPaymentDTO.getOrderNumber())
                .build();
        orderMapper.update(orders);
        return vo;

    }


    /**
     * 分页查询用户历史订单
     * @param queryDTO
     * @return
     */
    public PageResult page(OrdersPageQueryDTO queryDTO) {
        Long userId = BaseContext.getCurrentId();
        queryDTO.setUserId(userId);
        PageHelper.startPage(queryDTO.getPage(),queryDTO.getPageSize());
        Page<Orders> page = orderMapper.page(queryDTO);
        long total = page.getTotal();
        List<Orders> orders = page.getResult();
        List<OrderPageVo> list = new ArrayList<>();
        for (Orders order : orders) {
            List<OrderDetail> details = orderDetailMapper.getByOrderId(order.getId());
            OrderPageVo vo = new OrderPageVo();
            BeanUtils.copyProperties(order,vo);
            vo.setOrderDetailList(details);
            list.add(vo);
        }
        PageResult pageResult = new PageResult();
        pageResult.setTotal(total);
        pageResult.setRecords(list);
        return pageResult;
    }
}
