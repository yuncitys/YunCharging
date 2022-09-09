package com.sharecharge.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.biz.entity.DevicePrice;
import com.sharecharge.core.util.ResultUtil;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface DevicePriceService extends IService<DevicePrice> {
    ResultUtil list(Map map);
}
