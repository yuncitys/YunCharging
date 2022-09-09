package com.sharecharge.wxma.biz;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sharecharge.biz.entity.*;
import com.sharecharge.biz.service.*;
import com.sharecharge.biz.vo.DeviceDto;
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.core.util.StringUtils;
import com.sharecharge.core.util.http.SendRequest;
import com.sharecharge.system.entity.DbSharingConfig;
import com.sharecharge.system.service.DbSharingConfigService;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/app/user")
@RequiredArgsConstructor
public class AppUserController {
    String appid = "2021002144616193";
    String private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCbD/9BctmZL9/sii082HHMBVGY+hvzKDxkCJr4LxZ3TAN98s+WAx1eiA1orxoqCPWo0GdK2zk3d4eSzjRKB8Ltpz4aZGmsphtvhpYMZS4RoJ8/J5EL/hQLO4fqRfsxX2XZ/U7bXx/4UDu1AKj/ikE+/J4MITFJtkWg9dfsM0DaH+TopVjIawK7RG8dUVsmHHV3KfwDtcPc2MLtg1emXl2frbGvn9gGaxHhnJ+bxkBQ2vmOOU+G3UYdFQj6Y2QHNOgzZ0ZzGL9kI+PA/UAKh5NSkaSzJbs4LY1NcB/tGxTpQy4uWsqfYL/isfwOtihGa/Xd83KjqpoLfBEmJ7QYCFO/AgMBAAECggEAN7yPTvI+fw6zWa3tRtKyTNiYlPlJ9KRA2OAy/ATI+YasNqiBr3VdKC6VS0ythfCEhPKsPj8AmQxxAfb22qlhitUzwH7ZcUKf1/2zj68K2TE4LJZHT907TTJPz4jqGrwkuy+PGdFeh34niA89r+T9QOvCz69arRlEe/ACdheSNxTx/4RUZjJNTYGTEAeGI6+sXDnr5b6ACooENYvj6utUY1ulLDI5nW1y5uEoKJlrx982QuDAmoITHSELmvRYt2VLBq8V38gFpYaS1dg1DAlA5T75yNaWhOqq4KTDsI+Vf+BChPzdSWMBd+47BQXTka2loXHjzCRLywhlesoOSshtQQKBgQDZ5EVAYC7981KWrTGpJU2VLEp3VLRWZRqLMy3TCGNVBwDOdsTAW64KdAUaSribrtgl3y2jI11KTJITWGdo7r3qzyR/z/5rKsrEwoxJr1c7gIuQaWgrUvvhugTi+h3tpzeoQXXXmim2X5hXBptMk/4VkTwbrcQ0lT38Ku9bjX6PnwKBgQC2LqgECUTLQ0G/CN4cDfATOnirUgKXva8gNHsf4/wbKTszDgB9Hdf8stOYNOEKLF+AXRAXVJ2DnAru+2PHEOEdaCZtlNXT+TqaFTJjzDdG8Cj5Q2ELlLhOkgrq/uKzqHBd8/FpO08cFb8ItBHwy7Q/yg2wM76Z5JCaM3aU7qlH4QKBgGpe8BffXNGJChaY8pd6qIdcmfXrmiZbMTwnfgV6INQPmSsx/BdUKoDb5unQ9JK8JrVGYMc1qoNtIhrjm4g0lO6etVjYN4Il39tyhn4qZVGdDYUwkDQmTOUDpQMRy66LZbZ0To00q4xm2r4RcZtO3Em3Hzr7978b5KjIZRWy28hNAoGBAIrAA/R2/q7z/Um6PjcNom9yk96e6hyZOZyEe9Vw9FvXTh/3JQYlSY9Kvv5oH5B49Q81UYEDT6ehm24hhf9haqHT5ZiFx0jWwvwA8syxBtR/KiWVXH7OeIRWk/wfvZbozROxK4ZixkB0i3bcdeF3s0L15vDwp9RbEIpwcXrCPajhAoGAadU6LP8F8DYNY0cLl7XmYbgLCgirOHZ36Hdv/4eSSP5k0SfB4XsXvs4QujO2+ZJ1FdLJxmhEw0ZluI5W1TZDNRx0d1W7RA8EDK5Nrq9AsIXRQh/WwcWTnxfalJUABnDR9Kq+P3xQD+Uw/iZSFAYL67euNv83I2Ku26chgNQECrY=";
    String public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3U56yaKLqml67iR0pma93p4SaRPbK5uJoIWj4Bcr8qApVLZzuEOdaxIxzLvvDjLA5dwu46cNgXgN0jp7as7wmxS9BEqqCj61IYYPZLPVzs9vWLFflivx9PDckBBAxFcjQ2kyOIrqdGKxf9mbpJPss+oU5GH05FuDPDjXADkRy3CXHwXYSaHSe11bRgwzVkzY72pCGsUwLV3DGaeIBvyvXhnyDF2P7snpltyfQ1UjKZ284M3RqKEzk44Ug90ezmERwj84IW0AhygxYpPfKyvQBCydL3qaAhknmso3DpkRoncSPXBYqddiiaU53LfXPUcMtN+gmBTZnGL5fI4lho0AVwIDAQAB";

