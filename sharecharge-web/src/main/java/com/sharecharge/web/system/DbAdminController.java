package com.sharecharge.web.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.security.util.SecurityUtil;
import com.sharecharge.system.entity.DbAdminUser;
import com.sharecharge.system.service.DbAdminUserService;
import com.sharecharge.system.service.DbParentOrSonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Slf4j
@Controller
@RequestMapping("sys/admin")
@RequiredArgsConstructor
public class DbAdminController {

    private final DbAdminUserService adminService;
    private final DbParentOrSonService parentOrSonService;

    /**
     * 查询运营商列表
     *
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping("findAdminUserList")
    @ResponseBody
    @PreAuthorize("@ps.hasPermission(':sys:admin:findAdminUserList')")
    public ResultUtil findAdminUserList(@RequestParam("page") Integer page,
                                        @RequestParam("limit") Integer limit,
                                        DbAdminUser adminUser) {
        Map map = new HashMap();
        map.put("curPageStarRow", (page - 1) * limit);
        map.put("limit", limit);
        map.put("adminPhone", adminUser.getAdminPhone());
        map.put("adminName", adminUser.getAdminName());
        map.put("roleId", adminUser.getRoleId());
        map.put("ids", parentOrSonService.getSonByCurAdmin(SecurityUtil.getUserId()));
        return adminService.list(map);

    }


    /**
     * 添加管理用户
     *
     * @return
     */
    @RequestMapping("/addAdminUser")
    @ResponseBody
    @PreAuthorize("@ps.hasPermission(':sys:admin:addAdminUser')")
    public ResultUtil addAdminUser(DbAdminUser adminUser) {
        try {
            DbAdminUser dbAdminUser = (DbAdminUser) SecurityUtil.getUserInfo().getAdminUser();
            if (adminUser.getInterestRate() > dbAdminUser.getInterestRate()) {
                return ResultUtil.error("分层利率不能大于设定的利率上限！");
            }
            DbAdminUser one = adminService.getOne(new QueryWrapper<DbAdminUser>().lambda().eq(DbAdminUser::getAdminName, adminUser.getAdminName()));
            if (!Objects.isNull(one)){
                return ResultUtil.error("用户名已存在");
            }
            adminUser.setCreateTime(new Date());
            adminUser.setAdminPassword(SecurityUtil.encryptPassword("888888"));
            adminUser.setParentId(SecurityUtil.getUserId());
            adminService.save(adminUser);
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
    @PreAuthorize("@ps.hasPermission(':agent:agentList:edit')")
    public ResultUtil updateAdminUser(DbAdminUser dbAdminUser) {
        try {
            DbAdminUser adminUser = (DbAdminUser) SecurityUtil.getUserInfo().getAdminUser();
            if (adminUser.getId() == dbAdminUser.getId()) {
                return ResultUtil.error("更新失败,不可编辑自己!");
            }
            if (dbAdminUser.getInterestRate() > adminUser.getInterestRate()) {
                return ResultUtil.error("分层利率不能大于设定的利率上限！");
            }
            dbAdminUser.setUpdateTime(new Date());
            adminService.updateById(dbAdminUser);
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
    @PreAuthorize("@ps.hasPermission(':agent:agentList:delete')")
    public ResultUtil deleteAdminUser(Integer adminUserId) {
        try {
            if (SecurityUtil.getUserId() == adminUserId) {
                return ResultUtil.error("删除失败,不可删除自己自己!");
            }
//            DbAdminUser adminUser = new DbAdminUser();
//            adminUser.setId(adminUserId);
//            adminUser.setDeleteStatus(1);
            adminService.removeById(adminUserId);
            return ResultUtil.success("删除成功!");
        } catch (Exception e) {
            log.error("删除管理用户错误: " + e);
            return ResultUtil.error("删除失败!");
        }
    }

    /**
     * 初始化密码
     *
     * @param adminUserId
     * @return
     */
    @RequestMapping("/editPasswordAdminUser")
    @ResponseBody
    @PreAuthorize("@ps.hasPermission(':sys:admin:editPasswordAdminUser')")
    public ResultUtil editPasswordAdminUser(Integer adminUserId) {
        try {
            DbAdminUser adminUser = adminService.getById(adminUserId);
            adminUser.setAdminPassword(SecurityUtil.encryptPassword( "888888"));
            adminService.updateById(adminUser);
            return ResultUtil.success("初始化密码成功!");
        } catch (Exception e) {
            log.error("初始化密码错误: " + e);
            return ResultUtil.error("初始化密码失败!");
        }
    }


    /**
     * 根据id 查询管理员用户返回用户对象
     *
     * @return
     */
    @RequestMapping("/findAdminUserById")
    @ResponseBody
    public ResultUtil findAdminUserById(Integer adminUserId) {

        try {
            DbAdminUser adminUser = adminService.getById(adminUserId);
            return ResultUtil.success(adminUser);
        } catch (Exception e) {
            log.error("查询管理用户错误: " + e.getMessage());
            return ResultUtil.error("查询失败!");
        }
    }

    /**
     * 查询下级列表
     *
     * @param page
     * @param limit
     * @param
     * @param
     * @return
     */
    @RequestMapping("findAdminUserSonList")
    @ResponseBody
    @PreAuthorize("@ps.hasPermission(':sys:admin:findAdminUserSonList')")
    public ResultUtil findAdminUserSonList(@RequestParam("page") Integer page,
                                           @RequestParam("limit") Integer limit,
                                           @RequestParam("parentId") Integer parentId) {
        try {
            Page<DbAdminUser> adminUserPage=new Page<>();
            adminUserPage.setCurrent(page);
            adminUserPage.setSize(limit);
            IPage<DbAdminUser> adminUserList = adminService.page(adminUserPage, new QueryWrapper<DbAdminUser>().lambda().eq(DbAdminUser::getParentId, parentId));
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setMsg("查询代理商列表成功");
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            resultUtil.setData(adminUserList.getRecords());
            resultUtil.setCount(adminUserList.getTotal());
            return resultUtil;
        } catch (Exception e) {
            return ResultUtil.error("系统异常");
        }

    }

    /**
     * 冻结账户
     *
     * @param adminId
     * @param freezeStatus 0未冻结 1已冻结
     * @return
     */
    @RequestMapping("freezeAdminUser")
    @ResponseBody
    @PreAuthorize("@ps.hasPermission(':sys:admin:freezeAdminUser')")
    public ResultUtil freezeAdminUser(@RequestParam("adminId") Integer adminId,
                                      @RequestParam("freezeStatus") Integer freezeStatus) {
        try {
            DbAdminUser dbAdminUser = new DbAdminUser();
            dbAdminUser.setUpdateTime(new Date());
            dbAdminUser.setFreezeStatus(freezeStatus);
            dbAdminUser.setId(adminId);
            adminService.updateById(dbAdminUser);
            return ResultUtil.success("冻结成功");
        } catch (Exception e) {
            return ResultUtil.error("冻结失败");
        }
    }

    /**
     * 更新密码
     * @param id
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @RequestMapping("/updatePasswordAdminUser")
    @ResponseBody
    @PreAuthorize("@ps.hasPermission(':sys:admin:updatePasswordAdminUser')")
    public ResultUtil updatePasswordAdminUser(@RequestParam("id") Integer id,
                                              @RequestParam("oldPassword")String oldPassword,
                                              @RequestParam("newPassword")String newPassword) {
        return adminService.updatePasswordAdminUser(id, oldPassword, newPassword);
    }

}
