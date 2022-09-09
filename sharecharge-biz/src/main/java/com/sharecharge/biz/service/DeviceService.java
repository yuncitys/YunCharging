package com.sharecharge.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.biz.entity.Device;
import com.sharecharge.biz.vo.DeviceDto;
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.core.util.http.SendRequest;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.*;

public interface DeviceService extends IService<Device> {

    ResultUtil list(Integer page,
                    Integer limit,
                    Integer allocationStatus,Integer deviceChargePattern,String deviceCode, String networkAddress, Integer deviceStatus, Integer dealerId);

    ResultUtil batchAllocationDevice(Integer[] ids, Integer networkDotId, Integer dealerId);

    ResultUtil batchAddDevicePrice(String[] deviceCodes, Integer devicePriceId, Integer devicePriceType);

    ResultUtil batchDevicePutState(Integer[] deviceId, Integer pullStatus);

    ResultUtil updateDeviceStatus() ;

    ResultUtil downLoadDeviceCodes(Integer number,Integer deviceTypeId);

    DeviceDto findDeviceVo(String deviceCode);
}
