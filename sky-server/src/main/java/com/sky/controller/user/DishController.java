package com.sky.controller.user;


import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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
    final private   RedisTemplate redisTemplate;

    public DishController(DishService dishService, RedisTemplate redisTemplate) {
        this.dishService = dishService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 根据分类id查询菜品列表
     * @param categoryId
     * @return
     */
    @GetMapping("list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> getList(int categoryId){
        //构建key，规则:"dish_分类id"
        String key = "dish_" + categoryId;
        //先查询redis缓存中是否有数据，如有，从缓存中取出直接返回
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if (list != null){
            return Result.success(list);
        }

        //缓存中没有数据，查询数据库并保存在redis缓存
        list = dishService.getList(categoryId);
        redisTemplate.opsForValue().set(key,list);
        return Result.success(list);
    }
}
