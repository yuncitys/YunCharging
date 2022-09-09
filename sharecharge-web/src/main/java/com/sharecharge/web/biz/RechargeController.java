package com.sharecharge.web.biz;

import com.sharecharge.biz.entity.AppRecharge;
import com.sharecharge.biz.service.AppRechargeService;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.security.util.SecurityUtil;
import com.sharecharge.system.service.DbAdminUserService;
import com.sharecharge.system.service.DbParentOrSonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
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
@RequiredArgsConstructor
@RequestMapping("sys/recharge")
public class RechargeController {

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
    @PreAuthorize("@ps.hasPermission(':sys:recharge:findRechargeList')")
    public ResultUtil findRechargeList(@RequestParam("page") Integer page,
                                       @RequestParam("limit") Integer limit,
                                       Integer type,
                                       String adminName) {
        Map map=new HashMap();
        map.put("curPageStarRow", (page - 1) * limit);
        map.put("limit", limit);
        map.put("adminName", adminName);
        map.put("type", type);
        map.put("ids",parentOrSonService.getSonByCurAdmin(SecurityUtil.getUserId()));
        return appRechargeService.list(map);
    }

    /**
     * 添加套餐
     *
     * @param recharge
     * @return
     */
    @RequestMapping("addRecharge")
    @PreAuthorize("@ps.hasPermission(':sys:recharge:addRecharge')")
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
    @PreAuthorize("@ps.hasPermission(':sys:recharge:deleteRecharge')")
    public ResultUtil deleteRecharge(@RequestParam("rechargeId") Integer rechargeId) {
        try {
//            AppRecharge recharge=new AppRecharge();
//            recharge.setId(rechargeId);
//            recharge.setIsDelete(1);
            appRechargeService.removeById(rechargeId);
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
    @PreAuthorize("@ps.hasPermission(':sys:recharge:updateRecharge')")
    public ResultUtil updateRecharge(AppRecharge appRecharge) {
        try {
            appRechargeService.updateById(appRecharge);
            return ResultUtil.success("更新套餐成功");
        } catch (Exception e) {
            return ResultUtil.error("系统异常");
        }
    }

}
