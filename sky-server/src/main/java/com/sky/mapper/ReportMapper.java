package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {

    Double sumByMap(Map map);

    Integer numberByMap(Map map);

    List<GoodsSalesDTO> top10(LocalDateTime begin, LocalDateTime end);
}
