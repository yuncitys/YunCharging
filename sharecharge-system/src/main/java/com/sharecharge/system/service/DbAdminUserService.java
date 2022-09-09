package com.sharecharge.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.system.entity.DbAdminUser;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;


public interface DbAdminUserService extends IService<DbAdminUser> {
    //更新密码
    ResultUtil updatePasswordAdminUser(Integer id,String oldPassword,String newPassword);

    ResultUtil list(Map map);




}
