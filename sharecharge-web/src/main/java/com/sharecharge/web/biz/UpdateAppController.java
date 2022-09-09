package com.sharecharge.web.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sharecharge.biz.entity.Device;
import com.sharecharge.biz.entity.UpdateApp;
import com.sharecharge.biz.service.DeviceService;
import com.sharecharge.biz.service.UpdateAppService;
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.core.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("sys/updateApp")
@RequiredArgsConstructor
public class UpdateAppController {

    private final UpdateAppService updateAppService;
    private final DeviceService deviceService;

    /**
     * 查询升级软件列表
     * @param page
     * @param limit
     * @param fileName
     * @return
     */
    @RequestMapping("findUpdateAppList")
    @PreAuthorize("@ps.hasPermission(':sys:updateApp:findUpdateAppList')")
    public ResultUtil findUpdateAppList(@RequestParam("page") Integer page,//当前页
                                        @RequestParam("limit") Integer limit, String fileName){
        try{
            Page<UpdateApp> updateAppPage=new Page<>();
            updateAppPage.setCurrent(page);
            updateAppPage.setSize(limit);
            IPage<UpdateApp> pageList = updateAppService.page(updateAppPage, new QueryWrapper<UpdateApp>().lambda().like(!StringUtils.isEmpty(fileName),UpdateApp::getFileName, fileName));
            ResultUtil resultUtil=new ResultUtil();
            resultUtil.setData(pageList.getRecords());
            resultUtil.setMsg("查询列表成功");
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            resultUtil.setCount(pageList.getTotal());
            return resultUtil;
        }catch (Exception e){
            return ResultUtil.error("查询列表失败");
        }
    }

    /**
     * 添加升级软件
     * @return
     */
    @RequestMapping("addUpdateApp")
    @PreAuthorize("@ps.hasPermission(':sys:updateApp:addUpdateApp')")
    public ResultUtil addUpdateApp(UpdateApp updateApp){
        try{
            updateAppService.save(updateApp);
            return ResultUtil.success("添加成功");
        }catch (Exception e){
            return ResultUtil.error("系统异常");
        }
    }

    /**
     * 更新软件升级列表
     * @return
     */
    @RequestMapping("editUpdateApp")
    @PreAuthorize("@ps.hasPermission(':sys:updateApp:editUpdateApp')")
    public ResultUtil editUpdateApp(UpdateApp updateApp){
        try{
            updateAppService.updateById(updateApp);
            return ResultUtil.error("更新升级列表成功");
        }catch (Exception e){
            return ResultUtil.error("系统异常");
        }
    }

    /**
     * 查找设备号
     * @param deviceCode
     * @return
     */
    @RequestMapping("findDeviceCode")
    public ResultUtil findDeviceCode(@RequestParam("deviceCode") String deviceCode) {
        return ResultUtil.success(deviceService.list(new QueryWrapper<Device>().lambda().like(Device::getDeviceCode,deviceCode)));
    }

    @RequestMapping("deleteUpdateAppFile")
    @PreAuthorize("@ps.hasPermission(':updateApp:deleteupdateAppFile')")
    public ResultUtil deleteUpdateAppFile(@RequestParam("id")Integer id) {
        try{
//            UpdateApp updateApp=new UpdateApp();
//            updateApp.setId(id);
//            updateApp.setIsDelete(1);
            updateAppService.removeById(id);
            return ResultUtil.success("删除成功");
        }catch (Exception e){
            return ResultUtil.error("删除失败");
        }
    }
}
