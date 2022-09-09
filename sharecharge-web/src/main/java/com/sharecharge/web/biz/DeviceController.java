package com.sharecharge.web.biz;

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
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.core.util.http.SendRequest;
import com.sharecharge.security.util.SecurityUtil;
import com.sharecharge.system.entity.DbAdminUser;
import com.sharecharge.system.service.DbAdminUserService;
import com.sharecharge.system.service.DbParentOrSonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;


@Slf4j
@RestController
@RequestMapping("sys/device")
@RequiredArgsConstructor
public class DeviceController {
    final DeviceService deviceService;
    final DeviceTypeService deviceTypeService;
    final DbAdminUserService adminUserService;
    final NetworkDotService networkDotService;
    final DbParentOrSonService parentOrSonService;

    @RequestMapping("findDeviceInfoList")
    @PreAuthorize("@ps.hasPermission('sys:device:findDeviceInfoList')")
    public ResultUtil list(@RequestParam("page") Integer page,
                           @RequestParam("limit") Integer limit,
                           @RequestParam("allocationStatus") Integer allocationStatus, Integer deviceChargePattern,
                           String deviceCode, String networkAddress, Integer deviceStatus, Integer dealerId){
        return deviceService.list(page,limit,allocationStatus,deviceChargePattern,deviceCode,networkAddress,deviceStatus,dealerId);
    }

    @RequestMapping("findDeviceInfoById")
    @PreAuthorize("@ps.hasPermission(':sys:device:findDeviceInfoById')")
    public ResultUtil findDeviceInfoById(@RequestParam("deviceId") Integer id) {
        return ResultUtil.success(deviceService.getById(id));
    }


    @RequestMapping("deleteDevice")
    @PreAuthorize("@ps.hasPermission(':device:deviceList:oneDelete')")
    public ResultUtil deleteDeviceInfo(Integer id) {
        try {
//            Device device=new Device();
//            device.setIsDelete(1);
//            device.setId(id);
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setMsg("删除设备成功");
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            resultUtil.setData(deviceService.removeById(id));
            return resultUtil;
        } catch (Exception e) {
            log.error("删除设备失败", e.getMessage());
            return ResultUtil.error("删除设备失败");
        }
    }

    @RequestMapping("addDevice")
    @PreAuthorize("@ps.hasPermission(':device:deviceList:oneAdd')")
    public ResultUtil addDevice(Device device) {
        try {
            Device one = deviceService.getOne(new QueryWrapper<Device>().lambda().eq(Device::getDeviceCode, device.getDeviceCode()));
            if (!Objects.isNull(one)){
                return ResultUtil.error("设备已录入");
            }
            deviceService.save(device);
            return ResultUtil.success("添加成功");
        } catch (Exception e) {
            log.error("系统异常", e.getMessage());
            return ResultUtil.error("系统异常");
        }
    }
    @RequestMapping("findDeviceType")
    public ResultUtil findDeviceType() {
        try {
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setMsg("查询设备类型成功");
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            resultUtil.setData(deviceTypeService.list());
            return resultUtil;
        } catch (Exception e) {
            log.error("查询设备类型失败", e.getMessage());
            return ResultUtil.error("查询设备类型异常");
        }
    }

    @RequestMapping("updateDevice")
    @PreAuthorize("@ps.hasPermission(':device:deviceList:oneEdit')")
    public ResultUtil updateDevice(Device device) {
        try {
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setMsg("更新设备成功");
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            resultUtil.setData(deviceService.updateById(device));
            return resultUtil;
        } catch (Exception e) {
            log.error("更新设备失败", e.getMessage());
            return ResultUtil.error("更新设备失败");
        }
    }

    @RequestMapping("batchUpdateNetworkDot")
    @PreAuthorize("@ps.hasPermission(':device:deviceList:allDistribu')")
    public ResultUtil batchAllocationDevice(Integer[] ids, Integer networkDotId, Integer dealerId) {
        return deviceService.batchAllocationDevice(ids, networkDotId, dealerId);
    }
    @RequestMapping("deviceAllocation")
    @PreAuthorize("@ps.hasPermission(':device:deviceList:distribu')")
    public ResultUtil deviceAllocation(Integer deviceId, Integer networkDotId, Integer dealerId) {
        return deviceService.batchAllocationDevice(new Integer[]{deviceId}, networkDotId, dealerId);
    }
    @RequestMapping("findDealerList")
    public ResultUtil findDealerList() {
        try {
            List<Integer> sonByCurAdmin = parentOrSonService.getSonByCurAdmin(SecurityUtil.getUserId());
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setData(adminUserService.listByIds(sonByCurAdmin));
            resultUtil.setMsg("查询经销商成功");
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            return resultUtil;
        } catch (Exception e) {
            log.error("查询经销商失败：{}", e.getMessage());
            return ResultUtil.error("查询经销商失败");
        }
    }
    @RequestMapping("findNetworkList")
    public ResultUtil findNetworkList(Integer dealerId) {
        try {
            return ResultUtil.success(networkDotService.list(new QueryWrapper<NetworkDot>().lambda().eq(NetworkDot::getAdminId,dealerId)));
        } catch (Exception e) {
            log.error("查询对应网点信息失败");
            return ResultUtil.error("查询对应网点信息失败");
        }
    }


