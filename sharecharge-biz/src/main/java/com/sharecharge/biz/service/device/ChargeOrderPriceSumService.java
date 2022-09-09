package com.sharecharge.biz.service.device;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sharecharge.biz.entity.*;
import com.sharecharge.biz.mapper.*;
import com.sharecharge.biz.vo.ChargeOrderVo;
import com.sharecharge.biz.websocket.WebSocketServer;
import com.sharecharge.core.util.Crc16Util;
import com.sharecharge.core.util.HexUtils;
import com.sharecharge.core.util.MessagePushUtils;
import com.sharecharge.system.entity.DbAdminUser;
import com.sharecharge.system.entity.DbSharingConfig;
import com.sharecharge.system.mapper.DbAdminUserMapper;
import com.sharecharge.system.service.DbParentOrSonService;
import com.sharecharge.system.service.DbSharingConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChargeOrderPriceSumService {
    final DeviceMapper deviceMapper;
    final AppUserMapper appUserMapper;
    final ChargeOrderMapper chargeOrderMapper;
    final PowerRecordMapper powerRecordMapper;
    final DbAdminUserMapper adminUserMapper;
    final CardMapper cardMapper;
    //final MessageService messageService;
    final MqttService mqttService;
    final DbParentOrSonService parentOrSonService;
    final DbSharingConfigService sharingConfigService;


    /**
     * 用户主动关闭订单
     *
     * @param deviceCode
     * @param isSuccess
     * @param port
     * @param mqttMessage
     * @throws Exception
     */
    public void userClose(String deviceCode, String isSuccess, String port, String mqttMessage){
        if (isSuccess.equals("01")) {
            log.info("用户主动关闭订单：设备号：{}，端口号：{}，原因：{}", deviceCode, port, mqttMessage);
            Map<String,Object> deviceS = new HashMap();
            deviceS.put("deviceCode", deviceCode);
            deviceS.put("port" + port, 0);
            deviceMapper.updateDevice(deviceS);
            deviceS.put("orderStatus", 1);
            deviceS.put("page", 0);
            deviceS.put("limit", 1);
            deviceS.put("port", port);
            List<ChargeOrderVo> lastOrder = chargeOrderMapper.findLastOrder(deviceS);
            if (!lastOrder.isEmpty()) {
                log.info("待计费订单：{}",lastOrder.get(0).toString());
                /************************************************************************************************
                 *
                 * 计费
                 *
                 * **********************************************************************************************/
                ChargeOrderVo orderInfoVo = chargingAmount(lastOrder.get(0));
                log.info("计费后：{}",orderInfoVo.toString());

                /************************************************************************************************
                 *
                 * 扣费
                 *
                 * **********************************************************************************************/
                ChargeOrderVo order = feeDeduction(orderInfoVo);
                log.info("扣费后：{}",order.toString());
                //修改订单状态订单支付金额
                ChargeOrder chargeOrder=new ChargeOrder();
                chargeOrder.setId(order.getId());
                chargeOrder.setOrderStatus(8);
                chargeOrder.setActualPrice(new BigDecimal(order.getActualPrice()));
                chargeOrder.setRealityPayMoney(new BigDecimal(order.getRealityPayMoney()));
                chargeOrderMapper.updateById(chargeOrder);

                /************************************************************************************************
                 *
                 * 代理商分成
                 *
                 * **********************************************************************************************/
                log.info("待分成金额:{}", order.getRealityPayMoney());
                Device device = deviceMapper.selectOne(new QueryWrapper<Device>().lambda().eq(Device::getDeviceCode, deviceCode));
                if (!Objects.isNull(device)){
                    adminCashSeparate(device,chargeOrder);
                }
                log.info("设备不存在");

                /************************************************************************************************
                 *
                 * 充电完成消息推送
                 *
                 * **********************************************************************************************/
                // TODO: 2022/7/19  结束充电消息推送
                //String WECHAT_ACCESS_TOKEN = sharingConfigService.getGlobalConfig(DbSharingConfig.WECHAT_ACCESS_TOKEN);
                //MessagePushUtils.messageJSCD(order.getWxOpenId(),order.getStartTime(),new Date(),"用户主动关闭",order.getActualPrice(),order.getCash(),WECHAT_ACCESS_TOKEN);
            }
        }
        Map<String,Object> data = new HashMap();
        Map<String,Object> commentMap = new HashMap();
        commentMap.put("deviceCode", deviceCode);
        commentMap.put("type", 0);
        commentMap.put("commandContent", mqttMessage);
        commentMap.put("createTime", new Date());
        commentMap.put("commandRemarks", "关闭" + port + "端口");
        data.put("messageType", 1);
        data.put("messageData", commentMap);
        WebSocketServer.sendMsg(deviceCode, JSONUtils.toJSONString(data));
    }


    /**
     * 设备主动关闭
     *
     * @param deviceCode
     * @param isSuccess
     * @param port
     * @param mqttMessage
     * @throws Exception
     */
    public void deviceClose(String deviceCode, String isSuccess, String port, String mqttMessage){
        log.info("设备主动关闭订单：设备号：{}，端口号：{}，原因：{}", deviceCode, port, mqttMessage);

        Map<String,Object> commentMap = new HashMap();
        Map<String,Object> deviceMap = new HashMap();
        deviceMap.put("deviceCode", deviceCode);
        deviceMap.put("port" + port, 0);
        deviceMapper.updateDevice(deviceMap);
        deviceMap.put("orderStatus", 1);
        deviceMap.put("page", 0);
        deviceMap.put("limit", 1);
        deviceMap.put("port", port);
        List<ChargeOrderVo> lastOrder = chargeOrderMapper.findLastOrder(deviceMap);
        if (!lastOrder.isEmpty()) {
            ChargeOrderVo order = lastOrder.get(0);
            log.info("待计费订单：{}",order.toString());
            if (!isSuccess.equals("01")){
                /************************************************************************************************
                 *
                 * 计费
                 *
                 * **********************************************************************************************/
                ChargeOrderVo orderInfoVo = chargingAmount(order);
                log.info("计费后：{}",orderInfoVo.toString());


                /************************************************************************************************
                 *
                 * 扣费
                 *
                 * **********************************************************************************************/
                ChargeOrderVo feeOrder = feeDeduction(orderInfoVo);
                log.info("扣费后：{}",feeOrder.toString());

                /************************************************************************************************
                 *
                 * 修改订单状态 0x01计量完成；=0x02功率过低；=0x03功率过高;=0x04 零功率（未插充电器充电或保险丝断）；=0x05充电器被拔出（功率正常时突然为0W判被拔出）
                 *
                 * **********************************************************************************************/
                switch (isSuccess) {
                    case "02":
                        isSuccess = "3";
                        break;
                    case "03":
                        isSuccess = "4";
                        break;
                    case "04":
                        isSuccess = "5";
                        break;
                    case "05":
                        isSuccess = "6";
                        break;
                }
                ChargeOrder chargeOrder=new ChargeOrder();
                chargeOrder.setId(order.getId());
                chargeOrder.setOrderStatus(Integer.valueOf(isSuccess));
                chargeOrder.setActualPrice(new BigDecimal(feeOrder.getActualPrice()));
                chargeOrder.setRealityPayMoney(new BigDecimal(feeOrder.getRealityPayMoney()));
                chargeOrderMapper.updateById(chargeOrder);

                /************************************************************************************************
                 *
                 * 代理商分成
                 *
                 * **********************************************************************************************/
                log.info("带分成金额:{}", order.getRealityPayMoney());
                Device device = deviceMapper.selectOne(new QueryWrapper<Device>().lambda().eq(Device::getDeviceCode, deviceCode));
                adminCashSeparate(device,chargeOrder);
            }else{
                log.info("计量完成扣费");
                /************************************************************************************************
                 *
                 * 计费
                 *
                 * **********************************************************************************************/
                order.setActualPrice(order.getTotalPrice());

                /************************************************************************************************
                 *
                 * 扣费
                 *
                 * **********************************************************************************************/
                ChargeOrderVo feeOrder = feeDeduction(order);
                log.info("扣费后订单：{}",feeOrder.toString());

                /************************************************************************************************
                 *
                 * 修改订单状态 0x01计量完成；=0x02功率过低；=0x03功率过高;=0x04 零功率（未插充电器充电或保险丝断）；=0x05充电器被拔出（功率正常时突然为0W判被拔出）
                 *
                 * **********************************************************************************************/
                ChargeOrder chargeOrder=new ChargeOrder();
                chargeOrder.setId(order.getId());
                chargeOrder.setOrderStatus(2);
                chargeOrder.setActualPrice(new BigDecimal(feeOrder.getActualPrice()));
                chargeOrder.setRealityPayMoney(new BigDecimal(feeOrder.getRealityPayMoney()));
                chargeOrderMapper.updateById(chargeOrder);

                /************************************************************************************************
                 *
                 * 代理商分成
                 *
                 * **********************************************************************************************/
                log.info("带分成金额:{}", order.getRealityPayMoney());
                Device device = deviceMapper.selectOne(new QueryWrapper<Device>().lambda().eq(Device::getDeviceCode, deviceCode));
                adminCashSeparate(device,chargeOrder);
            }
            /************************************************************************************************
             *
             * 充电完成消息推送
             *
             * **********************************************************************************************/
            switch (isSuccess) {
                case "2":
                    isSuccess = "充满自停";
                    break;
                case "3":
                    isSuccess = "功率过低";
                    break;
                case "4":
                    isSuccess = "功率过高";
                    break;
                case "5":
                    isSuccess = "零功率（未插充电器充电或保险丝断）";
                    break;
                case "6":
                    isSuccess = "充电器被拔出";
                    break;
            }
            // TODO: 2022/7/19 充电完成消息推送
            //String WECHAT_ACCESS_TOKEN = sharingConfigService.getGlobalConfig(DbSharingConfig.WECHAT_ACCESS_TOKEN);
            //MessagePushUtils.messageJSCD(order.getWxOpenId(),order.getStartTime(),new Date(),isSuccess,order.getActualPrice(),order.getCash(),WECHAT_ACCESS_TOKEN);
        }
        /************************************************************************************************
         *
         * 下发指令
         *
         * **********************************************************************************************/
        switch (isSuccess) {
            case "01":
                isSuccess = "计量完成";
                break;
            case "02":
                isSuccess = "功率过低";
                break;
            case "03":
                isSuccess = "功率过高";
                break;
            case "04":
                isSuccess = "零功率（未插充电器充电或保险丝断）";
                break;
            case "05":
                isSuccess = "充电器被拔出";
                break;
        }
        Map<String,Object> data = new HashMap();
        commentMap.put("deviceCode", deviceCode);
        commentMap.put("type", 0);
        commentMap.put("commandContent", mqttMessage);
        commentMap.put("createTime", new Date());
        commentMap.put("commandRemarks", "设备" + port + "主动停机原因:" + isSuccess);
        data.put("messageType", 1);
        data.put("messageData", commentMap);
        WebSocketServer.sendMsg(deviceCode, JSONUtils.toJSONString(data));

        String ports = String.format("%02x", Integer.valueOf(port));
        String head = "2A07B1" + ports;
        byte[] bytes = HexUtils.hexStringToBytes(head);
        String crc = Crc16Util.getCRC(bytes);
        String crcs = HexUtils.HighAndLowSwap(crc);
        mqttService.sendMqttMessage("/down/" + deviceCode, 1, HexUtils.hexStringToBytes(head + crcs + "23"));

        commentMap.put("deviceCode", deviceCode);
        commentMap.put("type", 1);
        commentMap.put("commandContent", head + crcs + "23");
        commentMap.put("createTime", new Date());
        commentMap.put("commandRemarks", "主动停机回复:" + head + crcs + "23");
        data.put("messageType", 1);
        data.put("messageData", commentMap);
        WebSocketServer.sendMsg(deviceCode, JSONUtils.toJSONString(data));
    }


    public ChargeOrderVo chargingAmount(ChargeOrderVo order) {
        long min = DateUtil.between(order.getStartTime(), new Date(), DateUnit.MINUTE);

        //小时
        BigDecimal hour = new BigDecimal(min/60.00);

        log.info("充电时长：{}",min);

        double money = 0.00;

        DevicePrice devicePrice = order.getDevicePrice();
        log.info("设备收费方案:{}", JSONObject.toJSONString(devicePrice));

        if (min <= 1) {
            log.info("使用时间小于一分钟收费：{}",money);
            order.setActualPrice(money);
            return order;
        }
        if (devicePrice.getPriceContentList().isEmpty()) {
            money = min * 0.08;
            log.info("未设置方案计费：{}",money);
            order.setActualPrice(money);
            return order;
        }

        List<PriceContent> priceContentList = devicePrice.getPriceContentList();
        //0 时间 1电量 2功率
        if (devicePrice.getPriceType() == 0) {
            //收费方案
            Map<Long, PriceContent> doubleMap = new HashedMap();
            for (PriceContent price : priceContentList ) {
                if (min <= (Integer.valueOf(price.getDuration()) * 60)) {
                    log.info("收费方案：{}",price.toString());
                    doubleMap.put(min, price);
                    break;
                }
            }
            PriceContent priceContent = doubleMap.get(min);
            if (Objects.isNull(priceContent)) {
                money = order.getTotalPrice();
                log.info("未有匹配的收费挡位：{}",money);
                order.setActualPrice(money);
                return order;
            }
            //每小时费用
            BigDecimal price = new BigDecimal(priceContent.getMoney() / Double.valueOf(priceContent.getDuration()));
            if (devicePrice.getRealTimeCharging() == 0) {//0:实时1分钟收费 1:实时30分钟收费
                if (min < Integer.valueOf(priceContent.getDuration()) * 60) {
                    money = hour.multiply(price).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                }
                if (min == Integer.valueOf(priceContent.getDuration()) * 60) {
                    money = priceContent.getMoney();
                }
                if (money >= order.getTotalPrice()) {//priceContent.getMoney()
                    money = order.getTotalPrice();
                }
            } else if (devicePrice.getRealTimeCharging() == 1) {
                if (min < Integer.valueOf(priceContent.getDuration()) * 60) {
                    //有多少个30分钟
                    BigDecimal divide = hour.divide(new BigDecimal(0.5));
                    //向上取整
                    double numHalf = Math.ceil(divide.doubleValue());
                    //几个小时
                    double numHour = numHalf * 30 / 60;
                    money =  price.multiply(new BigDecimal(numHour)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                }
                if (min == Integer.valueOf(priceContent.getDuration()) * 60) {
                    money = priceContent.getMoney();
                }
                if (money >= order.getTotalPrice()) {//priceContent.getMoney()
                    money = order.getTotalPrice();
                }
            }
        } else if (devicePrice.getPriceType() == 1) {
            String syDl = powerRecordMapper.selectLastSyTime(order.getDeviceCode(), order.getDevicePort().toString());
            Double syDls = (Double.valueOf(syDl));
            log.info("剩余电量：{}", syDls);

            Map<Long, PriceContent> doubleMap = new HashedMap();
            for (PriceContent price : priceContentList) {
                if (order.getHour() == Integer.valueOf(price.getDuration())) {
                    doubleMap.put(min, price);
                    break;
                }
            }
            PriceContent priceContent = doubleMap.get(min);
            if (!Objects.isNull(priceContent)) {
                if (syDls.intValue() <= Integer.valueOf(priceContent.getDuration())) {
                    double waterPrice = new BigDecimal(
                            (priceContent.getMoney() / (Integer.valueOf(priceContent.getDuration())))
                    ).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    money = (Integer.valueOf(priceContent.getDuration()) - syDls.intValue()) * waterPrice;
                    if (money == 0.00) {
                        money = 0.5;
                    }
                }
                if (syDls.intValue() == 0) {
                    money = order.getTotalPrice();
                }
                if (money >= priceContent.getMoney()) {
                    money = priceContent.getMoney();
                }
            } else {
                double waterPrice = order.getTotalPrice() / order.getHour();
                money = (order.getHour() - (syDls.intValue() * 0.01)) * waterPrice;
            }
        } else if (devicePrice.getPriceType() == 2) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startTime = simpleDateFormat.format(order.getCreateTime());
            String curTime = simpleDateFormat.format(new Date());
            Map<String,Object> map = new HashMap();
            map.put("deviceCode", order.getDeviceCode());
            map.put("port", order.getDevicePort());
            map.put("startTime", startTime);
            map.put("curTime", curTime);
            Map selectPower = powerRecordMapper.selectPower(map);
            double power;
            /*if (devicePrice.getChargeType() == 0) {
                power = new BigDecimal(selectPower.get("avgPower").toString()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                log.info("平均功率：{}", power);
            } else {
                power = new BigDecimal(selectPower.get("maxPower").toString()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                log.info("最大功率：{}", power);
            }*/
            power = new BigDecimal(selectPower.get("avgPower").toString()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            log.info("平均功率：{}", power);

            if (devicePrice.getRealTimeCharging() == 1) {
                for (PriceContent priceContent : priceContentList) {
                    if (Double.valueOf(power) >= Double.valueOf(priceContent.getPowerSectionBefore())
                            && Double.valueOf(power) <= Double.valueOf(priceContent.getPowerSectionAfter())) {

                        log.info("收费方案：{}",priceContent.toString());

                        //每小时费用
                        BigDecimal price = new BigDecimal(priceContent.getMoney() / Double.valueOf(priceContent.getDuration()));
                        //有几个30分钟
                        BigDecimal divide = hour.divide(new BigDecimal(0.5));
                        double numHalf = Math.ceil(divide.doubleValue());
                        double numHour = numHalf * 30 / 60;
                        money =  price.multiply(new BigDecimal(numHour)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        break;
                    }
                }
            } else if (devicePrice.getRealTimeCharging() == 0) {
                for (PriceContent priceContent : priceContentList) {
                    if (Double.valueOf(power) >= Double.valueOf(priceContent.getPowerSectionBefore())
                            && Double.valueOf(power) <= Double.valueOf(priceContent.getPowerSectionAfter())) {

                        log.info("收费方案：{}",priceContent.toString());

                        BigDecimal price = new BigDecimal(priceContent.getMoney() / Double.valueOf(priceContent.getDuration()));
                        money = hour.multiply(price).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        break;
                    }
                }
            }
        }
        order.setActualPrice(money);
        return order;
    }


    public ChargeOrderVo feeDeduction(ChargeOrderVo order) {
        double money = order.getActualPrice();
        log.info("待支付金额:{}", money);
        double realMoney = 0.00;
        if (order.getOrderType() == 0) {
            Card cardByCardNo = cardMapper.selectOne(new QueryWrapper<Card>().lambda().eq(Card::getCardNo,order.getCardNo()));
            log.info("卡号：{}",cardByCardNo);
            Double cash = cardByCardNo.getCardCash();
            Double realityPayMoney = cardByCardNo.getRealityPayMoney();
            Double giveMoney = cardByCardNo.getGiveMoney();
            log.info("金额：{}，实际支付金额：{}，增送金额：{}", cash.doubleValue(), realityPayMoney.doubleValue(), giveMoney.doubleValue());

            if (realityPayMoney - money >= 0) {
                realMoney = money;
                cardByCardNo.setCardCash(cash - money);
                cardByCardNo.setRealityPayMoney(realityPayMoney-money);
                cardByCardNo.setGiveMoney(giveMoney);
                cardMapper.updateById(cardByCardNo);

                log.info("实际金额支付：{}", realMoney);
            } else if (giveMoney > 0){
                realMoney = realityPayMoney;
                double remain = money - realityPayMoney;//剩余支付金额
                cardByCardNo.setCardCash(cash - money);
                cardByCardNo.setRealityPayMoney(0.00);
                cardByCardNo.setGiveMoney(giveMoney - remain);
                cardMapper.updateById(cardByCardNo);

                log.info("赠送金额支付---实际支付金额：{}，赠送支付金额：{}", realityPayMoney, remain);
            }else {
                realMoney = realityPayMoney;
                cardByCardNo.setCardCash(cash - money);
                cardByCardNo.setRealityPayMoney(0.00);
                cardByCardNo.setGiveMoney(0.00);
                cardMapper.updateById(cardByCardNo);
            }
        } else {
            AppUser appUser = appUserMapper.selectById(order.getUserId());
            log.info("金额：{}，实际支付金额：{}，赠送金额：{}", appUser.getCash().doubleValue(), appUser.getRealityPayMoney(), appUser.getGiveMoney());
            appUser.setCash(appUser.getCash() - money);

            if (appUser.getRealityPayMoney() - money >= 0) {
                realMoney = money;
                appUser.setRealityPayMoney(appUser.getRealityPayMoney() - money);

                log.info("实际金额支付：{}", realMoney);
            } else if (appUser.getGiveMoney() > 0){
                realMoney = appUser.getRealityPayMoney();
                double remain = money - appUser.getRealityPayMoney();
                appUser.setRealityPayMoney(0.00);
                appUser.setGiveMoney(appUser.getGiveMoney() - remain);

                log.info("赠送金额支付---实际支付金额：{}，赠送支付金额：{}", realMoney, remain);
            }else {
                realMoney = appUser.getRealityPayMoney();
                appUser.setRealityPayMoney(0.00);
                appUser.setGiveMoney(0.00);
            }
            appUserMapper.updateById(appUser);
        }
        order.setRealityPayMoney(realMoney);
        return  order;
    }


    public void adminCashSeparate(Device device,ChargeOrder order) {
        BigDecimal totalPrice=order.getRealityPayMoney();

        Integer thirdAgentId = device.getFirstAgentId();
        Integer secondAgentId = device.getSecondAgentId();
        Integer firstAgentId = device.getThirdAgentId();
        log.info("一级代理：{}，二级代理：{}，三级代理：{}", firstAgentId, secondAgentId, thirdAgentId);

        BigDecimal thirdShouyi = new BigDecimal("0");
        BigDecimal secondShouyi = new BigDecimal("0");
        BigDecimal firstShouyi = new BigDecimal("0");
        BigDecimal adminShouyi = new BigDecimal("0");
        if (!Objects.isNull(thirdAgentId)) {//三级代理
            DbAdminUser adminUserById = adminUserMapper.selectById(thirdAgentId);
            String value = String.valueOf(adminUserById.getInterestRate());
            thirdShouyi = totalPrice.multiply(new BigDecimal(value));
            adminUserById.setBalanceAmount(adminUserById.getBalanceAmount().add(thirdShouyi));
            adminUserById.setTotalAmount(adminUserById.getTotalAmount().add(thirdShouyi));
            adminUserMapper.updateById(adminUserById);
        }
        if (!Objects.isNull(secondAgentId)) {//二级代理
            DbAdminUser adminUserById = adminUserMapper.selectById(thirdAgentId);
            String value = String.valueOf(adminUserById.getInterestRate());
            secondShouyi = totalPrice.multiply(new BigDecimal(value));
            if (thirdShouyi.equals(BigDecimal.ZERO)) {
                adminUserById.setBalanceAmount(adminUserById.getBalanceAmount().add(secondShouyi));
                adminUserById.setTotalAmount(adminUserById.getTotalAmount().add(secondShouyi));
                adminUserMapper.updateById(adminUserById);
            } else {
                secondShouyi = secondShouyi.subtract(thirdShouyi);
                adminUserById.setBalanceAmount(adminUserById.getBalanceAmount().add(secondShouyi));
                adminUserById.setTotalAmount(adminUserById.getTotalAmount().add(secondShouyi));
                adminUserMapper.updateById(adminUserById);
            }
        }
        if (!Objects.isNull(firstAgentId)) {//一级代理
            DbAdminUser adminUserById = adminUserMapper.selectById(thirdAgentId);
            String value = String.valueOf(adminUserById.getInterestRate());
            firstShouyi = totalPrice.multiply(new BigDecimal(value));
            if (secondShouyi.equals(BigDecimal.ZERO)) {
                adminUserById.setBalanceAmount(adminUserById.getBalanceAmount().add(firstShouyi));
                adminUserById.setTotalAmount(adminUserById.getTotalAmount().add(firstShouyi));
                adminUserMapper.updateById(adminUserById);
            } else {
                firstShouyi = firstShouyi.subtract(secondShouyi);
                adminUserById.setBalanceAmount(adminUserById.getBalanceAmount().add(firstShouyi));
                adminUserById.setTotalAmount(adminUserById.getTotalAmount().add(firstShouyi));
                adminUserMapper.updateById(adminUserById);
            }
        }
        adminShouyi = totalPrice.subtract(firstShouyi.add(secondShouyi).add(thirdShouyi));
        log.info("一级代理收益：{}，二级代理收益：{}，三级代理收益：{}，平台收益：{}", firstShouyi, secondShouyi, thirdShouyi, adminShouyi);
        order.setFirstAgentProfit(firstShouyi.doubleValue());
        order.setSecondAgentProfit(secondShouyi.doubleValue());
        order.setThirdAgentProfit(thirdShouyi.doubleValue());
        order.setProxyMoneyStatus(1);
        chargeOrderMapper.updateById(order);
    }
}
