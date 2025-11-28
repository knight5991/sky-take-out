package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import lombok.Generated;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 新增订单
     * @param orders
     */
    @Insert("insert into orders " +
            "(number, status, user_id, address_book_id, order_time, checkout_time, pay_method, pay_status, " +
            "amount, remark, phone, address, user_name, consignee, cancel_reason, rejection_reason, cancel_time, " +
            "estimated_delivery_time, delivery_status, delivery_time, pack_amount, tableware_number, tableware_status)" +
            " VALUE (#{number},#{status},#{userId},#{addressBookId},#{orderTime},#{checkoutTime},#{payMethod},#{payStatus}," +
            "#{amount},#{remark},#{phone},#{address},#{userName},#{consignee},#{cancelReason},#{rejectionReason},#{cancelTime}," +
            "#{estimatedDeliveryTime},#{deliveryStatus},#{deliveryTime},#{packAmount},#{tablewareNumber},#{tablewareStatus})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insert(Orders orders);

    /**
     * 更新订单方法
     * @param orders
     */

    void update(Orders orders);

    /**
     * 分页查询订单数据
     * @param queryDTO
     * @return
     */
    Page<Orders> page(OrdersPageQueryDTO queryDTO);


    /**
     * 根据id查询订单
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 查询各个订单状态数量统计
     * @return
     */
    @Select("select " +
            "SUM(case when status = 2 then 1 else 0 end) AS toBeConfirmed," +
            "SUM(case when status = 3 then 1 else 0 end) AS confirmed," +
            "SUM(case when status = 4 then 1 else 0 end) AS deliveryInProgress" +
            " from orders")
    OrderStatisticsVO getStatistics();

    /**
     * 根据订单状态和订单时间处理超时订单
     * @param pendingPayment
     * @param lastTime
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{lastTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime lastTime);
}
