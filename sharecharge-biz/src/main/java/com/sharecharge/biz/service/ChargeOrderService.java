package com.sharecharge.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.biz.entity.ChargeOrder;
import com.sharecharge.core.util.ResultUtil;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface ChargeOrderService extends IService<ChargeOrder> {
    ResultUtil list(Integer page, Integer limit,Integer orderType,
                             String userId, String phoneNumber,
                             String orderCode, String deviceCode, Integer orderStatus, String cardNo);

    double findAppOrderSumPrice(Integer userId);

    List<Map> findNotBeatOrder();
}
