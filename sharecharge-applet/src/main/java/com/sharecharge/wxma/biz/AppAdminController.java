package com.sharecharge.wxma.biz;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sharecharge.biz.entity.Device;
import com.sharecharge.biz.entity.NetworkDot;
import com.sharecharge.biz.entity.WithdrawCashRecord;
import com.sharecharge.biz.service.DeviceService;
import com.sharecharge.biz.service.NetworkDotService;
import com.sharecharge.biz.service.WithdrawCashRecordService;
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.core.util.StringUtils;
import com.sharecharge.security.util.SecurityUtil;
import com.sharecharge.system.entity.DbAdminUser;
import com.sharecharge.system.entity.DbRole;
import com.sharecharge.system.service.DbAdminUserService;
import com.sharecharge.system.service.DbParentOrSonService;
import com.sharecharge.system.service.DbRoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.management.relation.Role;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;


@Slf4j
@Controller
@RequestMapping("app/adminUser")
public class AppAdminController {
    @Autowired
    private DbAdminUserService adminUserService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private NetworkDotService networkDotService;
    @Autowired
    private DbParentOrSonService parentOrSonService;
    @Autowired
    private WithdrawCashRecordService withdrawCashRecordService;
    @Autowired
    private DbRoleService roleService;


    /**
     * 绑定代理商 代理商登录
     * @param openId
     * @param phoneNumber
     * @param passWord
     * @return
     */
    @RequestMapping("bindAdminUser")
    @ResponseBody
    public ResultUtil bindAdminUser(@RequestParam(value = "openId") String openId,
                                    @RequestParam("phoneNumber") String phoneNumber,
                                    @RequestParam("passWord") String passWord) {
        if (StringUtils.isBlank(openId) || StringUtils.isBlank(phoneNumber) || StringUtils.isBlank(passWord)) {
            return ResultUtil.error("参数错误");
        }
        int count = adminUserService.count(new QueryWrapper<DbAdminUser>().lambda().eq(DbAdminUser::getOpenId, openId));
        if (count>1) {
            return ResultUtil.error("一个用户只能绑定一个账号");
        }

        DbAdminUser login = adminUserService.getOne(new QueryWrapper<DbAdminUser>().lambda().eq(DbAdminUser::getAdminName,phoneNumber));
                //.eq(DbAdminUser::getAdminPassword, SecurityUtil.encryptPassword(passWord)));

        if (!SecurityUtil.matchesPassword(passWord,login.getAdminPassword())) {
            return ResultUtil.error("账号密码错误");
        }

        login.setOpenId(openId);
        adminUserService.updateById(login);
        return ResultUtil.success(login);
    }

    /**
     * 查询我的代理商信息
     *
     * @param
     * @return
     */
    @RequestMapping("getAdminUser")
    @ResponseBody
    public ResultUtil getAdminUser(@RequestParam(value = "openId") String openId) {
        if (StringUtils.isBlank(openId)) {
            return ResultUtil.error("参数错误");
        }
        DbAdminUser adminUserByOpenId = adminUserService.getOne(new QueryWrapper<DbAdminUser>().lambda().eq(DbAdminUser::getOpenId,openId));
        if (Objects.isNull(adminUserByOpenId)) {
            return ResultUtil.error("该用户不是代理商");
        }
        return ResultUtil.success(adminUserByOpenId);
    }


    /**
     * 根据id 查询管理员用户返回用户对象
     *
     * @param adminUserId
     * @return
     */
    @RequestMapping("/findAdminUserById")
    @ResponseBody
    public ResultUtil findAdminUserById(@RequestParam("adminId") Integer adminUserId) {
        try {
            DbAdminUser adminUser = adminUserService.getById(adminUserId);
            return ResultUtil.success(adminUser);
        } catch (Exception e) {
            log.error("查询管理用户错误: " + e.getMessage());
            return ResultUtil.error("查询失败!");
        }
    }


    /**
     * 管理员解绑
     *
     * @param adminId
     * @return
     */
    @RequestMapping("unbindAdminUser")
    @ResponseBody
    public ResultUtil unbindAdminUser(@RequestParam("adminId") Integer adminId) {
        try {
            DbAdminUser adminUserById = adminUserService.getById(adminId);
            if (Objects.isNull(adminUserById)) {
                return ResultUtil.error("管理员用户不存在");
            }
            DbAdminUser adminUser = new DbAdminUser();
            adminUser.setOpenId("");
            adminUser.setId(adminId);
            adminUserService.updateById(adminUser);
            return ResultUtil.success("解绑成功");
        } catch (Exception e) {
            return ResultUtil.error("解绑失败");
        }
    }

    /**
     * 代理商网点
     *
     * @param
     * @return
     */
    @RequestMapping("findNetWordList")
    @ResponseBody
    public ResultUtil findNetWordList(@RequestParam("adminId") Integer adminId) {
        List<NetworkDot> list = networkDotService.list(new QueryWrapper<NetworkDot>().lambda().in(NetworkDot::getAdminId, parentOrSonService.getSonByCurAdmin(adminId)));
        return ResultUtil.success(list);
    }

