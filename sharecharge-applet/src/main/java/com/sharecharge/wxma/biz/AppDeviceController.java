package com.sharecharge.wxma.biz;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sharecharge.biz.entity.Device;
import com.sharecharge.biz.entity.DeviceType;
import com.sharecharge.biz.entity.NetworkDot;
import com.sharecharge.biz.service.DeviceService;
import com.sharecharge.biz.service.DeviceTypeService;
import com.sharecharge.biz.service.NetworkDotService;
import com.sharecharge.biz.service.device.MqttService;
import com.sharecharge.biz.websocket.WebSocketServer;
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.Crc16Util;
import com.sharecharge.core.util.HexUtils;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.security.util.SecurityUtil;
import com.sharecharge.system.service.DbAdminUserService;
import com.sharecharge.system.service.DbParentOrSonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
@RestController
@RequestMapping("app/device")
@RequiredArgsConstructor
public class AppDeviceController {
    final DeviceService deviceService;
    final DbParentOrSonService parentOrSonService;
    final MqttService mqttService;

    @RequestMapping("findDeviceInfoList")
    public ResultUtil list(@RequestParam("page") Integer page,
                           @RequestParam("limit") Integer limit,
                           @RequestParam("allocationStatus") Integer allocationStatus, Integer deviceChargePattern,
                           String deviceCode, String networkAddress, Integer deviceStatus, Integer dealerId){
        return deviceService.list(page,limit,allocationStatus,deviceChargePattern,deviceCode,networkAddress,deviceStatus,dealerId);
    }

    @RequestMapping("findDeviceInfoById")
    public ResultUtil findDeviceInfoById(@RequestParam("deviceId") Integer id) {
        return ResultUtil.success(deviceService.getById(id));
    }

    /**
     * 添加收费方案
     *
     * @param devicePriceId
     * @param deviceCode
     * @return
     */
    @RequestMapping("addDevicePrice")
    public ResultUtil addDevicePrice(Integer devicePriceId, String deviceCode, Integer devicePriceType) {
        try {
            Device one = deviceService.getOne(new QueryWrapper<Device>().lambda().eq(Device::getDeviceCode, deviceCode));
            if (Objects.isNull(one)){
                return ResultUtil.error("设备不存在");
            }
            one.setDeviceCode(deviceCode);
            if (devicePriceType==2) {
                one.setDevicePriceId(0);
            }else if (devicePriceType==3){
                devicePriceType=0; //0时间 1电量 2免费 3功率
            }
            one.setDevicePriceId(devicePriceId);

            if (one.getDeviceChargePattern()!=devicePriceType){
                return ResultUtil.error("请将设备计费方式设置为与方案对应的计费类型");
            }
            deviceService.updateById(one);
            return ResultUtil.error("设置收费方案成功");
        } catch (Exception e) {
            log.error("添加收费方案失败：" + e.getMessage());
            return ResultUtil.error("系统异常");
        }
    }

    /**
     * 设备出入库操作
     *
     * @param pullStatus
     * @param deviceId
     * @return
     */
    @RequestMapping("updateDevicePutState")
    public ResultUtil updateDevicePutState(@RequestParam("pullStatus") Integer pullStatus,
                                           @RequestParam("deviceId") Integer deviceId) {

        try {
            Device device=new Device();
            device.setActivateStatus(pullStatus);
            device.setId(deviceId);
            deviceService.updateById(device);
            return ResultUtil.success("更新设备成功");
        }catch (Exception e){
            log.error("系统异常：{}",e.getMessage());
            return ResultUtil.error("更新失败");
        }
    }

    /**
     * 开启单个端口
     *
     * @param deviceCode 设备号
     * @param port       端口号
     * @return time 时间
     */
    @RequestMapping("openOnePort")
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
            map.put("deviceCode", deviceCode);
            map.put("type", 1);
            map.put("commandContent", cmd);
            map.put("commandRemarks", "启动" + port + "端口");
            data.put("messageData", map);
            WebSocketServer.sendMsg(deviceCode, JSONObject.toJSONString(data));
            mqttService.sendMqttMessage("/down/" + deviceCode, 1, HexUtils.hexStringToBytes(cmd));
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
            map.put("deviceCode", deviceCode);
            map.put("type", 1);
            map.put("commandContent", cmd);
            map.put("commandRemarks", "关闭" + port + "端口");
            data.put("messageData", map);
            WebSocketServer.sendMsg(deviceCode, JSONObject.toJSONString(data));
            mqttService.sendMqttMessage("/down/" + deviceCode, 1, HexUtils.hexStringToBytes(cmd));
            return ResultUtil.success("执行成功!");
        } catch (Exception e) {
            log.error("执行错误: " + e.getMessage());
            return ResultUtil.error("执行失败!");
        }
    }

    /**
     * 重启设备
     *
     * @param deviceCode
     * @param command
     * @return
     */
    @RequestMapping("restartDevice")
    public ResultUtil restartDevice(@RequestParam("deviceCode") String deviceCode,
                                    @RequestParam("command") String command) {
        try {
            if (StringUtils.isBlank(deviceCode) || Objects.isNull(command)) {
                return ResultUtil.error("参数错误!");
            }
            String cmc = "2A06" + command;
            //将十六进制字符串转数组
            byte[] bytes = HexUtils.hexStringToBytes(cmc);
            String crc = Crc16Util.getCRC(bytes);
            System.out.println("CRC:" + crc);
            String s = HexUtils.HighAndLowSwap(crc);
            String cmd = cmc + s + "23";
            System.out.println("重启设备指令:" + cmd);
            Map map = new HashMap();
            Map data = new HashMap();
            data.put("messageType", 1);
            map.put("deviceCode", deviceCode);
            map.put("type", 1);
            map.put("commandContent", cmd);
            map.put("commandRemarks", "重启设备:" + deviceCode);
            data.put("messageData", map);
            WebSocketServer.sendMsg(deviceCode, JSONObject.toJSONString(data));

            mqttService.sendMqttMessage("/down/" + deviceCode, 1, HexUtils.hexStringToBytes(cmd));
            return ResultUtil.success("执行成功!");
        } catch (Exception e) {
            log.error("执行错误: " + e.getMessage());
            return ResultUtil.error("执行失败!");
        }
    }

}
