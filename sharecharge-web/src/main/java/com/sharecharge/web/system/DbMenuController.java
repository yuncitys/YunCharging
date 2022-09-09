package com.sharecharge.web.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.security.util.SecurityUtil;
import com.sharecharge.system.entity.DbMenu;
import com.sharecharge.system.entity.DbRoleMenu;
import com.sharecharge.system.service.DbMenuService;
import com.sharecharge.system.service.DbRoleMenuService;
import com.sharecharge.web.util.MenuManageTree;
import com.sharecharge.web.util.MenuManageTreeUtil;
import com.sharecharge.web.util.MenuToRoleTree;
import com.sharecharge.web.util.MenuToRoleTreeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("sys/menu/")
@RequiredArgsConstructor
public class DbMenuController {

    final DbMenuService menuService;
    final DbRoleMenuService roleMenuService;

    /**
     * 添加菜单
     *
     * @param
     * @return
     */
    @RequestMapping("addMenu")
    @ResponseBody
    @PreAuthorize("@ps.hasPermission(':permission:menu:add')")
    public ResultUtil addMenu(DbMenu dbMenu) {
        try {
            //封装数据
            DbMenu menuMy = new DbMenu();
            menuMy.setParentId(dbMenu.getParentId());
            menuMy.setTitle(dbMenu.getTitle());
            QueryWrapper<DbMenu> queryWrapper=new QueryWrapper<>();
            queryWrapper.setEntity(menuMy);
            //判断名称是否重复
            List<DbMenu> list = menuService.list(queryWrapper);
            if (list != null && list.size() > 0) {
                return ResultUtil.error("添加失败,名称重复!");
            }
            //添加到数据库
            dbMenu.setCreateTime(new Date());
            menuService.save(dbMenu);
            list = menuService.list(queryWrapper);
            DbRoleMenu roleMenu = new DbRoleMenu();
            roleMenu.setRoleId(SecurityUtil.getUserInfo().getRoleId());
            roleMenu.setMenuId(list.get(0).getId());
            roleMenuService.save(roleMenu);
            return ResultUtil.success("添加成功!");
        } catch (Exception e) {
            log.error("添加菜单错误: " + e.getMessage());
            return ResultUtil.error("添加失败!");
        }
    }


    /**
     * 通过当前用户角色id 得到所有的菜单
     *
     * @return
     */
    @RequestMapping("findMenuByMyRoleId")
    @ResponseBody
    @PreAuthorize("@ps.hasPermission(':sys:menu:findMenuByMyRoleId')")
    public ResultUtil findMenuByMyRoleId() {
        try {
            List<DbMenu> menuList = menuService.findMenuByRoleId(SecurityUtil.getUserInfo().getRoleId());
            //封装数据
            List<MenuManageTree> menuManageTreeList = MenuManageTreeUtil.getMenuManageTree(menuList);
            return ResultUtil.success(menuManageTreeList);
        } catch (Exception e) {
            log.error("查询菜单树错误: " + e.getMessage());
            return ResultUtil.error("查询失败!");
        }
    }


    /**
     * 更新菜单
     *
     * @param
     * @return
     */
    @RequestMapping("updateMenu")
    @ResponseBody
    @PreAuthorize("@ps.hasPermission(':permission:menu:edit')")
    public ResultUtil updateMenu(DbMenu dbMenu) {
        try {
            //封装数据
            DbMenu menuMy = new DbMenu();
            menuMy.setId(dbMenu.getId());
            menuMy.setParentId(dbMenu.getParentId());
            menuMy.setTitle(dbMenu.getTitle());
            //判断名称是否重复
//            List<DbMenu> list = menuService.list(new QueryWrapper<DbMenu>().setEntity(menuMy));
//            if (list != null && list.size() > 0) {
//                return ResultUtil.error("更新失败,名称重复!");
//            }
            dbMenu.setUpdateTime(new Date());
            menuService.updateById(dbMenu);
            return ResultUtil.success("更新成功!");
        } catch (Exception e) {
            log.error("更新菜单错误: " + e.getMessage());
            return ResultUtil.error("更新失败!");
        }
    }

    /**
     * 删除菜单
     *
     * @param menuId
     * @return
     */
    @RequestMapping("deleteMenu")
    @ResponseBody
    @PreAuthorize("@ps.hasPermission(':permission:menu:delete')")
    public ResultUtil deleteMenu(Integer menuId) {
        try {
//            DbMenu menuMy = new DbMenu();
//            menuMy.setId(menuId);
//            menuMy.setDeleteStatus(1);
            menuService.removeById(menuId);
            return ResultUtil.success("删除成功!");
        } catch (Exception e) {
            log.error("删除菜单错误: " + e.getMessage());
            return ResultUtil.error("删除失败!");
        }
    }


    /**
     * 通过角色id 得到所有的菜单
     *
     * @param roleId
     * @return
     */
    @RequestMapping("findMenuByRoleId")
    @ResponseBody
    public ResultUtil findRoleMenuByRoleId(@RequestParam("roleId") Integer roleId) {
        try {

            List<DbMenu> menuList = menuService.findMenuByRoleId(SecurityUtil.getUserInfo().getRoleId());
            List<DbMenu> menuListToChecked = menuService.findMenuByRoleId(roleId);
            //封装数据
            List<MenuToRoleTree> menuToRoleTrees = MenuToRoleTreeUtil.getMenuToRoleTree(menuList, menuListToChecked);
            return ResultUtil.success(menuToRoleTrees);
        } catch (Exception e) {
            log.error("查询角色菜单树错误: " + e.getMessage());
            return ResultUtil.error("查询失败!");
        }
    }

}
