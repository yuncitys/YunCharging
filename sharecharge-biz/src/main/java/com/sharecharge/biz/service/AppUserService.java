package com.sharecharge.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.biz.entity.AppUser;
import com.sharecharge.core.util.ResultUtil;
import org.springframework.web.bind.annotation.RequestParam;

public interface AppUserService extends IService<AppUser> {
    ResultUtil list(Integer page,Integer limit, Integer userPlatform,
                    String userName, String status,
                    String createTimeStart, String createTimeEnd,
                    String phoneNumber, String userId);
}
