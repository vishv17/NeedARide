package com.app.ride.authentication.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatListModel implements Serializable {
    ArrayList<String> userIds;
    String  conversationKey,username;
    String  lastMessage,updatedAt,requestId;
    public ChatListModel() { }

    public ChatListModel(String username, ArrayList<String> userIds, String conversationKey, String lastMessage, String updatedAt, String requestId) {
        this.username = username;
        this.userIds = userIds;
        this.conversationKey = conversationKey;
        this.lastMessage = lastMessage;
        this.updatedAt = updatedAt;
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<String> userIds) {
        this.userIds = userIds;
    }

    public String getConversationKey() {
        return conversationKey;
    }

    public void setConversationKey(String conversationKey) {
        this.conversationKey = conversationKey;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
