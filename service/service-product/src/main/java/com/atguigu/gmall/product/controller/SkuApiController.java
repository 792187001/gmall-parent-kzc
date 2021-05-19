package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.util.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.SkuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class SkuApiController {
    @Autowired
    SkuService skuService;
    @RequestMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId){

        skuService.cancelSale(skuId);

        return Result.ok();
    }

    @RequestMapping("onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId){

        skuService.onSale(skuId);

        return Result.ok();
    }
    @RequestMapping("list/{page}/{limit}")
    public Result list(@PathVariable("page") Long page, @PathVariable("limit") Long limit){

        IPage<SkuInfo> infoIpage = skuService.list(page,limit);

        return Result.ok(infoIpage);
    }

    @RequestMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
         skuService.saveSkuInfo(skuInfo);
         return Result.ok();
    }
}
