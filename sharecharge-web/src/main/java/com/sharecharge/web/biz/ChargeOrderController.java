package com.sharecharge.web.biz;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sharecharge.biz.entity.AppUser;
import com.sharecharge.biz.entity.ChargeOrder;
import com.sharecharge.biz.entity.Device;
import com.sharecharge.biz.service.AppUserService;
import com.sharecharge.biz.service.ChargeOrderService;
import com.sharecharge.biz.service.DeviceService;
import com.sharecharge.biz.service.PowerRecordService;
import com.sharecharge.biz.service.device.MqttService;
import com.sharecharge.biz.websocket.WebSocketServer;
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.Crc16Util;
import com.sharecharge.core.util.HexUtils;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.core.util.StringUtils;
import com.sharecharge.system.entity.DbAdminUser;
import com.sharecharge.system.service.DbAdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Slf4j
@RestController
@RequestMapping("sys/orderInfo")
@RequiredArgsConstructor
public class ChargeOrderController {

    final ChargeOrderService chargeOrderService;
    final DeviceService deviceService;
    final DbAdminUserService adminUserService;
    final PowerRecordService powerRecordService;
    final MqttService mqttService;
    final AppUserService appUserService;

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
    @PreAuthorize("@ps.hasPermission(':sys:orderInfo:findOrderInfo')")
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
    @PreAuthorize("@ps.hasPermission(':sys:orderInfo:findOrderInfoById')")
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

    /**
     * 删除订单
     *
     * @param orderCode
     * @return
     */
    @RequestMapping("deleteOrder")
    @PreAuthorize("@ps.hasPermission(':order:scanOrderList:delete')")
    public ResultUtil deleteOrder(String orderCode) {
        try {
            ChargeOrder chargeOrder=new ChargeOrder();
            chargeOrder.setOrderCode(orderCode);
            chargeOrder.setIsDelete(1);
            chargeOrderService.update(chargeOrder,new UpdateWrapper<ChargeOrder>().lambda().eq(ChargeOrder::getOrderCode,orderCode));
            return ResultUtil.success("删除订单成功");
        } catch (Exception e) {
            log.error("删除订单失败");
            return ResultUtil.error("系统异常");
        }

    }

    /**
     * 导出订单信息
     *
     * @param orderCode
     * @param deviceCode
     * @param orderStatus
     * @return
     */
    @RequestMapping("downloadOrderInfo")
    @PreAuthorize("@ps.hasPermission(':sys:orderInfo:downloadOrderInfo')")
    public ResultUtil downloadOrderInfo(
                                        @RequestParam("orderType") Integer orderType,
                                        String userId, String phoneNumber,
                                        String orderCode, String deviceCode, Integer orderStatus, String cardNo) {
        return chargeOrderService.list(1,1000,orderType,userId,phoneNumber,orderCode,deviceCode,orderStatus,cardNo);
    }

    /**
     * 查询订单功率图
     *
     * @param startTime
     * @param curTime
     * @return
     */
    @RequestMapping("findDevicePowerDetails")
    @PreAuthorize("@ps.hasPermission('sys:orderInfo:findDevicePowerDetails')")
    public ResultUtil findDevicePowerDetails(String deviceCode, String prot, String startTime, String curTime) {
        if (StringUtils.isBlank(deviceCode) || StringUtils.isBlank(prot)
                || StringUtils.isBlank(startTime) || StringUtils.isBlank(curTime)) {
            return ResultUtil.error("参数有误");
        }
        return  powerRecordService.findDevicePowerDetails(deviceCode, prot, startTime, curTime);
    }

