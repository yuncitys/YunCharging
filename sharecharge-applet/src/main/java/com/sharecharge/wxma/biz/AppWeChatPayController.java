package com.sharecharge.wxma.biz;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sharecharge.biz.entity.AppPayDetails;
import com.sharecharge.biz.entity.AppUser;
import com.sharecharge.biz.entity.Card;
import com.sharecharge.biz.service.AppPayDetailsService;
import com.sharecharge.biz.service.AppUserService;
import com.sharecharge.biz.service.CardService;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.core.util.StringUtils;
import com.sharecharge.core.util.WebUtils;
import com.sharecharge.system.entity.DbSharingConfig;
import com.sharecharge.system.service.DbSharingConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("app/user")
@RequiredArgsConstructor
public class AppWeChatPayController {

    private static final String UNURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    final AppUserService appUserService;
    final DbSharingConfigService sharingConfigService;
    final AppPayDetailsService appPayDetailsService;
    final CardService cardService;


    /**
     * 用户余额充值
     *
     * @param request
     * @param wxCode
     * @param totalPrice
     * @return
     */
    @RequestMapping(value = "/wxPay")
    public ResultUtil wxPay(HttpServletRequest request,
                            @RequestParam("wxCode") String wxCode,
                            @RequestParam("totalPrice") Double totalPrice) {
        try {
            if (Objects.isNull(wxCode) || Objects.isNull(totalPrice)) {
                return ResultUtil.error("参数不能为空");
            }
            AppUser userInfo = appUserService.getOne(new QueryWrapper<AppUser>().lambda().eq(AppUser::getWxOpenId,wxCode));
            if (Objects.isNull(userInfo)) {
                return ResultUtil.error("用户不存在");
            }
            //创建条件
            SortedMap<String, String> paraMap = new TreeMap<String, String>();
            //设置body变量 (支付成功显示在微信支付 商品详情中)
            String body = "TEST";
            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar calendar = Calendar.getInstance();
            String dateName = df.format(calendar.getTime());
            int v = (int) ((Math.random() * 9 + 1) * 1000000);
            //设置商户订单号
            String outTradeNo = dateName + v;
            //设置随机字符串
            String nonceStr = (UUID.randomUUID()).toString().replaceAll("-", "");
            //设置请求参数(小程序ID)
            String appid = sharingConfigService.getGlobalConfig(DbSharingConfig.MINI_APPID);
            paraMap.put("appid", appid);
            //设置请求参数(商户号)
            String mch_id = sharingConfigService.getGlobalConfig(DbSharingConfig.MCH_ID);
            paraMap.put("mch_id", mch_id);
            //设置请求参数(随机字符串)
            paraMap.put("nonce_str", nonceStr);
            //设置请求参数(商品描述)
            paraMap.put("body", body);
            //设置请求参数(商户订单号)
            paraMap.put("out_trade_no", outTradeNo);
            //设置请求参数(总金额)
            paraMap.put("total_fee", String.valueOf(totalPrice.intValue()));
            //设置请求参数(终端IP)
            paraMap.put("spbill_create_ip", WebUtils.getRemoteAddr(request));
            //设置请求参数(通知地址)
            String notifyHost = sharingConfigService.getGlobalConfig(DbSharingConfig.DOMAIN_HOST);
            paraMap.put("notify_url", notifyHost + "/app/user/payCallback");
            //设置请求参数(交易类型)
            paraMap.put("trade_type", "JSAPI");
            //设置请求参数(openid)(在接口文档中 该参数 是否必填项 但是一定要注意 如果交易类型设置成'JSAPI'则必须传入openid)
            paraMap.put("openid", userInfo.getWxOpenId());
            paraMap.put("attach", userInfo.getWxOpenId());
            //调用逻辑传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            String stringA = WebUtils.formatUrlMap(paraMap, false, false);
            //第二步，在stringA最后拼接上key得到stringSignTemp字符串，并对stringSignTemp进行MD5运算，再将得到的字符串所有字符转换为大写，得到sign值signValue。(签名)
            String key = sharingConfigService.getGlobalConfig(DbSharingConfig.MCH_KEY);
            String sign = WebUtils.MD5(stringA + "&key=" + key).toUpperCase();
            //将参数 编写XML格式
            StringBuffer paramBuffer = new StringBuffer();
            paramBuffer.append("<xml>");
            paramBuffer.append("<appid>" + appid + "</appid>");
            paramBuffer.append("<mch_id>" + mch_id + "</mch_id>");
            paramBuffer.append("<nonce_str>" + paraMap.get("nonce_str") + "</nonce_str>");
            paramBuffer.append("<sign>" + sign + "</sign>");
            paramBuffer.append("<attach>" + userInfo.getWxOpenId() + "</attach>");
            paramBuffer.append("<body>" + body + "</body>");
            paramBuffer.append("<out_trade_no>" + paraMap.get("out_trade_no") + "</out_trade_no>");
            paramBuffer.append("<total_fee>" + paraMap.get("total_fee") + "</total_fee>");
            paramBuffer.append("<spbill_create_ip>" + paraMap.get("spbill_create_ip") + "</spbill_create_ip>");
            paramBuffer.append("<notify_url>" + paraMap.get("notify_url") + "</notify_url>");
            paramBuffer.append("<trade_type>" + paraMap.get("trade_type") + "</trade_type>");
            paramBuffer.append("<openid>" + paraMap.get("openid") + "</openid>");
            paramBuffer.append("</xml>");
            try {
                //发送请求(POST)(获得数据包ID)(这有个注意的地方 如果不转码成ISO8859-1则会告诉你body不是UTF8编码 就算你改成UTF8编码也一样不好使 所以修改成ISO8859-1)
                Map<String, String> map = WebUtils.doXMLParse(WebUtils.getRemotePortData(UNURL, new String(paramBuffer.toString().getBytes(), "ISO8859-1")));
                //应该创建 支付表数据
                if (map != null) {
                    if (!map.get("result_code").equals("FAIL")) {
                        AppPayDetails appPayDetails = new AppPayDetails();
                        appPayDetails.setPayCode(outTradeNo);
                        appPayDetails.setPayMoeny(BigDecimal.valueOf(totalPrice * 0.01));
                        appPayDetails.setGiftMoney(BigDecimal.valueOf(0));
                        appPayDetails.setUserId(userInfo.getId());
                        appPayDetails.setType(0);
                        appPayDetails.setPayStatus(0);
                        boolean sqlRow = appPayDetailsService.save(appPayDetails);
                        if (sqlRow) {
                            System.out.println("微信 统一下单 接口调用成功 并且新增支付信息成功");
                            ResultUtil prepay_id = generateSignature(map.get("prepay_id"));
                            return prepay_id;
                        }
                    }
                }
            } catch (UnsupportedEncodingException e) {
                log.error("支付失败：{}",e.getMessage());
            } catch (Exception e) {
                log.error("支付失败：{}",e.getMessage());
            }
            return ResultUtil.error("支付失败");
        } catch (Exception e) {
            return ResultUtil.error("支付失败");
        }
    }

