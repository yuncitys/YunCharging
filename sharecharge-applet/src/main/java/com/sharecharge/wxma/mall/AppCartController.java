package com.sharecharge.wxma.mall;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.sharecharge.core.util.JacksonUtil;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.core.util.StringUtils;
import com.sharecharge.mall.entity.*;
import com.sharecharge.mall.service.*;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/app/cart")
@ResponseBody
public class AppCartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsProductService goodsProductService;

    @Autowired
    private MemberAddressService memberAddressService;

    @Autowired
    private CouponUserService couponUserService;

    @Autowired
    private CouponService couponService;


    @GetMapping("/index")
    public ResultUtil index(Integer userId) {
        if (userId == null) {
            return ResultUtil.error("参数异常");
        }
        List<Cart> list = cartService.queryByUid(userId);
        List<Cart> cartList = new ArrayList<>();
        // 如果系统检查商品已删除或已下架，则系统自动删除。
        // 更好的效果应该是告知用户商品失效，允许用户点击按钮来清除失效商品。
        for (Cart cart : list) {
            Goods goods = goodsService.getOne(new QueryWrapper<Goods>().lambda().eq(Goods::getId, cart.getGoodsId()).eq(Goods::getDeleted, false).eq(Goods::getIsOnSale, true));
            if (goods == null || !goods.getIsOnSale()) {
                cartService.removeById(cart.getId());
            } else {
                cartList.add(cart);
            }
        }
        Integer goodsCount = 0;
        BigDecimal goodsAmount = new BigDecimal(0.00);
        Integer checkedGoodsCount = 0;
        BigDecimal checkedGoodsAmount = new BigDecimal(0.00);

        for (Cart cart : cartList) {
            goodsCount += cart.getNumber();
            goodsAmount = goodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            if (cart.getChecked()) {
                checkedGoodsCount += cart.getNumber();
                checkedGoodsAmount = checkedGoodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            }
        }

        Map<String, Object> cartTotal = new HashMap<>();
        cartTotal.put("goodsCount", goodsCount);
        cartTotal.put("goodsAmount", goodsAmount);
        cartTotal.put("checkedGoodsCount", checkedGoodsCount);
        cartTotal.put("checkedGoodsAmount", checkedGoodsAmount);

        Map<String, Object> result = new HashMap<>();
        result.put("cartList", cartList);
        result.put("cartTotal", cartTotal);

        return ResultUtil.success(result);

    }


    @PostMapping
    public ResultUtil add(Integer userId, @RequestBody @Valid Cart cart) {
        if (userId == null) {
            return ResultUtil.error("参数异常");
        }
        Integer productId = cart.getProductId();
        Integer number = cart.getNumber();
        Integer goodsId = cart.getGoodsId();
        if (!ObjectUtils.allNotNull(productId, number, goodsId)) {
            return ResultUtil.error("参数异常");
        }
        if (number <= 0) {
            return ResultUtil.error("参数异常");
        }

        // 判断商品是否可以购买
        Goods goods = goodsService.findById(goodsId);
        if (goods == null || !goods.getIsOnSale()) {
            return ResultUtil.error("商品已下架");
        }
        GoodsProduct product = goodsProductService.getById(productId);
        //判断购物车中是否存在此规格商品
        Cart existCart = cartService.getOne(new QueryWrapper<Cart>().lambda().eq(Cart::getGoodsId, goodsId).eq(Cart::getProductId, productId).eq(Cart::getUserId, userId).eq(Cart::getDeleted, false));
        if (existCart == null) {
            //取得规格的信息,判断规格库存
            if (product == null || number > product.getNumber()) return ResultUtil.error("库存不足.");

            cart.setId(null);
            cart.setGoodsSn(goods.getGoodsSn());
            cart.setGoodsName(goods.getName());
            if (StringUtils.isEmpty(product.getUrl())) {
                cart.setPicUrl(goods.getPicUrl());
            } else {
                cart.setPicUrl(product.getUrl());
            }
            cart.setPrice(product.getPrice());
            cart.setSpecifications(product.getSpecifications());
            cart.setUserId(userId);
            // 设置 商家id
            cart.setBusinessId(goods.getBusId());
            cart.setChecked(true);
            cartService.add(cart);
        } else {
            //取得规格的信息,判断规格库存
            int num = existCart.getNumber() + number;
            if (num > product.getNumber()) {
                return ResultUtil.error("库存不足");
            }
            existCart.setNumber(num);
            if (cartService.update(existCart) == 0) {
                return ResultUtil.error("系统异常");
            }
        }
        return ResultUtil.success();
    }

    @PutMapping
    public ResultUtil update(Integer userId, @RequestBody @Valid Cart cart) {
        if (userId == null) {
            return ResultUtil.error("参数异常");
        }
        Integer productId = cart.getProductId();
        Integer number = cart.getNumber();
        Integer goodsId = cart.getGoodsId();
        Integer id = cart.getId();
        if (!ObjectUtils.allNotNull(id, productId, number, goodsId)) {
            return ResultUtil.error("参数异常");
        }

        if (number <= 0) {
            return ResultUtil.error("参数异常");
        }
        //判断是否存在该订单
        // 如果不存在，直接返回错误
        Cart existCart = cartService.findById(userId, id);
        if (existCart == null) {
            return ResultUtil.error("参数异常");
        }
        if (!existCart.getGoodsId().equals(goodsId)) {
            return ResultUtil.error("参数异常");
        }
        if (!existCart.getProductId().equals(productId)) {
            return ResultUtil.error("参数异常");
        }
        //判断商品是否可以购买
        Goods goods = goodsService.findById(goodsId);
        if (goods == null) {
            return ResultUtil.error("商品已下架");
        }
        //取得规格的信息,判断规格库存
        GoodsProduct product = goodsProductService.findById(productId);
        if (product == null || product.getNumber() < number) {
            return ResultUtil.error("库存不足");
        }
        existCart.setNumber(number);
        if (cartService.update(existCart) == 0) {
            return ResultUtil.error("更新失败");
        }

        return ResultUtil.success();
    }


    @DeleteMapping
    public ResultUtil delete(Integer userId, @NotBlank(message = "{required}") String ids) {
        if (userId == null) {
            return ResultUtil.error("参数异常");
        }
        String[] arr = ids.split(StringPool.COMMA);
        cartService.deletes(arr, userId);
        return ResultUtil.success();
    }


    @PostMapping("/fastadd")
    public ResultUtil fastadd(Integer userId, @RequestBody @Valid Cart cart) {
        if (userId == null) {
            return ResultUtil.error("请先登录");
        }

        Integer productId = cart.getProductId();
        Integer number = cart.getNumber();
        Integer goodsId = cart.getGoodsId();
        if (!ObjectUtils.allNotNull(productId, number, goodsId)) {
            return ResultUtil.error("参数异常");
        }
        if (number <= 0) {
            return ResultUtil.error("参数异常");
        }

        //判断商品是否可以购买
        Goods goods = goodsService.findById(goodsId);
        if (goods == null || !goods.getIsOnSale()) {
            return ResultUtil.error("商品已下架");
        }
        GoodsProduct product = goodsProductService.findById(productId);
        //判断购物车中是否存在此规格商品
        Cart existCart = cartService.getOne(new QueryWrapper<Cart>().lambda().eq(Cart::getGoodsId, goodsId).eq(Cart::getProductId, productId).eq(Cart::getUserId, userId).eq(Cart::getDeleted, false));
        if (existCart == null) {
            //取得规格的信息,判断规格库存
            if (product == null || number > product.getNumber()) return ResultUtil.error("库存不足");

            cart.setId(null);
            cart.setGoodsSn(goods.getGoodsSn());
            cart.setGoodsName((goods.getName()));
            if (StringUtils.isEmpty(product.getUrl())) {
                cart.setPicUrl(goods.getPicUrl());
            } else {
                cart.setPicUrl(product.getUrl());
            }
            cart.setPrice(product.getPrice());
            cart.setSpecifications(product.getSpecifications());
            cart.setUserId(userId);
            cart.setChecked(true);
            cartService.add(cart);
        } else {
            //取得规格的信息,判断规格库存
            int num = number;
            if (num > product.getNumber()) {
                return ResultUtil.error("库存不足");
            }
            existCart.setNumber(num);
            if (cartService.update(existCart) == 0) {
                return ResultUtil.error("更新失败");
            }
        }
        return ResultUtil.success(existCart != null ? existCart.getId() : cart.getId());
    }


    @GetMapping("/goodscount")
    public ResultUtil goodscount(Integer userId) {
        if (userId == null) {
            return ResultUtil.success(0);
        }
        int count = cartService.count(new QueryWrapper<Cart>().lambda().eq(Cart::getUserId, userId).eq(Cart::getDeleted, false));
        return ResultUtil.success(count);

    }


    @PostMapping("/checked")
    public ResultUtil checked(Integer userId, @RequestBody String body) {
        if (userId == null) {
            return ResultUtil.error("请先登录");
        }
        if (body == null) {
            return ResultUtil.error("参数异常");
        }
        List<Integer> productIds = JacksonUtil.parseIntegerList(body, "productIds");
        if (productIds == null) {
            return ResultUtil.error("参数异常");
        }
        Integer checkValue = JacksonUtil.parseInteger(body, "isChecked");
        if (checkValue == null) {
            return ResultUtil.error("参数异常");
        }
        Boolean isChecked = (checkValue == 1);
        cartService.updateCheck(userId, productIds, isChecked);
        return index(userId);
    }


    @GetMapping("/checkout")
    public ResultUtil checkout(Integer userId, Integer cartId, Integer addressId, Integer couponId, Integer userCouponId, Integer productId, Integer number) {
        if (userId == null) {
            return ResultUtil.error("请先登录");
        }

        // 收货地址
        MemberAddress checkedAddress = null;
        if (addressId == null || addressId == 0) {
            checkedAddress = memberAddressService.getOne(new QueryWrapper<MemberAddress>().lambda().eq(MemberAddress::getUserId, userId).eq(MemberAddress::getDeleted, false).eq(MemberAddress::getIsDefault, true));
            // 如果仍然没有地址，则是没有收货地址
            // 返回一个空的地址id=0，这样前端则会提醒添加地址
            if (checkedAddress == null) {
                addressId = 0;
            } else {
                addressId = checkedAddress.getId();
            }
        } else {
            checkedAddress = memberAddressService.getOne(new QueryWrapper<MemberAddress>().lambda().eq(MemberAddress::getId, addressId).eq(MemberAddress::getUserId, userId).eq(MemberAddress::getDeleted, false));
            // 如果null, 则报错
            if (checkedAddress == null) return ResultUtil.error("参数异常");
        }

        // 商品价格
        List<Cart> checkedGoodsList = null;
        if (cartId == null || cartId == 0) {
            checkedGoodsList = cartService.list(new QueryWrapper<Cart>().lambda().eq(Cart::getUserId, userId).eq(Cart::getChecked, true).eq(Cart::getDeleted, false));
        } else {

            Goods byId = goodsService.getById(cartId);
            if (byId == null) {
                return ResultUtil.error("参数异常");
            }
            GoodsProduct product = goodsProductService.findById(productId);
            if (product == null || 1 > product.getNumber()) {
                return ResultUtil.error("库存不足");
            }

            Cart cart = new Cart();
            cart.setProductId(productId);
            cart.setGoodsSn(byId.getGoodsSn());
            cart.setGoodsName(byId.getName());

            if (StringUtils.isEmpty(product.getUrl())) {
                cart.setPicUrl(byId.getPicUrl());
            } else {
                cart.setPicUrl(product.getUrl());
            }
            cart.setPrice(product.getPrice());
            cart.setSpecifications(product.getSpecifications());
            cart.setUserId(userId);
            cart.setNumber(number);
            cart.setGoodsId(byId.getId());
            cart.setDeleted(false);
            cart.setChecked(false);
            // 设置商家id
            cart.setBusinessId(byId.getBusId());
            checkedGoodsList = new ArrayList<>(1);
            checkedGoodsList.add(cart);

//            Cart cart = cartService.findById(userId, cartId);
//            if (cart == null){
//                return ResultUtil.fail402();
//            }
//            checkedGoodsList = new ArrayList<>(1);
//            checkedGoodsList.add(cart);


        }
        // 所有商家id
        List<Integer> busIds = new ArrayList<>();

        // BigDecimal checkedGoodsPrice = new BigDecimal(0.00);
        for (Cart cart : checkedGoodsList) {
            busIds.add(cart.getBusinessId());
            //  checkedGoodsPrice = checkedGoodsPrice.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
        }
        // 去后的重商家
        List<Integer> rebusIds = busIds.stream().distinct().collect(Collectors.toList());
//        // 计算优惠券可用情况
//        BigDecimal tmpCouponPrice = new BigDecimal(0.00);
//        Integer tmpCouponId = 0;
//        Integer tmpUserCouponId = 0;
//        int tmpCouponLength = 0;
//
//        List<CouponUser> couponUserList = couponUserService.list(new QueryWrapper<CouponUser>().lambda().eq(CouponUser::getUserId,userId).orderByDesc(CouponUser::getAddTime));
//        for(CouponUser couponUser : couponUserList){
//            Coupon coupon = couponService.checkCoupon(userId, couponUser.getCouponId(), couponUser.getId() ,checkedGoodsPrice, checkedGoodsList);
//            if(coupon == null){
//                continue;
//            }
//            tmpCouponLength++;
//            if(tmpCouponPrice.compareTo(coupon.getDiscount()) == -1){
//                tmpCouponPrice = coupon.getDiscount();
//                tmpCouponId = coupon.getId();
//                tmpUserCouponId = couponUser.getId();
//            }
//        }
//        // 获取优惠券减免金额，优惠券可用数量
//        int availableCouponLength = tmpCouponLength;
//        BigDecimal couponPrice = new BigDecimal(0);
//        // 这里存在三种情况
//        // 1. 用户不想使用优惠券，则不处理
//        // 2. 用户想自动使用优惠券，则选择合适优惠券
//        // 3. 用户已选择优惠券，则测试优惠券是否合适
//        if (couponId == null || couponId == -1){
//            couponId = -1;
//            userCouponId = -1;
//        } else if (couponId == 0) {
//            couponPrice = tmpCouponPrice;
//            couponId = tmpCouponId;
//            userCouponId = tmpUserCouponId;
//        } else {
//            Coupon coupon = couponService.checkCoupon(userId, couponId, userCouponId,checkedGoodsPrice, checkedGoodsList);
//            // 用户选择的优惠券有问题，则选择合适优惠券，否则使用用户选择的优惠券
//            if(coupon == null){
//                couponPrice = tmpCouponPrice;
//                couponId = tmpCouponId;
//                userCouponId = tmpUserCouponId;
//            }
//            else {
//                couponPrice = coupon.getDiscount();
//            }
//        }
//
//        // 根据订单商品总价计算运费，满88则免运费，否则8元；
//        BigDecimal freightPrice = new BigDecimal(0.00);
//        if (checkedGoodsPrice.compareTo(new BigDecimal("88")) < 0) {
//            freightPrice = new BigDecimal(8);
//        }
//
//        // 可以使用的其他钱，例如用户积分
//        BigDecimal integralPrice = new BigDecimal(0.00);
//
//        // 订单费用
//        BigDecimal orderTotalPrice = checkedGoodsPrice.add(freightPrice).subtract(couponPrice).max(new BigDecimal(0.00));
//
//
//        BigDecimal actualPrice = orderTotalPrice.subtract(integralPrice);

        //  aa{
        // xx
        // }
        List<Object> list = new ArrayList<>();

        // 购物车的信息区分起来

        BigDecimal orderPriceSum = new BigDecimal("0");
        BigDecimal originalTotalSum = new BigDecimal("0");
        for (Integer buid : rebusIds) {
            Map<String, Object> map = new HashMap<>();
            // 根据id 查询店铺名称
            // 匹配商品
            List<Cart> cartList = checkedGoodsList.stream().filter(cart -> cart.getBusinessId().equals(buid)).collect(Collectors.toList());

            BigDecimal checkedGoodsPrice = new BigDecimal(0.00);
            for (Cart cart : cartList) {
                checkedGoodsPrice = checkedGoodsPrice.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            }

            // 计算优惠券可用情况
            BigDecimal tmpCouponPrice = new BigDecimal(0.00);
            Integer tmpCouponId = 0;
            Integer tmpUserCouponId = 0;
            int tmpCouponLength = 0;

            List<CouponUser> couponUserList = couponUserService.list(new QueryWrapper<CouponUser>().lambda().eq(CouponUser::getUserId, userId).eq(CouponUser::getBusId, buid).orderByDesc(CouponUser::getAddTime));
            for (CouponUser couponUser : couponUserList) {
                Coupon coupon = couponService.checkCoupon(userId, couponUser.getCouponId(), couponUser.getId(), checkedGoodsPrice, buid, cartList);
                if (coupon == null) {
                    continue;
                }
                tmpCouponLength++;
                if (tmpCouponPrice.compareTo(coupon.getDiscount()) == -1) {
                    tmpCouponPrice = coupon.getDiscount();
                    tmpCouponId = coupon.getId();
                    tmpUserCouponId = couponUser.getId();
                }
            }
            // 获取优惠券减免金额，优惠券可用数量
            int availableCouponLength = tmpCouponLength;
            BigDecimal couponPrice = new BigDecimal(0);
            // 这里存在三种情况
            // 1. 用户不想使用优惠券，则不处理
            // 2. 用户想自动使用优惠券，则选择合适优惠券
            // 3. 用户已选择优惠券，则测试优惠券是否合适
            if (couponId == null || couponId == -1) {
                couponId = -1;
                userCouponId = -1;
            } else if (couponId == 0) {
                couponPrice = tmpCouponPrice;
                couponId = tmpCouponId;
                userCouponId = tmpUserCouponId;
            } else {
                Coupon coupon = couponService.checkCoupon(userId, couponId, userCouponId, checkedGoodsPrice, buid, cartList);
                // 用户选择的优惠券有问题，则选择合适优惠券，否则使用用户选择的优惠券
                if (coupon == null) {
                    couponPrice = tmpCouponPrice;
                    couponId = tmpCouponId;
                    userCouponId = tmpUserCouponId;
                } else {
                    couponPrice = coupon.getDiscount();
                }
            }

            // 根据订单商品总价计算运费，满88则免运费，否则8元；
            BigDecimal freightPrice = new BigDecimal(0.00);
            if (checkedGoodsPrice.compareTo(new BigDecimal("88")) < 0) {
                freightPrice = new BigDecimal(8);
            }

            // 可以使用的其他钱，例如用户积分
            BigDecimal integralPrice = new BigDecimal(0.00);

            // 订单费用
            BigDecimal orderTotalPrice = checkedGoodsPrice.add(freightPrice).subtract(couponPrice).max(new BigDecimal(0.00));


            BigDecimal actualPrice = orderTotalPrice.subtract(integralPrice);


            map.put("cartList", cartList);
            map.put("bus", "商家名称");
            map.put("buid", buid);

            map.put("addressId", addressId);
            map.put("couponId", couponId);
            map.put("userCouponId", userCouponId);
            map.put("cartId", cartId);
            map.put("availableCouponLength", availableCouponLength);
            // 商品金额
            map.put("goodsTotalPrice", checkedGoodsPrice);
            map.put("freightPrice", freightPrice);
            map.put("couponPrice", couponPrice);
            // 订单金额
            map.put("orderTotalPrice", orderTotalPrice);
            map.put("actualPrice", actualPrice);

            // 总金额相加 优惠后
            orderPriceSum = orderPriceSum.add(orderTotalPrice);
            // 原价
            originalTotalSum = originalTotalSum.add(checkedGoodsPrice);
            list.add(map);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("addressId", addressId);
//        data.put("couponId", couponId);
//        data.put("userCouponId", userCouponId);
//        data.put("cartId", cartId);
        data.put("checkedAddress", checkedAddress);
//        data.put("availableCouponLength", availableCouponLength);
//        data.put("goodsTotalPrice", checkedGoodsPrice);
//        data.put("freightPrice", freightPrice);
//        data.put("couponPrice", couponPrice);
//        data.put("orderTotalPrice", orderTotalPrice);
//        data.put("actualPrice", actualPrice);
        data.put("discountTotalPrice", orderPriceSum);
        data.put("originalTotalPrice", originalTotalSum);
        data.put("checkedGoodsList", list);
        return ResultUtil.success(data);
    }


}
