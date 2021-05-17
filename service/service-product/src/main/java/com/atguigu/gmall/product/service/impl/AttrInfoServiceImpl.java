package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.AttrInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttrInfoServiceImpl implements AttrInfoService {

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    @Override
    public List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id) {

        QueryWrapper<BaseAttrInfo> queryWrapper = new QueryWrapper<>();
        //三级分类id
        queryWrapper.eq("category_id",category3Id);
        //默认三级分类
        queryWrapper.eq("category_level",3);

        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.selectList(queryWrapper);
        //外键直接循环打开
        for (BaseAttrInfo baseAttrInfo : baseAttrInfos) {
            Long attrId = baseAttrInfo.getId();

            QueryWrapper<BaseAttrValue> queryWrapperValue = new QueryWrapper<>();

            queryWrapperValue.eq("attr_id",attrId);

            List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.selectList(queryWrapperValue);

            baseAttrInfo.setAttrValueList(baseAttrValues);
        }

        return baseAttrInfos;
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        // 判断id
        Long id = baseAttrInfo.getId();

        if(null==id||id<=0){
            // 添加

            // 保存属性
            baseAttrInfoMapper.insert(baseAttrInfo);

            // 生成主键id
            Long attrId = baseAttrInfo.getId();

            id = attrId;

        }else{

            // 修改
            baseAttrInfoMapper.updateById(baseAttrInfo);

            QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("attr_id",id);
            baseAttrValueMapper.delete(queryWrapper);

        }
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();

        for (BaseAttrValue baseAttrValue : attrValueList) {
            baseAttrValue.setAttrId(id);
            baseAttrValueMapper.insert(baseAttrValue);
        }

        


    }

    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {


        QueryWrapper<BaseAttrValue> queryWrapperValue = new QueryWrapper<>();

        queryWrapperValue.eq("attr_id",attrId);

        List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.selectList(queryWrapperValue);


        return baseAttrValues;
    }
}
