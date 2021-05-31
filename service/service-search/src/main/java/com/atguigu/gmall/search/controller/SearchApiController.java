package com.atguigu.gmall.search.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import com.atguigu.gmall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("api/search")
public class SearchApiController {

    @Autowired
    SearchService searchService;



    @RequestMapping("list")
    SearchResponseVo list(@RequestBody SearchParam searchParam){
        SearchResponseVo responseVo = searchService.list(searchParam);

        return responseVo;
    }

    @RequestMapping("index")
    List<JSONObject> index() {
        List<JSONObject> jsonObjects = searchService.getBaseCategoryList();

        return jsonObjects;
    }
    @RequestMapping("cancelSale/{skuId}")
    void cancelSale(@PathVariable("skuId") Long skuId){
        searchService.cancelSale(skuId);
    }

    @RequestMapping("onSale/{skuId}")
    void onSale(@PathVariable("skuId") Long skuId){
        searchService.onSale(skuId);
    }


    @RequestMapping("hotScore/{skuId}")
    void hotScore(@PathVariable("skuId")Long skuId){
        searchService.hotScore(skuId);
    }
}