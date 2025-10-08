package com.sky.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

//@SpringBootTest
public class SpringDataRedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void testRedisTemplate(){
        System.out.println(redisTemplate);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        HashOperations hashOperations = redisTemplate.opsForHash();
        ListOperations listOperations = redisTemplate.opsForList();
        SetOperations setOperations = redisTemplate.opsForSet();
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
    }

    /**
     * 操作字符串
     */
    @Test
    void testForString(){
        //set get setex setnx
        redisTemplate.opsForValue().set("city","beijing");
        String city = (String) redisTemplate.opsForValue().get("city");
        System.out.println(city);
        redisTemplate.opsForValue().set("code",1234,3, TimeUnit.MINUTES);
        redisTemplate.opsForValue().setIfAbsent("test","123");
        redisTemplate.opsForValue().setIfAbsent("test","1565");
    }

    /**
     * 操作Hash
     */
    @Test
    void testForHash(){
        //hset hget hdel hkeys havls
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.put("100","name","lisi");
        hashOperations.put("100","age","10");
        Object name = hashOperations.get("100", "name");
        System.out.println(name);
        hashOperations.delete("100","age");
        Set keys = hashOperations.keys("100");
        System.out.println(keys);
        List values = hashOperations.values("100");
        System.out.println(values);
    }
}
