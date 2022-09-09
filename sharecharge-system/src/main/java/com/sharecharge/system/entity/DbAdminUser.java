package com.sharecharge.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "代理商")
@TableName("db_admin_user")
@EqualsAndHashCode(callSuper = true)
public class DbAdminUser extends Model<DbAdminUser>{

    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 所属角色（外键）
     */
    private Integer roleId;
    /**
     * 机构(外键)
     */
    private Integer organId;

    /**
     * 上级ID
     */
    private Integer parentId;

    /**
     * 绑定的user微信用户
     */
    private String openId;

    /**
     * 管理员名称
     */
    private String adminName;
    /**
     * 真实姓名
     */
    private String adminFullname;
    /**
     * 管理员手机号
     */
    private String adminPhone;
    /**
     * 管理员密码
     */
    private String adminPassword;
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    /**
     * 余额
     */
    private BigDecimal balanceAmount;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 冻结: 0 未冻结, 1 已冻结
     */
    private Integer freezeStatus;

    /**
     * 利率 分成比例
     */
    private Float interestRate;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    /**
     * 是否删除0: 未删除, 1表示删除
     */
    @TableLogic
    private Integer deleteStatus;
}
