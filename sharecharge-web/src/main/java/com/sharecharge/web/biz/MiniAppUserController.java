package com.sharecharge.web.biz;

import com.sharecharge.biz.entity.AppUser;
import com.sharecharge.biz.service.AppUserService;
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.ResultUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("sys/appUser")
@RequiredArgsConstructor
public class MiniAppUserController {

    final AppUserService appUserService;


    /**
     * 删除用户
     *
     * @param
     * @return
     */
    @RequestMapping("deleteAppUser")
    @PreAuthorize("@ps.hasPermission(':user:wxuserList:oneDelete')")
    public ResultUtil deleteAppUser(Integer appUserId) {
        try {
//            AppUser appUserMy = new AppUser();
//            appUserMy.setId(appUserId);
//            appUserMy.setIsDelete(1);
            appUserService.removeById(appUserId);
            return ResultUtil.success("删除成功!");
        } catch (Exception e) {
            log.error("删除用户错误: " + e.getMessage());
            return ResultUtil.error("删除失败!");
        }
    }

    /**
     * 更新用户
     *
     * @param
     * @return
     */
    @RequestMapping("updateAppUser")
    @PreAuthorize("@ps.hasPermission(':user:wxuserList:oneEdit')")
    public ResultUtil updateAppUser(AppUser appUser) {
        try {
            appUserService.updateById(appUser);
            return ResultUtil.success("更新成功!");
        } catch (Exception e) {
            log.error("更新用户错误: " + e.getMessage());
            return ResultUtil.error("更新失败!");
        }
    }

    /**
     * 查询微信用户信息列表
     *
     * @param page
     * @param limit
     * @param userName
     * @return
     */
    @RequestMapping("findAppUserList")
    @PreAuthorize("@ps.hasPermission(':user:wxuserList:all')")
    public ResultUtil findAppUserList(@RequestParam(value = "page") Integer page,//当前页
                                      @RequestParam("limit") Integer limit,//每页最大数据量
                                      @RequestParam("userPlatform") Integer userPlatform,
                                      String userName, String status,
                                      String createTimeStart, String createTimeEnd,
                                      String phoneNumber, String userId) {
        return appUserService.list(page,limit,userPlatform,userName,status,createTimeStart,createTimeEnd,phoneNumber,userId);
    }


    @RequestMapping("updateCash")
    @PreAuthorize("@ps.hasPermission(':sys:appUser:updateCash')")
    public ResultUtil updateCash(@RequestParam("userId") Integer id,
                                 @RequestParam("money") Double money) {
        try {
            ResultUtil resultUtil = new ResultUtil();
            if (money < 0 || Objects.isNull(money)) {
                return ResultUtil.error("参数异常");
            }
            AppUser appUserById = appUserService.getById(id);
            if (Objects.isNull(appUserById)) {
                return ResultUtil.error("用户不存在");
            }
            appUserById.setCash(money + appUserById.getCash());
            appUserById.setGiveMoney( appUserById.getGiveMoney() + money);
            resultUtil.setData(appUserService.updateById(appUserById));
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            resultUtil.setMsg("充值成功");
            return resultUtil;
        } catch (Exception e) {
            log.error("充值成功", e.getMessage());
            return ResultUtil.error("充值成功");
        }
    }


}
