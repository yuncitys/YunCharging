package com.sharecharge.wxma.biz;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sharecharge.biz.entity.ChargeOrder;
import com.sharecharge.biz.service.ChargeOrderService;
import com.sharecharge.biz.service.DeviceService;
import com.sharecharge.biz.service.PowerRecordService;
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.core.util.StringUtils;
import com.sharecharge.system.service.DbAdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("app/orderInfo")
@RequiredArgsConstructor
public class AppChargeOrderController {
    final ChargeOrderService chargeOrderService;
    final DeviceService deviceService;
    final DbAdminUserService adminUserService;
    final PowerRecordService powerRecordService;

    /**
     * 查询订单数据列表详情
     *
     * @param page
     * @param limit
     * @param orderCode
     * @param deviceCode
     * @return
     */
    @RequestMapping("findOrderInfo")
    public ResultUtil findOrderInfo(@RequestParam("page") Integer page,
                                    @RequestParam("limit") Integer limit,
                                    @RequestParam("orderType") Integer orderType,
                                    String userId, String phoneNumber,
                                    String orderCode, String deviceCode, Integer orderStatus, String cardNo) {
        return chargeOrderService.list(page,limit,orderType,userId,phoneNumber,orderCode,deviceCode,orderStatus,cardNo);
    }

    /**
     * 根据id查询订单详情
     *
     * @param id
     * @return
     */
    @RequestMapping("findOrderInfoById")
    public ResultUtil findOrderInfoById(@RequestParam("orderId") Integer id) {
        try {
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setData(chargeOrderService.getById(id));
            resultUtil.setMsg("查询订单信息成功");
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            return resultUtil;
        } catch (Exception e) {
            log.error("查询订单详情失败", e.getMessage());
            return ResultUtil.error("查询订单详情失败");
        }
    }


}
