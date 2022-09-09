package com.sharecharge.web.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sharecharge.biz.entity.WithdrawCashRecord;
import com.sharecharge.biz.service.WithdrawCashRecordService;
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.security.util.SecurityUtil;
import com.sharecharge.system.entity.DbAdminUser;
import com.sharecharge.system.service.DbAdminUserService;
import com.sharecharge.system.service.DbParentOrSonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("sys/withdrawCashRecord")
@RequiredArgsConstructor
public class WithdrawCashRecordController {
    final WithdrawCashRecordService withdrawCashRecordService;
    final DbAdminUserService adminUserService;
    final DbParentOrSonService parentOrSonService;

    /**
     * 查询提现记录详情
     *
     * @param status
     * @param withdrawCode
     * @return
     */
    @RequestMapping("findWithdrawCashRecordInfo")
    @PreAuthorize("@ps.hasPermission(':sys:withdrawCashRecord:findWithdrawCashRecordInfo')")
    public ResultUtil findWithdrawCashRecordInfo(
                                                 @RequestParam("page") Integer page,
                                                 @RequestParam("limit") Integer limit,
                                                 Integer status, String withdrawCode,String phone) {
        Map map=new HashMap();
        map.put("curPageStarRow", (page - 1) * limit);
        map.put("limit", limit);
        map.put("withdrawCode", withdrawCode);
        map.put("status", status);
        map.put("phone", phone);
        map.put("ids", parentOrSonService.getSonByCurAdmin(SecurityUtil.getUserId()));
        return withdrawCashRecordService.list(map);
    }

    /**
     * 申请提现
     *
     * @param money
     * @return
     */
    @RequestMapping("gotoWithdrawCash")
    @PreAuthorize("@ps.hasPermission(':sys:withdrawCashRecord:gotoWithdrawCash')")
    public ResultUtil gotoWithdrawCash(@RequestParam("money") Integer money) {
        try {
            DbAdminUser adminUser = (DbAdminUser) SecurityUtil.getUserInfo().getAdminUser();
            if (money <= 0) {
                return ResultUtil.error("提现金额错误");
            }
            int a = adminUser.getBalanceAmount().compareTo(BigDecimal.valueOf(money));
            if (a < 0) {
                return ResultUtil.error("提现金额不能大于余额");
            }
            Integer freezeStatus = adminUser.getFreezeStatus();
            if (freezeStatus==1) {
                return ResultUtil.error("账户异常被冻结,请联系管理员");
            }
            BigDecimal balanceAmount = adminUser.getBalanceAmount();
            BigDecimal subtractMoney = balanceAmount.subtract(BigDecimal.valueOf(money));
            //更新余额
            DbAdminUser dbAdminUser = new DbAdminUser();
            dbAdminUser.setUpdateTime(new Date());
            dbAdminUser.setBalanceAmount(subtractMoney);
            dbAdminUser.setId(adminUser.getId());
            adminUserService.updateById(dbAdminUser);

            WithdrawCashRecord withdrawCashRecord = new WithdrawCashRecord();
            Timestamp timestamp = new Timestamp(new Date().getTime());
            withdrawCashRecord.setCreateTime(timestamp);
            withdrawCashRecord.setMoney(BigDecimal.valueOf(money));
            withdrawCashRecord.setPayAdminId(null);
            String withDrawCode = "T" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + RandomStringUtils.randomNumeric(9);
            withdrawCashRecord.setWithdrawCode(withDrawCode);
            withdrawCashRecord.setAdminId(adminUser.getId());
            withdrawCashRecord.setStatus(0);
            withdrawCashRecord.setPayTime(timestamp);
            withdrawCashRecord.setPayType(0);
            withdrawCashRecordService.save(withdrawCashRecord);
            return ResultUtil.success("申请提现成功");
        } catch (Exception e) {
            return ResultUtil.error("申请提现失败");
        }
    }


    /**
     * 线下打款
     *
     * @param withdrawCode
     * @param withdrawAdminId
     * @param money
     * @return
     */
    @RequestMapping("updateWithdrawCashStatus")
    @PreAuthorize("@ps.hasPermission(':finance:record:addMoney')")
    public ResultUtil updateWithdrawCashStatus(@RequestParam("withdrawCode") String withdrawCode,
                                               @RequestParam("withdrawAdminId") Integer withdrawAdminId,
                                               @RequestParam("money") Integer money) {
        try {
            Map map = new HashMap();
            map.put("withdrawCode", withdrawCode);
            map.put("status", 1);
            map.put("payType", 2);
            map.put("payAdminId", 2);
            WithdrawCashRecord withdrawCashRecord = new WithdrawCashRecord();
            withdrawCashRecord.setWithdrawCode(withdrawCode);
            withdrawCashRecord.setStatus(1);
            withdrawCashRecord.setPayAdminId(2);
            withdrawCashRecord.setPayType(2);
            withdrawCashRecordService.updateById(withdrawCashRecord);
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setMsg("打款成功");
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            return resultUtil;
        } catch (Exception e) {
            log.error("提现金额异常", e.getMessage());
            return ResultUtil.error("提现失败");
        }
    }

    /**
     * 驳回操作
     *
     * @param withdrawCode
     * @param adminPhone
     * @return
     */
    @RequestMapping("updateAdminUserCash")
    @PreAuthorize("@ps.hasPermission(':finance:record:reject')")
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil updateAdminUserCash(@RequestParam("withdrawCode") String withdrawCode, @RequestParam("adminPhone") String adminPhone) {
        WithdrawCashRecord one = withdrawCashRecordService.getOne(new QueryWrapper<WithdrawCashRecord>().lambda().eq(WithdrawCashRecord::getWithdrawCode, withdrawCode));
        one.setStatus(3);
        DbAdminUser byId = adminUserService.getById(one.getAdminId());
        byId.setTotalAmount(byId.getTotalAmount().add(one.getMoney()));
        byId.setBalanceAmount(byId.getBalanceAmount().add(one.getMoney()));
        withdrawCashRecordService.updateById(one);
        adminUserService.updateById(byId);
        return ResultUtil.success();
    }
}
