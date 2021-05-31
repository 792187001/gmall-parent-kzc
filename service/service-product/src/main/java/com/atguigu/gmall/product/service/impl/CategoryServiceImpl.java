package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.aop.GmallCache;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    BaseCategory1Mapper baseCategory1Mapper;


    @Autowired
    BaseCategory2Mapper baseCategory2Mapper;



    @Autowired
    BaseCategory3Mapper baseCategory3Mapper;


    @Autowired
    BaseCategoryViewMapper baseCategoryViewMapper;

    @Override
    public List<BaseCategory1> getCategory1() {

        // dao层查询
        List<BaseCategory1> baseCategory1s = baseCategory1Mapper.selectList(null);

        return baseCategory1s;
    }

    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {

        QueryWrapper<BaseCategory2> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category1_id",category1Id);
        // dao层查询
        List<BaseCategory2> baseCategory2s = baseCategory2Mapper.selectList(queryWrapper);

        return baseCategory2s;
    }

    @GmallCache
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        QueryWrapper<BaseCategory3> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category2_id",category2Id);
        // dao层查询
        List<BaseCategory3> baseCategory3s = baseCategory3Mapper.selectList(queryWrapper);

        return baseCategory3s;
    }

    @Override
    public BaseCategoryView getCategoryViewByC3Id(Long category3Id) {

        QueryWrapper<BaseCategoryView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_id",category3Id);
        BaseCategoryView baseCategoryView = baseCategoryViewMapper.selectOne(queryWrapper);

        return baseCategoryView;
    }

    @Override
    public List<BaseCategoryView> getCategoryView() {
        List<BaseCategoryView> baseCategoryViews = baseCategoryViewMapper.selectList(null);
        return baseCategoryViews;
    }
}
