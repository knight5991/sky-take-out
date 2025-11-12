package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
@Service
@Slf4j
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    SetMealMapper setMealMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;
    @Autowired
    DishMapper dishMapper;
    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<Setmeal> page = setMealMapper.page(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 新增套餐和套餐对应的菜品关系
     * @param setmealDTO
     */
    @Transactional
    public void saveWithSetMealDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //注意这里需要获取插入数据生成的id，以生成套餐对应菜品的记录，故使用xml编写sql
        setMealMapper.save(setmeal);
        List<SetmealDish> list = setmealDTO.getSetmealDishes();
        //为套餐对应菜品记录补充id
        for (SetmealDish setmealDish : list) {
            setmealDish.setSetmealId(setmeal.getId());
        }
        setmealDishMapper.saves(list);
    }

    /**
     * 起售停售套餐
     * @param status
     * @param id
     */
    public void StopAndOpen(String status, String id) {
        //该处起售套餐时需要判断是否有商品处于停售状态
        if (Integer.valueOf(status) == StatusConstant.ENABLE){
            List<Dish> list = dishMapper.selectBySetMealId(id);
            for (Dish dish : list) {
                if (dish.getStatus() == StatusConstant.DISABLE){
                    //有菜品处于停售状态，套餐无法起售
                    throw  new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }
        Setmeal setmeal = Setmeal.builder().id(Long.valueOf(id)).status(Integer.valueOf(status)).build();
        setMealMapper.update(setmeal);
    }

    /**
     * 根据id查询套餐信息
     * @param id
     * @return
     */
    public SetmealVO getByIdWithDish(String id) {
        Setmeal setmeal = setMealMapper.getById(id);
        List<SetmealDish> list = setmealDishMapper.getBySetMealId(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(list);
        return setmealVO;
    }

    /**
     * 更新套餐信息
     * @param setmealDTO
     */
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        //更新套餐涉及到套餐表和套餐菜品关联表
        //1.修改套餐表
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setMealMapper.update(setmeal);
        //2.套餐菜品关联表比较复杂，这里采用先删除后添加的方式
        setmealDishMapper.deleteBySetMealId(setmealDTO.getId());
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        //这里补充记录中的套餐id
        for (SetmealDish dish : dishes) {
            dish.setSetmealId(setmealDTO.getId());
        }
        setmealDishMapper.saves(dishes);
    }

    /**
     * 根据id批量删除套餐
     * @param ids
     */
    @Transactional
    public void delete(List<Long> ids) {
        //判断套餐是否处于起售中
        for (Long id : ids) {
            Setmeal setmeal = setMealMapper.getById(String.valueOf(id));
            if (setmeal.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        //1.删除套餐表中的套餐信息
       setMealMapper.delete(ids);
       //2.删除套餐表所绑定的菜品信息
        setmealDishMapper.deleteBySetMealIds(ids);
    }

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    public List<Setmeal> getList(int categoryId) {
        List<Setmeal>list = setMealMapper.getList(categoryId);
        return list;
    }

    /**
     * 查对应套餐下的菜品
     * @param setMealId
     * @return
     */
    public List<DishItemVO> getDish(Integer setMealId) {
        List<Dish> dishes = dishMapper.selectBySetMealId(String.valueOf(setMealId));

        List<DishItemVO> list = new ArrayList<>();
        for (Dish dish : dishes) {
            DishItemVO dishItemVO = new DishItemVO();
            BeanUtils.copyProperties(dish,dishItemVO);
            int copies = setmealDishMapper.getCopiesByDishId(dish.getId());
            dishItemVO.setCopies(copies);
            list.add(dishItemVO);
        }
        return list;
    }
}
