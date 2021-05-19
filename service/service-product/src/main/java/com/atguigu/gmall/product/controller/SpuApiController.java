package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.util.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.SpuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class SpuApiController {
    @Autowired
    SpuService spuService;

    @RequestMapping("spuImageList/{spuId}")
    public Result spuImageList(@PathVariable("spuId") Long spuId){

        List<SpuImage> spuImages = spuService.spuImageList(spuId);

        return Result.ok(spuImages);
    }


    @RequestMapping("spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable("spuId") Long spuId){

        List<SpuSaleAttr> spuSaleAttrs = spuService.spuSaleAttrList(spuId);

        return Result.ok(spuSaleAttrs);
    }
    @RequestMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){

        spuService.saveSpuInfo(spuInfo);

        return Result.ok();
    }
    @RequestMapping("baseSaleAttrList")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrs =  spuService.baseSaleAttrList();

        return Result.ok(baseSaleAttrs);
    }

    @RequestMapping("{page} /{limit}")
    public Result spuList(@PathVariable("page") Long page,@PathVariable("limit") Long limit,Long category3Id){

        IPage<SpuInfo> infoIPage =  spuService.spuList(page,limit,category3Id);

        return Result.ok(infoIPage);
    }

}
