package com.sharecharge.biz.emq;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmqActionBean {

    private String action;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("username")
    private String userName;

    @JsonProperty("from_client_id")
    private String fromClientId;

    @JsonProperty("from_username")
    private String fromUserName;

    private String topic;

    private int qos;

    private boolean retain;

    @JsonProperty("payload")
    private String deviceMessage;

    @JsonProperty("ts")
    private long timestamp;

    @JsonProperty("conn_ack")
    private int connectAck;

    private String reason;

    private OptsBean opts;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFromClientId() {
        return fromClientId;
    }

    public void setFromClientId(String fromClientId) {
        this.fromClientId = fromClientId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean isRetain() {
        return retain;
    }

    public void setRetain(boolean retain) {
        this.retain = retain;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getConnectAck() {
        return connectAck;
    }

    public void setConnectAck(int connectAck) {
        this.connectAck = connectAck;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public OptsBean getOpts() {
        return opts;
    }

    public void setOpts(OptsBean opts) {
        this.opts = opts;
    }

    @Override
    public String toString() {
        return "EmqActionBean [action=" + action + ", clientId=" + clientId + ", userName=" + userName
                + ", fromClientId=" + fromClientId + ", fromUserName=" + fromUserName + ", topic=" + topic + ", qos="
                + qos + ", retain=" + retain + ", deviceMessage=" + getDeviceMessage() + ", timestamp=" + timestamp
                + ", connectAck=" + connectAck + ", reason=" + reason + ", opts=" + opts + "]";
    }

    public String getDeviceMessage() {
        return deviceMessage;
    }

    public void setDeviceMessage(String deviceMessage) {
        this.deviceMessage = deviceMessage;
    }

}
