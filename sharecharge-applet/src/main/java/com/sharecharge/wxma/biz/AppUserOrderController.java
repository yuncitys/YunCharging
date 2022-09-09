package com.sharecharge.wxma.biz;

import com.alibaba.druid.support.json.JSONUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sharecharge.biz.entity.AppUser;
import com.sharecharge.biz.entity.ChargeOrder;
import com.sharecharge.biz.entity.Device;
import com.sharecharge.biz.service.AppUserService;
import com.sharecharge.biz.service.ChargeOrderService;
import com.sharecharge.biz.service.DeviceService;
import com.sharecharge.biz.service.device.MqttService;
import com.sharecharge.biz.websocket.WebSocketServer;
import com.sharecharge.core.util.Crc16Util;
import com.sharecharge.core.util.HexUtils;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.core.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;

@Slf4j
@RestController
@RequestMapping("app/order")
@RequiredArgsConstructor
public class AppUserOrderController {
    final AppUserService appUserService;
    final DeviceService deviceService;
    final ChargeOrderService chargeOrderService;
    final MqttService mqttService;

    /**
     * 录入订单
     *
     * @param
     * @return
     */
    @RequestMapping("savaOrder")
    @Transactional
    public ResultUtil savaOrder(@RequestParam(value = "userId") Integer userId,
                                @RequestParam(value = "deviceCode") String deviceCode,
                                @RequestParam(value = "totalPrice") Double totalPrice,
                                @RequestParam(value = "devicePort") Integer devicePort,
                                @RequestParam(value = "orderType") Integer orderType,
                                @RequestParam(value = "hour") Integer hour,
                                @RequestParam(value = "type") String type) {
        if (Objects.isNull(userId) || StringUtils.isBlank(deviceCode) || Objects.isNull(totalPrice) || Objects.isNull(devicePort)
                || Objects.isNull(orderType) || Objects.isNull(hour)) {
            return ResultUtil.error("参数不合格");
        }
        AppUser appUser = appUserService.getById(userId);
        if (Objects.isNull(appUser)) {
            return ResultUtil.error("用户不存在");
        }
        Device device = deviceService.getOne(new QueryWrapper<Device>().lambda().eq(Device::getDeviceCode, deviceCode));
        if (Objects.isNull(device)) {
            return ResultUtil.error("设备未录入");
        }
        ChargeOrder chargeOrder = new ChargeOrder();
        chargeOrder.setDeviceCode(deviceCode);
        chargeOrder.setDevicePort(devicePort);
        chargeOrder.setOrderStatus(1);
        int one = chargeOrderService.count(new QueryWrapper<>(chargeOrder));
        if (one>1) {
            return ResultUtil.error("端口" + devicePort + "正在使用状态");
        }
        int a = new BigDecimal(appUser.getCash()).compareTo(new BigDecimal(totalPrice));
        if (a < 0) {
            return ResultUtil.error("订单金额不能大于余额");
        }
        double appOrderSumPrice = chargeOrderService.findAppOrderSumPrice(userId);
        BigDecimal remain = new BigDecimal(appUser.getCash()).subtract(new BigDecimal(appOrderSumPrice));
        if (remain.doubleValue() < totalPrice.doubleValue()) {
            return ResultUtil.error("当前已有订单在充电,余额不足开启其他端口,请充值");
        }
        //String nonceStr = (UUID.randomUUID()).toString().replaceAll("-", "");
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        String dateName = df.format(calendar.getTime());
        int v = (int) ((Math.random() * 9 + 1) * 1000000);
        //设置订单号
        String outTradeNo = dateName + v;
        ChargeOrder orderInfoMy = new ChargeOrder();
        orderInfoMy.setOrderCode(outTradeNo);
        orderInfoMy.setUserId(appUser.getId());
        orderInfoMy.setDeviceId(device.getId());
        orderInfoMy.setTotalPrice(BigDecimal.valueOf(totalPrice));
        orderInfoMy.setDeviceCode(deviceCode);
        orderInfoMy.setDevicePort(devicePort);
        orderInfoMy.setOrderType(orderType);
        orderInfoMy.setOrderStatus(0);
        orderInfoMy.setPayStatus(1);
        orderInfoMy.setPriceType(device.getDeviceChargePattern());//0时间 1电量 2免费
        Date date = new Date();
        orderInfoMy.setCreateTime(date);
        orderInfoMy.setStartTime(date);
        orderInfoMy.setHour(String.valueOf(hour));

        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        switch (type) {
            case "00":
                ca.add(Calendar.MINUTE, hour);
                orderInfoMy.setPriceType(0);
                break;
            case "01":
                ca.add(Calendar.MINUTE, hour * 100);
                orderInfoMy.setPriceType(1);
                break;
            case "02":
                ca.add(Calendar.MINUTE, 25);
                orderInfoMy.setPriceType(2);
                break;
        }
        Date end = ca.getTime();
        orderInfoMy.setEndTime(end);
        chargeOrderService.save(orderInfoMy);

        String ports = String.format("%02x", devicePort);
        if (type.equals("01")) {
            hour = hour * 100;
        }
        if (type.equals("02")) {
            type = "00";
            hour = 25;
        }

        String hous = String.format("%04x", hour);
        String cmc = "2A0AAE" + ports + type + hous;
        //将十六进制字符串转数组
        byte[] bytes = HexUtils.hexStringToBytes(cmc);
        String crc = Crc16Util.getCRC(bytes);
        System.out.println("CRC:" + crc);
        String s = HexUtils.HighAndLowSwap(crc);
        String cmd = cmc + s + "23";
        mqttService.sendMqttMessage("/down/" + deviceCode, 1, HexUtils.hexStringToBytes(cmd));

        Map<String,Object> map = new HashMap();
        Map<String,Object> data = new HashMap();
        data.put("messageType", 1);
        map.put("deviceCode", deviceCode);
        map.put("devicePort", devicePort);
        map.put("type", 1);
        map.put("commandContent", cmd);
        map.put("createTime", new Date());
        map.put("commandRemarks", "启动" + devicePort + "端口");
        data.put("messageData", map);
        WebSocketServer.sendMsg(deviceCode, JSONUtils.toJSONString(data));
        return ResultUtil.success("正在开启充电，请稍等。。。");
    }

