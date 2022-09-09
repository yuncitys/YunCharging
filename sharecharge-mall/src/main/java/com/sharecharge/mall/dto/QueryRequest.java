package com.sharecharge.mall.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryRequest implements Serializable {

    private static final long serialVersionUID = -4869594085374385813L;

    // 条数
    private int pageSize = 10;
    // 页数
    private int pageNum = 1;

    private String sortField;
    private String sortOrder;

    public QueryRequest(){}

    public QueryRequest(int pageSize, int pageNum) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }
}
