package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id查询关联套餐
     * @param ids
     * @return
     */
    List<SetmealDish> getsetmealDishByDishId(List<Long> ids);

    /**
     * 新增套餐绑定的菜品记录
     * @param list
     */
    void saves(List<SetmealDish> list);

    /**
     * 根据套餐id查询记录
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where  setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetMealId(String setmealId);

    /**
     * 根据套餐id删除记录
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetMealId(Long setmealId);

    /**
     * 根据套餐id批量删除记录
     * @param ids
     */
    void deleteBySetMealIds(List<Long> ids);
}
