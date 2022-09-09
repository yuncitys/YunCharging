package com.sharecharge.web.biz;

import com.sharecharge.biz.service.AppPayDetailsService;
import com.sharecharge.core.util.ResultUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("sys/payDetails")
@RequiredArgsConstructor
public class PayDetailsController {

    final AppPayDetailsService payDetailsService;


    /**
     * 充值记录列表
     *
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping("findPayDetails")
    @PreAuthorize("@ps.hasPermission(':sys:payDetails:findPayDetails')")
    public ResultUtil findPayDetails(@RequestParam("page") Integer page,
                                     @RequestParam("limit") Integer limit,
                                     Integer type,
                                     String payCode,
                                     String userName,
                                     String userId,
                                     String phone,
                                     String createTimeStart,
                                     String createTimeEnd) {
        return payDetailsService.list(page,limit,type,payCode,userName,userId,phone,createTimeStart,createTimeEnd);
    }

}
