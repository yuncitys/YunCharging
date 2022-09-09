package com.sharecharge.mall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.mall.dto.GoodsAllinone;
import com.sharecharge.mall.dto.GoodsDto;
import com.sharecharge.mall.entity.Goods;

import java.util.List;
import java.util.Map;

public interface GoodsService extends IService<Goods> {

    IPage<Goods> list(Long userId, Integer goodsId, String goodsSn, String name,
                      Integer page, Integer limit);

    Map<String, Object> list2();

    ResultUtil update(GoodsAllinone goodsAllinone);

    Object delete(Goods goods);

    Object create(GoodsAllinone goodsAllinone);

    Object detail(Integer id);


    /**
     * 获取热卖商品
     *
     * @param offset
     * @param limit
     * @return
     */
    List<Goods> queryByHot(int offset, int limit, Goods goods);

    /**
     * 获取新品上市
     *
     * @param offset
     * @param limit
     * @return
     */
    List<Goods> queryByNew(int offset, int limit);

    /**
     * 获取分类下的商品
     *
     * @param catList
     * @param offset
     * @param limit
     * @return
     */
    List<Goods> queryByCategory(List<Integer> catList, int offset, int limit);


    /**
     * 获取分类下的商品
     *
     * @param catId
     * @param offset
     * @param limit
     * @return
     */
    List<Goods> queryByCategory(Integer catId, int offset, int limit);


    List<Goods> querySelective(Long userId, Integer catId, Integer brandId, String keywords, Boolean isHot, Boolean isNew, Integer offset, Integer limit);

    IPage<Goods> querySelective(Long userId, Integer goodsId, String goodsSn, String name, Integer page, Integer size);

    /**
     * 获取某个商品信息,包含完整信息
     *
     * @param id
     * @return
     */
    Goods findById(Integer id);

    /**
     * 获取某个商品信息，仅展示相关内容
     *
     * @param id
     * @return
     */
    Goods findByIdVO(Integer id);


    /**
     * 获取所有在售物品总数
     *
     * @return
     */
    Integer queryOnSale(Long userId);


    /**
     * 获取所有物品总数，包括在售的和下架的，但是不包括已删除的商品
     *
     * @return
     */
    int count(Long userId);

    List<Integer> getCatIds(Integer brandId, String keywords, Boolean isHot, Boolean isNew);

    boolean checkExistByName(String name) ;

     List<Goods> queryByIds(Integer[] ids);

    IPage<Goods> searchgoods(Page page, GoodsDto goodsDto);

}
