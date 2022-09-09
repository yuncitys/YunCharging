package com.sharecharge.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.biz.entity.WithdrawCashRecord;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.system.entity.DbAdminUser;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface WithdrawCashRecordService extends IService<WithdrawCashRecord> {
    ResultUtil list(Map map);

    ResultUtil Withdraw(WithdrawCashRecord withdrawCashRecord, DbAdminUser adminUser);
}
