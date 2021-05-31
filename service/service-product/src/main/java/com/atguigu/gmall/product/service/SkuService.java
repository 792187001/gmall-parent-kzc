package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SkuService {
    void saveSkuInfo(SkuInfo skuInfo);

    IPage<SkuInfo> list(Long page, Long limit);

    void onSale(Long skuId);

    void cancelSale(Long skuId);

    SkuInfo getSkuById(Long skuId);

    List<SkuImage> getSkuImagesBySkuId(Long skuId);

    BigDecimal getSkuPriceById(Long skuId);

    List<Map<String, Object>> getValuesSku(Long spuId);

    Goods getGoodsBySkuId(Long skuId);
}

