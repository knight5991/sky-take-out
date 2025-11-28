package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.*;
import com.sky.websocket.WebSocketServer;
import io.swagger.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final AddressBookMapper addressBookMapper;

    private final WebSocketServer webSocketServer;
    public OrderServiceImpl(OrderMapper orderMapper, ShoppingCartMapper shoppingCartMapper, OrderDetailMapper orderDetailMapper, AddressBookMapper addressBookMapper, WebSocketServer webSocketServer) {
        this.orderMapper = orderMapper;
        this.shoppingCartMapper = shoppingCartMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.addressBookMapper = addressBookMapper;
        this.webSocketServer = webSocketServer;
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

        //调用websocket接口实现通信，通知商家端有新订单
        Map map = new HashMap();
        map.put("type",1);
        map.put("orderId",orders.getId());
        map.put("content","订单号："+ordersPaymentDTO.getOrderNumber());
        String s = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(s);


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

    /**
     * 根据id查询订单详细
     * @param id
     * @return
     */
    public OrderPageVo getDetail(Long id) {
        Orders orders = orderMapper.getById(id);
        OrderPageVo vo = new OrderPageVo();
        BeanUtils.copyProperties(orders,vo);
        List<OrderDetail> detailList = orderDetailMapper.getByOrderId(id);
        vo.setOrderDetailList(detailList);
        return vo;
    }

    /**
     * 用户取消订单
     * @param id
     */
    public void cancelOrder(Long id) {
        //这里在取消订单的同时应该给用户退款
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 用户再来一单
     * @param id
     */
    @Transactional
    public void repetition(Long id) {
        Orders orders = orderMapper.getById(id);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(null);
        orders.setPayMethod(1);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setCancelTime(null);
        orders.setCancelReason(null);
        orders.setRejectionReason(null);
        orders.setEstimatedDeliveryTime(LocalDateTime.now());
        orders.setId(null);

        orderMapper.insert(orders);
        List<OrderDetail> details = orderDetailMapper.getByOrderId(id);

        //3.向订单详细表插入多条数据
        for (OrderDetail detail : details) {
            detail.setId(null);
            detail.setOrderId(orders.getId());
        }
        orderDetailMapper.insertBatch(details);
    }

    /**
     * 管理员搜索订单
     * @param dto
     * @return
     */
    public PageResult orderSearch(OrdersPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(),dto.getPageSize());
        Page<Orders> page = orderMapper.page(dto);
        long total = page.getTotal();
        List<Orders> orders = page.getResult();
        List<OrderSearchVO> list = new ArrayList<>();
        for (Orders order : orders) {
            OrderSearchVO vo = new OrderSearchVO();
            BeanUtils.copyProperties(order,vo);
            List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(order.getId());
            StringBuilder builder = new StringBuilder();
            for (OrderDetail detail : orderDetails) {
                builder.append(detail.getName());
                builder.append("*"+detail.getNumber()+";");
            }
            vo.setOrderDishes(builder.toString());
            list.add(vo);
        }
        PageResult pageResult = new PageResult();
        pageResult.setTotal(total);
        pageResult.setRecords(list);
        return pageResult;
    }

     /**
     * 查询各个状态的订单数量统计
     * @return
     */
    public OrderStatisticsVO getStatistics() {
        OrderStatisticsVO vo = orderMapper.getStatistics();

        return vo;
    }

    /**
     * 管理员接单
     * @param ordersConfirmDTO
     */
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = new Orders();
        orders.setId(ordersConfirmDTO.getId());
        orders.setStatus(ordersConfirmDTO.getStatus());
        orderMapper.update(orders);
    }

    /**
     * 管理员拒单
     * @param ordersRejectionDTO
     */
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = new Orders();
        orders.setId(ordersRejectionDTO.getId());
        orders.setStatus(6);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orderMapper.update(orders);
    }

    /**
     * 管理员取消订单
     * @param ordersCancelDTO
     */
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = new Orders();
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(6);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orderMapper.update(orders);
    }

    /**
     * 管理员派送订单
     * @param id
     */
    public void delivery(Long id) {
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }

    /**
     * 管理员完成订单
     * @param id
     */
    public void complete(Long id) {
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(Orders.COMPLETED);
        orderMapper.update(orders);
    }

    /**
     * 用户催单
     * @param id
     * @return
     */
    public void reminder(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //调用websocket接口实现通信，进行催单
        Map map = new HashMap();
        map.put("type",2);
        map.put("orderId",id);
        map.put("content","订单号："+ orders.getNumber());
        String s = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(s);

    }
}
