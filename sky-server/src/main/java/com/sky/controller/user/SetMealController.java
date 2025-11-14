package com.sky.controller.user;

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController("UserSetMealController")
@RequestMapping("/user/setmeal")
@Slf4j
@Api(tags = "C端套餐接口")
public class SetMealController {
    final SetMealService setMealService;

    public SetMealController(SetMealService setMealService) {
        this.setMealService = setMealService;
    }

    /**
     * 根据分类id查询套餐列表
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    @Cacheable(cacheNames = "setMealCache", key = "#categoryId")
    public Result<List<Setmeal>> getList(int categoryId){
        List<Setmeal> list = setMealService.getList(categoryId);
        return Result.success(list);
    }

    @GetMapping("/dish/{setMealId}")
    @ApiOperation("查询套餐下的菜品")
    public  Result<List<DishItemVO>> getDish(@PathVariable Integer setMealId){
        List<DishItemVO> list = setMealService.getDish(setMealId);
        return Result.success(list);
    }
}
