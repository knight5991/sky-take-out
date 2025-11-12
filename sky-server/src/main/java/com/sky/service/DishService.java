package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/**
 * 新增菜品方法
 */
public interface DishService {
    void save( DishDTO dishDTO);

    /**
     * 分页查询菜品
     * @param queryDTO
     * @return
     */
    PageResult page(DishPageQueryDTO queryDTO);

    /**
     * 启用或停用菜品
     * @param status
     * @param id
     */
    void stopAndUp(String status, String id);

    /**
     * 更新菜品数据
     * @param dishDTO
     */
    void update(DishDTO dishDTO);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    List<Dish> getByCategoryId(String categoryId);

    /**
     * 根据id查询菜品具体信息
     * @param id
     * @return
     */
    DishVO getByIdWithFlover(String id);

    /**
     * 根据id批量删除菜品
     * @param ids
     */
    void delete(List<Long> ids) throws Exception;

    /**
     * 查询指定分类下的菜品列表（携带口味）
     * @param categoryId
     * @return
     */
    List<DishVO> getList(int categoryId);
}
