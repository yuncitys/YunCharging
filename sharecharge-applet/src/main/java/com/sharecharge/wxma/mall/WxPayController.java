package com.sharecharge.wxma.mall;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.sharecharge.biz.entity.AppUser;
import com.sharecharge.biz.service.AppUserService;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.core.util.WebUtils;
import com.sharecharge.mall.entity.Order;
import com.sharecharge.mall.service.OrderService;
import com.sharecharge.system.entity.DbSharingConfig;
import com.sharecharge.system.service.DbSharingConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;


@Slf4j
@Controller
@RequestMapping("app/wxpay")
@ResponseBody
public class WxPayController {

    private static final String UNURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    @Autowired
    private OrderService orderService;
    @Autowired
    private DbSharingConfigService sharingConfigService;
    @Autowired
    private AppUserService memberService;


    @PostMapping("/pay")
    public ResultUtil Wxpay(HttpServletRequest request, Integer userId, @NotBlank String orderId) {
        if (userId == null) {
            return ResultUtil.error("请先登录");
        }
        String[] split = orderId.split(StringPool.COMMA);
        List<String> orderids = Arrays.asList(split);

        List<Order> list = orderService.list(new QueryWrapper<Order>().lambda().eq(Order::getUserId, userId).eq(Order::getDeleted, false).in(Order::getId, orderids));
        if (list.size() <= 0) {
            return ResultUtil.error("参数异常");
        }

        // 计算金额
        BigDecimal sumMoney = BigDecimal.ZERO;
        for (int i = 0; i < list.size(); i++) {
            sumMoney = sumMoney.add(list.get(i).getOrderPrice());
        }

        AppUser appUserById = memberService.getById(userId);

        JSONObject ResultUtilJson = new JSONObject();
        SortedMap<String, String> paraMap = new TreeMap<String, String>();
        // 设置body变量 (支付成功显示在微信支付 商品详情中)
        String body = "Mall";
        String nonceStr = (UUID.randomUUID()).toString().replaceAll("-", "").toUpperCase();
        // 设置请求参数(小程序ID)
        String appId = sharingConfigService.getGlobalConfig(DbSharingConfig.MINI_APPID);
        String mchId = sharingConfigService.getGlobalConfig(DbSharingConfig.MCH_ID);
        String machKey = sharingConfigService.getGlobalConfig(DbSharingConfig.MCH_KEY);
        paraMap.put("appid", appId);
        // 设置请求参数(商户号)
        paraMap.put("mch_id", mchId);
        paraMap.put("openid", appUserById.getWxOpenId());
        // 设置请求参数(随机字符串)
        paraMap.put("nonce_str", nonceStr);
        // 设置请求参数(商品描述)
        paraMap.put("body", body);
        // 设置请求参数(商户订单号)
        //paraMap.put("out_trade_no", mchId + System.currentTimeMillis());
        paraMap.put("out_trade_no", list.get(0).getOrderSn());
        //转为单位为分
        Double doubleMoney = sumMoney.doubleValue() * 100.0;
        //double转int类型
        int i = doubleMoney.intValue();
        //额外信息
        Map<String, String> attachMap = new HashMap<>();
        attachMap.put("userId", userId + "");
        attachMap.put("orderId", orderId + "");
        // 转成json
        String s = JSON.toJSONString(attachMap);
        //标价金额
        paraMap.put("total_fee", String.valueOf(i));
        //附加数据  usercode  以及饮料类型
        paraMap.put("attach", s);
        // 设置请求参数(终端IP)
        paraMap.put("spbill_create_ip", request.getRemoteAddr());
        String notifyHost = sharingConfigService.getGlobalConfig(DbSharingConfig.DOMAIN_HOST);
        // 设置请求参数(通知地址)回调地址
        paraMap.put("notify_url", notifyHost + "/app/wxpay/payCallBack");
        // 设置请求参数(交易类型)
        paraMap.put("trade_type", "JSAPI");
        //生成签名
        //调用逻辑传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        String stringA = WebUtils.formatUrlMap(paraMap, false, false);
        //第二步，在stringA最后拼接上key得到stringSignTemp字符串，并对stringSignTemp进行MD5运算，再将得到的字符串所有字符转换为大写，得到sign值signValue。(签名)
        String key = sharingConfigService.getGlobalConfig(DbSharingConfig.MCH_KEY);
        String sign = WebUtils.MD5(stringA + "&key=" + key).toUpperCase();
        paraMap.put("sign", sign);

        try {
            String requestXml = WebUtils.mapToXml(paraMap);
            String remotePortData = WebUtils.getRemotePortData(UNURL, requestXml); //进行支付
            log.info("<<-------->>>>?"+remotePortData);
            Map<String, String> ResultUtilMap = WebUtils.xmlToMap(remotePortData);
            //应该创建 支付表数据
            if (ResultUtilMap != null) {
                String return_code = ResultUtilMap.get("return_code");
                String prepay_id = null;
                if (return_code.contains("SUCCESS")) {
                    //获取到prepay_id
                    prepay_id = ResultUtilMap.get("prepay_id");
                    //生成时间戳//（转换成秒）//（截取前10位）
                    long currentTimeMillis = System.currentTimeMillis();
                    long second = currentTimeMillis / 1000L;
                    String seconds = String.valueOf(second).substring(0, 10);

                    //生成签名
                    SortedMap<String, String> signParam = new TreeMap<>();
                    signParam.put("appId", appId);
                    signParam.put("signType", "MD5");
                    signParam.put("package", "prepay_id=" + prepay_id);
                    signParam.put("nonceStr", nonceStr);
                    signParam.put("timeStamp", seconds);
                    String signAgain = createSign("UTF-8", signParam, machKey);
                    //返回数据给前端
                    ResultUtilJson.put("appid", appId);
                    ResultUtilJson.put("partnerid", mchId);
                    ResultUtilJson.put("nonce_str", nonceStr);
                    ResultUtilJson.put("prepay_id", "prepay_id=" + prepay_id);
                    ResultUtilJson.put("prepay_id", prepay_id);
                    ResultUtilJson.put("timestamp", seconds);
                    ResultUtilJson.put("sign", signAgain);
                    // 返回前端
                    return ResultUtil.success(ResultUtilJson);

                } else {
                    return ResultUtil.error("扫码失败!请重新扫码");
                }
            } else {
                return ResultUtil.error("获取返回值失败");
            }
        } catch (Exception e) {
            return ResultUtil.error("获取支付失败");
        }

    }

