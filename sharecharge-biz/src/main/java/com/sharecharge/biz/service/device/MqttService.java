package com.sharecharge.biz.service.device;

import com.sharecharge.biz.mqtt.MqttClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.QoS;
import org.omg.CORBA.SystemException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MqttService {
    final MqttClient mqttClient;
    /**
     * 发送mqtt消息
     *
     * @param mqttMessage
     * @throws
     */
    public void sendMqttMessage(String topic, int qos, byte[] mqttMessage) throws SystemException {
        log.debug("***** 发送mqtt消息。topic: {}, qos: {}, message: {}", topic, qos, mqttMessage);
        CallbackConnection callbackConnection = mqttClient.getCallbackConnection();
        callbackConnection.publish(topic, mqttMessage, QoS.AT_MOST_ONCE, false,
                new Callback<Void>() {
                    public void onSuccess(Void v) {
                        System.out.println("========发布信息成功2***=======");
                    }

                    public void onFailure(Throwable value) {
                        System.out.println("Message========发布失败=======" + value.getMessage());
                        callbackConnection.disconnect(null); // 发布失败
                    }
                });
    }
}
