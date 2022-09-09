package com.sharecharge.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.mall.entity.MemberAddress;

public interface MemberAddressService extends IService<MemberAddress> {

    MemberAddress findById(Integer userId, Integer id);
}