    /**
     * 查询充电进行中的订单
     *
     * @param deviceCode
     * @param port
     * @param page
     * @param limit
     * @param userId
     * @param orderStatus
     * @return
     */
    @RequestMapping("getOrderList")
    public ResultUtil getOrderList(@RequestParam("deviceCode") String deviceCode,
                                   @RequestParam("port") Integer port,
                                   @RequestParam("page") Integer page,
                                   @RequestParam("limit") Integer limit,
                                   @RequestParam("userId") Integer userId,
                                   @RequestParam("orderStatus") Integer orderStatus) {
        if (StringUtils.isBlank(deviceCode) || Objects.isNull(userId) || Objects.isNull(port) || Objects.isNull(page) || Objects.isNull(limit) || Objects.isNull(orderStatus)) {
            return ResultUtil.error("参数不合格");
        }
        try {
            return ResultUtil.success();
        } catch (Exception e) {
            return ResultUtil.error("查询设备信息失败");
        }
    }

    /**
     * 充电结束后查看弹出订单详情
     *
     * @param orderCode
     * @return
     */
    @RequestMapping("getOrderListByOrderCode")
    public ResultUtil getOrderListByOrderCode(@RequestParam("orderCode") String orderCode) {
        if (StringUtils.isBlank(orderCode)) {
            return ResultUtil.error("参数不合格");
        }
        try {
            ChargeOrder appOrderList = chargeOrderService.getOne(new QueryWrapper<ChargeOrder>().lambda().eq(ChargeOrder::getOrderCode, orderCode));
            return ResultUtil.success(appOrderList);
        } catch (Exception e) {
            return ResultUtil.error("查询设备信息失败");
        }
    }

    /**
     * 查询订单列表
     *
     * @param page
     * @param limit
     * @param userId
     * @param orderStatus
     * @return
     */
    @RequestMapping("getAppOrderListTable")
    public ResultUtil findAppOrderListTable(
            @RequestParam("page") Integer page,
            @RequestParam("limit") Integer limit,
            @RequestParam("userId") Integer userId,
            @RequestParam("orderStatus") Integer orderStatus) {
        if (Objects.isNull(page) || Objects.isNull(limit) || Objects.isNull(orderStatus)) {
            return ResultUtil.error("参数不合格");
        }
        try {
            Map maps = new HashMap();
            maps.put("orderStatus", orderStatus);
            maps.put("page", (page - 1) * 10);
            maps.put("limit", limit);
            maps.put("userId", userId);
            Page<ChargeOrder> chargeOrderPage = new Page();
            chargeOrderPage.setCurrent(page);
            chargeOrderPage.setSize(limit);
            ChargeOrder chargeOrder = new ChargeOrder();
            chargeOrder.setOrderStatus(orderStatus);
            chargeOrder.setUserId(userId);
            List<ChargeOrder> appOrderList = chargeOrderService.page(chargeOrderPage, new QueryWrapper<>(chargeOrder)).getRecords();
            return ResultUtil.success(appOrderList);
        } catch (Exception e) {
            return ResultUtil.error("查询设备信息失败");
        }
    }


    /**
     * 取消订单
     *
     * @param orderCode
     * @return
     */
    @RequestMapping("closeOrder")
    public ResultUtil closeOrder(@RequestParam("orderCode") String orderCode) {
        if (StringUtils.isBlank(orderCode)) {
            return ResultUtil.error("参数不合格");
        }
        try {
            Map maps = new HashMap();
            maps.put("orderCode", orderCode);
            ChargeOrder appOrderList = chargeOrderService.getOne(new QueryWrapper<ChargeOrder>().lambda().eq(ChargeOrder::getOrderCode, orderCode));
            if (appOrderList.getOrderStatus() != 1) {
                return ResultUtil.error("订单不可结束");
            }
            String ports = String.format("%02x", appOrderList.getDevicePort());
            String cmc = "2A07AF" + ports;
            //将十六进制字符串转数组
            byte[] bytes = HexUtils.hexStringToBytes(cmc);
            String crc = Crc16Util.getCRC(bytes);
            System.out.println("CRC:" + crc);
            String s = HexUtils.HighAndLowSwap(crc);
            String cmd = cmc + s + "23";
            Map data = new HashMap();
            data.put("messageType",1);
            Map map = new HashMap();
            map.put("deviceCode", appOrderList.getDeviceCode());
            map.put("type", 1);
            map.put("commandContent", cmd);
            map.put("commandRemarks", "关闭订单" + ports + "端口");
            data.put("messageData",map);
            WebSocketServer.sendMsg(appOrderList.getDeviceCode(), JSONUtils.toJSONString(map));
            mqttService.sendMqttMessage("/down/" + appOrderList.getDeviceCode(), 1, HexUtils.hexStringToBytes(cmd));
            return ResultUtil.success();
        } catch (Exception e) {
            return ResultUtil.error("查询设备信息失败");
        }
    }
}
