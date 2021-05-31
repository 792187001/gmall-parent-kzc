package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.atguigu.gmall.search.client.SearchFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    SearchFeignClient searchFeignClient;

    @Override
    public Map<String, Object> item(Long skuId) {
        long start= System.currentTimeMillis();

        Map<String, Object> map = new HashMap<>();
        //需要调用service-product 查询基础数据

        CompletableFuture<SkuInfo> skuCompletableFuture = CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                SkuInfo skuInfo = productFeignClient.getSkuById(skuId);
                return skuInfo;
            }
        },threadPoolExecutor);
        //sku图片
        CompletableFuture<Void> imgCompletableFuture = skuCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                List<SkuImage> skuImages = productFeignClient.getSkuImagesBySkuId(skuId);
                skuInfo.setSkuImageList(skuImages);
                map.put("skuInfo", skuInfo);
            }
        },threadPoolExecutor);

        CompletableFuture<Void> priceCompletableFuture = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                //价格
                BigDecimal price = productFeignClient.getSkuPriceById(skuId);
                map.put("price", price);
            }
        },threadPoolExecutor);


        CompletableFuture<Void> categoryCompletableFuture = skuCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                // 商品分类
                BaseCategoryView categoryView = productFeignClient.getCategoryViewByC3Id(skuInfo.getCategory3Id());
                map.put("categoryView",categoryView);
            }
        },threadPoolExecutor);

        CompletableFuture<Void> spuSaleAttrCompletableFuture = skuCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                // 销售属性列表n
                List<SpuSaleAttr> spuSaleAttrs = productFeignClient.getSpuSaleAttrListCheckBySku(skuInfo.getSpuId(), skuInfo.getId());
                map.put("spuSaleAttrList", spuSaleAttrs);
            }
        },threadPoolExecutor);


        CompletableFuture<Void> valuesCompletableFuture = skuCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {

                //销售属性值对应的skuHash表格
                List<Map<String,Object>> valuesSkus =  productFeignClient.getValuesSku(skuInfo.getSpuId());


                if(null!=valuesSkus&&valuesSkus.size()>0){
                    Map<String,Object> valuesSkusMap = new HashMap<>();
                    for (Map<String, Object> valuesSku : valuesSkus) {
                        String valueIds = (String)valuesSku.get("valueIds");
                        Integer skuIdForValues = (Integer)valuesSku.get("sku_id");
                        valuesSkusMap.put(valueIds,skuIdForValues);
                    }
                    String valuesSkuJson = JSON.toJSONString(valuesSkusMap);
                    map.put("valuesSkuJson",valuesSkuJson);
                }
            }
        },threadPoolExecutor);


        CompletableFuture.allOf(
                skuCompletableFuture,
                imgCompletableFuture,
                priceCompletableFuture,
                categoryCompletableFuture,
                spuSaleAttrCompletableFuture,
                valuesCompletableFuture).join();

        long end= System.currentTimeMillis();

        System.out.println("item执行时间"+(end-start));
        // 调用search搜索服务更新该商品的热度值
        try {
            searchFeignClient.hotScore(skuId);
        }catch (Exception e){
            e.printStackTrace();
            return map;
        }

        return map;
    }
}

