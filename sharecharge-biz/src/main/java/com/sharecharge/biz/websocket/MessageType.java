package com.sharecharge.biz.websocket;

public enum MessageType {
    /**
     * 消息类型
     */
    CLIENT_SIDE("【设备端】"),
    WEB_SERVER("【服务器】");

    private String messageType;

    MessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageType() {
        return messageType;
    }
}
