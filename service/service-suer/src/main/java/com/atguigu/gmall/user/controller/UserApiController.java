package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.util.Result;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/user/passport")
public class UserApiController {

    @Autowired
    UserService userService;

    @RequestMapping("login")
    Result login(@RequestBody UserInfo userInfo){
        // 调用登录
        UserInfo userInfoResult = userService.login(userInfo);
        if(null!=userInfoResult){
            return Result.ok(userInfoResult);
        }else {
            return Result.fail("用户名或者密码错误");
        }
    }

    @RequestMapping("verify/{token}")
    Map<String,Object> verify(@PathVariable("token") String token){
        //应设置token过期时间
        Map<String,Object> map = userService.verify(token);
        return map;
    }

}