    /**
     * 余额充值回调
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/payCallback")
    public void payCallback(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("微信回调接口方法 start");
        String inputLine = "";
        String notityXml = "";
        try {
            while ((inputLine = request.getReader().readLine()) != null) {
                notityXml += inputLine;
            }
            //关闭流
            request.getReader().close();
            log.info("微信回调内容信息：" + notityXml);
            //解析成Map
            Map<String, String> map = WebUtils.doXMLParse(notityXml);
            //判断 支付是否成功
            if ("SUCCESS".equals(map.get("result_code"))) {
                //获得 返回的商户订单号
                String outTradeNo = map.get("out_trade_no");
                String openid = map.get("openid");

                AppUser userInfo = appUserService.getOne(new QueryWrapper<AppUser>().lambda().eq(AppUser::getWxOpenId,openid));

                AppPayDetails payDetails = appPayDetailsService.getOne(new QueryWrapper<AppPayDetails>().lambda().eq(AppPayDetails::getPayCode,outTradeNo));

                if (!Objects.isNull(payDetails) && !Objects.isNull(userInfo)) {
                    payDetails.setPayStatus(1);
                    appPayDetailsService.updateById(payDetails);
                    //加余额
                    userInfo.setCash(userInfo.getCash() + payDetails.getPayMoeny().doubleValue());
                    userInfo.setRealityPayMoney(userInfo.getRealityPayMoney() + payDetails.getPayMoeny().doubleValue());
                    boolean sqlRow = appUserService.updateById(userInfo);
                    if (sqlRow) {
                        StringBuffer buffer = new StringBuffer();
                        buffer.append("<xml>");
                        buffer.append("<return_code>SUCCESS</return_code>");
                        buffer.append("<return_msg>OK</return_msg>");
                        buffer.append("</xml>");
                        //给微信服务器返回 成功标示 否则会一直询问 咱们服务器 是否回调成功
                        PrintWriter writer = response.getWriter();
                        //返回
                        writer.print(buffer.toString());
                    }
                }
            }
        } catch (IOException e) {
            log.error("支付回调异常：{}",e.getMessage());
        } catch (Exception e) {
            log.error("支付回调异常：{}",e.getMessage());
        }
    }

    /**
     * 生成签名
     *
     * @param prepay_id
     * @return
     */
    public ResultUtil generateSignature(String prepay_id) {
        //实例化返回对象
        JSONObject resultJson = new JSONObject();
        //获得参数(微信统一下单接口生成的prepay_id )
        String timeStamp = Long.valueOf(System.currentTimeMillis()).toString();
        //创建 随机串
        String nonceStr = (UUID.randomUUID()).toString().replaceAll("-", "");
        //创建 MD5
        String signType = "MD5";
        //创建hashmap(用户获得签名)
        SortedMap<String, String> paraMap = new TreeMap<String, String>();
        //设置(小程序ID)(这块一定要是大写)
        String appid = sharingConfigService.getGlobalConfig(DbSharingConfig.MINI_APPID);
        paraMap.put("appId", appid);
        //设置(时间戳)
        paraMap.put("timeStamp", timeStamp);
        //设置(随机串)
        paraMap.put("nonceStr", nonceStr);
        //设置(数据包)
        paraMap.put("package", "prepay_id=" + prepay_id);
        //设置(签名方式)
        paraMap.put("signType", signType);
        //调用逻辑传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        String stringA = WebUtils.formatUrlMap(paraMap, false, false);
        //第二步，在stringA最后拼接上key得到stringSignTemp字符串，并对stringSignTemp进行MD5运算，再将得到的字符串所有字符转换为大写，得到sign值signValue。(签名)
        String key = sharingConfigService.getGlobalConfig(DbSharingConfig.MCH_KEY);
        String sign = WebUtils.MD5(stringA + "&key=" + key).toUpperCase();
        if (StringUtils.isNotBlank(sign)) {
            //返回签名信息
            resultJson.put("paySign", sign);
            resultJson.put("appId", appid);
            //返回随机串(这个随机串是新创建的)
            resultJson.put("nonceStr", nonceStr);
            //返回时间戳
            resultJson.put("timeStamp", timeStamp);
            //返回数据包
            resultJson.put("package", "prepay_id=" + prepay_id);
        }
        return ResultUtil.success(resultJson);
    }

