package com.sharecharge.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.mall.entity.GoodsProduct;

/**
 * <p>
 * 商品货品表 服务类
 * </p>
 *
 * @author shiyuan
 * @since 2020-12-16
 */
public interface GoodsProductService extends IService<GoodsProduct> {

    GoodsProduct findById(Integer id);
    int reduceStock(Integer id, Short num);
    int addStock(Integer id, Short num);
}