    final AppUserService appUserService;
    final DbSharingConfigService sharingConfigService;
    final NetworkDotService networkDotService;
    final DeviceService deviceService;
    final DeviceFeeBackService deviceFeeBackService;
    final AppRechargeService appRechargeService;
    final ImageCarouselService imageCarouselService;
    /**
     * 授权登录
     *
     * @param code      微信授权登录Code
     * @param headImage 头像
     * @param nickName  微信昵称
     * @param code      微信授权登录Code
     * @param type      0微信  1支付宝
     * @return
     */
    @RequestMapping("/wxLogin")
    public ResultUtil wxLogin(@RequestParam(value = "code") String code,
                              @RequestParam(value = "headImage") String headImage,
                              @RequestParam(value = "nickName") String nickName,
                              @RequestParam(value = "type") Integer type){
        if (StringUtils.isBlank(code) || Objects.isNull(type)) {
            return ResultUtil.error("参数不合格");
        }
        //微信小程序授权登录
        if (type == 0) {
            String appid = sharingConfigService.getGlobalConfig(DbSharingConfig.MINI_APPID);
            String secret = sharingConfigService.getGlobalConfig(DbSharingConfig.MINI_APPSECRET);
            String params = "appid=" + appid + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";
            String sr = SendRequest.sendGet("https://api.weixin.qq.com/sns/jscode2session", params);
            JSONObject json = JSONObject.parseObject(sr);
            String sessionKey = (String) json.get("session_key");
            String openId = (String) json.get("openid");
            if (StringUtils.isBlank(sessionKey) && StringUtils.isBlank(openId)) {
                return ResultUtil.error("授权失败");
            } else {
                Map map = new HashMap();
                map.put("openid", openId);
                map.put("sessionKey", sessionKey);
                AppUser userInfo = appUserService.getOne(new QueryWrapper<AppUser>().lambda().eq(AppUser::getWxOpenId,openId));
                if (Objects.isNull(userInfo)) { //如果用户已注册    修改头像跟昵称
                    AppUser userMy = new AppUser();
                    userMy.setWxOpenId(openId);
                    userMy.setHeadImg(headImage);
                    userMy.setUserName(nickName);
                    userMy.setLoginTime(new Date());
                    userMy.setUserPlatform(1);
                    appUserService.save(userMy);
                    map.put("isPhone", 1);
                } else {
                    userInfo.setHeadImg(headImage);
                    userInfo.setUserName(nickName);
                    userInfo.setLoginTime(new Date());
                    appUserService.updateById(userInfo);
                    map.put("isPhone", StringUtils.isBlank(userInfo.getPhoneNumber()) ? 1 : 0);
                }
                return ResultUtil.success(map);

            }
        } else {
            // TODO: 2022/7/20 支付宝授权登录
            return null;
        }
    }


    /**
     * 更新手机号码
     *
     * @return
     */
    @RequestMapping("updatePhoneNumber")
    public ResultUtil updatePhoneNumber(
            @RequestParam(value = "openId") String openId,
            @RequestParam(value = "phoneNumber") String phoneNumber) {
        if (StringUtils.isBlank(openId) || StringUtils.isBlank(phoneNumber)) {
            return ResultUtil.error("参数不合格");
        }
        AppUser userInfo = appUserService.getOne(new QueryWrapper<AppUser>().lambda().eq(AppUser::getWxOpenId,openId));
        userInfo.setPhoneNumber(phoneNumber);
        appUserService.updateById(userInfo);
        return ResultUtil.success();
    }

