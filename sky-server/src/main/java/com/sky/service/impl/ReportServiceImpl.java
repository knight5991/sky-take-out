package com.sky.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    ReportMapper reportMapper;

    @Autowired
    UserMapper userMapper;
    /**
     * 根据日期获取营业额记录
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList();  //日期列表
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Double> turnOverList = new ArrayList<>(); //营业额列表
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap(){
                {
                    put("begin",beginTime);
                    put("end",endTime);
                    put("status", Orders.COMPLETED);
                }
            };
            Double tunOver = reportMapper.sumByMap(map);
            tunOver = tunOver == null ? 0.0 : tunOver;
            turnOverList.add(tunOver);
        }

        return TurnoverReportVO.
                builder().
                dateList(StringUtil.join(",", dateList)).
                turnoverList(StringUtil.join(",", turnOverList)).
                build();
    }

    /**
     * 根据日期获取用户统计数据
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> totalUser = new ArrayList<>();
        List<Integer> newUser = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map<String, LocalDateTime> map = new HashMap<>();
            map.put("end", endTime);
            Integer totalU = userMapper.numberByMap(map);
            map.put("begin", beginTime);
            Integer newU = userMapper.numberByMap(map);
            totalUser.add(totalU);
            newUser.add(newU);
        }


        return UserReportVO
                .builder()
                .dateList(StringUtil.join(",",dateList))
                .totalUserList(StringUtil.join(",", totalUser))
                .newUserList(StringUtil.join(",", newUser))
                .build();
    }


    /**
     * 根据日期获取订单统计数据
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        //获取订单总数和有效订单总数
        Map map = new HashMap<>();
        map.put("begin", LocalDateTime.of(begin, LocalTime.MIN));
        map.put("end", LocalDateTime.of(end, LocalTime.MAX));
        Integer totalOrderCount = reportMapper.numberByMap(map);
        map.put("status", Orders.COMPLETED);
        Integer validOrderCount = reportMapper.numberByMap(map);

        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //获取每日数据
        List orderCountList = new ArrayList();
        List validOrderCountList = new ArrayList();
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map mmap = new HashMap();
            mmap.put("begin", beginTime);
            mmap.put("end", endTime);
            Integer total = reportMapper.numberByMap(mmap);
            orderCountList.add(total);
            mmap.put("status", Orders.COMPLETED);
            Integer valid = reportMapper.numberByMap(mmap);
            validOrderCountList.add(valid);
        }

        double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            orderCompletionRate =validOrderCount * 1.0 / totalOrderCount;
        }

        return OrderReportVO
                .builder()
                .dateList(StringUtil.join(",", dateList))
                .orderCountList(StringUtil.join(",", orderCountList))
                .validOrderCountList(StringUtil.join(",", validOrderCountList))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }
}
