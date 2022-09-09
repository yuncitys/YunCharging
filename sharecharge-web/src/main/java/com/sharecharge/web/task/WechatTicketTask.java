package com.sharecharge.web.task;

import com.alibaba.fastjson.JSONObject;
import com.sharecharge.core.util.StringUtils;
import com.sharecharge.core.util.http.SendRequest;
import com.sharecharge.system.entity.DbSharingConfig;
import com.sharecharge.system.service.DbSharingConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 获取微信 基础支持 access_token
 * 获取微信javascript api ticket
 */
@Component
public class WechatTicketTask {

    private final static Logger logger = LoggerFactory.getLogger(WechatTicketTask.class);

    @Autowired
    private DbSharingConfigService sharingConfigService;

    @Scheduled(fixedRate = 7000000)
    public void doWechatTicketTask() {

        String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token" ;
        String param = "grant_type=client_credential&appid=APPID&secret=APPSECRET";

        param = param.replace("APPID", sharingConfigService.getGlobalConfig(DbSharingConfig.WECHAT_APPID))
                .replace("APPSECRET", sharingConfigService.getGlobalConfig(DbSharingConfig.WECHAT_APPSECRET));
        logger.info("tokenUrl: {}", tokenUrl);
        // 获取基础支持 	access_token
        String result = SendRequest.sendGet(tokenUrl, param);

        if (Objects.nonNull(result)) {
            logger.info(result);
            String accessToken = JSONObject.parseObject(result).getString("access_token");
            if (StringUtils.isBlank(accessToken)) {
                return;
            }
            sharingConfigService.createOrUpdateGlobalConfig(DbSharingConfig.WECHAT_ACCESS_TOKEN, accessToken);
        }

    }
}
