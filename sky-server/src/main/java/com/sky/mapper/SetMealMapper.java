package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface SetMealMapper {
    /**
     * 根据分类查询套餐数量
     * @param id
     * @return
     */
    @Select("select count(*) from setmeal where category_id = #{id}")
    int getCounts(Long id);

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    Page<Setmeal> page(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 新建套餐
     * @param setmeal
     */
    @AutoFill(OperationType.INSERT)
    void save(Setmeal setmeal);

    /**
     * 更新套餐方法
     * @param setmeal
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 通过id查询套餐方法
     * @param id
     * @return
     */
    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(String id);

    /**
     * 通过id批量删除数据
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 根据分类id查询套餐列表
     * @param categoryId
     * @return
     */
    @Select("select * from setmeal where category_id = #{categoryId} and status = 1")
    List<Setmeal> getList(int categoryId);


    /**
     * 根据条件统计套餐数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
