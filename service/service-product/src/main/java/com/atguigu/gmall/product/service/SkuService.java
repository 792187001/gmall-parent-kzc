package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface SkuService {
    void saveSkuInfo(SkuInfo skuInfo);

    IPage<SkuInfo> list(Long page, Long limit);

    void onSale(Long skuId);

    void cancelSale(Long skuId);
}
