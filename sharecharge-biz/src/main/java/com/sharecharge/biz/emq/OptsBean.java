package com.sharecharge.biz.emq;

public class OptsBean {

    private int qos;

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    @Override
    public String toString() {
        return "OptsBean [qos=" + qos + "]";
    }

}
