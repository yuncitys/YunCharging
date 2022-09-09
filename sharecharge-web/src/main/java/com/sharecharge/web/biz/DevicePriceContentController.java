package com.sharecharge.web.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sharecharge.biz.entity.DevicePrice;
import com.sharecharge.biz.entity.PriceContent;
import com.sharecharge.biz.service.DevicePriceService;
import com.sharecharge.biz.service.PriceContentService;
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.security.util.SecurityUtil;
import com.sharecharge.system.entity.DbAdminUser;
import com.sharecharge.system.service.DbAdminUserService;
import com.sharecharge.system.service.DbParentOrSonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
//没有@ResponseBody {"timestamp":"2022-07-21 18:27:19","status":404,"error":"Not Found","message":"No message available","path":"/sys/price/batchDevicePrices"}
@RequestMapping("sys/price")
@RequiredArgsConstructor
public class DevicePriceContentController {
    final PriceContentService priceContentService;
    final DbAdminUserService adminUserService;
    final DevicePriceService devicePriceService;
    final DbParentOrSonService parentOrSonService;

    /**
     * 查询充电收费方案数据列表
     *
     * @param page
     * @param limit
     * @param feeName
     * @return
     */
    @RequestMapping("priceList")
    @PreAuthorize("@ps.hasPermission(':sys:price:priceList')")
    public ResultUtil findPriceContentByType(@RequestParam("page") Integer page,
                                             @RequestParam("limit") Integer limit,
                                             @RequestParam("priceType") Integer priceType, String feeName) {
        Map map = new HashMap();
        map.put("curPageStarRow", (page - 1) * limit);
        map.put("limit", limit);
        map.put("priceType", priceType);
        map.put("feeName", feeName);
        map.put("ids", parentOrSonService.getSonByCurAdmin(SecurityUtil.getUserId()));
        return devicePriceService.list(map);
    }

    /**
     * 添加收费方案
     *
     * @param feeName
     * @param duration
     * @param money
     * @return
     */
    @RequestMapping("batchDevicePrices")
    @PreAuthorize("@ps.hasPermission(':netWorkDot:charge:timeCharge:add')")
    @Transactional
    public ResultUtil batchDevicePrices(@RequestParam("feeName") String feeName,
                                        @RequestParam("priceType") Integer priceType,
                                        Integer realTimeCharging,
                                        String[] powerSectionBefore, String[] powerSectionAfter,
                                        String[] duration, Double[] money) {
        try {
            //生成六位随机数
            int v = (int) ((Math.random() * 9 + 1) * 1000000);
            DevicePrice devicePrice = new DevicePrice();
            devicePrice.setId(v);
            devicePrice.setAdminUserId(SecurityUtil.getUserId());
            devicePrice.setFeeName(feeName);
            devicePrice.setPriceType(priceType);
            devicePrice.setRealTimeCharging(realTimeCharging);
            List<DevicePrice> list = devicePriceService.list(new QueryWrapper<DevicePrice>().lambda().eq(DevicePrice::getFeeName, feeName));
            if (!list.isEmpty()) {
                return ResultUtil.error("该方案已存在");
            }
            devicePriceService.save(devicePrice);
            List<PriceContent> priceContents = new ArrayList<>();
            for (int i = 0; i < duration.length; i++) {
                PriceContent priceContent = new PriceContent();
                priceContent.setDevicePriceId(v);
                priceContent.setDuration(duration[i]);
                priceContent.setMoney(money[i]);
                if (powerSectionAfter != null) {
                    priceContent.setPowerSectionAfter(powerSectionAfter[i]);
                    priceContent.setPowerSectionBefore(powerSectionBefore[i]);
                }
                priceContents.add(priceContent);
            }
            priceContentService.saveBatch(priceContents);
            return ResultUtil.success("添加方案成功");
        } catch (Exception e) {
            return ResultUtil.error("添加方案失败");
        }
    }

    /**
     * 删除方案
     */
    @RequestMapping("deleteDevicePrice")
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("@ps.hasPermission(':netWorkDot:charge:timeCharge:delete')")
    public ResultUtil deleteDevicePrice(Integer id) {
        try {
//            DevicePrice devicePrice=new DevicePrice();
//            devicePrice.setIsDelete(1);
//            devicePrice.setId(id);
            devicePriceService.removeById(id);
            priceContentService.remove(new QueryWrapper<PriceContent>().lambda().eq(PriceContent::getDevicePriceId, id));
            return ResultUtil.success("删除方案成功");
        } catch (Exception e) {
            return ResultUtil.error("系统异常");
        }
    }

    /**
     * 更新方案
     *
     * @param feeName
     * @param duration
     * @param money
     * @param status
     * @return
     */
    @RequestMapping("updateDevicePrice")
    @Transactional
    @PreAuthorize("@ps.hasPermission(':netWorkDot:charge:timeCharge:edit')")
    public ResultUtil updateDevicePrice(@RequestParam("tdpId") Integer tdpId, String feeName,
                                        Integer status,
                                        Integer realTimeCharging,
                                        String[] duration, Double[] money, String[] powerSectionBefore, String[] powerSectionAfter) {
        try {
            DevicePrice devicePrice = new DevicePrice();
            devicePrice.setAdminUserId(SecurityUtil.getUserId());
            devicePrice.setFeeName(feeName);
            devicePrice.setStatus(status);
            devicePrice.setRealTimeCharging(realTimeCharging);
            /*List<DevicePrice> list = devicePriceService.list(new QueryWrapper<DevicePrice>().lambda().eq(DevicePrice::getFeeName, feeName));
            if (!list.isEmpty()){
                return ResultUtil.error("该方案已存在");
            }*/

            //删除方案内容
            priceContentService.remove(new QueryWrapper<PriceContent>().lambda().eq(PriceContent::getDevicePriceId, tdpId));
            //priceContentService.removeById(tdpId);

            List<PriceContent> priceContents = new ArrayList<>();
            for (int i = 0; i < duration.length; i++) {
                PriceContent priceContent = new PriceContent();
                priceContent.setDevicePriceId(tdpId);
                priceContent.setDuration(duration[i]);
                priceContent.setMoney(money[i]);
                if (powerSectionAfter != null) {
                    priceContent.setPowerSectionAfter(powerSectionAfter[i]);
                    priceContent.setPowerSectionBefore(powerSectionBefore[i]);
                }
                priceContents.add(priceContent);
            }
            priceContentService.saveBatch(priceContents);
            return ResultUtil.success("添加方案成功");
        } catch (Exception e) {
            return ResultUtil.error("系统异常");
        }
    }

    /**
     * 查询方案名称
     *
     * @param priceType
     * @return
     */
    @RequestMapping("findDevicePriceByPriceType")
    public ResultUtil findDevicePriceByPriceType(@RequestParam("priceType") Integer priceType) {
        try {
            List<DevicePrice> list = devicePriceService.list(new QueryWrapper<DevicePrice>().lambda().eq(DevicePrice::getPriceType, priceType).in(DevicePrice::getAdminUserId, parentOrSonService.getSonByCurAdmin(SecurityUtil.getUserId())));
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setMsg("查询方案名称成功");
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            resultUtil.setData(list);
            return resultUtil;
        } catch (Exception e) {
            return ResultUtil.error("系统异常");
        }

    }
}
