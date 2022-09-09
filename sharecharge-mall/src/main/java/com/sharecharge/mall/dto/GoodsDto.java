package com.sharecharge.mall.dto;


import com.sharecharge.mall.entity.Goods;
import lombok.Data;

@Data
public class GoodsDto extends Goods {
    private String priceDescOrAsc;
    private String salesDescOrAsc;
    private Float startPrice;
    private Float endPrice;
}
