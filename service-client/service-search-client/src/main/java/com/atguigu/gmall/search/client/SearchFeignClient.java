package com.atguigu.gmall.search.client;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(value = "service-search")
public interface SearchFeignClient {

    @RequestMapping("api/search/index")
    List<JSONObject> index();

    @RequestMapping("api/search/onSale/{skuId}")
    void onSale(@PathVariable("skuId") Long skuId);

    @RequestMapping("api/search/cancelSale/{skuId}")
    void cancelSale(@PathVariable("skuId") Long skuId);

    @RequestMapping("api/search/list")
    SearchResponseVo list(@RequestBody SearchParam searchParam);

    @RequestMapping("api/search/hotScore/{skuId}")
    void hotScore(@PathVariable("skuId")Long skuId);
}
