package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.impl.CategoryServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    /**
     * 分页查询分类数据
     * @param categoryPageQueryDTO
     * @return
     */
    @ApiOperation("分页查询分类数据")
    @GetMapping("/page")
    public Result<PageResult> getByPage(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询分类数据:{}",categoryPageQueryDTO);
        PageResult result= categoryService.getByPage(categoryPageQueryDTO);
        return Result.success(result);
    }

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    @ApiOperation("新增分类方法")
    @PostMapping
    public Result insert(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类:{}",categoryDTO);
        categoryService.insert(categoryDTO);
        return Result.success();
    }

    /**
     * 启用或禁用分类
     * @param status
     * @param id
     * @return
     */
    @ApiOperation("启用禁用分类方法")
    @PostMapping("/status/{status}")
    public Result stopOrOpen(@PathVariable int status,long id){
        log.info("修改id为{}的分类状态:{}",id,status);
        categoryService.stopOrOpen(status,id);
        return Result.success();
    }

    /**
     * 通过类型查询分类
     * @param type
     * @return
     */
    @ApiOperation("通过类型查询分类方法")
    @GetMapping("/list")
    public Result<List> getByType(int type){
        log.info("通过类型{}查询分类",type);
        List<Category> list = categoryService.getByType(type);
        return Result.success(list);
    }


    /**
     * 更新分类信息
     * @param categoryDTO
     * @return
     */
    @ApiOperation("更新分类信息方法")
    @PutMapping
    public Result update(@RequestBody CategoryDTO categoryDTO){
        log.info("更新分类信息:{}",categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 根据id删除分类(删除时需检查该分类是否还有套餐和菜品)
     * @param id
     * @return
     */
    @ApiOperation("删除分类方法")
    @DeleteMapping
    public Result delete(Long id){
        log.info("删除分类:{}",id);
        categoryService.delete(id);
        return Result.success();
    }
}
