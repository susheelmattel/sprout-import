package com.fuhu.pipeline.mqtt;

public interface MqttAction {
    int CONNECT = 1;
    int SUBSCRIBE = 2;
    int PUBLISH_MESSAGE = 3;
    int UNSUBSCRIBE = 4;
    int DISCONNECT = 5;
}
