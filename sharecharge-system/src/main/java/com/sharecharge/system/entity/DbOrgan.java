package com.sharecharge.system.entity;

import lombok.Data;

import java.util.Date;

@Data
public class DbOrgan  {

    private static final long serialVersionUID = 1L;

	private Integer id;
    /**
     * 父级ID
     */
	private Integer parentId;
    /**
     * 机构名称
     */
	private String organName;
    /**
     * 备注
     */
	private String remake;
    /**
     * 排序
     */
	private Integer sorting;
    /**
     * 代表层次关系: 0 表示总节点
     */
	private Integer level;
    /**
     * 创建时间
     */
	private Date createTime;
    /**
     * 修改时间
     */
	private Date updateTime;
    /**
     * 是否删除 0 未删除 1 表示已删除
     */
	private Integer deleteStatus;

}
