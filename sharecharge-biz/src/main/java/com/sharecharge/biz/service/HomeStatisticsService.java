package com.sharecharge.biz.service;

import java.util.List;
import java.util.Map;

public interface HomeStatisticsService {
    /**
     * 首页统计
     *
     * @param
     * @return
     */
    Map homeCount(Map map);

    List<Map> selectOrderAndUser(Map map);

    Map orderTypeSum(Map map);

    List<Map> provinceRatio(Map map);

    List<Map> yearMonthCount(Map map);

    Map selectPhoneAdminHome(Map map);

    //经营报表
    List<Map> findCountMoneyAndOrderByDevice(Map map);

    Map findReportCount(Map map);

    List<Map> findAdminInfoByDeviceAndOrder(Map map);

    int findAdminInfoByDeviceAndOrderByCount(Map map);

    int findCountMoneyAndOrderByDeviceOnCount(Map map);
}
