package com.sharecharge.mall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.mall.dto.QueryRequest;
import com.sharecharge.mall.entity.Cart;
import com.sharecharge.mall.entity.Coupon;
import com.sharecharge.mall.entity.CouponUser;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService extends IService<Coupon> {
    Coupon checkCoupon(Integer userId, Integer couponId, Integer userCouponId, BigDecimal checkedGoodsPrice, Integer busId, List<Cart> cartList);

    IPage<Coupon> list(String name, Short type, Short status, QueryRequest queryRequest);

    IPage<CouponUser> userCouponlist(Integer couponId, Long userId, Short status, QueryRequest queryRequest);
}