    /**
     * 用户套餐充值
     *
     * @param request
     * @param wxCode
     * @param payMoeny
     * @param giftMoney
     * @return
     */
    @RequestMapping(value = "/wxPayDetails")
    public ResultUtil wxPayDetails(HttpServletRequest request,
                                   @RequestParam("wxCode") String wxCode,
                                   @RequestParam("payMoeny") Double payMoeny,
                                   @RequestParam("giftMoney") Double giftMoney) {
        try {
            if (Objects.isNull(wxCode) || Objects.isNull(payMoeny) || Objects.isNull(giftMoney)) {
                return ResultUtil.error("参数不能为空");
            }
            AppUser userInfo = appUserService.getOne(new QueryWrapper<AppUser>().lambda().eq(AppUser::getWxOpenId,wxCode));
            if (Objects.isNull(userInfo)) {
                return ResultUtil.error("用户不存在");
            }
            //创建条件
            SortedMap<String, String> paraMap = new TreeMap<String, String>();
            //设置body变量 (支付成功显示在微信支付 商品详情中)
            String body = "TEST";
            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar calendar = Calendar.getInstance();
            String dateName = df.format(calendar.getTime());
            int v = (int) ((Math.random() * 9 + 1) * 1000000);
            //设置商户订单号
            String outTradeNo = dateName + v;
            //设置随机字符串
            String nonceStr = (UUID.randomUUID()).toString().replaceAll("-", "");
            //设置请求参数(小程序ID)
            String appid = sharingConfigService.getGlobalConfig(DbSharingConfig.MINI_APPID);
            paraMap.put("appid", appid);
            //设置请求参数(商户号)
            String mch_id = sharingConfigService.getGlobalConfig(DbSharingConfig.MCH_ID);
            paraMap.put("mch_id", mch_id);
            //设置请求参数(随机字符串)
            paraMap.put("nonce_str", nonceStr);
            //设置请求参数(商品描述)
            paraMap.put("body", body);
            //设置请求参数(商户订单号)
            paraMap.put("out_trade_no", outTradeNo);
            //设置请求参数(总金额)
            paraMap.put("total_fee", String.valueOf(payMoeny.intValue()));
            //设置请求参数(终端IP)
            paraMap.put("spbill_create_ip", WebUtils.getRemoteAddr(request));
            //设置请求参数(通知地址)
            String notifyHost = sharingConfigService.getGlobalConfig(DbSharingConfig.DOMAIN_HOST);
            paraMap.put("notify_url", notifyHost + "/app/user/payDetailsCallback");
            //设置请求参数(交易类型)
            paraMap.put("trade_type", "JSAPI");
            //设置请求参数(openid)(在接口文档中 该参数 是否必填项 但是一定要注意 如果交易类型设置成'JSAPI'则必须传入openid)
            paraMap.put("openid", userInfo.getWxOpenId());
            paraMap.put("attach", userInfo.getWxOpenId());
            //调用逻辑传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            String stringA = WebUtils.formatUrlMap(paraMap, false, false);
            //第二步，在stringA最后拼接上key得到stringSignTemp字符串，并对stringSignTemp进行MD5运算，再将得到的字符串所有字符转换为大写，得到sign值signValue。(签名)
            String key = sharingConfigService.getGlobalConfig(DbSharingConfig.MCH_KEY);
            String sign = WebUtils.MD5(stringA + "&key=" + key).toUpperCase();
            //将参数 编写XML格式
            StringBuffer paramBuffer = new StringBuffer();
            paramBuffer.append("<xml>");
            paramBuffer.append("<appid>" + appid + "</appid>");
            paramBuffer.append("<mch_id>" + mch_id + "</mch_id>");
            paramBuffer.append("<nonce_str>" + paraMap.get("nonce_str") + "</nonce_str>");
            paramBuffer.append("<sign>" + sign + "</sign>");
            paramBuffer.append("<attach>" + userInfo.getWxOpenId() + "</attach>");
            paramBuffer.append("<body>" + body + "</body>");
            paramBuffer.append("<out_trade_no>" + paraMap.get("out_trade_no") + "</out_trade_no>");
            paramBuffer.append("<total_fee>" + paraMap.get("total_fee") + "</total_fee>");
            paramBuffer.append("<spbill_create_ip>" + paraMap.get("spbill_create_ip") + "</spbill_create_ip>");
            paramBuffer.append("<notify_url>" + paraMap.get("notify_url") + "</notify_url>");
            paramBuffer.append("<trade_type>" + paraMap.get("trade_type") + "</trade_type>");
            paramBuffer.append("<openid>" + paraMap.get("openid") + "</openid>");
            paramBuffer.append("</xml>");

            try {
                //发送请求(POST)(获得数据包ID)(这有个注意的地方 如果不转码成ISO8859-1则会告诉你body不是UTF8编码 就算你改成UTF8编码也一样不好使 所以修改成ISO8859-1)
                Map<String, String> map = WebUtils.doXMLParse(WebUtils.getRemotePortData(UNURL, new String(paramBuffer.toString().getBytes(), "ISO8859-1")));
                //应该创建 支付表数据
                if (map != null) {
                    if (!map.get("result_code").equals("FAIL")) {
                        AppPayDetails appPayDetails = new AppPayDetails();
                        appPayDetails.setPayCode(outTradeNo);
                        appPayDetails.setPayMoeny(BigDecimal.valueOf(payMoeny * 0.01));
                        appPayDetails.setGiftMoney(BigDecimal.valueOf(giftMoney * 0.01));
                        appPayDetails.setUserId(userInfo.getId());
                        appPayDetails.setPayStatus(0);
                        appPayDetails.setType(1);
                        boolean sqlRow = appPayDetailsService.save(appPayDetails);
                        if (sqlRow) {
                            System.out.println("微信 统一下单 接口调用成功 并且新增支付信息成功");
                            ResultUtil prepay_id = generateSignature(map.get("prepay_id"));
                            return prepay_id;
                        }
                    }
                }
            } catch (UnsupportedEncodingException e) {
                log.error("套餐统一下单失败：{}",e.getMessage());
            } catch (Exception e) {
                log.error("套餐统一下单失败：{}",e.getMessage());
            }
            return ResultUtil.error("支付失败");
        } catch (Exception e) {
            return ResultUtil.error("支付失败");
        }

    }

