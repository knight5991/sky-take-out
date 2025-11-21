package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
@Api(tags = "c端订单相关接口")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 用户提交订单
     * @param ordersSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    @ApiOperation("用户提交订单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        OrderSubmitVO vo = orderService.submit(ordersSubmitDTO);
        return Result.success(vo);
    }


    /**
     * 用户支付订单方法
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("用户支付订单")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO){
        OrderPaymentVO vo = orderService.payment(ordersPaymentDTO);
        return Result.success(vo);
    }

    /**
     * 分页查询用户历史订单
     * @param queryDTO
     * @return
     */
    @GetMapping("historyOrders")
    @ApiOperation("用户获取历史订单")
    Result<PageResult>  page(OrdersPageQueryDTO queryDTO){
        PageResult pageResult = orderService.page(queryDTO);
        return Result.success(pageResult);
    }
}
