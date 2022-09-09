package com.sharecharge.web.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.security.util.SecurityUtil;
import com.sharecharge.system.entity.DbRole;
import com.sharecharge.system.entity.DbRoleMenu;
import com.sharecharge.system.service.DbRoleMenuService;
import com.sharecharge.system.service.DbRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("sys/role/")
@RequiredArgsConstructor
public class DbRoleController {
    private final DbRoleService roleService;
    private final DbRoleMenuService roleMenuService;

    /**
     * 添加角色
     *
     * @param
     * @return
     */
    @RequestMapping("addRole")
    @ResponseBody
    @PreAuthorize("@ps.hasPermission(':permission:role:add')")
    public ResultUtil addRole(@RequestParam("roleName") String roleName,
                              @RequestParam("roleRemark") String roleRemark,
                              @RequestParam("menuIdArray") String menuIdArray) {

        try {
            DbRole role=new DbRole();
            role.setRoleName(roleName);
            role.setRemark(roleRemark);
            List<DbRole> list = roleService.list(new QueryWrapper<DbRole>().lambda().eq(DbRole::getRoleName, roleName));
            if (list != null && list.size() > 0) {
                return ResultUtil.error("添加失败,名称重复!");
            }
            roleService.save(role);
            list = roleService.list(new QueryWrapper<DbRole>().lambda().eq(DbRole::getRoleName, roleName));
            //添加菜单角色
            String[] idList = menuIdArray.split(",");
            for (Object object : idList) {
                DbRoleMenu roleMenu = new DbRoleMenu();
                roleMenu.setMenuId(Integer.valueOf(object.toString()));
                roleMenu.setRoleId(list.get(0).getId());
                roleMenuService.save(roleMenu);
            }
            return ResultUtil.success("添加成功!");
        } catch (Exception e) {
            log.error("添加角色错误: " + e.getMessage());
            return ResultUtil.error("添加失败!");
        }
    }


    /**
     * 根据id  查询角色
     *
     * @param roleId
     * @return
     */
    @RequestMapping("findRoleById")
    @ResponseBody
    public ResultUtil findRoleById( Integer roleId) {
        try {
            DbRole role = roleService.getById(roleId);
            return ResultUtil.success(role);
        } catch (Exception e) {
            log.error("根据id  查询角色错误: " + e.getMessage());
            return ResultUtil.error("根据id查询角色失败!");
        }
    }


    /**
     * 更新角色
     *
     * @param
     * @return
     */
    @RequestMapping("updateRole")
    @ResponseBody
    @PreAuthorize("@ps.hasPermission(':permission:role:edit')")
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil updateRole(@RequestParam("menuIdArray") String menuIdArray,
                                 @RequestParam("myRoleId") Integer myRoleId,
                                 @RequestParam("roleName") String roleName,
                                 @RequestParam("roleRemark") String roleRemark) {
        try {

            if (myRoleId.equals(SecurityUtil.getUserInfo().getId().toString())) {
                return ResultUtil.error("修改错误, 不能修改自己!!");
            }
            //  删除菜单角色
            roleMenuService.remove(new QueryWrapper<DbRoleMenu>().eq("role_id",myRoleId));
            //封装数据
            DbRole roleMy = new DbRole();
            roleMy.setId(myRoleId);
            roleMy.setRoleName(roleName);
            //判断名称是否重复
            List<DbRole> list = roleService.list(new QueryWrapper<DbRole>().setEntity(roleMy));
            if (list != null && list.size() > 0) {
                return ResultUtil.error("修改失败,名称重复!");
            }
            roleMy.setRemark(roleRemark);
            roleMy.setCreateTime(new Date());
            roleService.updateById(roleMy);

            //添加菜单角色
            String[] idList = menuIdArray.split(",");
            for (Object object : idList) {
                DbRoleMenu roleMenu = new DbRoleMenu();
                roleMenu.setMenuId(Integer.valueOf(object.toString()));
                roleMenu.setRoleId(myRoleId);
                roleMenuService.save(roleMenu);
            }
            return ResultUtil.success("修改成功!");
        } catch (Exception e) {
            log.error("修改角色错误: " + e.getMessage());
            return ResultUtil.error("修改失败!");
        }
    }

    /**
     * 删除角色
     *
     * @param roleId
     * @return
     */
    @RequestMapping("deleteRole")
    @ResponseBody
    @PreAuthorize("@ps.hasPermission(':permission:role:delete')")
    @Transactional(rollbackFor = Exception.class)
    public ResultUtil deleteRole(Integer roleId) {
        try {
//            DbRole role = new DbRole();
//            role.setId(roleId);
//            role.setDeleteStatus(1);
            roleService.removeById(roleId);
            roleMenuService.remove(new QueryWrapper<DbRoleMenu>().lambda().eq(DbRoleMenu::getRoleId,roleId));
            return ResultUtil.success("删除成功!");
        } catch (Exception e) {
            log.error("删除角色错误: " + e.getMessage());
            return ResultUtil.error("删除失败!");
        }
    }


    /**
     * 分页查询角色列表
     *
     * @param
     * @return
     */
    @RequestMapping("findRoleList")
    @ResponseBody
    @PreAuthorize("@ps.hasPermission(':sys:role:findRoleList')")
    public ResultUtil findRoleList(@RequestParam("page") Integer page,
                                   @RequestParam("limit") Integer limit) {
        try {
            Page<DbRole> rolePage=new Page<>();
            rolePage.setCurrent(page);
            rolePage.setSize(limit);
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            resultUtil.setMsg("查询成功!");
            resultUtil.setCount(roleService.page(rolePage).getTotal());
            resultUtil.setData(roleService.page(rolePage).getRecords());
            return resultUtil;
        } catch (Exception e) {
            log.error("添加角色错误: " + e.getMessage());
            return ResultUtil.error("查询失败!");
        }
    }

    /**
     * 查询所有角色列表
     *
     * @return
     */
    @RequestMapping("findRoleAllList")
    @ResponseBody
    public ResultUtil findRoleAllList() {
        try {
            List<DbRole> list = roleService.list();
            return ResultUtil.success(list);
        } catch (Exception e) {
            log.error("添加角色错误: " + e.getMessage());
            return ResultUtil.error("查询失败!");
        }
    }

}
