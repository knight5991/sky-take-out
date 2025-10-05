package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.FlavorMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.apache.commons.lang3.exception.ExceptionContext;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    DishMapper dishMapper;
    @Autowired
    FlavorMapper flavorMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;
    /**
     * 新增菜品方法
     */
    @Transactional
    public void save( DishDTO dishDTO) {
        //保存菜品信息和对应口味信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.save(dish);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() != 0){
            Long id = dish.getId();
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(id);
            }
            flavorMapper.save(flavors);
        }

    }

    /**
     * 分页查询菜品
     * @param queryDTO
     * @return
     */
    public PageResult page(DishPageQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPage(),queryDTO.getPageSize());
        Page<DishVO> page = dishMapper.page(queryDTO);
        long total = page.getTotal();
        List<DishVO> result = page.getResult();
        return new PageResult(total,result);
    }

    /**
     * 启用或停用菜品
     * @param status
     * @param id
     */
    public void stopAndUp(String status, String id) {
        Dish dish = Dish.builder().id((long) Integer.parseInt(id)).status(Integer.valueOf(status)).build();
        dishMapper.update(dish);
    }

    /**
     * 更新菜品
     * @param dishDTO
     */
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        //todo 修改关联的口味



    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    public List<Dish> getByCategoryId(String categoryId) {
        List<Dish> list = dishMapper.getByCategoryId(categoryId);
        return list;
    }

    /**
     * 根据菜品id查询菜品具体信息
     * @param id
     * @return
     */
    public DishVO getByIdWithFlover(String id) {
        //因为这里涉及到dish表和dish_flavor表，这里分开两次查询后将结果合并
        Dish dish = dishMapper.getById(Long.valueOf(id));
        List<DishFlavor> list = flavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(list);
        return dishVO;
    }

    /**
     * 根据id批量删除菜品
     * @param ids
     */
    @Transactional
    public void delete(List<Long> ids) throws Exception {
        //删除菜品是敏感操作，涉及到多方面，故
        //检查菜品是否在售
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //检查菜品是否包含在某一套餐中
        List<SetmealDish> list = setmealDishMapper.getsetmealDishByDishId(ids);
        if (list != null && list.size() != 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品和菜品关联的口味
        dishMapper.delete(ids);
        flavorMapper.deleteByDishId(ids);
    }
}
