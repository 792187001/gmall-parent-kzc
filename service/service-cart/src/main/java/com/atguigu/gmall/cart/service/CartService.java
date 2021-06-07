package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;


public interface CartService {
    CartInfo addCart(Long skuId, Long skuNum, String userId);

    List<CartInfo> cartList(String userId);


    void checkCart(String userId, Long skuId, String isChecked);
}
