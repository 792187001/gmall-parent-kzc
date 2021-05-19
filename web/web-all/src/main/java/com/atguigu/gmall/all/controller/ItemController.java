package com.atguigu.gmall.all.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {
    @Autowired


    @RequestMapping("{skuId}.html")
    public String index(Model model, @PathVariable("skuId") Long  skuId){
        return "item/index";
    }

    @RequestMapping("test1")
    public String test1(){
        return "test1";
    }
}
