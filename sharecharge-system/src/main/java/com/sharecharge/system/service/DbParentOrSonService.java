package com.sharecharge.system.service;

import com.sharecharge.system.entity.DbAdminUser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface DbParentOrSonService {
    /**
     * 所有上级
     * @param adminId
     */
    public List<DbAdminUser> getParentByCurAdmin(Integer adminId);

    /**
     * 获取所有下级
     * @param adminId
     */
    public List<Integer> getSonByCurAdmin(Integer adminId);


    /**
     * 分成金额计算
     * @param adminId 设备代理商id
     * @param totalPrice 订单总价
     * @return
     */
    public Map adminCashSeparate(Integer adminId, BigDecimal totalPrice);

    /**
     * 退款分成计算
     * @param orderCode
     */
    public void subtractAdminMoney(String orderCode);

}
