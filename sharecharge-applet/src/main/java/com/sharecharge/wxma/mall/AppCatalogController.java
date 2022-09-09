package com.sharecharge.wxma.mall;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.mall.entity.Category;
import com.sharecharge.mall.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotNull;
import java.util.List;


@Controller
@RequestMapping("/app/catalog")
public class AppCatalogController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getFirstCategory")
    @ResponseBody
    public ResultUtil getFirstCategory() {
        List<Category> categoryList = categoryService.list(new QueryWrapper<Category>().lambda().eq(Category::getLevel, "L1").eq(Category::getPid, 0).eq(Category::getDeleted, false));
        return ResultUtil.success(categoryList);
    }

    @GetMapping("/getSecondCategory")
    @ResponseBody
    public ResultUtil getSecondCategory(@NotNull Integer id) {
        List<Category> categoryList = categoryService.list(new QueryWrapper<Category>().lambda().eq(Category::getPid, id));
        return ResultUtil.success(categoryList);
    }


}
