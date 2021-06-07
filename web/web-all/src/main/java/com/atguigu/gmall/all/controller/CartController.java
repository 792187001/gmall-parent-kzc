package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
public class CartController {

    @Autowired
    CartFeignClient cartFeignClient;

    @RequestMapping("cart/cart.html")
    public String cartHtml(Model model) {

        return "cart/index";

    }

    @RequestMapping("addCart.html")
    public String addCart(HttpServletRequest request ,Long skuId, Long skuNum) throws UnsupportedEncodingException {
        //单点登入获得用户id
        String userId = request.getHeader("userId");
        String userTempId = request.getHeader("userTempId");

        // 调用后台cartService微服务
        CartInfo cartInfo = cartFeignClient.addCart(skuId, skuNum);

        // 通过url将购物车页面参数传递过去addCart.html?skuName=111&sku..
       /* return "redirect:http://cart.gmall.com:8300/cart/addCart.html?skuName=" + cartInfo.
                getSkuName() + "&skuDefaultImg=" + cartInfo.getImgUrl();*/
        // 防止重复提交，重定向到静态资源
        return "redirect:http://cart.gmall.com/cart/addCart.html?"
                + "skuName="+ URLEncoder.encode(cartInfo.getSkuName(),"UTF-8").replace("+"," ")
                +"&imgUrl="+cartInfo.getImgUrl()
                +"&skuId="+cartInfo.getSkuId();

    }

}