    /**
     * 设备分配
     *
     * @param deviceId：设备id
     * @param adminId：经销商id
     * @param networkDotId：网点ID
     * @return // 设备ID  经销商ID    网点ID
     */
    @RequestMapping("deviceAllocation")
    @ResponseBody
    public ResultUtil deviceAllocation(Integer deviceId,
                                       String longitude,
                                       String latitude,
                                       Integer networkDotId,
                                       Integer adminId) {
        try {
            //分配状态 0：未生产 1：已安装 2：已入库  3：未安装
            Device deviceInfoById = deviceService.getById(deviceId);
            if (deviceInfoById.getAllocationStatus() == 1 ) {
                return ResultUtil.error("设备已安装");
            }
            if (deviceInfoById.getAllocationStatus() == 0){
                return ResultUtil.error("该设备未入库,请先进行入库操作");
            }
            DbAdminUser adminUserByOpenId = adminUserService.getById(adminId);
            if (adminUserByOpenId.getRoleId() == 5) {
                deviceInfoById.setThirdAgentId(adminId);
                DbAdminUser adminUserParent = adminUserService.getById(adminUserByOpenId.getParentId());
                if (adminUserParent.getRoleId() == 4) {
                    deviceInfoById.setSecondAgentId(adminUserParent.getId());
                    DbAdminUser adminUserParents = adminUserService.getById(adminUserParent.getParentId());
                    if (adminUserParents.getRoleId() == 3) {
                        deviceInfoById.setFirstAgentId(adminUserParents.getId());
                    }
                }
            } else if (adminUserByOpenId.getRoleId() == 4) {
                deviceInfoById.setSecondAgentId(adminId);
                DbAdminUser adminUserParents = adminUserService.getById(adminUserByOpenId.getParentId());
                if (adminUserParents.getRoleId() == 3) {
                    deviceInfoById.setSecondAgentId(adminUserParents.getId());
                }
            } else if (adminUserByOpenId.getRoleId() == 3) {
                deviceInfoById.setFirstAgentId(adminId);
            }
            deviceInfoById.setNetworkDotId(networkDotId);
            deviceInfoById.setAllocationStatus(1);
            deviceInfoById.setLongitude(longitude);
            deviceInfoById.setLatitude(latitude);
            deviceService.updateById(deviceInfoById);
            return ResultUtil.success("设备分配成功");
        } catch (Exception e) {
            log.error("设备分配失败", e.getMessage());
            return ResultUtil.error("设备分配失败");
        }
    }

    /**
     * 设备入库操作
     *
     * @param deviceCode
     * @return
     */
    @RequestMapping("devicePut")
    @ResponseBody
    public ResultUtil devicePut(@RequestParam("deviceCode") String deviceCode) {
        try {
            Device deviceInfoByDeviceCode = deviceService.getOne(new QueryWrapper<Device>().lambda().eq(Device::getDeviceCode,deviceCode));

            if (Objects.isNull(deviceInfoByDeviceCode)) {
                return ResultUtil.error("该设备不存在");
            }
            if (deviceInfoByDeviceCode.getAllocationStatus() != 0) {
                return ResultUtil.error("设备已入库,请更换设备");
            }
            deviceInfoByDeviceCode.setAllocationStatus(2);
            deviceService.updateById(deviceInfoByDeviceCode);
            return ResultUtil.success("设备入库成功");
        } catch (Exception e) {
            log.error("设备出入库失败");
            return ResultUtil.error("系统异常");
        }
    }


    /**
     * 微信提现
     *
     * @param money
     * @return
     */
    @RequestMapping("/addWithdraw")
    @ResponseBody
    public ResultUtil addWithdraw(@RequestParam Integer money, @RequestParam Integer adminId) {
        try {
            DbAdminUser adminUser = adminUserService.getById(adminId);
            if (Objects.isNull(adminUser)){
                return ResultUtil.error("请先登录");
            }
            if (money <= 0) {
                return ResultUtil.error("提现金额不正确");
            }
            if (adminUser.getFreezeStatus()==1) {
                return ResultUtil.error("账户异常被冻结,请联系客服");
            }
            int a = adminUser.getBalanceAmount().compareTo(BigDecimal.valueOf(money));
            if (a < 0) {
                return ResultUtil.error("提现金额不能大于余额");
            }

            WithdrawCashRecord withdrawCashRecord = new WithdrawCashRecord();
            Timestamp timestamp = new Timestamp(new Date().getTime());
            withdrawCashRecord.setCreateTime(timestamp);
            withdrawCashRecord.setMoney(BigDecimal.valueOf(money));
            withdrawCashRecord.setPayAdminId(2);
            String withDrawCode = "T" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + RandomStringUtils.randomNumeric(9);
            withdrawCashRecord.setWithdrawCode(withDrawCode);
            withdrawCashRecord.setAdminId(adminUser.getId());
            withdrawCashRecord.setStatus(1);
            withdrawCashRecord.setPayTime(timestamp);
            withdrawCashRecord.setPayType(1);
            return withdrawCashRecordService.Withdraw(withdrawCashRecord, adminUser);
        } catch (Exception e) {
            return ResultUtil.error("微信零钱转账错误!" + e.getMessage());
        }
    }



