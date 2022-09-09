package com.sharecharge.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.mall.entity.Cart;

import java.util.List;

/**
 * <p>
 * 购物车商品表 服务类
 * </p>
 *
 * @author shiyuan
 * @since 2020-12-16
 */
public interface CartService extends IService<Cart> {

    public List<Cart> queryByUid(Integer userId);

    public Cart findById(Integer id);

    public void add(Cart cart);

    public int update(Cart cart);

    public Cart findById(Integer userId, Integer id);

    void deletes(String[] ids, Integer userId);

    void updateCheck(Integer userId, List<Integer> idsList, Boolean checked);

    List<Cart> queryByUidAndChecked(Integer userId);

    void clearGoods(Integer userId);

}