    @RequestMapping(value = "/payCallBack")
    public void payCallBack(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String resXml = "";
        InputStream inStream = request.getInputStream();
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int lenth = 0;
        while ((lenth = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, lenth);
        }
        String notityXml = new String(outSteam.toByteArray(), "utf-8");
        log.info("微信回调内容信息：" + notityXml);

        Map<String, String> ResultUtilMap = WebUtils.doXMLParse(notityXml);
        outSteam.close();
        inStream.close();
        try {
            // 支付成功
            if ("SUCCESS".equals(ResultUtilMap.get("ResultUtil_code"))) {
                String attach = ResultUtilMap.get("attach");
                String out_trade_no = ResultUtilMap.get("out_trade_no");

                Map<String, String> attachMap = JSONObject.parseObject(attach, Map.class);
                Integer userId = Integer.valueOf(attachMap.get("userId"));
                Integer orderId = Integer.valueOf(attachMap.get("orderId"));

                boolean b = orderService.payOk(userId, orderId, out_trade_no);
                if (b) {

                    resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                            + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
                    BufferedOutputStream out = new BufferedOutputStream(
                            response.getOutputStream());
                    out.write(resXml.getBytes());
                    out.flush();
                    out.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String createSign(String characterEncoding, SortedMap<String,String> parameters,String key){
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + key);//最后加密时添加商户密钥，由于key值放在最后，所以不用添加到SortMap里面去，单独处理，编码方式采用UTF-8
        String sign = WebUtils.MD5(sb.toString()).toUpperCase();
        return sign;
    }


}
