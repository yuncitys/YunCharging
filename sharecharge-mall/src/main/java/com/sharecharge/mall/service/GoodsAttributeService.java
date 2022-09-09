package com.sharecharge.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.mall.entity.GoodsAttribute;

import java.util.List;

public interface GoodsAttributeService extends IService<GoodsAttribute> {

    List<GoodsAttribute> queryByGid(Integer goodsId);

//    void add(GoodsAttribute goodsAttribute);

//    GoodsAttribute findById(Integer id);

    void deleteByGid(Integer gid);

//    void deleteById(Integer id);

//    void updateById(GoodsAttribute attribute);
}
