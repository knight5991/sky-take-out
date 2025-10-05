package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FlavorMapper {

    /**
     * 新增菜品对应口味
     *
     * @param flavors
     */
    void save(List<DishFlavor> flavors);

    /**
     * 根据菜品id查询对应口味
     * @param id
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getByDishId(String id);

    /**
     * 根据菜品id删除
     * @param ids
     */
    void deleteByDishId(List<Long> ids);
}
