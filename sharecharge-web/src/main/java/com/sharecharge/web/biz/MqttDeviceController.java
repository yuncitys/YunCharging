package com.sharecharge.web.biz;

import com.alibaba.druid.support.json.JSONUtils;
import com.sharecharge.biz.service.device.MqttService;
import com.sharecharge.biz.websocket.WebSocketServer;
import com.sharecharge.core.util.Crc16Util;
import com.sharecharge.core.util.HexUtils;
import com.sharecharge.core.util.ResultUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("mqtt/device")
@RequiredArgsConstructor
public class MqttDeviceController {
    final MqttService mqttService;

    /**
     * 打开全部端口
     *
     * @param deviceCode 设备号
     * @param time       时间 分钟为单位
     * @return
     */
    @RequestMapping("openAllPort")
    @PreAuthorize("@ps.hasPermission(':mqtt:device:openAllPort')")
    public ResultUtil openAllPort(@RequestParam("deviceCode") String deviceCode,
                                  @RequestParam("command") String command,
                                  @RequestParam("time") Integer time) {
        try {
            if (StringUtils.isBlank(deviceCode) || Objects.isNull(time)) {
                return ResultUtil.error("参数错误!");
            }
            String hours = String.format("%04x", time);
            String cmc = "2A08" + command + hours;
            //将十六进制字符串转数组
            byte[] bytes = HexUtils.hexStringToBytes(cmc);

            String crc = Crc16Util.getCRC(bytes);
            System.out.println("CRC:" + crc);
            String s = HexUtils.HighAndLowSwap(crc);
            String cmd = cmc + s + "23";
            System.out.println("启动全部端口指令:" + cmd);
            Map map = new HashMap();
            Map data = new HashMap();
            data.put("messageType", 1);
            map.put("deviceCode", deviceCode);
            map.put("type", 1);
            map.put("commandContent", cmd);
            map.put("commandRemarks", "启动全部端口");
            map.put("createTime", new Date());
            data.put("messageData", map);

            mqttService.sendMqttMessage("/down/" + deviceCode, 1, HexUtils.hexStringToBytes(cmd));

            WebSocketServer.sendMsg(deviceCode, JSONUtils.toJSONString(data));
            return ResultUtil.success("执行成功!");
        } catch (Exception e) {
            log.error("执行错误: " + e.getMessage());
            return ResultUtil.error("执行失败!");
        }
    }


    /**
     * 单个设备发送
     *
     * @param deviceCode 设备号
     * @param port       端口号
     * @return time 时间
     */
    @RequestMapping("openOnePort")
    @PreAuthorize("@ps.hasPermission(':mqtt:device:openOnePort')")
    public ResultUtil openOnePort(
            @RequestParam("deviceCode") String deviceCode,
            @RequestParam("port") Integer port,
            @RequestParam("command") String command,
            @RequestParam("time") Integer time,
            @RequestParam("type") String type) {
        try {
            if (StringUtils.isBlank(deviceCode) || StringUtils.isBlank(command) || Objects.isNull(time) || Objects.isNull(port)) {
                return ResultUtil.error("参数错误!");
            }
            String ports = String.format("%02x", port);
            String hous = String.format("%04x", time);
            String cmc = "2A0A" + command + ports + type + hous;
            //将十六进制字符串转数组
            byte[] bytes = HexUtils.hexStringToBytes(cmc);
            String crc = Crc16Util.getCRC(bytes);
            System.out.println("CRC:" + crc);
            String s = HexUtils.HighAndLowSwap(crc);
            String cmd = cmc + s + "23";
            System.out.println("单个端口发送指令:" + cmd);
            Map map = new HashMap();
            Map data = new HashMap();
            data.put("messageType", 1);
            map.put("deviceCode", deviceCode);
            map.put("type", 1);
            map.put("commandContent", cmd);
            map.put("commandRemarks", "启动" + port + "端口");
            map.put("createTime", new Date());
            data.put("messageData", map);
            mqttService.sendMqttMessage("/down/" + deviceCode, 1, HexUtils.hexStringToBytes(cmd));

            WebSocketServer.sendMsg(deviceCode, JSONUtils.toJSONString(data));
            return ResultUtil.success("执行成功!");
        } catch (Exception e) {
            log.error("执行错误: " + e.getMessage());
            return ResultUtil.error("执行失败!");
        }
    }

