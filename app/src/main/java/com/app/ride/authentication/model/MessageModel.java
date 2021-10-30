package com.app.ride.authentication.model;

import java.io.Serializable;

public class MessageModel implements Serializable {
    String message, senderId, receiverId;
    Boolean Seen;
    Integer type;
    Long createdAt;
    public MessageModel() { }
    public Boolean getSeen() {
        return Seen;
    }

    public void setSeen(Boolean seen) {
        Seen = seen;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public MessageModel(String message, String senderId, String receiverId, Boolean seen, Integer type, Long createdAt) {
        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
        Seen = seen;
        this.type = type;
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }


}
