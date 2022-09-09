package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@TableName("business_examine")
@Data
public class BusinessExamine {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("编号")
    private Integer id;
    @ApiModelProperty("店铺名称")
    @NotBlank
    private String shopName;
    @ApiModelProperty("介绍")
    private String introduce;
    @ApiModelProperty("所在地区")
    private String address;
    @ApiModelProperty("开店时间")
    private LocalDateTime addTime;
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
    @ApiModelProperty("经营类型")
    private Integer categoryType;
    @ApiModelProperty("姓名")
    @NotBlank
    private String bName;
    @ApiModelProperty("银行卡账号")
    @NotBlank
    private String bankAccount;
    @ApiModelProperty("开户银行")
    @NotBlank
    private String bankName;
    @ApiModelProperty("手机号码")
    @NotBlank
    private String bankPhone;
    @ApiModelProperty("身份证")
    @NotBlank
    private String sfz;
    @ApiModelProperty("商家编号")
    private Integer busId;
    @ApiModelProperty("商家logo")
    @NotBlank
    private String logo;
    @ApiModelProperty("状态 1是待审核 2是成功审核 3是驳回")
    private Integer status;
    @ApiModelProperty("删除 逻辑删除 0未删除 1删除成功")
    private Boolean deleted;

    private transient Map<String,Object> map;

}
