package com.sharecharge.web.biz;

import com.sharecharge.biz.service.HomeStatisticsService;
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.security.util.SecurityUtil;
import com.sharecharge.system.entity.DbAdminUser;
import com.sharecharge.system.service.DbParentOrSonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("sys/home")
@RequiredArgsConstructor
public class HomeStatisticsController {
    final HomeStatisticsService homeStatisticsService;
    final DbParentOrSonService parentOrSonService;

    /**
     * 查询首页统计
     *
     * @return
     */
    @RequestMapping("findHomeData")
    public ResultUtil findHomeData() {
        try {
            Map map = new HashMap();
            DbAdminUser adminUser= (DbAdminUser) SecurityUtil.getUserInfo().getAdminUser();
            if (adminUser.getRoleId() == 3) {
                map.put("firstAgentId", adminUser.getId());
            } else if (adminUser.getRoleId() == 4) {
                map.put("secondAgentId", adminUser.getId());
            } else if (adminUser.getRoleId() == 5) {
                map.put("thirdAgentId", adminUser.getId());
            }
            //查询首页统计
            Map homeData = homeStatisticsService.homeCount(map);
            //首页设备状态统计
            List<Map> orderUser = homeStatisticsService.selectOrderAndUser(map);

            Map homeWeekOrderMoneyData = homeStatisticsService.orderTypeSum(map);

            map.put("ids",parentOrSonService.getSonByCurAdmin(adminUser.getId()) );
            List<Map> provinceRatio = homeStatisticsService.provinceRatio(map);

            List<Map> yearMonthCount = homeStatisticsService.yearMonthCount(map);

            Map homeMap = new HashedMap();
            homeMap.put("homeData", homeData);
            homeMap.put("orderUser", orderUser);
            homeMap.put("orderType", homeWeekOrderMoneyData);
            homeMap.put("provinceRatio", provinceRatio);
            homeMap.put("yearMonthCount", yearMonthCount);
            return ResultUtil.success(homeMap);
        } catch (Exception e) {
            log.info("查询统计失败：{}",e.getMessage());
            return ResultUtil.error("查询失败!");
        }
    }

    /**
     * 报表统计-代理商统计
     *
     * @return
     */
    @RequestMapping("findReportsAndStatistics")
    @PreAuthorize("@ps.hasPermission(':sys:home:findReportsAndStatistics')")
    public ResultUtil findReportsAndStatistics(@RequestParam("page") Integer page,
                                               @RequestParam("limit") Integer limit,
                                               Integer dearId) {
        try {
            Map map = new HashMap();
            DbAdminUser adminUser= (DbAdminUser) SecurityUtil.getUserInfo().getAdminUser();
            if (adminUser.getRoleId() == 3) {
                map.put("firstAgentId", adminUser.getId());
            } else if (adminUser.getRoleId() == 4) {
                map.put("secondAgentId", adminUser.getId());
            } else if (adminUser.getRoleId() == 5) {
                map.put("thirdAgentId", adminUser.getId());
            }
            map.put("id", adminUser.getId());

            Map reportCount = homeStatisticsService.findReportCount(map);

            map.put("ids", parentOrSonService.getSonByCurAdmin(adminUser.getId()));
            map.put("allocationStatus", 0);
            map.put("curPageStartRow", (page - 1) * limit);
            map.put("limit", limit);

            map.put("dearId", dearId);

            List<Map> adminInfoByDeviceAndOrder = homeStatisticsService.findAdminInfoByDeviceAndOrder(map);
            int adminInfoByDeviceAndOrderByCount = homeStatisticsService.findAdminInfoByDeviceAndOrderByCount(map);

            Map homeMap = new HashedMap();
            homeMap.put("reportCount", reportCount);
            homeMap.put("adminInfoByDeviceAndOrder", adminInfoByDeviceAndOrder);
            homeMap.put("adminInfoByDeviceAndOrderByCount", adminInfoByDeviceAndOrderByCount);
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            resultUtil.setCode(adminInfoByDeviceAndOrderByCount);
            resultUtil.setData(homeMap);
            return ResultUtil.success(homeMap);
        } catch (Exception e) {
            log.info("查询失败：{}",e.getMessage());
            return ResultUtil.error("查询失败!");
        }
    }


    /**
     * 报表统计-设备统计
     *
     * @return
     */
    @RequestMapping("findReportsAndStatisticsByDevice")
    @PreAuthorize("@ps.hasPermission(':sys:home:findReportsAndStatisticsByDevice')")
    public ResultUtil findReportsAndStatisticsByDevice(@RequestParam("page") Integer page,
                                                       @RequestParam("limit") Integer limit,
                                                       String deviceCode, Integer dearId) {
        try {
            Map map = new HashMap();
            DbAdminUser adminUser= (DbAdminUser) SecurityUtil.getUserInfo().getAdminUser();
            if (adminUser.getRoleId() == 3) {
                map.put("firstAgentId", adminUser.getId());
            } else if (adminUser.getRoleId() == 4) {
                map.put("secondAgentId", adminUser.getId());
            } else if (adminUser.getRoleId() == 5) {
                map.put("thirdAgentId", adminUser.getId());
            }
            map.put("ids", parentOrSonService.getSonByCurAdmin(adminUser.getId()));
            map.put("allocationStatus", 0);
            map.put("curPageStartRow", (page - 1) * limit);
            map.put("limit", limit);

            map.put("dearId", dearId);
            map.put("deviceCode", deviceCode);

            List<Map> countMoneyAndOrderByDevice = homeStatisticsService.findCountMoneyAndOrderByDevice(map);
            int countMoneyAndOrderByDeviceOnCount = homeStatisticsService.findCountMoneyAndOrderByDeviceOnCount(map);

            Map homeMap = new HashedMap();
            homeMap.put("countMoneyAndOrderByDevice", countMoneyAndOrderByDevice);
            homeMap.put("countMoneyAndOrderByDeviceOnCount", countMoneyAndOrderByDeviceOnCount);
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            resultUtil.setCount(Long.valueOf(countMoneyAndOrderByDeviceOnCount));
            resultUtil.setData(countMoneyAndOrderByDevice);
            return resultUtil;
        } catch (Exception e) {
            log.error("查询失败：{}",e.getMessage());
            return ResultUtil.error("查询失败!");
        }
    }

}
