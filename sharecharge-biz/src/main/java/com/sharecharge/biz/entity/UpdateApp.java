package com.sharecharge.biz.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * t_update_app
 *
 * @author bianj
 * @version 1.0.0 2021-06-10
 */
@Data
@TableName("t_update_app")
public class UpdateApp {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 设备类型id
     */
    private Integer deviceTypeId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 升级文件路径
     */
    private String upfileUrl;

    /**
     * MD5值
     */
    private String md5Value;

    /**
     * fileIze
     */
    private Integer fileSize;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 下发指令成功次数
     */
    private Integer successCount;

    /**
     * 下发指令失败次数
     */
    private Integer failCount;

    /**
     * 最后下发指令时间
     */
    private Date lastTime;

    /**
     * 最后下发指令内容
     */
    private String lastContent;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
}
