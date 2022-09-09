package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("member")
@Data
public class Member {

    /**
     * 账户状态
     */
    public static final String STATUS_VALID = "0";

    public static final String STATUS_DISABLE = "1";

    public static final String STATUS_CANCELLATION = "2";

    public static final String DEFAULT_AVATAR = "default.jpg";


    @ApiModelProperty("用户编号")
    @TableId(type = IdType.AUTO)
    public Integer id;
    @ApiModelProperty("用户名称")
    private String username;
    @ApiModelProperty("用户密码")
    private String password;
    @ApiModelProperty("性别：0 未知， 1男， 1 女")
    private Integer gender;
    @ApiModelProperty("生日")
    private LocalDateTime birthday;
    @ApiModelProperty("最后一次登录IP地址")
    private String lastLoginIp;
    @ApiModelProperty("用户昵称或网络名称")
    private String nickname;
    @ApiModelProperty("用户手机号码")
    private String mobile;
    @ApiModelProperty("用户头像图片")
    private String avatar;
    @ApiModelProperty("微信登录openid")
    private String weixinOpenid;
    @ApiModelProperty("微信登录会话KEY")
    private String sessionKey;
    @ApiModelProperty("0 可用, 1 禁用, 2 注销")
    private Integer status;
    @ApiModelProperty("创建时间")
    private LocalDateTime addTime;
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    private Integer type;

}


