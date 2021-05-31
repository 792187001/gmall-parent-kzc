package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.aop.GmallCache;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.product.mapper.SkuImageMapper;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.mapper.SkuSaleAttrValueMapper;
import com.atguigu.gmall.product.service.SkuService;
import com.atguigu.gmall.search.client.SearchFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SkuServiceImpl  implements SkuService {
    @Autowired
    SkuInfoMapper skuInfoMapper;
    @Autowired
    SkuImageMapper skuImageMapper;
    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    SearchFeignClient searchFeignClient;

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        skuInfoMapper.insert(skuInfo);
        //保存sku image
        Long skuId = skuInfo.getId();
        for (SkuImage skuImage : skuInfo.getSkuImageList()) {
            skuImage.setSkuId(skuId);
            skuImageMapper.insert(skuImage);
        }
        //保存skuAtrrValue
        for (SkuAttrValue skuAttrValue : skuInfo.getSkuAttrValueList()) {
            skuAttrValue.setAttrId(skuId);
            skuAttrValueMapper.insert(skuAttrValue);
        }
        //保存相关销售的属性
        for (SkuSaleAttrValue skuSaleAttrValue : skuInfo.getSkuSaleAttrValueList()) {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        }
    }

    @Override
    public IPage<SkuInfo> list(Long page, Long limit) {
        IPage<SkuInfo> infoIPage = new Page<>(page,limit);
        IPage<SkuInfo> infoIPageResult = skuInfoMapper.selectPage(infoIPage, null);
        return infoIPageResult;
    }




    @GmallCache
    @Override
    public SkuInfo getSkuById(Long skuId) {
        SkuInfo skuInfo = null;
        // 查询数据库
        skuInfo = skuInfoMapper.selectById(skuId);

        return skuInfo;
    }



    public SkuInfo getSkuByIdBak(Long skuId) {
        SkuInfo skuInfo = null;

        // 查询缓存
        skuInfo = (SkuInfo) redisTemplate.opsForValue().get("sku:" + skuId + ":info");

        if (null == skuInfo) {
            String lockTag = UUID.randomUUID().toString();
            Boolean ifLock = redisTemplate.opsForValue().setIfAbsent("Sku:" + skuId + ":lock", lockTag, 1, TimeUnit.SECONDS);// 1秒后，自动删除锁

            if (ifLock){
                // 查询数据库
                skuInfo = skuInfoMapper.selectById(skuId);
                if (null != skuInfo){
                    // 同步到缓存
                    redisTemplate.opsForValue().set("Sku:"+skuId+":info",skuInfo);

                }else {
                    //若为空，等一会
                    redisTemplate.opsForValue().set("Sku:"+skuId+":info",new SkuInfo(),10, TimeUnit.SECONDS);
                }
                //还锁前 先判断是否为开始时创建的锁
             /*   String currentTag = (String) redisTemplate.opsForValue().get("Sku" + skuId + ":lock");
                if (StringUtils.isNotEmpty(currentTag)&&currentTag.endsWith(lockTag)){
                    redisTemplate.delete("Sku:"+skuId+":lock");
                }*/
                //用lua脚本
                String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                redisScript.setResultType(Long.class);
                redisScript.setScriptText(luaScript);
                redisTemplate.execute(redisScript, Arrays.asList("Sku:" + skuId + ":lock"), lockTag);
            }else {
                //自旋
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return getSkuById(skuId);
            }

        }

        return skuInfo;
    }



    @GmallCache
    @Override
    public List<SkuImage> getSkuImagesBySkuId(Long skuId) {
        QueryWrapper<SkuImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sku_id",skuId);
        List<SkuImage> skuImages = skuImageMapper.selectList(queryWrapper);
        return skuImages;
    }

    @GmallCache
    @Override
    public BigDecimal getSkuPriceById(Long skuId) {

        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);

        return skuInfo.getPrice();
    }

    @GmallCache
    @Override
    public List<Map<String, Object>> getValuesSku(Long spuId) {
        List<Map<String, Object>> maps =  skuSaleAttrValueMapper.selectValuesSku(spuId);
        return maps;
    }

    @Override
    public Goods getGoodsBySkuId(Long skuId) {
        Goods goods = skuInfoMapper.getGoodsBySkuId(skuId);

        return goods;
    }

    @Override
    public void cancelSale(Long skuId) {

        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);

        // 同步搜索引擎(搜索服务器，删除该商品)
        searchFeignClient.cancelSale(skuId);
    }

    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);

        // 同步搜索引擎(搜索服务器，添加该商品)
        searchFeignClient.onSale(skuId);
    }
}