    /**
     * 获取手机号码
     *
     * @param encryptedData
     * @param sessionKey
     * @param iv
     * @param type
     * @return
     * @throws Exception
     */
    @RequestMapping("getPhoneNumber")
    public ResultUtil getPhoneNumber(String encryptedData, String sessionKey, String iv, Integer type) throws Exception {
        if (StringUtils.isBlank(encryptedData) || StringUtils.isBlank(sessionKey) || StringUtils.isBlank(iv)) {
            return ResultUtil.error("参数有误");
        }
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null == resultByte) {
                return ResultUtil.error("解密失败");
            }
            String result = new String(resultByte, "UTF-8");
            ResultUtil resultAjax = new ResultUtil();
            resultAjax.setData(JSONObject.parseObject(result));
            resultAjax.setCode(200);
            resultAjax.setMsg("解密成功");
            return resultAjax;
        }catch (Exception e){
            log.info("解密手机号失败：{}",e.getMessage());
            return ResultUtil.error("解密失败");
        }
    }

    /**
     * 得到个人用户信息
     *
     * @param
     * @return
     */
    @RequestMapping("getUserInfo")
    public ResultUtil getUserInfo(String wxCode) {
        if (StringUtils.isBlank(wxCode)) {
            return ResultUtil.error("参数不合格");
        }
        try {
            AppUser userInfo = appUserService.getOne(new QueryWrapper<AppUser>().lambda().eq(AppUser::getWxOpenId,wxCode));
            if (Objects.isNull(userInfo)) {
                return ResultUtil.error("用户不存在");
            }
            return ResultUtil.success(userInfo);
        } catch (Exception e) {
            log.error("得到用户信息错误: " + e.getMessage());
            return ResultUtil.error("系统错误");
        }
    }

    /**
     * 得到个人用户信息
     *
     * @param
     * @return
     */
    @RequestMapping("getUserInfoAlipay")
    public ResultUtil getUserInfoAlipay(String wxCode) {
        if (StringUtils.isBlank(wxCode)) {
            return ResultUtil.error("参数不合格");
        }
        try {
            AppUser userInfo = appUserService.getOne(new QueryWrapper<AppUser>().lambda().eq(AppUser::getZfbAuthCode,wxCode));
            if (Objects.isNull(userInfo)) {
                return ResultUtil.error("用户不存在");
            }
            return ResultUtil.success(userInfo);
        } catch (Exception e) {
            log.error("得到用户信息错误: " + e.getMessage());
            return ResultUtil.error("系统错误");
        }
    }

    /**
     * 首页所有网点信息
     *
     * @param longitude 精度
     * @param latitude  纬度
     * @return
     */
    @RequestMapping("getNetWorkList")
    public ResultUtil getAppNetWorkList(@RequestParam("longitude") String longitude,
                                     @RequestParam("latitude") String latitude,
                                     @RequestParam("searchContent") String searchContent) {
        if (StringUtils.isBlank(longitude) || StringUtils.isBlank(latitude)) {
            return ResultUtil.error("参数不合格");
        }
        Map map=new HashMap();
        map.put("longitude",longitude);
        map.put("latitude",latitude);
        map.put("searchContent",searchContent);
        try {
            List<NetworkDot> netWorkDotApp = networkDotService.findNetWorkDotApp(map);
            return ResultUtil.success(netWorkDotApp);
        } catch (Exception e) {
            log.error("查询网点信息: " + e.getMessage());
            return ResultUtil.error("查询网点信息");
        }
    }


    /**
     * 通过网点ID 查询设备列表
     *
     * @param netWorkId 网点ID
     * @return
     */
    @RequestMapping("getDeviceList")
    public ResultUtil getDeviceList(@RequestParam("netWorkId") Integer netWorkId) {
        if (Objects.isNull(netWorkId)) {
            return ResultUtil.error("参数不合格");
        }
        try {
            List<Map> deviceByNetWorkId = networkDotService.findDeviceByNetWorkId(netWorkId);
            return ResultUtil.success(deviceByNetWorkId);
        } catch (Exception e) {
            log.error("查询网点设备: " + e.getMessage());
            return ResultUtil.error("查询网点设备");
        }
    }

    /**
     * 通过设备号 查询设备信息
     *
     * @param deviceCode 设备号
     * @return
     */
    @RequestMapping("getDeviceInfo")
    public ResultUtil getDeviceInfo(@RequestParam("deviceCode") String deviceCode) {
        if (StringUtils.isBlank(deviceCode)) {
            return ResultUtil.error("参数不合格");
        }
        try {
            //Device deviceByDeviceCode = deviceService.getOne(new QueryWrapper<Device>().lambda().eq(Device::getDeviceCode, deviceCode));
            DeviceDto deviceVo = deviceService.findDeviceVo(deviceCode);
            if (Objects.isNull(deviceVo)) {
                return ResultUtil.error("设备未录入");
            } else if (deviceVo.getOperationState() == 4) {
                return ResultUtil.error("该设备已禁用");
            }
            return ResultUtil.success(deviceVo);
        } catch (Exception e) {
            log.error("查询设备信息: " + e.getMessage());
            return ResultUtil.error("查询设备信息失败");
        }
    }

    /**
     * 查询充值模式
     *
     * @return
     */
    @RequestMapping("cheakPrestore")
    public ResultUtil cheakPrestore() {
        try {
            String IS_PRESTORE = sharingConfigService.getGlobalConfig(DbSharingConfig.IS_PRESTORE);
            return ResultUtil.success("充值方案", IS_PRESTORE);
        } catch (Exception e) {
            log.error("查询充值模式: " + e.getMessage());
            return ResultUtil.error("查询充值模式失败");
        }
    }


    /**
     * 提交故障申请
     *
     * @param userId
     * @param deviceCode
     * @param reservedPhone
     * @param port
     * @param feedbackContent
     * @return
     */
    @RequestMapping("commitFeeBack")
    public ResultUtil commitFeeBack(@RequestParam("userId") Integer userId,
                                    @RequestParam("deviceCode") String deviceCode,
                                    @RequestParam("reservedPhone") String reservedPhone,
                                    @RequestParam("port") Integer port,
                                    @RequestParam("feedbackContent") String feedbackContent) {
        try {
            if (Objects.isNull(userId) || Objects.isNull(port) || StringUtils.isBlank(deviceCode)
                    || StringUtils.isBlank(reservedPhone) || StringUtils.isBlank(feedbackContent)) {
                return ResultUtil.error("参数不合格");
            }
            AppUser appUserById = appUserService.getById(userId);
            if (Objects.isNull(appUserById)) {
                return ResultUtil.error("该用户不存在");
            }
            Device deviceByDeviceCode = deviceService.getOne(new QueryWrapper<Device>().lambda().eq(Device::getDeviceCode,deviceCode));
            if (Objects.isNull(deviceByDeviceCode)) {
                return ResultUtil.error("设备号异常");
            }
            //保存数据入库
            DeviceFeeBack deviceFeeBack=new DeviceFeeBack();
            deviceFeeBack.setUserId(userId);
            deviceFeeBack.setDeviceCode(deviceCode);
            deviceFeeBack.setFeeBackContent(feedbackContent);
            deviceFeeBack.setPort(String.valueOf(port));
            deviceFeeBack.setReservedPhone(reservedPhone);
            deviceFeeBackService.save(deviceFeeBack);
            // TODO: 2022/7/20 推送反馈消息
            return ResultUtil.success("反馈成功");
        } catch (Exception e) {
            log.error("反馈失败:" + e.getMessage());
            return ResultUtil.error("系统异常");
        }
    }

    /**
     * 查询充值套餐
     *
     * @return
     */
    @RequestMapping("findPayMoney")
    public ResultUtil findPayMoney(@RequestParam("type") Integer type) {
        try {
            List<AppRecharge> appRechargeToApp = appRechargeService.list(new QueryWrapper<AppRecharge>().lambda().eq(AppRecharge::getType,type));
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setMsg("查询成功");
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            resultUtil.setData(appRechargeToApp);
            return resultUtil;
        } catch (Exception e) {
            log.error("查询失败：" + e.getMessage());
            return ResultUtil.error("查询失败");
        }
    }

    /**
     * 查询广告信息
     *
     * @return
     */
    @RequestMapping("findImageCarouselMapper")
    public ResultUtil findImageCarouselMapper() {
        try {
            List<ImageCarousel> list = imageCarouselService.list();
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setMsg("查询成功");
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            resultUtil.setData(list);
            return resultUtil;
        } catch (Exception e) {
            log.error("查询失败：" + e.getMessage());
            return ResultUtil.error("查询失败");
        }
    }


}
