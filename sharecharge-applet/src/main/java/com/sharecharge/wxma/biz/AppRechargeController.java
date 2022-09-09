package com.sharecharge.wxma.biz;

import com.sharecharge.biz.entity.AppRecharge;
import com.sharecharge.biz.service.AppRechargeService;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.security.util.SecurityUtil;
import com.sharecharge.system.service.DbAdminUserService;
import com.sharecharge.system.service.DbParentOrSonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("app/recharge")
public class AppRechargeController {

    final AppRechargeService appRechargeService;
    final DbAdminUserService adminUserService;
    final DbParentOrSonService parentOrSonService;

    /**
     * 查询充值套餐方案
     *
     * @param page
     * @param limit
     * @param adminName
     * @return
     */
    @RequestMapping("findRechargeList")
    public ResultUtil findRechargeList(@RequestParam("adminId")Integer adminId,
                                       @RequestParam("page") Integer page,
                                       @RequestParam("limit") Integer limit,
                                       Integer type,
                                       String adminName) {
        Map map=new HashMap();
        map.put("curPageStarRow", (page - 1) * limit);
        map.put("limit", limit);
        map.put("adminName", adminName);
        map.put("type", type);
        map.put("ids",parentOrSonService.getSonByCurAdmin(adminId));
        return appRechargeService.list(map);
    }

    /**
     * 添加套餐
     *
     * @param recharge
     * @return
     */
    @RequestMapping("addRecharge")
    public ResultUtil addRecharge(AppRecharge recharge) {
        try {
            recharge.setAdminId(SecurityUtil.getUserId());
            appRechargeService.save(recharge);
            return ResultUtil.success("添加套餐成功");
        } catch (Exception e) {
            return ResultUtil.error("系统异常");
        }
    }

    /**
     * 删除套餐
     *
     * @param rechargeId
     * @return
     */
    @RequestMapping("deleteRecharge")
    @ResponseBody
    public ResultUtil deleteRecharge(@RequestParam("rechargeId") Integer rechargeId) {
        try {
            AppRecharge recharge=new AppRecharge();
            recharge.setId(rechargeId);
            recharge.setIsDelete(1);
            appRechargeService.updateById(recharge);
            return ResultUtil.success("删除方案成功");
        } catch (Exception e) {
            return ResultUtil.error("系统异常");
        }
    }

    /**
     * 更新套餐
     *
     * @return
     */
    @RequestMapping("updateRecharge")
    @ResponseBody
    public ResultUtil updateRecharge(AppRecharge appRecharge) {
        try {
            appRechargeService.updateById(appRecharge);
            return ResultUtil.success("更新套餐成功");
        } catch (Exception e) {
            return ResultUtil.error("系统异常");
        }
    }

}
