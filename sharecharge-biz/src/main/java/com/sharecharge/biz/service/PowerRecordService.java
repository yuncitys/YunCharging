package com.sharecharge.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.biz.entity.PowerRecord;
import com.sharecharge.core.util.ResultUtil;

public interface PowerRecordService extends IService<PowerRecord> {
    public ResultUtil findDevicePowerDetails(String deviceCode, String prot, String startTime, String curTime) ;
}
