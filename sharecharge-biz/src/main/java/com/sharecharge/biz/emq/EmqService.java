package com.sharecharge.biz.emq;

import com.sharecharge.biz.mapper.DeviceMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 * @author SANJI
 *
 */
@Service("emqService")
public class EmqService {

    Logger logger = LogManager.getLogger(EmqService.class);

    @Autowired
    public DeviceMapper deviceMapper;

    /**
     * 处理emq action
     *
     * @param emqActionBean
     */
    public void processEmqAction(EmqActionBean emqActionBean) {
        logger.info("****收到的应答消息****： {}", emqActionBean);
        // TODO 记录所有应答
        // 连接/断开 修改设备状态 转发消息
        int deviceStatusId = 3;
        String clientId = StringUtils.isEmpty(emqActionBean.getClientId()) ? emqActionBean.getFromClientId() : emqActionBean.getClientId();
        logger.info("***** {} action is: {} ****", clientId, emqActionBean.getAction());
        if (EmqActionEnum.client_connected.name().equals(emqActionBean.getAction())) {
            if (!StringUtils.contains(clientId, "server")) {
                deviceStatusId = 1;
            }
        } else if (EmqActionEnum.client_disconnected.name().equals(emqActionBean.getAction())) {
            deviceStatusId = 0;
        }
        if (deviceStatusId < 3) {
            try {
                Map map = new HashMap();//接受map
                map.put("deviceCode", clientId);
                map.put("deviceStatus", deviceStatusId);
                deviceMapper.updateDevice(map);
            } catch (Exception e) {
                logger.error("接收应答消息，更改设备状态出错. emqAction: {}, status: {}. ", emqActionBean, deviceStatusId);
            }
        }
    }


}