    /**
     * 用户套餐充值回调
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/payDetailsCallback")
    public void payDetailsCallback(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("微信回调接口方法 start");
        String inputLine = "";
        String notityXml = "";
        try {
            while ((inputLine = request.getReader().readLine()) != null) {
                notityXml += inputLine;
            }
            //关闭流
            request.getReader().close();
            log.info("微信回调内容信息：" + notityXml);
            //解析成Map
            Map<String, String> map = WebUtils.doXMLParse(notityXml);
            //判断 支付是否成功
            if ("SUCCESS".equals(map.get("result_code"))) {
                //获得 返回的商户订单号
                String outTradeNo = map.get("out_trade_no");
                AppPayDetails payDetails = appPayDetailsService.getOne(new QueryWrapper<AppPayDetails>().lambda().eq(AppPayDetails::getPayCode,outTradeNo));
                AppUser appUser = appUserService.getById(payDetails.getUserId());
                if (!Objects.isNull(appUser) && !Objects.isNull(payDetails)) {
                    payDetails.setPayStatus(1);
                    appPayDetailsService.updateById(payDetails);

                    appUser.setCash(appUser.getCash() + payDetails.getPayMoeny().doubleValue() + payDetails.getGiftMoney().doubleValue());
                    appUser.setRealityPayMoney(appUser.getRealityPayMoney() + payDetails.getPayMoeny().doubleValue());
                    appUser.setGiveMoney(appUser.getGiveMoney() + payDetails.getGiftMoney().doubleValue());
                    boolean sqlRow = appUserService.updateById(appUser);
                    if (sqlRow) {
                        StringBuffer buffer = new StringBuffer();
                        buffer.append("<xml>");
                        buffer.append("<return_code>SUCCESS</return_code>");
                        buffer.append("<return_msg>OK</return_msg>");
                        buffer.append("</xml>");
                        //给微信服务器返回 成功标示 否则会一直询问 咱们服务器 是否回调成功
                        PrintWriter writer = response.getWriter();
                        //返回
                        writer.print(buffer.toString());
                    }
                }
            }
        } catch (IOException e) {
            log.error("套餐充值回调失败：{}",e.getMessage());
        } catch (Exception e) {
            log.error("套餐充值回调失败：{}",e.getMessage());
        }

    }


    /**
     * 电卡充值
     *
     * @param request
     * @param wxOpenId
     * @param cardNo
     * @param payMoeny
     * @param giftMoney
     * @return
     */
    @RequestMapping("updateCardNoCash")
    public ResultUtil updateCardNoCash(HttpServletRequest request,
                                       @RequestParam("wxOpenId") String wxOpenId,
                                       @RequestParam("cardNo") String cardNo,
                                       @RequestParam("payMoeny") Double payMoeny,
                                       @RequestParam("giftMoney") Double giftMoney) {
        if (Objects.isNull(wxOpenId) || Objects.isNull(payMoeny) || Objects.isNull(giftMoney) || Objects.isNull(cardNo)) {
            return ResultUtil.error("参数不能为空");
        }
        Card card = cardService.getOne(new QueryWrapper<Card>().lambda().eq(Card::getCardNo,cardNo));
        if (Objects.isNull(card)) {
            return ResultUtil.error("该卡号不存在");
        }
        /*if (card.getActivateStatus()==0) {
            return ResultUtil.error("卡号未激活");
        }*/
        if (card.getCardStatus() == 1) {
            return ResultUtil.error("电卡已挂失");
        }

        AppUser userInfo = appUserService.getOne(new QueryWrapper<AppUser>().lambda().eq(AppUser::getWxOpenId,wxOpenId));
        if (Objects.isNull(userInfo)) {
            return ResultUtil.error("用户不存在");
        }
        try {
            //创建条件
            SortedMap<String, String> paraMap = new TreeMap<String, String>();
            //设置body变量 (支付成功显示在微信支付 商品详情中)
            String body = "TEST";
            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar calendar = Calendar.getInstance();
            String dateName = df.format(calendar.getTime());
            int v = (int) ((Math.random() * 9 + 1) * 1000000);
            //设置商户订单号
            String outTradeNo = dateName + v;
            //设置随机字符串
            String nonceStr = (UUID.randomUUID()).toString().replaceAll("-", "");
            //设置请求参数(小程序ID)
            String appid = sharingConfigService.getGlobalConfig(DbSharingConfig.MINI_APPID);
            paraMap.put("appid", appid);
            //设置请求参数(商户号)
            String mch_id = sharingConfigService.getGlobalConfig(DbSharingConfig.MCH_ID);
            paraMap.put("mch_id", mch_id);
            //设置请求参数(随机字符串)
            paraMap.put("nonce_str", nonceStr);
            //设置请求参数(商品描述)
            paraMap.put("body", body);
            //设置请求参数(商户订单号)
            paraMap.put("out_trade_no", outTradeNo);
            //设置请求参数(总金额)
            paraMap.put("total_fee", String.valueOf(payMoeny.intValue()));
            //设置请求参数(终端IP)
            paraMap.put("spbill_create_ip", WebUtils.getRemoteAddr(request));
            //设置请求参数(通知地址)
            String notifyHost = sharingConfigService.getGlobalConfig(DbSharingConfig.DOMAIN_HOST);
            paraMap.put("notify_url", notifyHost + "/app/user/payCardDetailsCallback");
            //设置请求参数(交易类型)
            paraMap.put("trade_type", "JSAPI");
            //设置请求参数(openid)(在接口文档中 该参数 是否必填项 但是一定要注意 如果交易类型设置成'JSAPI'则必须传入openid)
            paraMap.put("openid", userInfo.getWxOpenId());
            paraMap.put("attach", userInfo.getWxOpenId());
            //调用逻辑传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            String stringA = WebUtils.formatUrlMap(paraMap, false, false);
            //第二步，在stringA最后拼接上key得到stringSignTemp字符串，并对stringSignTemp进行MD5运算，再将得到的字符串所有字符转换为大写，得到sign值signValue。(签名)
            String key = sharingConfigService.getGlobalConfig(DbSharingConfig.MCH_KEY);
            String sign = WebUtils.MD5(stringA + "&key=" + key).toUpperCase();
            //将参数 编写XML格式
            StringBuffer paramBuffer = new StringBuffer();
            paramBuffer.append("<xml>");
            paramBuffer.append("<appid>" + appid + "</appid>");
            paramBuffer.append("<mch_id>" + mch_id + "</mch_id>");
            paramBuffer.append("<nonce_str>" + paraMap.get("nonce_str") + "</nonce_str>");
            paramBuffer.append("<sign>" + sign + "</sign>");
            paramBuffer.append("<attach>" + userInfo.getWxOpenId() + "</attach>");
            paramBuffer.append("<body>" + body + "</body>");
            paramBuffer.append("<out_trade_no>" + paraMap.get("out_trade_no") + "</out_trade_no>");
            paramBuffer.append("<total_fee>" + paraMap.get("total_fee") + "</total_fee>");
            paramBuffer.append("<spbill_create_ip>" + paraMap.get("spbill_create_ip") + "</spbill_create_ip>");
            paramBuffer.append("<notify_url>" + paraMap.get("notify_url") + "</notify_url>");
            paramBuffer.append("<trade_type>" + paraMap.get("trade_type") + "</trade_type>");
            paramBuffer.append("<openid>" + paraMap.get("openid") + "</openid>");
            paramBuffer.append("</xml>");

            try {
                //发送请求(POST)(获得数据包ID)(这有个注意的地方 如果不转码成ISO8859-1则会告诉你body不是UTF8编码 就算你改成UTF8编码也一样不好使 所以修改成ISO8859-1)
                Map<String, String> map1 = WebUtils.doXMLParse(WebUtils.getRemotePortData(UNURL, new String(paramBuffer.toString().getBytes(), "ISO8859-1")));
                //应该创建 支付表数据
                if (map1 != null) {
                    if (!map1.get("result_code").equals("FAIL")) {
                        AppPayDetails appPayDetails = new AppPayDetails();
                        appPayDetails.setPayCode(outTradeNo);
                        appPayDetails.setPayMoeny(BigDecimal.valueOf(payMoeny * 0.01));
                        appPayDetails.setGiftMoney(BigDecimal.valueOf(giftMoney * 0.01));
                        appPayDetails.setUserId(userInfo.getId());
                        appPayDetails.setPayStatus(0);
                        appPayDetails.setCardNo(cardNo);
                        appPayDetails.setType(2);
                        boolean sqlRow = appPayDetailsService.save(appPayDetails);
                        if (sqlRow) {
                            System.out.println("微信 统一下单 接口调用成功 并且新增支付信息成功");
                            ResultUtil prepay_id = generateSignature(map1.get("prepay_id"));
                            return prepay_id;
                        }
                    }
                }
            } catch (UnsupportedEncodingException e) {
                log.error("电卡充值统一下单失败：{}",e.getMessage());
            } catch (Exception e) {
                log.error("电卡充值统一下单失败：{}",e.getMessage());
            }
            return ResultUtil.error("支付失败");
        } catch (Exception e) {
            return ResultUtil.error("支付失败");
        }
    }


