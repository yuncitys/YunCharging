package com.sharecharge.mall.config;

import com.sharecharge.mall.service.MallSystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


/**
 * 系统启动服务，用于设置系统配置信息、检查系统状态及打印系统信息
 */
@Slf4j
@Component
class MallSystemInitService {
    private MallSystemInitService mallSystemInitService;

    @PostConstruct
    private void inist() {
        log.info("========= 初始化商城系统配置 =========");
        mallSystemInitService = this;
        initConfigs();
    }


    private final static Map<String, String> DEFAULT_CONFIGS = new HashMap<>();
    static {
        // 小程序相关配置默认值
        DEFAULT_CONFIGS.put(MallSystemConfig.LITEMALL_WX_INDEX_NEW, "6");
        DEFAULT_CONFIGS.put(MallSystemConfig.LITEMALL_WX_INDEX_HOT, "6");
        DEFAULT_CONFIGS.put(MallSystemConfig.LITEMALL_WX_INDEX_BRAND, "4");
        DEFAULT_CONFIGS.put(MallSystemConfig.LITEMALL_WX_INDEX_TOPIC, "4");
        DEFAULT_CONFIGS.put(MallSystemConfig.LITEMALL_WX_INDEX_CATLOG_LIST, "4");
        DEFAULT_CONFIGS.put(MallSystemConfig.LITEMALL_WX_INDEX_CATLOG_GOODS, "4");
        DEFAULT_CONFIGS.put(MallSystemConfig.LITEMALL_WX_SHARE, "false");
        // 运费相关配置默认值
        DEFAULT_CONFIGS.put(MallSystemConfig.LITEMALL_EXPRESS_FREIGHT_VALUE, "8");
        DEFAULT_CONFIGS.put(MallSystemConfig.LITEMALL_EXPRESS_FREIGHT_MIN, "88");
        // 订单相关配置默认值
        DEFAULT_CONFIGS.put(MallSystemConfig.LITEMALL_ORDER_UNPAID, "30");
        DEFAULT_CONFIGS.put(MallSystemConfig.LITEMALL_ORDER_UNCONFIRM, "7");
        DEFAULT_CONFIGS.put(MallSystemConfig.LITEMALL_ORDER_COMMENT, "7");
        // 商城相关配置默认值
        DEFAULT_CONFIGS.put(MallSystemConfig.LITEMALL_MALL_NAME, "litemall");
        DEFAULT_CONFIGS.put(MallSystemConfig.LITEMALL_MALL_ADDRESS, "上海");
        DEFAULT_CONFIGS.put(MallSystemConfig.LITEMALL_MALL_Latitude, "31.201900");
        DEFAULT_CONFIGS.put(MallSystemConfig.LITEMALL_MALL_LONGITUDE, "121.587839");
        DEFAULT_CONFIGS.put(MallSystemConfig.LITEMALL_MALL_PHONE, "021-xxxx-xxxx");
        DEFAULT_CONFIGS.put(MallSystemConfig.LITEMALL_MALL_QQ, "705144434");
    }

    @Autowired
    private MallSystemService mallSystemService;

    private void initConfigs() {
        log.info("========= 正在初始化商城系统配置 =========");
        // 1. 读取数据库全部配置信息
        Map<String, String> configs = mallSystemService.queryAll();

        // 2. 分析DEFAULT_CONFIGS
        for (Map.Entry<String, String> entry : DEFAULT_CONFIGS.entrySet()) {
            if (configs.containsKey(entry.getKey())) {
                continue;
            }
            configs.put(entry.getKey(), entry.getValue());
            mallSystemService.addConfig(entry.getKey(), entry.getValue());
        }

        MallSystemConfig.setConfigs(configs);
    }


}
