package com.atguigu.gmall.product.client;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(value = "service-product")
public interface ProductFeignClient {
    @RequestMapping("api/product/getSkuById/{skuId}")
    SkuInfo getSkuById(@PathVariable("skuId") Long skuId);

    @RequestMapping("api/product/getSkuImagesBySkuId/{skuId}")
    List<SkuImage> getSkuImagesBySkuId(@PathVariable("skuId") Long skuId);

    @RequestMapping("api/product/getSkuPriceById/{skuId}")
    BigDecimal getSkuPriceById(@PathVariable("skuId") Long skuId);

    @RequestMapping("api/product/getCategoryViewByC3Id/{category3Id}")
    BaseCategoryView getCategoryViewByC3Id(@PathVariable("category3Id") Long category3Id);

    @RequestMapping("api/product/getSpuSaleAttrListCheckBySku/{spuId}/{skuId}")
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("spuId") Long spuId,@PathVariable("skuId")  Long skuId);

    @RequestMapping("api/product/getValuesSku/{spuId}")
    List<Map<String, Object>> getValuesSku(@PathVariable("spuId") Long spuId);

    @RequestMapping("api/product/getCategoryView")
    List<BaseCategoryView> getCategoryView();

    @RequestMapping("api/product/getGoodsBySkuId/{skuId}")
    Goods getGoodsBySkuId(@PathVariable("skuId") Long skuId);
}
