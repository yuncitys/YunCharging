package com.sharecharge.web.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sharecharge.biz.entity.ImageCarousel;
import com.sharecharge.biz.service.ImageCarouselService;
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.ResultUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("sys/ImageCarousel")
@RequiredArgsConstructor
public class ImageCarouselController {
    //图片资源访问路径
    //static String fileLocation = "/restaurantRes/";

    final ImageCarouselService imageCarouselService;

    /**
     * 查询所有广告信息
     *
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping("findAllImageCarousel")
    @PreAuthorize("@ps.hasPermission(':sys:ImageCarousel:findAllImageCarousel')")
    public ResultUtil findAllImageCarousel(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                           @RequestParam(value = "limit") Integer limit) {
        try {
            //构建返回结果
            Page<ImageCarousel> imageCarouselPage=new Page<>();
            imageCarouselPage.setCurrent(page);
            imageCarouselPage.setSize(limit);
            IPage<ImageCarousel> imageCarouselPageList = imageCarouselService.page(imageCarouselPage);
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setMsg("查询广告信息成功");
            resultUtil.setData(imageCarouselPageList.getRecords());
            resultUtil.setCount(imageCarouselPageList.getTotal());
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            return resultUtil;
        } catch (Exception e) {
            log.error("查询广告失败", e.getMessage());
            return ResultUtil.error("查询广告失败");
        }
    }

    /**
     * 删除广告
     *
     * @param id
     * @return
     */
    @RequestMapping("deleteImageCarousel")
    @PreAuthorize("@ps.hasPermission(':AD:ADList:delete')")
    public ResultUtil deleteImageCarousel(@RequestParam("id") Integer id) {
        try {
//            ImageCarousel imageCarousel=new ImageCarousel();
//            imageCarousel.setId(id);
//            imageCarousel.setDeleteStatus(1);
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setMsg("删除广告成功");
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            resultUtil.setData(imageCarouselService.removeById(id));
            return resultUtil;
        } catch (Exception e) {
            log.error("删除广告失败", e.getMessage());
            return ResultUtil.error("删除广告失败");
        }
    }

    /**
     * 添加广告
     *
     * @param imageCarousel
     * @return
     */
    @RequestMapping("addImageCarousel")
    @PreAuthorize("@ps.hasPermission(':AD:ADList:add')")
    public ResultUtil addImageCarousel(ImageCarousel imageCarousel) {

        try {
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            resultUtil.setMsg("添加广告成功");
            resultUtil.setData(imageCarouselService.save(imageCarousel));
            return resultUtil;
        } catch (Exception e) {
            log.error("添加广告失败", e.getMessage());
            return ResultUtil.error("添加广告失败");
        }
    }

    /**
     * 更新广告信息
     *
     * @param imageCarousel
     * @return
     */
    @RequestMapping("updateImageCarousel")
    @PreAuthorize("@ps.hasPermission(':AD:ADList:edit')")
    public ResultUtil updateImageCarousel(ImageCarousel imageCarousel) {
        try {
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setMsg("更新广告成功");
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            resultUtil.setData(imageCarouselService.updateById(imageCarousel));
            return resultUtil;
        } catch (Exception e) {
            log.error("更新广告失败", e.getMessage());
            return ResultUtil.error("更新广告失败");
        }

    }
}