    /**
     * 查询运营商列表
     *
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping("findAdminUserList")
    @ResponseBody
    public ResultUtil findAdminUserList(@RequestParam("page") Integer page,
                                        @RequestParam("limit") Integer limit,
                                        @RequestParam("adminId")Integer adminId,
                                        DbAdminUser adminUser) {
        if (Objects.isNull(adminId)){
            return ResultUtil.error("请先登录");
        }
        Map map = new HashMap();
        map.put("curPageStarRow", (page - 1) * limit);
        map.put("limit", limit);
        map.put("adminPhone", adminUser.getAdminPhone());
        map.put("adminName", adminUser.getAdminName());
        map.put("roleId", adminUser.getRoleId());
        map.put("ids", parentOrSonService.getSonByCurAdmin(adminId));
        return adminUserService.list(map);
    }


    /**
     * 添加管理用户
     *
     * @return
     */
    @RequestMapping("/addAdminUser")
    @ResponseBody
    public ResultUtil addAdminUser(@RequestParam("adminId") Integer adminId,DbAdminUser adminUser) {
        try {
            DbAdminUser dbAdminUser = adminUserService.getById(adminId);
            if (Objects.isNull(dbAdminUser)){
                return ResultUtil.error("请先登录");
            }
            if (adminUser.getInterestRate() > dbAdminUser.getInterestRate()) {
                return ResultUtil.error("分层利率不能大于设定的利率上限！");
            }
            DbAdminUser one = adminUserService.getOne(new QueryWrapper<DbAdminUser>().lambda().eq(DbAdminUser::getAdminName, adminUser.getAdminName()));
            if (!Objects.isNull(one)){
                return ResultUtil.error("用户名已存在");
            }
            adminUser.setCreateTime(new Date());
            adminUser.setAdminPassword(SecurityUtil.encryptPassword("888888"));
            adminUser.setParentId(SecurityUtil.getUserId());
            adminUserService.save(adminUser);
            return ResultUtil.success("添加成功");
        } catch (Exception e) {
            log.error("添加管理用户错误: " + e);
            return ResultUtil.error("添加失败!");
        }
    }


    /**
     * 更新用户
     * @param dbAdminUser
     * @return
     */
    @RequestMapping("/updateAdminUser")
    @ResponseBody
    public ResultUtil updateAdminUser(DbAdminUser dbAdminUser) {
        try {
            DbAdminUser adminUser = (DbAdminUser) SecurityUtil.getUserInfo().getAdminUser();
            if (adminUser.getId() == adminUser.getId()) {
                return ResultUtil.error("更新失败,不可编辑自己!");
            }
            if (adminUser.getInterestRate() > adminUser.getInterestRate()) {
                return ResultUtil.error("分层利率不能大于设定的利率上限！");
            }
            dbAdminUser.setUpdateTime(new Date());
            adminUserService.updateById(dbAdminUser);
            return ResultUtil.success("更新成功");
        } catch (Exception e) {

            log.error("更新管理用户错误: " + e);
            return ResultUtil.error("更新失败!");
        }
    }

    /**
     * 删除管理员用户
     *
     * @param adminUserId
     * @return
     */
    @RequestMapping("/deleteAdminUser")
    @ResponseBody
    public ResultUtil deleteAdminUser(Integer adminUserId) {
        try {
            if (SecurityUtil.getUserId() == adminUserId) {
                return ResultUtil.error("删除失败,不可删除自己自己!");
            }
//            DbAdminUser adminUser = new DbAdminUser();
//            adminUser.setId(adminUserId);
//            adminUser.setDeleteStatus(1);
//            adminUserService.updateById(adminUser);
            adminUserService.removeById(adminUserId);
            return ResultUtil.success("删除成功!");
        } catch (Exception e) {
            log.error("删除管理用户错误: " + e);
            return ResultUtil.error("删除失败!");
        }
    }

    /**
     * 查询角色列表
     *
     * @param adminId
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping("/findRoleList")
    @ResponseBody
    public ResultUtil findRoleList(@RequestParam("adminId") Integer adminId,
                                   @RequestParam("page") Integer page,
                                   @RequestParam("limit") Integer limit) {
        try {
            //获取登录用户
            DbAdminUser adminUser = adminUserService.getById(adminId);
            if (Objects.isNull(adminUser)){
                return ResultUtil.error("请先登录");
            }
            Page<DbRole> rolePage=new Page<>();
            rolePage.setCurrent(page);
            rolePage.setSize(limit);
            IPage<DbRole> page1 = roleService.page(rolePage, new QueryWrapper<DbRole>().lambda().ge(DbRole::getId, adminUser.getRoleId()));
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            resultUtil.setMsg("查询成功!");
            resultUtil.setCount(page1.getTotal());
            resultUtil.setData(page1.getRecords());
            return resultUtil;
        } catch (Exception e) {
            log.error("添加角色错误: " + e.getMessage());
            return ResultUtil.error("查询失败!");
        }
    }


}
