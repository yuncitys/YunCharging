package com.sharecharge.biz.service.device;


public interface MqttMessageService {

    /**
     * 接收消息
     *
     * @param
     */
    public void receiveMessage(String deviceCode, String mqttMessage, int length);

    /**
     * 发送mqtt消息
     *
     * @param mqttMessage
     * @throws
     */
    //public void sendMqttMessage(String topic, int qos, byte[] mqttMessage);



}
