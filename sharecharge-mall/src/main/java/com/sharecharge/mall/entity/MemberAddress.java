package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 收货地址
 */
@TableName("member_address")
@Data
public class MemberAddress {
    @TableId(type = IdType.AUTO)
    //("编号")
    private Integer id;
    //("收货人名称")
    @NotBlank(message = "{required}")
    private String name;
    //("用户表的用户ID")
    private Integer userId;
    //("行政区域表的省ID")
    private String province;
    //("行政区域表的市ID")
    private String city;
    //("行政区域表的区县ID")
    private String county;
    //("详细收货地址")
    @NotBlank(message = "{required}")
    private String addressDetail;
    //("地区编码")
    private String areaCode;
    //("邮政编码")
    private String postalCode;
    @NotBlank(message = "{required}")
    //("手机号码")
    private String tel;
    //("是否默认地址")
    private Boolean isDefault;
    //("创建时间")
    private Date addTime;
    //("更新时间")
    private Date updateTime;
    //("逻辑删除")
    private Boolean deleted;

}
