package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    private final OrderMapper mapper;

    public OrderTask(OrderMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 * * * * ? ")
    public void processOrdersTimeOut(){
        log.info("处理超时订单:{}", LocalDateTime.now());
        LocalDateTime lastTime = LocalDateTime.now().plusMinutes(-15);
        List<Orders> list = mapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT,lastTime);
        if (list != null && list.size() > 0){
            for (Orders orders : list) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelTime(LocalDateTime.now());
                orders.setCancelReason("订单超时，自动取消");
                mapper.update(orders);
            }
        }

    }

    /**
     * 每天凌晨一点处理未配送完的订单
     */
    @Scheduled(cron = "0 0 1 * * ? ")
    public void processDelivery(){
        log.info("处理正在配送的订单:{}", LocalDateTime.now());
        LocalDateTime lastTime = LocalDateTime.now().plusMinutes(-60);
        List<Orders> list = mapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, lastTime);
        if (list != null && list.size() > 0){
            for (Orders orders : list) {
                orders.setStatus(Orders.COMPLETED);
                mapper.update(orders);
            }
        }
    }

}
