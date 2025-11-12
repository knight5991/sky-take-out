package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClients;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.impl.STTabJcImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    UserMapper userMapper;
    /**
     * 用户通过微信登录
     * @param userLoginDTO
     * @return
     */
    public User login(UserLoginDTO userLoginDTO) {
        String openid = getWxOpenid(userLoginDTO.getCode());
        log.info("openid：{}",openid);
        //检查用户id是否为空，若为空则登录失败，抛出业务异常
        if (openid == null || openid.equals("")){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //检查用户是否为新用户
        User user = userMapper.findByOpenid(openid);
        //注册新用户
        if (user == null){
            user= User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
            log.info("新用户：{}",user);
        }
        //返回用户信息
        return user;
    }

    /**
     * 调用wx接口服务获取微信openid
     * @param code
     * @return
     */
    private String getWxOpenid(String code){
        //通过微信接口获取用户信息(用户的openid)
        Map<String, String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN_URL, map);
        JSONObject object = JSON.parseObject(json);
        log.info("微信接口返回数据：{}",object);
        String openid =object.getString("openid");
        return openid;
    }
}
