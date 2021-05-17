package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseTrademarkServiceImpl implements BaseTrademarkService {
    @Autowired
    BaseTrademarkMapper baseTrademarkMapper;

    @Override
    public IPage<BaseTrademark> baseTrademark(Long page, Long limit) {

        IPage<BaseTrademark> ipage = new Page<BaseTrademark>();
        ipage.setCurrent(page);
        ipage.setSize(limit);
        IPage<BaseTrademark> baseTrademarkIPage = baseTrademarkMapper.selectPage(ipage, null);

        return baseTrademarkIPage;
    }
}
