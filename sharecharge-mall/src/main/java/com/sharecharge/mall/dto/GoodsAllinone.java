package com.sharecharge.mall.dto;


import com.sharecharge.mall.entity.Goods;
import com.sharecharge.mall.entity.GoodsAttribute;
import com.sharecharge.mall.entity.GoodsProduct;
import com.sharecharge.mall.entity.GoodsSpecification;
import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsAllinone implements Serializable {
    Goods goods;
    GoodsSpecification[] specifications;
    GoodsAttribute[] attributes;
    GoodsProduct[] products;

}
