package com.sharecharge.biz.emq;

public enum EmqActionEnum {
    client_connected, client_disconnected, client_subscribe, client_unsubscribe, session_created, session_subscribed,
    session_unsubscribed, session_terminated, message_publish, message_delivered, message_acked

}
