package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserInfo;

import java.util.Map;

public interface UserService {
    UserInfo login(UserInfo userInfo);

    Map<String,Object> verify(String token);
}
