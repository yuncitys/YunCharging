package com.sharecharge.wxma.biz;

import com.sharecharge.biz.service.HomeStatisticsService;
import com.sharecharge.biz.service.WithdrawCashRecordService;
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.system.entity.DbAdminUser;
import com.sharecharge.system.service.DbAdminUserService;
import com.sharecharge.system.service.DbParentOrSonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("app/adminUserFinance")
public class AppAdminStatisticsController {
    final DbAdminUserService adminUserService;
    final WithdrawCashRecordService withdrawCashRecordService;
    final DbParentOrSonService parentOrSonService;
    final HomeStatisticsService homeStatisticsService;

    /**
     * 统计代理商信息
     *
     * @param
     * @return
     */
    @RequestMapping("getAdminUserHomeFinance")
    public ResultUtil getAdminUserHomeFinance(@RequestParam(value = "adminId") Integer adminId) {
        DbAdminUser adminUser = adminUserService.getById(adminId);
        if (Objects.isNull(adminUser)) {
            return ResultUtil.error("请先登录");
        }
        Map map = new HashMap();
        //角色为管理员查询所有
        if (adminUser.getRoleId() == 3) {
            map.put("firstAgentId", adminUser.getId());
        } else if (adminUser.getRoleId() == 4) {
            map.put("secondAgentId", adminUser.getId());
        } else if (adminUser.getRoleId() == 5) {
            map.put("thirdAgentId", adminUser.getId());
        }
        map.put("adminId", adminUser.getId());
        Map map1 = homeStatisticsService.selectPhoneAdminHome(map);
        return ResultUtil.success(map1);
    }


    /**
     * 查询提现记录详情
     *
     * @param status
     * @param withdrawCode
     * @return
     */
    @RequestMapping("findWithdrawCashRecordInfo")
    public ResultUtil findWithdrawCashRecordInfo(@RequestParam("adminId") Integer adminId,
                                                 @RequestParam("page") Integer page,
                                                 @RequestParam("limit") Integer limit,
                                                 Integer status, String withdrawCode) {
        try {
            if (Objects.isNull(adminId)){
                return ResultUtil.error("请先登录");
            }
            Map map=new HashMap();
            map.put("curPageStarRow", (page - 1) * limit);
            map.put("limit", limit);
            map.put("withdrawCode", withdrawCode);
            map.put("status", status);
            map.put("ids", parentOrSonService.getSonByCurAdmin(adminId));
            return withdrawCashRecordService.list(map);
        } catch (Exception e) {
            log.error("查询提现记录失败", e.getMessage());
            return ResultUtil.error("查询提现记录失败");
        }
    }


}