    /**
     * 关闭订单
     *
     * @param id
     * @return
     */
    @PreAuthorize("@ps.hasPermission(':sys:orderInfo:updateOrderStatus')")
    @RequestMapping("updateOrderStatus")
    @Transactional
    public ResultUtil updateOrderStatus(Integer id) {
        try {
            ChargeOrder byId = chargeOrderService.getById(id);
            Integer port = byId.getDevicePort() ;
            String ports = String.format("%02x", port);
            String cmc = "2A07AF" + ports;
            //将十六进制字符串转数组
            byte[] bytes = HexUtils.hexStringToBytes(cmc);
            String crc = Crc16Util.getCRC(bytes);
            System.out.println("CRC:" + crc);
            String s = HexUtils.HighAndLowSwap(crc);
            String cmd = cmc + s + "23";
            System.out.println("关闭单个端口指令:" + cmd);
            Map data = new HashMap();
            data.put("messageType",1);
            Map map = new HashMap();
            map.put("deviceCode", byId.getDeviceCode());
            map.put("type", 1);
            map.put("commandContent", cmd);
            map.put("commandRemarks", "关闭" + port + "端口");
            data.put("messageData",map);
            WebSocketServer.sendMsg(byId.getDeviceCode(), JSONObject.toJSONString(data));
            mqttService.sendMqttMessage("/down/" + byId.getDeviceCode(), 1, HexUtils.hexStringToBytes(cmd));
            return ResultUtil.success("发送结束订单指令");
        } catch (Exception e) {
            log.error("关闭订单失败", e.getMessage());
            return ResultUtil.error("关闭订单失败");
        }
    }

    /**
     * 订单退款
     *
     * @param orderCode
     * @param wxOpenId
     * @return
     */
    @PreAuthorize("@ps.hasPermission(':sys:orderInfo:updateOrder')")
    @RequestMapping("updateOrder")
    @Transactional
    public ResultUtil updateOrder(@RequestParam("orderCode") String orderCode, @RequestParam("wxOpenId") String wxOpenId) {
        try {
            ChargeOrder one = chargeOrderService.getOne(new QueryWrapper<ChargeOrder>().lambda().eq(ChargeOrder::getOrderCode, orderCode));
            if (one.getOrderType()==0){
                //退卡
            }else {
                AppUser byId = appUserService.getById(one.getUserId());
                byId.setCash(byId.getCash()+one.getRealityPayMoney().doubleValue());
                byId.setRealityPayMoney(byId.getRealityPayMoney()+one.getRealityPayMoney().doubleValue());
                appUserService.updateById(byId);
                // TODO: 2022/7/23 推送退款通知
                one.setOrderStatus(7);
                one.setProxyMoneyStatus(0);
                chargeOrderService.updateById(one);
            }
            Device byId = deviceService.getById(one.getDeviceId());
            Integer thirdAgentId = byId.getThirdAgentId();
            Integer secondAgentId = byId.getSecondAgentId();
            Integer firstAgentId = byId.getFirstAgentId();
            if (!Objects.isNull(byId.getThirdAgentId())) {//三级代理
                DbAdminUser adminUserById = adminUserService.getById(thirdAgentId);
                adminUserById.setBalanceAmount(adminUserById.getBalanceAmount().subtract(new BigDecimal(one.getThirdAgentProfit())));
                adminUserById.setTotalAmount(adminUserById.getTotalAmount().subtract(new BigDecimal(one.getThirdAgentProfit())));
                adminUserService.updateById(adminUserById);
            }
            if (!Objects.isNull(secondAgentId)) {//二级代理
                DbAdminUser adminUserById = adminUserService.getById(secondAgentId);

                adminUserById.setBalanceAmount(adminUserById.getBalanceAmount().subtract(new BigDecimal(one.getSecondAgentProfit())));
                adminUserById.setTotalAmount(adminUserById.getTotalAmount().subtract(new BigDecimal(one.getSecondAgentProfit())));

                adminUserService.updateById(adminUserById);

            }
            if (!Objects.isNull(firstAgentId)) {//一级代理
                DbAdminUser adminUserById = adminUserService.getById(firstAgentId);
                adminUserById.setBalanceAmount(adminUserById.getBalanceAmount().subtract(new BigDecimal(one.getFirstAgentProfit())));
                adminUserById.setTotalAmount(adminUserById.getTotalAmount().subtract(new BigDecimal(one.getFirstAgentProfit())));
                adminUserService.updateById(adminUserById);
            }
            DbAdminUser adminUserById = adminUserService.getById(2);
            adminUserById.setBalanceAmount(adminUserById.getBalanceAmount().subtract(new BigDecimal(one.getAdminProfit())));
            adminUserById.setTotalAmount(adminUserById.getTotalAmount().subtract(new BigDecimal(one.getAdminProfit())));
            adminUserService.updateById(adminUserById);
            return ResultUtil.success("退款成功");
        } catch (Exception e) {
            return ResultUtil.error("系统异常");
        }
    }


}
