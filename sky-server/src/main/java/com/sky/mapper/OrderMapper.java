package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import lombok.Generated;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;

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
    @Update("update orders set status = #{status},pay_status =#{payStatus},checkout_time = #{checkoutTime} where number  = #{number}")
    void update(Orders orders);

    /**
     * 分页查询订单数据
     * @param queryDTO
     * @return
     */
    Page<Orders> page(OrdersPageQueryDTO queryDTO);
}
