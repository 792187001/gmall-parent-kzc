package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public UserInfo login(UserInfo userInfo) {

        String loginName = userInfo.getLoginName();
        String passwd = userInfo.getPasswd();

        String encryptPasswd = MD5.encrypt(passwd);

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("login_name", loginName);
        queryWrapper.eq("passwd", encryptPasswd);

        UserInfo userInfoDb = userInfoMapper.selectOne(queryWrapper);

        if (null != userInfoDb) {
            // 登录成功
            // 生成token
            String token = UUID.randomUUID().toString();
            // 保存token
            redisTemplate.opsForValue().set("user:login:" + token, userInfoDb);
            userInfoDb.setToken(token);
            return userInfoDb;
        } else {
            return null;
        }
    }

    @Override
    public Map<String, Object> verify(String token) {

        Map<String, Object> map = new HashMap<>();

        UserInfo userInfo = (UserInfo) redisTemplate.opsForValue().get("user:login:" + token);

        if (null != userInfo) {
            map.put("success", "success");
            map.put("userId", userInfo.getId() + "");
            //不论if else 都返回mapcar
        }
        return map;
    }
}
