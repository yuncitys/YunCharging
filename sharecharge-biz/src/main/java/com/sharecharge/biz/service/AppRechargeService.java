package com.sharecharge.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.biz.entity.AppRecharge;
import com.sharecharge.core.util.ResultUtil;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface AppRechargeService extends IService<AppRecharge> {
    //查询pc充值套餐
    ResultUtil list(Map map);
}