    /**
     * 电卡充值回调
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/payCardDetailsCallback")
    public void payCardDetailsCallback(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("微信回调接口方法 start");
        log.info("微信回调接口方法 start");
        String inputLine = "";
        String notityXml = "";
        try {
            while ((inputLine = request.getReader().readLine()) != null) {
                notityXml += inputLine;
            }
            //关闭流
            request.getReader().close();
            log.info("微信回调内容信息：" + notityXml);
            //解析成Map
            Map<String, String> map = WebUtils.doXMLParse(notityXml);
            //判断 支付是否成功
            if ("SUCCESS".equals(map.get("result_code"))) {
                //获得 返回的商户订单号
                String outTradeNo = map.get("out_trade_no");
                AppPayDetails payDetails = appPayDetailsService.getOne(new QueryWrapper<AppPayDetails>().lambda().eq(AppPayDetails::getPayCode,outTradeNo));
                if (payDetails.getPayStatus() == 0) {
                    Card card= cardService.getOne(new QueryWrapper<Card>().lambda().eq(Card::getCardNo,payDetails.getCardNo()));
                    if (!Objects.isNull(card) && !Objects.isNull(payDetails)) {
                        payDetails.setPayStatus(1);
                        appPayDetailsService.updateById(payDetails);

                        card.setCardCash(card.getCardCash() + payDetails.getPayMoeny().doubleValue() + payDetails.getGiftMoney().doubleValue());
                        card.setRealityPayMoney(card.getRealityPayMoney() + payDetails.getPayMoeny().doubleValue());
                        card.setGiveMoney(card.getGiveMoney() + payDetails.getGiftMoney().doubleValue());
                        boolean sqlRow = cardService.updateById(card);
                        if (sqlRow) {
                            StringBuffer buffer = new StringBuffer();
                            buffer.append("<xml>");
                            buffer.append("<return_code>SUCCESS</return_code>");
                            buffer.append("<return_msg>OK</return_msg>");
                            buffer.append("</xml>");
                            //给微信服务器返回 成功标示 否则会一直询问 咱们服务器 是否回调成功
                            PrintWriter writer = response.getWriter();
                            //返回
                            writer.print(buffer.toString());
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("电卡充值回调失败：{}",e.getMessage());
        } catch (Exception e) {
            log.error("电卡充值回调失败：{}", e.getMessage());
        }
    }
}
