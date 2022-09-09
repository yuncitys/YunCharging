package com.sharecharge.wxma.mall;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sharecharge.biz.entity.AppUser;
import com.sharecharge.biz.service.AppUserService;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.mall.dto.GoodsDto;
import com.sharecharge.mall.entity.*;
import com.sharecharge.mall.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.*;

@Controller
@RequestMapping("/app/goods")
@ResponseBody
public class AppGoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SearchHistoryService searchHistoryService;

    @Autowired
    private GoodsAttributeService goodsAttributeService;

    @Autowired
    private GoodsSpecificationService goodsSpecificationService;

    @Autowired
    private GoodsProductService goodsProductService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private AppUserService memberService;

    @Autowired
    private CollectService collectService;

    @Autowired
    private FootprintService footprintService;

    @Autowired
    private GoodsSalesService goodsSalesService;

    @Autowired
    private BusinessDetailService businessDetailService;


    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(16, 16, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);


    @GetMapping("/category")
    public ResultUtil category(@NotNull Integer id) {
        Category category = categoryService.getById(id);
        Category parent = null;
        List<Category> children = null;
        if (category.getPid() == 0) {
            parent = category;
            children = categoryService.list(new QueryWrapper<Category>().lambda().eq(Category::getPid, category.getId()));
            category = children.size() > 0 ? children.get(0) : category;
        } else {
            parent = categoryService.getById(category.getPid());
            children = categoryService.list(new QueryWrapper<Category>().lambda().eq(Category::getPid, category.getPid()));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("currentCategory", category);
        data.put("parentCategory", parent);
        data.put("brotherCategory", children);
        return ResultUtil.success(data);
    }

    /**
     * 根据条件搜素商品
     * <p>
     * 1. 这里的前五个参数都是可选的，甚至都是空
     * 2. 用户是可选登录，如果登录，则记录用户的搜索关键字
     *
     * @param categoryId 分类类目ID，可选
     * @param brandId    品牌商ID，可选
     * @param keyword    关键字，可选
     * @param isNew      是否新品，可选
     * @param isHot      是否热买，可选
     * @param userId     用户ID
     * @param pageSize   分页页数
     * @param pageNum    分页大小
     * @return 根据条件搜素的商品详情
     */
    @GetMapping("/search")
    public ResultUtil list(
            Integer categoryId,
            Integer brandId,
            String keyword,
            Boolean isNew,
            Boolean isHot,
            Integer isPrice,
            Integer salesSum,
            Float startPrice,
            Float endPrice,
            Integer userId,
            @RequestParam(defaultValue = "1") Integer pageSize,
            @RequestParam(defaultValue = "10") Integer pageNum) {

        // 添加到搜索历史
        if (userId != null && !StringUtils.isEmpty(keyword)) {
            SearchHistory searchHistoryVo = new SearchHistory();
            searchHistoryVo.setAddTime(new Date());
            searchHistoryVo.setUpdateTime(new Date());
            searchHistoryVo.setKeyword(keyword);
            searchHistoryVo.setSource("wx");
            searchHistoryVo.setUserId(userId);
            searchHistoryService.save(searchHistoryVo);
        }
        IPage<Goods> page = new Page<>(pageSize, pageNum);

        GoodsDto goodsDto = new GoodsDto();
        goodsDto.setCategoryId(categoryId);
        goodsDto.setBrandId(brandId);
        goodsDto.setKeywords(keyword);
        goodsDto.setIsHot(isHot);
        goodsDto.setIsNew(isNew);
        goodsDto.setStartPrice(startPrice);
        goodsDto.setEndPrice(endPrice);

        if (isPrice != null) {
            if (isPrice == 1) goodsDto.setPriceDescOrAsc("ASC");
            else goodsDto.setPriceDescOrAsc("DESC");
        }
        if (salesSum != null) {
            if (salesSum == 1) goodsDto.setSalesDescOrAsc("DESC");
        }


        IPage<Goods> searchgoods = goodsService.searchgoods((Page) page, goodsDto);

        return ResultUtil.success(searchgoods);

    }

    @GetMapping("/detail")
    public ResultUtil detail(Integer userId, @NotNull Integer id) {
        // 商品信息
        Goods serviceById = goodsService.getOne(new QueryWrapper<Goods>().lambda().eq(Goods::getId, id).eq(Goods::getDeleted, false));

        // 发货地址 需要查询商家
        BusinessDetail serviceOne = businessDetailService.getOne(new QueryWrapper<BusinessDetail>().lambda().eq(BusinessDetail::getBusId, serviceById.getBusId()));
        if (serviceOne != null) {
            serviceById.setFormAddress(serviceOne.getAddress());
        }

        // 查询销量
        GoodsSales goodsSales = goodsSalesService.getOne(new QueryWrapper<GoodsSales>().lambda().eq(GoodsSales::getGoodId, serviceById.getId()));
        serviceById.setSalesSum(goodsSales.getSalesSum());
        // 商品属性
        Callable<List> goodsAttributeListCallable = () -> goodsAttributeService.list(new QueryWrapper<GoodsAttribute>().lambda().eq(GoodsAttribute::getGoodsId, id).eq(GoodsAttribute::getDeleted, false));
        // 商品规格 返回的是定制的GoodsSpecificationVo
        Callable<Object> objectCallable = () -> goodsSpecificationService.getSpecificationVoList(id);

        // 商品规格对应的数量和价格
        Callable<List> productListCallable = () -> goodsProductService.list(new QueryWrapper<GoodsProduct>().lambda().eq(GoodsProduct::getGoodsId, id).eq(GoodsProduct::getDeleted, false));

        // 商品问题，这里是一些通用问题  忽略

        // 商品品牌商
        Callable<Brand> brandCallable = () -> {
            Integer brandId = serviceById.getBrandId();
            Brand brand;
            if (brandId == 0) {
                brand = new Brand();
            } else {
                brand = brandService.findById(serviceById.getBrandId());
            }
            return brand;
        };

        // 评论


        // 评论
        Callable<Map> commentsCallable = () -> {
            IPage<Comment> page = new Page<>(1, 2);
            //List<Comment> comments = commentService.queryGoodsByGid(id, 0, 2);
            IPage<Comment> commentIPage = commentService.page(page, new QueryWrapper<Comment>().lambda().eq(Comment::getValueId, id).eq(Comment::getType, 0).eq(Comment::getDeleted, false).orderByDesc(Comment::getAddTime));
            List<Comment> comments = commentIPage.getRecords();
            long commentCount = commentIPage.getTotal();
            List<Map<String, Object>> commentsVo = new ArrayList<>(comments.size());
            for (Comment comment : comments) {
                Map<String, Object> c = new HashMap<>();
                c.put("id", comment.getId());
                c.put("addTime", comment.getAddTime());
                c.put("content", comment.getContent());
                c.put("adminContent", comment.getAdminContent());
                AppUser appUserById = memberService.getById(userId);
                c.put("nickname", appUserById == null ? "" : appUserById.getUserName());
                c.put("avatar", appUserById == null ? "" : appUserById.getHeadImg());
                c.put("picList", comment.getPicUrls());
                commentsVo.add(c);
            }
            Map<String, Object> commentList = new HashMap<>();
            commentList.put("count", commentCount);
            commentList.put("data", commentsVo);
            return commentList;
        };

        // 用户收藏
        int userHasCollect = 0;
        if (userId != null) {
            userHasCollect = collectService.count(new QueryWrapper<Collect>().lambda().eq(Collect::getUserId, userId).eq(Collect::getValueId, id).eq(Collect::getDeleted, false));
        }
        // 记录用户的足迹 异步处理
        if (userId != null) {
            executorService.execute(() -> {
                Footprint footprint = new Footprint();
                footprint.setUserId(userId);
                footprint.setGoodsId(id);
                footprint.setAddTime(new Date());
                footprint.setUpdateTime(new Date());
                footprintService.save(footprint);
            });
        }

        FutureTask<List> goodsAttributeListTask = new FutureTask<>(goodsAttributeListCallable);
        FutureTask<Object> objectCallableTask = new FutureTask<>(objectCallable);
        FutureTask<List> productListCallableTask = new FutureTask<>(productListCallable);
        FutureTask<Map> commentsCallableTsk = new FutureTask<>(commentsCallable);
        FutureTask<Brand> brandCallableTask = new FutureTask<>(brandCallable);


        executorService.submit(goodsAttributeListTask);
        executorService.submit(objectCallableTask);
        executorService.submit(productListCallableTask);
        executorService.submit(commentsCallableTsk);
        executorService.submit(brandCallableTask);

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> business = new HashMap<>();
        if (serviceOne != null) {
            business.put("shopName", serviceOne.getShopName());
            business.put("id", serviceOne.getBusId());
            business.put("introduce", serviceOne.getIntroduce());
        }


        try {
            data.put("info", serviceById);
            data.put("userHasCollect", userHasCollect);
            data.put("comment", commentsCallableTsk.get());
            data.put("specificationList", objectCallableTask.get());
            data.put("productList", productListCallableTask.get());
            data.put("attribute", goodsAttributeListTask.get());
            data.put("brand", brandCallableTask.get());
            data.put("business", business);
//            SystemConfig.isAutoCreateShareImage()
//            data.put("share", SystemConfig.isAutoCreateShareImage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResultUtil.success(data);
    }


    private class VO {
        private String name;
        private List<GoodsSpecification> valueList;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<GoodsSpecification> getValueList() {
            return valueList;
        }

        public void setValueList(List<GoodsSpecification> valueList) {
            this.valueList = valueList;
        }
    }


}
