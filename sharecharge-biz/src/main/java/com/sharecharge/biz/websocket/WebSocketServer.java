package com.sharecharge.biz.websocket;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sanji
 */
@Slf4j
@Component
@ServerEndpoint(value = "/webSocket/{deviceCode}", configurator = WebSocketEndpointConfigure.class)
public class WebSocketServer {
    /**
     * ConcurrentHashMap用来存放每个客户端对应的WebSocketServer对象。
     */
    private static ConcurrentHashMap<String, Session> webSocketSet = new ConcurrentHashMap<String, Session>();

    //private static Multimap<String, Session> webSocketSet = ArrayListMultimap.create();

    public static void main(String[] args) {
//        Multimap<String, String> myMultimap = ArrayListMultimap.create();
//        IdentityHashMap map = new IdentityHashMap();
//        myMultimap.put("1CDZ022111020029", "1");
//        myMultimap.put("1CDZ022111020029", "2");
//        System.out.println(myMultimap);
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("deviceCode") String deviceCode) {
        log.info("一个客户端已连接：{},会话Id：{}",deviceCode,session.getId());
        webSocketSet.put(deviceCode, session);
        System.out.println(webSocketSet);
        //相关业务处理，根据拿到的用户ID判断其为那种角色，根据角色ID去查询是否有需要推送给该角色的消息，有则推送
//        List<String> totalPushMessages = new ArrayList<>();
//        totalPushMessages.add("欢迎进入");
//        if (!totalPushMessages.isEmpty()) {
//            totalPushMessages.forEach(e -> sendInfo(deviceCode, e));
//        }
    }

    public void sendMessage(String userId, String message) {
        try {
            Session currentSession = webSocketSet.get(userId);
            if (currentSession != null) {
                currentSession.getBasicRemote().sendText(message);
            }
//            Collection<Session> sessions = webSocketSet.get(userId);
//            if (!CollectionUtils.isEmpty(sessions)) {
//                for (Session session : sessions) {
//                    session.getBasicRemote().sendText(message);
//                }
//            }

        } catch (IOException e) {
            e.getMessage();
        }
    }

//    public static void sendMsg(LogType logType, String msg) {
//        //"ws://localhost:9124/webSocket/555"
//        WebSocketServer.sendInfo("123", logType.getLogType() + " " + msg);
//    }

    public static void sendMsg(String deviceCode, String msg) {
        //"ws://localhost:9124/webSocket/555"
        WebSocketServer.sendInfo(deviceCode, msg);
    }

    /**
     * 自定义消息
     */
    public static synchronized void sendInfo(String deviceCode, String message) {
        try {
            Session currentSession = webSocketSet.get(deviceCode);
            if (!Objects.isNull(currentSession)) {
                log.info(message);
                if (currentSession.isOpen()) {
                    currentSession.getBasicRemote().sendText(message);
                }
            }
//            Collection<Session> sessions = webSocketSet.get(deviceCode);
//            if (!CollectionUtils.isEmpty(sessions)) {
//                for (Session session : sessions) {
//                    if (session.isOpen()) {
//                        log.info("服务器发送消息{}", message);
//                        session.getBasicRemote().sendText(message);
//                    }
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户退出时，连接关闭调用的方法
     */
    public static void onCloseConection(String deviceCode, Session session) {
        // 从set中删除
        webSocketSet.remove(deviceCode, session);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        log.info("一个客户端关闭连接");
    }


    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("WebSocket接收到客户端消息: " + session.getId() + " 消息： " + message);
        if (session.isOpen()) {
            try {
                Map data=new HashMap();
                Map commentMap=new HashMap();
                commentMap.put("deviceCode","");
                commentMap.put("type",0);
                commentMap.put("commandContent","回复心跳检查");
                commentMap.put("commandRemarks","");
                commentMap.put("createTime",new Date());
                data.put("messageData",commentMap);
                session.getBasicRemote().sendText(JSONObject.toJSONString(data));
            } catch (IOException e) {
                log.info("回复心跳失败：{}",e.getMessage());
            }
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error(" 【启动日志】WebSocket错误: " + session.getId() + " 消息： " + error.getMessage());
    }


}
