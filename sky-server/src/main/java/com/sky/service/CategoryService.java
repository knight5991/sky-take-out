package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    /**
     * 分页查询分类数据
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult getByPage(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 新增分类
     * @param categoryDTO
     */
    void insert(CategoryDTO categoryDTO);

    /**
     * 修改分类状态
     * @param status
     * @param id
     */
    void stopOrOpen(int status, long id);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    List<Category> getByType(int type);

    /**
     * 更新分类信息
     * @param categoryDTO
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 删除分类
     * @param id
     */
    void delete(Long id);
}
