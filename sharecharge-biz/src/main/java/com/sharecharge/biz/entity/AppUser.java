package com.sharecharge.biz.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * C端用户信息(t_app_user)
 *
 * @author bianj
 * @version 1.0.0 2021-03-31
 */
@Data
@TableName("t_app_user")
public class AppUser {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 头像
     */
    private String headImg;

    /**
     * 微信openID
     */
    private String wxOpenId;

    /**
     * 支付宝ID
     */
    private String zfbAuthCode;

    /**
     * 余额
     */
    private Double cash;

    /**
     * 用户状态 0  正常  1  冻结
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 最后登录时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date loginTime;

    /**
     * 用户平台 1微信小程序 2支付宝
     */
    private Integer userPlatform;

    /***
     * 实际支付金额
     */

    private Double realityPayMoney;

    /**
     * 赠送金额
     */
    private Double giveMoney;

    /**
     * 是否删除 0 未删除  1删除
     */
    @TableLogic
    private Integer isDelete;
}
