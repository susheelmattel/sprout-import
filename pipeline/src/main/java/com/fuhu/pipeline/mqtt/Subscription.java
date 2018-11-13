package com.fuhu.pipeline.mqtt;


public class Subscription {
    private String topic;
    private int qos;
    private String lastMessage;
    private boolean enableNotifications;

    public Subscription(String topic, int qos){
        this.topic = topic;
        this.qos = qos;
        this.enableNotifications = true;
    }

    public Subscription(String topic, int qos, boolean enableNotifications){
        this.topic = topic;
        this.qos = qos;
        this.enableNotifications = enableNotifications;
    }

    /**
     * Get the topic name.
     * @return topic name
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Set the topic name.
     * @param topic name
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * Get the quality of service at which to subscribe.
     * @return
     */
    public int getQos() {
        return qos;
    }

    /**
     * Set the quality of service at which to subscribe.
     * @param qos
     */
    public void setQos(int qos) {
        this.qos = qos;
    }

    /**
     * Get the last message of this subscription.
     * @return
     */
    public String getLastMessage() {
        return lastMessage;
    }

    /**
     * Set the last message of this subscription.
     * @param lastMessage
     */
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    /**
     * Check if the notifications is enabled.
     * @return enabled or disabled.
     */
    public boolean isEnableNotifications() {
        return enableNotifications;
    }

    /**
     * Set the notification status.
     * @param enableNotifications enable or disable
     */
    public void setEnableNotifications(boolean enableNotifications) {
        this.enableNotifications = enableNotifications;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "topic='" + topic + '\'' +
                ", qos=" + qos +
                ", enableNotifications='" + enableNotifications + '\''+
                '}';
    }
}
