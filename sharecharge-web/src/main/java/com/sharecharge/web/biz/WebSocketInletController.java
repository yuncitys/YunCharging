package com.sharecharge.web.biz;

import com.sharecharge.core.util.ResultUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("sys/websocket")
public class WebSocketInletController {

    @Value("${server.port}")
    private String webSocketPort;

    @RequestMapping("webSocketConUrl")
    public ResultUtil getWebSocketUrl(@RequestParam("deviceCode") String deviceCode){
        Map<String, Object> map = new HashMap<>(2);
        map.put("webSocketUrl", "ws://" + "127.0.0.1" + ":" + webSocketPort + "/webSocket/" + deviceCode);// 充电桩
        return ResultUtil.success(map);
    }
}
