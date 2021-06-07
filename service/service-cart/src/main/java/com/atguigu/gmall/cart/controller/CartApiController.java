package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.util.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("api/cart")
//@CrossOrigin
public class CartApiController {

    @Autowired
    CartService cartService;

    @RequestMapping("checkCart/{skuId}/{isChecked}")
    public Result checkCart(HttpServletRequest request ,@PathVariable("skuId") Long skuId, @PathVariable("isChecked") String isChecked) {
        // 通过单点登录体系获得用户id
        String userId = request.getHeader("userId");
        String userTempId = request.getHeader("userTempId");
        cartService.checkCart(userId,skuId,isChecked);
        return Result.ok();
    }

    @RequestMapping("cartList")
    public Result cartList(HttpServletRequest request) {

        //单点登入获得用户id
        String userId = request.getHeader("userId");
        String userTempId = request.getHeader("userTempId");

        List<CartInfo> cartInfos = cartService.cartList(userId);

        return Result.ok(cartInfos);
    }


    @RequestMapping("addCart/{skuId}/{skuNum}")
    public CartInfo addCart(HttpServletRequest request,@PathVariable("skuId") Long skuId, @PathVariable("skuNum") Long skuNum) {

        //单点登入获得用户id
        String userId = request.getHeader("userId");
        String userTempId = request.getHeader("userTempId");

        CartInfo cartInfo = cartService.addCart(skuId, skuNum, userId);

        return cartInfo;
    }

}
