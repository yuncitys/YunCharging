package com.sharecharge.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.mall.entity.GoodsSpecification;

import java.util.List;

/**
 * <p>
 * 商品规格表 服务类
 * </p>
 *
 * @author shiyuan
 * @since 2020-12-16
 */
public interface GoodsSpecificationService extends IService<GoodsSpecification> {

    List<GoodsSpecification> queryByGid(Integer id);

//    GoodsSpecification findById(Integer id);

    void deleteByGid(Integer gid) ;


    Object getSpecificationVoList(Integer id);



}
