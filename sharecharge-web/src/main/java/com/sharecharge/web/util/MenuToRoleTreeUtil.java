package com.sharecharge.web.util;


import com.sharecharge.system.entity.DbMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuToRoleTreeUtil {
    public static List<MenuToRoleTree> getMenuToRoleTree(List<DbMenu> menuList, List<DbMenu> menuListToChecked) {
        List<MenuToRoleTree> menuToRoleTreeList = new ArrayList<>();

        for (int i = 0; i < menuList.size(); i++) {
            MenuToRoleTree menuToRoleTree = new MenuToRoleTree();
            DbMenu menu = menuList.get(i);

            //判断是否存在父级, 如果没有, 则表示是一级菜单
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                //判断是否是一级菜单
                if (menu.getHref() != null && !"".equals(menu.getHref())) {
                    menuToRoleTree.setId(menu.getId().toString());
                    menuToRoleTree.setTitle(menu.getTitle());
                    menuToRoleTree.setChecked(isChecked(menu.getId(), menuListToChecked));
                } else {
                    menuToRoleTree.setId(menu.getId().toString());
                    menuToRoleTree.setTitle(menu.getTitle());
                    menuToRoleTree.setExpand(true);
                    menuToRoleTree.setChildren(getgetMenuToRoleTreeChild(menu.getId(), menuList, menuListToChecked));
                }
                menuToRoleTreeList.add(menuToRoleTree);
            }

        }
        return menuToRoleTreeList;
    }

    //得到二级子级
    private static List<MenuToRoleTree> getgetMenuToRoleTreeChild(Integer id, List<DbMenu> menuList, List<DbMenu> menuListToChecked) {
        List<MenuToRoleTree> menuToRoleTreeList = new ArrayList<>();
        for (DbMenu menu : menuList) {
            if (menu.getParentId().equals(id)) {
                MenuToRoleTree menuToRoleTree = new MenuToRoleTree();
                menuToRoleTree.setExpand(true);
                menuToRoleTree.setId(menu.getId().toString());
                menuToRoleTree.setTitle(menu.getTitle());
                menuToRoleTree.setChildren(getgetMenuToRoleTreeChildTWO(menu.getId(), menuList, menuListToChecked));
                if (menuToRoleTree.getChildren() == null || menuToRoleTree.getChildren().size() == 0) {
                    menuToRoleTree.setChecked(isChecked(menu.getId(), menuListToChecked));
                }
                menuToRoleTreeList.add(menuToRoleTree);
            }
        }
        return menuToRoleTreeList;

    }

    //得到三级子级
    private static List<MenuToRoleTree> getgetMenuToRoleTreeChildTWO(Integer id, List<DbMenu> menuList, List<DbMenu> menuListToChecked) {
        List<MenuToRoleTree> menuToRoleTreeList = new ArrayList<>();
        for (DbMenu menu : menuList) {
            if (menu.getParentId().equals(id)) {
                MenuToRoleTree menuToRoleTree = new MenuToRoleTree();
                menuToRoleTree.setChecked(isChecked(menu.getId(), menuListToChecked));
                menuToRoleTree.setId(menu.getId().toString());
                menuToRoleTree.setTitle(menu.getTitle());
                menuToRoleTreeList.add(menuToRoleTree);
            }
        }
        return menuToRoleTreeList;

    }

    //判断是否选中了
    private static Boolean isChecked(Integer id, List<DbMenu> menuListToChecked) {
        for (DbMenu menu : menuListToChecked) {
            if (menu.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }


}
