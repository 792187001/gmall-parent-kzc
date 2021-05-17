package com.atguigu.gmall.product.controller;
import com.atguigu.gmall.common.util.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class TrademarkController {

    @Autowired
    BaseTrademarkService baseTrademarkService;

    @RequestMapping("baseTrademark/{page}/{limit}")
    public Result baseTrademark(@PathVariable("page") Long page,@PathVariable("limit") Long limit){

        IPage<BaseTrademark> baseTrademarks=baseTrademarkService.baseTrademark(page,limit);

        return Result.ok(baseTrademarks);
    }

}