    /**
     * 关闭单个端口
     *
     * @param deviceCode
     * @param port
     * @return time
     */
    @RequestMapping("closePort")
    @PreAuthorize("@ps.hasPermission(':mqtt:device:closePort')")
    public ResultUtil closePort(@RequestParam("deviceCode") String deviceCode,
                                @RequestParam("port") Integer port,
                                @RequestParam("command") String command) {
        try {
            if (StringUtils.isBlank(deviceCode) || Objects.isNull(port)) {
                return ResultUtil.error("参数错误!");
            }
            String ports = String.format("%02x", port);
            String cmc = "2A07" + command + ports;
            //将十六进制字符串转数组
            byte[] bytes = HexUtils.hexStringToBytes(cmc);
            String crc = Crc16Util.getCRC(bytes);
            System.out.println("CRC:" + crc);
            String s = HexUtils.HighAndLowSwap(crc);
            String cmd = cmc + s + "23";
            System.out.println("关闭单个端口指令:" + cmd);
            Map map = new HashMap();
            Map data = new HashMap();
            data.put("messageType", 1);
            map.put("deviceCode", deviceCode);
            map.put("type", 1);
            map.put("commandContent", cmd);
            map.put("commandRemarks", "关闭" + port + "端口");
            map.put("createTime", new Date());
            data.put("messageData", map);

            mqttService.sendMqttMessage("/down/" + deviceCode, 1, HexUtils.hexStringToBytes(cmd));

            WebSocketServer.sendMsg(deviceCode, JSONUtils.toJSONString(data));
            return ResultUtil.success("执行成功!");
        } catch (Exception e) {
            log.error("执行错误: " + e.getMessage());
            return ResultUtil.error("执行失败!");
        }
    }

    /**
     * 查询操作
     *
     * @param deviceCode
     */
    @RequestMapping("queryDeviceToCommand")
    @PreAuthorize("@ps.hasPermission('/mqtt/device/queryDeviceToCommand')")
    public ResultUtil queryDeviceToCommand(@RequestParam("deviceCode") String deviceCode,
                                           @RequestParam("command") String command) {
        try {
            if (StringUtils.isBlank(deviceCode)) {
                return ResultUtil.error("参数错误!");
            }
            String cmc = "2A06" + command;
            //将十六进制字符串转数组
            byte[] bytes = HexUtils.hexStringToBytes(cmc);
            String crc = Crc16Util.getCRC(bytes);
            System.out.println("CRC:" + crc);
            String s = HexUtils.HighAndLowSwap(crc);
            String cmd = cmc + s + "23";
            System.out.println(command + "查询操作指令:" + cmd);
            Map map = new HashMap();
            Map data = new HashMap();
            data.put("messageType", 1);
            map.put("deviceCode", deviceCode);
            map.put("type", 1);
            map.put("commandContent", cmd);
            map.put("commandRemarks", "查询" + command + "操作");
            map.put("createTime", new Date());
            data.put("messageData", map);

            mqttService.sendMqttMessage("/down/" + deviceCode, 1, HexUtils.hexStringToBytes(cmd));

            WebSocketServer.sendMsg(deviceCode, JSONUtils.toJSONString(data));
            return ResultUtil.success("执行成功!");
        } catch (Exception e) {
            log.error("执行错误: " + e.getMessage());
            return ResultUtil.error("执行失败!");
        }
    }

