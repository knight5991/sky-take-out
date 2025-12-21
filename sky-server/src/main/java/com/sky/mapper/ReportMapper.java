package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface ReportMapper {

    Double sumByMap(Map map);

    Integer numberByMap(Map map);
}
