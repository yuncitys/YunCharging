package com.sharecharge.system.entity;

/**
 * 全局配置表
 */
public class DbSharingConfig {

    public static final String DOMAIN_HOST = "DOMAIN_HOST";                     //服务器域名，用于接口回调等
    public static final String WECHAT_ACCESS_TOKEN = "WECHAT_ACCESS_TOKEN";     //公众号基础支持accesstoken
    public static final String WECHAT_JSAPI_TICKET = "WECHAT_JSAPI_TICKET";     //公众号JSAPI_Ticket
    public static final String WECHAT_APPID = "WECHAT_APPID";                   //公众号appid
    public static final String WECHAT_APPSECRET = "WECHAT_APPSECRET";           //公众号appsecret
    public static final String MINI_APPID = "MINI_APPID";                       //小程序appid
    public static final String MINI_APPSECRET = "MINI_APPSECRET";               //小程序appsecret
    public static final String YD_APPID = "YD_APPID";                       //小程序appid
    public static final String YD_APPSECRET = "YD_APPSECRET";               //小程序appsecret
    public static final String MCH_ID = "MCH_ID";                               //商户号ID
    public static final String MCH_KEY = "MCH_KEY";                             //商户号KEY
    public static final String MAX_CASH_WITHDRAW = "MAX_CASH_WITHDRAW";         //一次提现金额需要审核阈值
    public static final String TX_HOST = "TX_HOST";         //一次提现金额需要审核阈值
    public static final String DAY_NUMBER = "DAY_NUMBER";         //当天输入的条数
    public static final String IS_PRESTORE = "IS_PRESTORE";         //收费模式 配置值 0：预存充值 1：单次充值

    private String configName;
    private String configValue;

    public DbSharingConfig() {
    }

    public DbSharingConfig(String configName, String configValue) {
        this.configName = configName;
        this.configValue = configValue;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

}
