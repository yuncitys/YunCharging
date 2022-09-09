package com.sharecharge.web.util;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MenuManageTree implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String title;
    private String icon;
    private String href;
    private String perms;
    private Integer parentId;
    private Integer sorting;
    private Integer grade;//等级, 目前严格按照3级来做
    private List<MenuManageTree> children;
}
