package com.sky.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
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
    /**
     * 根据日期获取营业额记录
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getReportVo(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList();  //日期列表
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        dateList.add(end);

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
}
