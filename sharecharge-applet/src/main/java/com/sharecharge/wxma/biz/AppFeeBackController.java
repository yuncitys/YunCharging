package com.sharecharge.wxma.biz;

import com.sharecharge.biz.service.DeviceFeeBackService;
import com.sharecharge.core.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("app/feeback")
@RestController
public class AppFeeBackController {

    @Autowired
    private DeviceFeeBackService deviceFeeBackService;

    /**
     * 查询故障反馈列表
     *
     * @param page
     * @param limit
     * @param reservedPhone
     * @param createTimeStart
     * @param createTimeEnd
     * @return
     */
    @RequestMapping("findDeviceFeeBackList")
    public ResultUtil findDeviceFeeBackList(@RequestParam("page") Integer page,
                                            @RequestParam("limit") Integer limit,
                                            String reservedPhone, String createTimeStart, String createTimeEnd, Integer status) {
        return deviceFeeBackService.list(page,limit,reservedPhone,createTimeStart,createTimeEnd,status);
    }

}

