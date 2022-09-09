package com.sharecharge.web.mall;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.mall.dto.GoodsAllinone;
import com.sharecharge.mall.dto.QueryRequest;
import com.sharecharge.mall.entity.*;
import com.sharecharge.mall.service.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsProductService goodsProductService;

    @Autowired
    private GoodsAttributeService goodsAttributeService;

    @Autowired
    private GoodsSpecificationService goodsSpecificationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private GoodsSalesService salesService;


    @ApiOperation(value = "列表", notes = "列表")
    @RequestMapping("/list")
    @ResponseBody
    public ResultUtil list(Integer goodsId,
                           String goodsSn,
                           String name,
                           QueryRequest queryRequest) {
        Integer userId = 34;
        return ResultUtil.success(goodsService.list(userId.longValue(), goodsId, goodsSn, name, queryRequest.getPageNum(), queryRequest.getPageSize()));
    }

    @ApiOperation(value = "类目与品牌列表", notes = "类目与品牌列表")
    @RequestMapping("/catAndBrand")
    @ResponseBody
    public ResultUtil list2() {
        return ResultUtil.success(goodsService.list2());
    }


    @ApiOperation(value = "添加", notes = "添加")
    @RequestMapping("create")
    @ResponseBody
    public Object create(@RequestBody GoodsAllinone goodsAllinone) {
        goodsAllinone.getGoods().setBusId(34);
        return goodsService.create(goodsAllinone);
    }

    @ApiOperation(value = "修改", notes = "修改")
    @PutMapping
    @ResponseBody
    public ResultUtil update(@RequestBody GoodsAllinone goodsAllinone) {
        return goodsService.update(goodsAllinone);
    }


    @ApiOperation(value = "删除", notes = "删除")
    @ApiImplicitParam(name = "ids", value = "商品编号集合", required = true, dataType = "String", paramType = "query")
    @DeleteMapping
    @ResponseBody
    public ResultUtil delete(@NotBlank(message = "{required}") String ids) {
        String[] arr = ids.split(StringPool.COMMA);
        for (int i = 0; i < arr.length; i++) {
            Goods serviceOne = goodsService.getOne(new QueryWrapper<Goods>().lambda().eq(Goods::getId, arr[i]));
            if (serviceOne != null) {
                goodsService.removeById(serviceOne.getId());
            }
        }
        return ResultUtil.success();
    }

    @ApiOperation(value = "查询详细", notes = "根据编号查询详细")
    @ApiImplicitParam(name = "id", value = "商品编号", required = true, dataType = "int", paramType = "query")
    @RequestMapping("/detail")
    @ResponseBody
    public ResultUtil detail(@NotNull Integer id) {
        Goods goods = goodsService.getById(id);
        GoodsSales goodsSales = salesService.getOne(new QueryWrapper<GoodsSales>().lambda().eq(GoodsSales::getGoodId, id).eq(GoodsSales::getDeleted, false));
        if (goodsSales != null) {
            goods.setSalesSum(goodsSales.getSalesSum());
        }
        List<GoodsProduct> products = goodsProductService.list(new QueryWrapper<GoodsProduct>().lambda().eq(GoodsProduct::getGoodsId, id).eq(GoodsProduct::getDeleted, false));
        List<GoodsSpecification> specifications = goodsSpecificationService.queryByGid(id);
        List<GoodsAttribute> attributes = goodsAttributeService.queryByGid(id);

        Integer categoryId = goods.getCategoryId();
        Category category = categoryService.getById(categoryId);
        Integer[] categoryIds = new Integer[]{};
        if (category != null) {
            Integer parentCategoryId = category.getPid();
            categoryIds = new Integer[]{parentCategoryId, categoryId};
        }

        Map<String, Object> data = new HashMap<>();
        data.put("goods", goods);
        data.put("specifications", specifications);
        data.put("products", products);
        data.put("attributes", attributes);
        data.put("categoryIds", categoryIds);
        return ResultUtil.success(data);

    }

    @RequestMapping("/updateBannerById")
    @ResponseBody
    public ResultUtil updateBannerById(@NotNull Integer goodsId, @NotNull Integer status) {
        Goods goods = goodsService.findById(goodsId);
        if (goods == null) {
            return ResultUtil.error("商品不存在");
        }
        goods.setBannerStatus(status);
        goodsService.updateById(goods);
        return ResultUtil.success("更新成功");
    }

}
