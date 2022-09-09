package com.sharecharge.mall.vo;

import lombok.Data;

@Data
public class SubOrderVo {
    private String cartId;  // 购物车id  201,202,203 必填的  [1,2,3]
    private String message; // 留言  我是谁
    private Integer couponId; // 优惠券编号
    private Integer userCouponeId; // 用户优惠券编号
    private String productId; // 货品规格编号  101,102,103  必填的  [1,2,3]
    private Integer number; // 数量
}
