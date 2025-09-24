package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetMealMapper {
    /**
     * 根据分类查询套餐数量
     * @param id
     * @return
     */
    @Select("select count(*) from setmeal")
    int getCounts(Long id);
}