    /**
     * 查询设备单个端口剩余时间/电量
     *
     * @param deviceCode
     */
    @RequestMapping("queryOneProtStatus")
    @PreAuthorize("@ps.hasPermission('/mqtt/device/queryOneProtStatus')")
    public ResultUtil queryOnePortStatus(@RequestParam("deviceCode") String deviceCode,
                                         @RequestParam("command") String command,
                                         @RequestParam("isTime") String isTime,
                                         @RequestParam("port") Integer port
    ) {
        try {
            if (StringUtils.isBlank(deviceCode)) {
                return ResultUtil.error("参数错误!");
            }
            String ports = String.format("%02x", port);
            String cmc = "2A08" + command + isTime + ports;
            //将十六进制字符串转数组
            byte[] bytes = HexUtils.hexStringToBytes(cmc);
            String crc = Crc16Util.getCRC(bytes);
            System.out.println("CRC:" + crc);
            String s = HexUtils.HighAndLowSwap(crc);
            String cmd = cmc + s + "23";
            System.out.println("查询设备单个端口剩余时间:" + cmd);
            Map map = new HashMap();
            Map data = new HashMap();
            data.put("messageType", 1);
            map.put("deviceCode", deviceCode);
            map.put("type", 1);
            map.put("commandContent", cmd);
            map.put("commandRemarks", "设备" + port + "端口剩余时间/电量");
            map.put("createTime", new Date());
            data.put("messageData", map);
            mqttService.sendMqttMessage("/down/" + deviceCode, 1, HexUtils.hexStringToBytes(cmd));

            WebSocketServer.sendMsg(deviceCode, JSONUtils.toJSONString(data));
            return ResultUtil.success("执行成功!");
        } catch (Exception e) {
            log.error("执行错误: " + e.getMessage());
            return ResultUtil.error("执行失败!");
        }
    }

    /**
     * 续充设备单端口
     *
     * @param deviceCode 设备号
     * @param port       端口号
     * @return time 时间
     */
    @RequestMapping("chargeOnePort")
    @PreAuthorize("@ps.hasPermission(':mqtt:device:chargeOnePort')")
    public ResultUtil chargeOnePort(
            @RequestParam("deviceCode") String deviceCode,
            @RequestParam("port") Integer port,
            @RequestParam("command") String command,
            @RequestParam("type") String type,
            @RequestParam("time") Integer time) {
        try {
            if (StringUtils.isBlank(deviceCode) || StringUtils.isBlank(command) || Objects.isNull(time) || Objects.isNull(port)) {
                return ResultUtil.error("参数错误!");
            }
            String ports = String.format("%02x", port);
            String hous = String.format("%04x", time);
            String cmc = "2A0A" + command + ports + type + hous;
            //将十六进制字符串转数组
            byte[] bytes = HexUtils.hexStringToBytes(cmc);
            String crc = Crc16Util.getCRC(bytes);
            System.out.println("CRC:" + crc);
            String s = HexUtils.HighAndLowSwap(crc);
            String cmd = cmc + s + "23";
            System.out.println("单个端口发送指令:" + cmd);
            Map map = new HashMap();
            Map data = new HashMap();
            data.put("messageType", 1);
            map.put("deviceCode", deviceCode);
            map.put("type", 1);
            map.put("commandContent", cmd);
            map.put("commandRemarks", "续充" + port + "端口");
            map.put("createTime", new Date());
            data.put("messageData", map);

            mqttService.sendMqttMessage("/down/" + deviceCode, 1, HexUtils.hexStringToBytes(cmd));
            WebSocketServer.sendMsg(deviceCode, JSONUtils.toJSONString(data));
            return ResultUtil.success("执行成功!");
        } catch (Exception e) {
            log.error("执行错误: " + e.getMessage());
            return ResultUtil.error("执行失败!");
        }
    }

