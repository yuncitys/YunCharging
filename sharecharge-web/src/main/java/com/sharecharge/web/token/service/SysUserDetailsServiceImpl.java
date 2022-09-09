package com.sharecharge.web.token.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sharecharge.core.exception.ServiceException;
import com.sharecharge.security.model.LoginUser;
import com.sharecharge.security.service.SysUserDetailsService;
import com.sharecharge.system.entity.DbAdminUser;
import com.sharecharge.system.entity.DbMenu;
import com.sharecharge.system.service.DbAdminUserService;
import com.sharecharge.system.service.DbMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class SysUserDetailsServiceImpl implements SysUserDetailsService {

    /**
     * {bcrypt} 加密的特征码
     */
    private static final String BCRYPT = "{bcrypt}";

    private final DbAdminUserService dbAdminUserService;
    private final DbMenuService dbMenuService;

    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        QueryWrapper<DbAdminUser> queryWrapper=new QueryWrapper<DbAdminUser>();
        queryWrapper.eq("admin_name",s);
        DbAdminUser dbAdminUser = dbAdminUserService.getOne(queryWrapper);
        if (Objects.isNull(dbAdminUser)){
            throw new ServiceException("登录用户：" + dbAdminUser.getAdminName() + " 不存在");
        }
        List<DbMenu> menuByRoleId = dbMenuService.findMenuByRoleId(dbAdminUser.getRoleId());
        Set<String> dbAuthsSet = new HashSet<>();
        for (DbMenu dbMenu:menuByRoleId){
            dbAuthsSet.add(dbMenu.getPerms());
        }
        Collection<? extends GrantedAuthority> authorities = AuthorityUtils
                .createAuthorityList(dbAuthsSet.toArray(new String[0]));

        LoginUser loginUser = new LoginUser
                (
                dbAdminUser.getId(),dbAdminUser.getParentId(),dbAdminUser.getAdminPhone(),
                null, dbAdminUser.getRoleId(),dbAdminUser,
                dbAdminUser.getAdminName(),dbAdminUser.getAdminPassword(),
                true, true, true, true,
                authorities
                );
        return loginUser;
    }
}
