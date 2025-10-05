package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetMealService {
    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);


    /**
     * 新增套餐和套餐对应的菜品关系
     * @param setmealDTO
     */
    void saveWithSetMealDish(SetmealDTO setmealDTO);

    /**
     * 起售或停售套餐
     * @param status
     * @param id
     */
    void StopAndOpen(String status, String id);

    /**
     * 根据id查询套餐信息
     * @param id
     * @return
     */
    SetmealVO getByIdWithDish(String id);

    /**
     * 更新套餐信息
     * @param setmealDTO
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 根据id批量删除套餐
     * @param ids
     */
    void delete(List<Long> ids);
}
