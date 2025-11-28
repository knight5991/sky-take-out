package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPageVo;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;

import java.io.Serializable;

public interface OrderService {
    /**
     * 用户提交订单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 用户支付订单
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);

    /**
     * 分页查询用户历史订单
     * @param queryDTO
     * @return
     */
    PageResult page(OrdersPageQueryDTO queryDTO);

    /**
     * 根据id查询订单详细
     * @param id
     * @return
     */
    OrderPageVo getDetail(Long id);

    /**
     * 用户取消订单
     * @param id
     */
    void cancelOrder(Long id);

    /**
     * 用户再来一单
     * @param id
     */
    void repetition(Long id);

    /**
     * 管理员搜索订单
     * @param dto
     * @return
     */
    PageResult orderSearch(OrdersPageQueryDTO dto);

    /**
     * 查询各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO getStatistics();

    /**
     * 管理员接单
     * @param ordersConfirmDTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 管理员拒单
     * @param ordersRejectionDTO
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 管理员取消订单
     * @param ordersCancelDTO
     */
    void cancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 管理员派送订单
     * @param id
     */
    void delivery(Long id);

    /**
     * 管理员完成订单
     * @param id
     */
    void complete(Long id);
}
