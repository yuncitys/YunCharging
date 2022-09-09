package com.sharecharge.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.system.entity.DbMenu;
import com.sharecharge.system.entity.DbRoleMenu;

import java.util.List;

public interface DbMenuService extends IService<DbMenu> {

    /**
     * 通过角色id 得到所有的菜单
     * @param roleId
     * @return
     */
    List<DbMenu> findMenuByRoleId(Integer roleId);

}