    /**
     * 添加收费方案
     *
     * @param devicePriceId
     * @param deviceCode
     * @return
     */
    @RequestMapping("addDevicePrice")
    @PreAuthorize("@ps.hasPermission(':device:deviceList:oneCharge')")
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
            return ResultUtil.success("设置收费方案成功");
        } catch (Exception e) {
            log.error("添加收费方案失败：" + e.getMessage());
            return ResultUtil.error("系统异常");
        }
    }
    /**
     * 批量添加设备收费方案
     *
     * @param deviceCodes
     * @param devicePriceId
     * @return
     */
    @RequestMapping("batchAddDevicePrice")
    @PreAuthorize("@ps.hasPermission(':device:deviceList:allCharge')")
    public ResultUtil batchAddDevicePrice(String[] deviceCodes, Integer devicePriceId, Integer devicePriceType) {
        return deviceService.batchAddDevicePrice(deviceCodes,devicePriceId,devicePriceType);
    }

    /**
     * 更新设备在线离线状态
     *
     * @return
     */
    @RequestMapping("updateDeviceStatus")
    @PreAuthorize("@ps.hasPermission(':sys:device:updateDeviceStatus')")
    public ResultUtil updateDeviceStatus() {
        return deviceService.updateDeviceStatus();
    }

    /**
     * 批量设备入库
     *
     * @param pullStatus
     * @param deviceId
     * @return
     */
    @RequestMapping("batchDevicePutState")
    @PreAuthorize("@ps.hasPermission(':sys:device:batchDevicePutState')")
    public ResultUtil batchDevicePutState(@RequestParam("deviceId") Integer[] deviceId,@RequestParam("pullStatus") Integer pullStatus) {
        return deviceService.batchDevicePutState(deviceId,pullStatus);
    }

    @RequestMapping("downLoadFindDeviceList")
    @PreAuthorize("@ps.hasPermission(':sys:device:downLoadFindDeviceList')")
    public ResultUtil downLoadFindDeviceList(@RequestParam("allocationStatus") Integer allocationStatus, Integer deviceChargePattern,
                           String deviceCode, String networkAddress, Integer deviceStatus, Integer dealerId) {
        return deviceService.list(1, 10000, allocationStatus, deviceChargePattern, deviceCode, networkAddress, deviceStatus, dealerId);
    }

    @RequestMapping("downLoadDeviceCodes")
    @PreAuthorize("@ps.hasPermission(':device:deviceList:allAdd')")
    public ResultUtil downLoadDeviceCodes(@RequestParam("number") Integer number,
                                          @RequestParam("deviceTypeId") Integer deviceTypeId) {
        return deviceService.downLoadDeviceCodes(number, deviceTypeId);
    }

    @RequestMapping("operationDevice")
    @PreAuthorize("@ps.hasPermission(':sys:device:operationDevice')")
    public ResultUtil operationDevice(@RequestParam("operationState") Integer operationState, @RequestParam("deviceId") Integer deviceId) {
        try {
            Device device=new Device();
            device.setId(deviceId);
            device.setOperationState(operationState);
            deviceService.updateById(device);
            return ResultUtil.success("更新设备成功");
        } catch (Exception e) {
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
            device.setActivateStatus(1);
            device.setAllocationStatus(pullStatus);
            device.setId(deviceId);
            deviceService.updateById(device);
            return ResultUtil.success("更新设备成功");
        }catch (Exception e){
            log.error("系统异常：{}",e.getMessage());
            return ResultUtil.error("更新失败");
        }
    }





    /**
     * 添加设备类型
     *
     * @param deviceType
     * @return
     */
    @RequestMapping("saveDeviceType")
    @PreAuthorize("@ps.hasPermission(':device:saveDeviceType')")
    public ResultUtil saveDeviceType(DeviceType deviceType) {
        try {
            deviceTypeService.save(deviceType);
            return ResultUtil.success("添加成功");
        } catch (Exception e) {
            return ResultUtil.error("添加失败");
        }
    }

    @RequestMapping("findDeviceTypeList")
    @PreAuthorize("@ps.hasPermission(':sys:device:findDeviceTypeList')")
    public ResultUtil findDeviceTypeList(@RequestParam("page") Integer page,
                                         @RequestParam("limit") Integer limit) {
        try {
            Page<DeviceType> deviceTypePage=new Page<>();
            deviceTypePage.setCurrent(page);
            deviceTypePage.setSize(limit);
            IPage<DeviceType> deviceTypeList = deviceTypeService.page(deviceTypePage);
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setData(deviceTypeList.getRecords());
            resultUtil.setCount(deviceTypeList.getTotal());
            resultUtil.setMsg("查询成功");
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            return resultUtil;
        } catch (Exception e) {
            return ResultUtil.error("查询失败");
        }
    }

    @RequestMapping("updateDeviceType")
    @PreAuthorize("@ps.hasPermission(':device:updateDeviceType')")
    public ResultUtil updateDeviceType(DeviceType deviceType) {
        try {
            deviceTypeService.updateById(deviceType);
            return ResultUtil.success("修改成功");
        } catch (Exception e) {
            return ResultUtil.error("编辑失败");
        }
    }

    @RequestMapping("deleteDeviceType")
    @PreAuthorize("@ps.hasPermission(':device:deleteDeviceType')")
    public ResultUtil deleteDeviceType(Integer deviceTypeId) {
        try {
            DeviceType deviceType = new DeviceType();
            deviceType.setDeviceTypeId(deviceTypeId);
            deviceType.setIsDelete(1);
            deviceTypeService.updateById(deviceType);
            return ResultUtil.success("修改成功");
        } catch (Exception e) {
            return ResultUtil.error("编辑失败");
        }
    }

}
