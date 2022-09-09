package com.sharecharge.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.biz.entity.AppPayDetails;
import com.sharecharge.core.util.ResultUtil;
import org.springframework.web.bind.annotation.RequestParam;

public interface AppPayDetailsService extends IService<AppPayDetails> {
    ResultUtil list(Integer page,Integer limit,
                    Integer type,
                    String payCode,
                    String userName,
                    String userId,
                    String phone,
                    String createTimeStart,
                    String createTimeEnd);
}
