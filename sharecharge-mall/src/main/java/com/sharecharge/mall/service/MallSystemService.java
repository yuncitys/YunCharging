package com.sharecharge.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.mall.entity.MallSystem;

import java.util.Map;

public interface MallSystemService extends IService<MallSystem> {
    public Map<String, String> queryAll();
    public void addConfig(String key, String value) ;
}
