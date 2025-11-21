package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 新增数据
     * @param
     */
    void insertBatch(List<OrderDetail> details);

    /**
     * 根据订单id查询订单信息
     * @param
     * @return
     */
    @Select("select * from order_detail where  order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);
}
