package com.sharecharge.web.task;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sharecharge.biz.entity.Device;
import com.sharecharge.biz.service.ChargeOrderService;
import com.sharecharge.biz.service.DeviceService;
import com.sharecharge.biz.service.device.ChargeOrderPriceSumService;
import com.sharecharge.system.entity.DbSharingConfig;
import com.sharecharge.system.service.DbSharingConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class UpdateOrder {
    @Autowired
    private ChargeOrderPriceSumService chargeOrderPriceSumService;
    @Autowired
    private ChargeOrderService chargeOrderService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DbSharingConfigService sharingConfigService;

    /**
     * 设备没有心跳
     * @throws Exception
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void UpdateDevice() throws Exception {
        List<Map> notBeatOrder = chargeOrderService.findNotBeatOrder();
        Device device=new Device();
        for (Map map : notBeatOrder) {
            log.info("离线设备订单：{}，设备号，{}，端口号{}",map.get("deviceCode"),map.get("devicePort"));
            String deviceCode = (String) map.get("deviceCode");
            String devicePort = (String) map.get("devicePort");
            device.setDeviceStatus(0);
            deviceService.update(device,new UpdateWrapper<Device>().lambda().eq(Device::getDeviceCode,deviceCode));
            chargeOrderPriceSumService.userClose(deviceCode, "01", devicePort, "设备停止心跳");
        }
    }

    @Scheduled(cron = "0 01 00 ? * *")
    public void deviceNoMoney() {
        sharingConfigService.createOrUpdateGlobalConfig(DbSharingConfig.DAY_NUMBER, String.valueOf(0));
    }
}
