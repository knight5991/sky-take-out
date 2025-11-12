package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userCategoryController")
@RequestMapping("/user/category")
@Api(tags = "C端分类接口")
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 查询分类列表
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查询分类接口")
    public Result<List<Category>> getTypeList(@RequestParam(required = false) Integer type){
        log.info("查询分类接口，type={}",type);
        List<Category> list = null;
        if (type == null){
             list=  categoryService.getCategoryList();
        }else {
            list = categoryService.getByType(type);
        }
        return Result.success(list);
    }
}
