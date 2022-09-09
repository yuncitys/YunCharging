package com.sharecharge.web.biz;

import com.sharecharge.biz.entity.DeviceFeeBack;
import com.sharecharge.biz.service.DeviceFeeBackService;
import com.sharecharge.core.util.ResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sys/feeback")
@RequiredArgsConstructor
public class DeviceFeeBackController {
    final DeviceFeeBackService deviceFeeBackService;

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
    @PreAuthorize("@ps.hasPermission(':sys:feeback:findDeviceFeeBackList')")
    public ResultUtil findDeviceFeeBackList(@RequestParam("page") Integer page,
                                            @RequestParam("limit") Integer limit,
                                            String reservedPhone, String createTimeStart, String createTimeEnd, Integer status) {
        return deviceFeeBackService.list(page,limit,reservedPhone,createTimeStart,createTimeEnd,status);

    }

    /**
     * 处理反馈结果
     *
     * @param id
     * @return
     */
    @RequestMapping("updateDeviceFeeBackList")
    @PreAuthorize("@ps.hasPermission(':report:reportList:confirm')")
    public ResultUtil updateDeviceFeeBackList(Integer id) {
        try {
//            DeviceFeeBack deviceFeeBack=new DeviceFeeBack();
//            deviceFeeBack.setStatus(1);
//            deviceFeeBack.setId(id);
            deviceFeeBackService.removeById(id);
            return ResultUtil.success("修改状态成功");
        } catch (Exception e) {
            return ResultUtil.error("系统异常");
        }
    }
}
