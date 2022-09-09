package com.sharecharge.mall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.mall.entity.Brand;

import java.util.List;

public interface BrandService extends IService<Brand> {

    IPage<Brand> query(Integer page, Integer limit, String sort, String order);

    IPage<Brand> query(Integer page, Integer limit);

    Brand findById(Integer id);

    IPage<Brand> querySelective(String id, String name, Integer page, Integer size, String sort, String order);


    void deleteById(Integer id);

    void add(Brand brand);

    List<Brand> all();
}
