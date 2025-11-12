package com.sky.controller.user;


import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Api(tags = "C端菜品接口")
@Slf4j
public class DishController {
   final DishService dishService;


    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    /**
     * 根据分类id查询菜品列表
     * @param categoryId
     * @return
     */
    @GetMapping("list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> getList(int categoryId){
        List<DishVO> list = dishService.getList(categoryId);
        return Result.success(list);
    }
}
