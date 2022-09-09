package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商家详情
 */
@TableName("business_detail")
@Data
public class BusinessDetail {

    @TableId(type = IdType.AUTO)
    //("编号")
    private Integer id;
    //电话
    private String phone;
    //("店铺名称")
    private String shopName;
    //("介绍")
    private String introduce;
    //("所在地区")
    private String address;
    //("开店时间")
    private Date addTime;
    //("更新时间")
    private Date updateTime;
    //("经营类型")
    private Integer categoryType;
    //("金额")
    private BigDecimal money;
    //("累计体现")
    private BigDecimal withdrawalSum;
    //("总销售金额")
    private BigDecimal salesAmount;
    //("姓名")
    private String bName;
    //("银行卡账号")
    private String bankAccount;
    //("开户银行")
    private String bankName;
    //("手机号码")
    private String bankPhone;
    //("身份证")
    private String sfz;
    //("商家编号")
    private Integer busId;
    //("商家logo")
    private String logo;
    //("用户id")
    private Integer userId;
    // 0 是未审核  1是通过
    private Integer status;



}
