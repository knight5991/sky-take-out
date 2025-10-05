package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {
    @Autowired
    DishService dishService;

    /**
     * 新增菜品方法0
     * @param dishDTO
     * @return
     */
    @ApiOperation("新增菜品方法")
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品:{}",dishDTO);
        dishService.save(dishDTO);
        return Result.success();
    }

    /**
     * 分页查询菜品数据
     * @param queryDTO
     * @return
     */
    @ApiOperation("分页查询菜品")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO queryDTO){
        log.info("分页查询菜品数据.....");
        PageResult result = dishService.page(queryDTO);
        return Result.success(result);
    }

    /**
     * 启用或停用菜品
     * @param status
     * @param id
     * @return
     */
    @ApiOperation("启用停用菜品")
    @PostMapping("/status/{status}")
    public Result stopAndUp(@PathVariable String status, String id){
        dishService.stopAndUp(status,id);
        return Result.success();
    }

    /**
     * 更新菜品数据
     * @param dishDTO
     * @return
     */
    @ApiOperation("更新菜品")
    @PutMapping
    public Result update(@RequestBody  DishDTO dishDTO){
        dishService.update(dishDTO);
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @return
     */
    @ApiOperation("根据分类id查询菜品方法")
    @GetMapping("/list")
    public Result<List<Dish>> getByCategoryId(String categoryId){
        log.info("根据分类id:{}查询菜品",categoryId);
        List<Dish> list = dishService.getByCategoryId(categoryId);
        return Result.success(list);
    }

    /**
     * 根据id查询菜品具体信息
     * @param id
     * @return
     */
    @ApiOperation("根据id查询菜品信息方法")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable String id){
        DishVO dishVO = dishService.getByIdWithFlover(id);
        return Result.success(dishVO);
    }

    /**
     * 根据id批量删除菜品
     * @param ids
     * @return
     */
    @ApiOperation("根据id批量删除菜品方法")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids) throws Exception {
        log.info("批量删除菜品:{}",ids);
        dishService.delete(ids);
        return Result.success();
    }
}
