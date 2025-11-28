package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPageVo;
import com.sky.vo.OrderSearchVO;
import com.sky.vo.OrderStatisticsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "订单相关接口")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 管理员端订单搜索
     * @param dto
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> orderSearch(OrdersPageQueryDTO dto){
        PageResult page = orderService.orderSearch(dto);
        return Result.success(page);
    }


    /**
     * 查询各个状态的订单数量统计
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("查询各个状态的订单数量统计")
    public Result<OrderStatisticsVO> getStatistics(){
        OrderStatisticsVO vo = orderService.getStatistics();
        return Result.success(vo);
    }

    /**
     * 管理员查看订单详细
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详细")
    public Result<OrderPageVo> getDetail(@PathVariable Long id){
        OrderPageVo vo = orderService.getDetail(id);
        return Result.success(vo);
    }

    /**
     * 管理员接单
     * @param ordersConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("管理员接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        ordersConfirmDTO.setStatus(3);
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }


    /**
     * 管理员拒单
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("管理员拒单订单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 管理员取消订单
     * @param ordersCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation("管理员取消订单")
    Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 管理员完成订单
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("管理员派送订单")
    Result delivery(@PathVariable Long id){
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 管理员完成订单
     * @param id
     * @return
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("管理员完成订单")
    Result complete(@PathVariable Long id){
        orderService.complete(id);
        return Result.success();
    }


}

