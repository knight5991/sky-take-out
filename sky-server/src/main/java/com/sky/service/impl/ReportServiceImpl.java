package com.sky.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    ReportMapper reportMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    WorkspaceService workspaceService;


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

    /**
     * 根据日期获取销量top10的商品
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> top10 = reportMapper.top10(beginTime, endTime);
        List<String> names = top10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numbers = top10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        return SalesTop10ReportVO
                .builder()
                .nameList(StringUtil.join(",", names))
                .numberList(StringUtil.join(",", numbers))
                .build();
    }

    /**
     * 导出运营数据报表（近30天）
     * @param response
     */
    public void exportBusinessXlsx(HttpServletResponse response) {
        //1.查询数据库
        //查询近30天的运营数据
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));

        //2.读入Excel模板,并填充数据
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            //基于模板文件创建一个新的excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);

            XSSFSheet sheet = excel.getSheetAt(0);

            //填充时间
            sheet.getRow(1).getCell(1).setCellValue("时间: "+ begin + "至" + end);

            //填充30天的概览数据
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());

            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());


            //填写30天的明细数据
            int rowIndex = 7;
            while(!begin.equals(end.plusDays(1))){
                businessData = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(begin, LocalTime.MAX));

                row = sheet.getRow(rowIndex++);
                row.getCell(1).setCellValue(begin.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());

                begin = begin.plusDays(1);
            }

            //3.输出流
            excel.write(response.getOutputStream());

            //4.关闭资源
            in.close();
            excel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