    /**
     * 设置设备参数
     *
     * @param deviceCode
     */
    @RequestMapping("setParmDevice")
    @PreAuthorize("@ps.hasPermission(':mqtt:device:setParmDevice')")
    public ResultUtil setParamDevice(@RequestParam("deviceCode") String deviceCode,
                                    @RequestParam("command") String command,
                                    @RequestParam("chargeType") Integer chargeType,
//                                    @RequestParam("portCount") Integer portCount,
                                    @RequestParam("heartbeatTime") Integer heartbeatTime,
                                    @RequestParam("waitTime") Integer waitTime,
                                    @RequestParam("totalPowerUpper") Integer totalPowerUpper,
                                    @RequestParam("powerUpper") Integer powerUpper,
                                    @RequestParam("powerLower") Integer powerLower,
                                    @RequestParam("highPowerUpper") Integer highPowerUpper,
                                    @RequestParam("highPowerLower") Integer highPowerLower,
                                    @RequestParam("lowTemperature") Integer lowTemperature,
                                    @RequestParam("highTemperature") Integer highTemperature,
                                    @RequestParam("warningTemperature") Integer warningTemperature) {
        try {
            if (StringUtils.isBlank(deviceCode)) {
                return ResultUtil.error("参数错误!");
            }
            String waitTimes = String.format("%02x", waitTime);
            String chargeTypes = String.format("%02x", chargeType);
            String totalPowerUpperS = String.format("%04x", totalPowerUpper);
            String powerUppers = String.format("%04x", powerUpper);
            String powerLowers = String.format("%02x", powerLower);
            String highPowerUppers = String.format("%04x", highPowerUpper);
            String highPowerLowers = String.format("%02x", highPowerLower);
            String lowTemperatures = String.format("%02x", lowTemperature);
            String highTemperatures = String.format("%02x", highTemperature + 60);
            String warningTemperatures = String.format("%02x", warningTemperature + 60);
            String heartbeatTimes = String.format("%04x", heartbeatTime);
            String protCounts = String.format("%02x", 10);
            String cmc = "2A17" + command + chargeTypes + waitTimes + totalPowerUpperS + powerUppers + powerLowers + highPowerUppers + highPowerLowers + lowTemperatures + warningTemperatures + highTemperatures + heartbeatTimes + protCounts + "00";
            //将十六进制字符串转数组
            byte[] bytes = HexUtils.hexStringToBytes(cmc);
            String crc = Crc16Util.getCRC(bytes);
            System.out.println("CRC:" + crc);
            String s = HexUtils.HighAndLowSwap(crc);
            String cmd = cmc + s + "23";
            System.out.println("设置参数:" + cmd);
            Map map = new HashMap();
            Map data = new HashMap();
            data.put("messageType", 1);
            map.put("deviceCode", deviceCode);
            map.put("type", 1);
            map.put("commandContent", cmd);
            map.put("commandRemarks", "设置参数");
            map.put("createTime", new Date());
            data.put("messageData", map);
            mqttService.sendMqttMessage("/down/" + deviceCode, 1, HexUtils.hexStringToBytes(cmd));
            WebSocketServer.sendMsg(deviceCode, JSONUtils.toJSONString(data));
            return ResultUtil.success("执行成功!");
        } catch (Exception e) {
            log.error("执行错误: " + e.getMessage());
            return ResultUtil.error("执行失败!");
        }
    }

    /**
     * 发送给设备升级指令
     *
     * @param
     * @param
     * @return
     */
    @RequestMapping("sendOtaDevice")
    @PreAuthorize("@ps.hasPermission(':mqtt:device:sendOtaDevice')")
    public ResultUtil sendOtaDevice(@RequestParam("url") String url,
                                    @RequestParam("md5") String md5,
                                    @RequestParam("len") String len,
                                    @RequestParam("deviceCode") String deviceCode,
                                    @RequestParam("type") Integer type) {
        String cmd = "ULR:" + url + ";MD5:" + md5 + ";LEN:" + len + ";";
        if (type == 0) {
            deviceCode = "#";
        }
        Map data = new HashMap();
        data.put("messageType", 1);
        Map map = new HashMap();
        map.put("deviceCode", deviceCode);
        map.put("type", 1);
        map.put("commandContent", cmd);
        map.put("commandRemarks", "远程升级");
        map.put("createTime", new Date());
        data.put("messageData", map);

        byte[] bytes = Crc16Util.ConvertToASCII(cmd);
        mqttService.sendMqttMessage("/down/" + deviceCode, 1, bytes);

        WebSocketServer.sendMsg(deviceCode, JSONUtils.toJSONString(data));
        return ResultUtil.success();
    }


    /**
     * 查询设备指令操作详情
     *
     * @param startTime
     * @param curTime
     * @return
     */
    @RequestMapping("findDeviceCommandDetails")
    @PreAuthorize("@ps.hasPermission('/mqtt/device/findDeviceCommandDetails')")
    public ResultUtil findDeviceCommandDetails(String deviceCode, String startTime, String curTime) {
        Map map = new HashMap();
        map.put("deviceCode", deviceCode);
        map.put("startTime", startTime);
        map.put("curTime", curTime);
        return ResultUtil.success(map);
    }
}
