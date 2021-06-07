package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartInfoMapper cartInfoMapper;

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    @Transactional
    public CartInfo addCart(Long skuId, Long skuNum,String userId) {

        SkuInfo skuById = productFeignClient.getSkuById(skuId);// 查询sku信息

        CartInfo cartInfo = new CartInfo();
        cartInfo.setSkuNum(skuNum.intValue());
        cartInfo.setSkuId(skuId);
        cartInfo.setUserId(userId);
        cartInfo.setSkuName(skuById.getSkuName());
        cartInfo.setImgUrl(skuById.getSkuDefaultImg());
        cartInfo.setCartPrice(skuById.getPrice().multiply(new BigDecimal(skuNum)));

        // 以缓存内容为主(购物车功能是缓存库，数据库用来备份)
        CartInfo cartInfoCache = (CartInfo) redisTemplate.opsForHash().get("user:" + userId + ":cart", skuId + "");

        // 备份数据库
        if (null == cartInfoCache) {
            // 插入到数据库
            cartInfoMapper.insert(cartInfo);
        } else {
            // 更新到数据库
            QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            queryWrapper.eq("sku_id", skuId);
            cartInfo.setSkuNum(cartInfoCache.getSkuNum() + skuNum.intValue());
            cartInfo.setCartPrice(skuById.getPrice().multiply(new BigDecimal(cartInfo.getSkuNum())));
            cartInfoMapper.update(cartInfo, queryWrapper);
        }

        // 写入缓存,更新和插入是同一个方法
        cartInfo.setSkuPrice(skuById.getPrice());
        redisTemplate.opsForHash().put("user:" + userId + ":cart", skuId + "", cartInfo);
        return cartInfo;
    }

    @Override
    public List<CartInfo> cartList(String userId) {
        List<CartInfo> cartInfos =(List<CartInfo>) redisTemplate.opsForHash().values("user:" + userId + ":cart");

        if (cartInfos==null || cartInfos.size()<= 0){
            QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            List<CartInfo> cartInfosDB = cartInfoMapper.selectList(queryWrapper);

            if (null != cartInfosDB && cartInfosDB.size() > 0) {
                Map<String, Object> map = new HashMap<>();
                for (CartInfo cartInfo : cartInfosDB) {
                    cartInfo.setSkuPrice(productFeignClient.getSkuPriceById(cartInfo.getSkuId()));
                    map.put(cartInfo.getSkuId() + "", cartInfo);
                }
                redisTemplate.opsForHash().putAll("user:" + userId + ":cart", map);
            }
        }
        return cartInfos;
    }

    @Override
    public void checkCart(String userId, Long skuId, String isChecked) {


        CartInfo cartInfo = (CartInfo)redisTemplate.opsForHash().get("user:" + userId + ":cart", skuId + "");

        Long cartInfoId = cartInfo.getId();

        cartInfo.setIsChecked(Integer.parseInt(isChecked));

        // 修改缓存的选中状态
        redisTemplate.opsForHash().put("user:" + userId + ":cart", skuId + "",cartInfo);

        // 同步数据库
        cartInfoMapper.updateById(cartInfo);

    }
}
