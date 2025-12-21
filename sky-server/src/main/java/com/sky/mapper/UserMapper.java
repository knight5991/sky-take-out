package com.sky.mapper;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User findByOpenid(String openid);

    /**
     * 插入新用户
     * @param user
     */
    void insert(User user);

    /**
     * 根据起始日期查询用户统计数据
     * @return
     */
    Integer numberByMap(Map map);
}
