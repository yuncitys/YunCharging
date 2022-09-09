package com.sharecharge.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.biz.entity.DeviceFeeBack;
import com.sharecharge.core.util.ResultUtil;
import org.springframework.web.bind.annotation.RequestParam;

public interface DeviceFeeBackService extends IService<DeviceFeeBack> {
    ResultUtil list(Integer page,Integer limit,
                                     String reservedPhone, String createTimeStart, String createTimeEnd, Integer status);
}
