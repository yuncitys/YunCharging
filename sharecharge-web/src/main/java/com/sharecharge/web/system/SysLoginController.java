package com.sharecharge.web.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.security.model.LoginUser;
import com.sharecharge.security.service.SysLoginService;
import com.sharecharge.security.util.SecurityUtil;
import com.sharecharge.system.entity.DbAdminUser;
import com.sharecharge.system.entity.DbMenu;
import com.sharecharge.system.service.DbAdminUserService;
import com.sharecharge.system.service.DbMenuService;
import com.sharecharge.web.util.MenuManageTreeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/sys/admin")
@RequiredArgsConstructor
public class SysLoginController {
    private final SysLoginService sysLoginService;
    private final DbAdminUserService dbAdminUserService;
    private final DbMenuService menuService;

    @RequestMapping("/login")
    public ResultUtil login(String userName, String passWord) {
        try {
            Map loginInfo = sysLoginService.login(userName, passWord);
            DbAdminUser adminUser = dbAdminUserService.getOne(new QueryWrapper<DbAdminUser>().eq("admin_name", userName));
            loginInfo.put("adminUser",adminUser);
            return ResultUtil.success(loginInfo);
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                log.error("用户密码错误：{}",e.getMessage());
                return ResultUtil.error("用户密码错误");
            } else {
                log.error("系统异常：{}",e.getMessage());
                return ResultUtil.error("系统异常");
            }

        }
    }

    /**
     * 得到用户的所有信息
     * @return
     */
    @GetMapping("/findAdminUserInfo")
    public ResultUtil findAdminUserInfo() {
        try {
            LoginUser userInfo = SecurityUtil.getUserInfo();
            List<DbMenu> list = menuService.findMenuByRoleId(userInfo.getRoleId());
            Map map = new HashMap();
            map.put("adminUser", userInfo.getAdminUser());
            map.put("meunList", MenuManageTreeUtil.getMenuManageTree(list));
            map.put("authentionList", list);
            return ResultUtil.success(map);
        } catch (Exception e) {
            log.error("得到用户信息异常:" + e);
            return ResultUtil.error("得到用户信息异常!");
        }
    }
}
