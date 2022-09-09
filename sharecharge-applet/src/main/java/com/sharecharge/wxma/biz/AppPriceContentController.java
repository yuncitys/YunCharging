package com.sharecharge.wxma.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sharecharge.biz.entity.DevicePrice;
import com.sharecharge.biz.entity.PriceContent;
import com.sharecharge.biz.service.DevicePriceService;
import com.sharecharge.biz.service.PriceContentService;
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.security.util.SecurityUtil;
import com.sharecharge.system.service.DbAdminUserService;
import com.sharecharge.system.service.DbParentOrSonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("app/price")
@RequiredArgsConstructor
public class AppPriceContentController {
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
    @ResponseBody
    public ResultUtil findPriceContentByType(@RequestParam("adminId")Integer adminId,
                                             @RequestParam("page") Integer page,
                                             @RequestParam("limit") Integer limit,
                                             @RequestParam("priceType") Integer priceType, String feeName) {
        Map map=new HashMap();
        map.put("curPageStarRow", (page - 1) * limit);
        map.put("limit", limit);
        map.put("priceType", priceType);
        map.put("feeName", feeName);
        map.put("ids", parentOrSonService.getSonByCurAdmin(adminId));
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
    @Transactional//事务注解
    public ResultUtil batchDevicePrices(@RequestParam("feeName") String feeName,
                                        @RequestParam("priceType") Integer priceType,
                                        Integer realTimeCharging,
                                        String[] powerSectionBefore, String[] powerSectionAfter,
                                        String[] duration, Double[] money) {
        try{
            //生成六位随机数
            int v = (int) ((Math.random() * 9 + 1) * 1000000);
            DevicePrice devicePrice=new DevicePrice();
            devicePrice.setAdminUserId(SecurityUtil.getUserId());
            devicePrice.setFeeName(feeName);
            devicePrice.setPriceType(priceType);
            devicePrice.setRealTimeCharging(realTimeCharging);
            List<DevicePrice> list = devicePriceService.list(new QueryWrapper<DevicePrice>().lambda().eq(DevicePrice::getFeeName, feeName));
            if (!list.isEmpty()){
                return ResultUtil.error("该方案已存在");
            }
            devicePriceService.save(devicePrice);
            List<PriceContent> priceContents=new ArrayList<>();
            for (String i:duration){
                PriceContent priceContent=new PriceContent();
                priceContent.setDevicePriceId(v);
                priceContent.setDuration(i);
                priceContent.setMoney(money[Integer.valueOf(i)]);
                if (powerSectionAfter!=null){
                    priceContent.setPowerSectionAfter(powerSectionAfter[Integer.valueOf(i)]);
                    priceContent.setPowerSectionBefore(powerSectionBefore[Integer.valueOf(i)]);
                }
                priceContents.add(priceContent);
            }
            priceContentService.saveBatch(priceContents);
            return ResultUtil.success("添加方案成功");
        }catch (Exception e){
            return ResultUtil.error("添加方案失败");
        }
    }

    /**
     * 删除方案
     */
    @RequestMapping("deleteDevicePrice")
    public ResultUtil deleteDevicePrice(Integer id) {
        try {
            DevicePrice devicePrice=new DevicePrice();
            devicePrice.setIsDelete(1);
            devicePrice.setId(id);
            devicePriceService.updateById(devicePrice);
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
    @ResponseBody
    @Transactional
    public ResultUtil updateDevicePrice(@RequestParam("tdpId") Integer tdpId, String feeName,
                                        Integer status,
                                        Integer realTimeCharging,
                                        String[] duration, Double[] money, String[] powerSectionBefore, String[] powerSectionAfter) {
        try {
            DevicePrice devicePrice=new DevicePrice();
            devicePrice.setAdminUserId(SecurityUtil.getUserId());
            devicePrice.setFeeName(feeName);
            devicePrice.setStatus(status);
            devicePrice.setRealTimeCharging(realTimeCharging);
//            List<DevicePrice> list = devicePriceService.list(new QueryWrapper<DevicePrice>().lambda().eq(DevicePrice::getFeeName, feeName));
//            if (!list.isEmpty()){
//                return ResultUtil.error("该方案已存在");
//            }

            //删除方案内容
            priceContentService.removeById(tdpId);

            List<PriceContent> priceContents=new ArrayList<>();
            for (String i:duration){
                PriceContent priceContent=new PriceContent();
                priceContent.setDevicePriceId(tdpId);
                priceContent.setDuration(i);
                priceContent.setMoney(money[Integer.valueOf(i)]);
                if (powerSectionAfter!=null){
                    priceContent.setPowerSectionAfter(powerSectionAfter[Integer.valueOf(i)]);
                    priceContent.setPowerSectionBefore(powerSectionBefore[Integer.valueOf(i)]);
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
    public ResultUtil findDevicePriceByPriceType(@RequestParam("adminId") Integer adminId,@RequestParam("priceType") Integer priceType) {
        try {
            List<DevicePrice> list = devicePriceService.list(new QueryWrapper<DevicePrice>().lambda().eq(DevicePrice::getPriceType, priceType).in(DevicePrice::getAdminUserId, parentOrSonService.getSonByCurAdmin(adminId)));
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
