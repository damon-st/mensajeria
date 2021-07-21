package com.damon.messenger;

import java.util.ArrayList;

public class MessageObject {

    String  messageId,
            senderId,
            message;

    ArrayList<String> mediaUrlList;
    String imageUrl;
    String creator;
    String chat;


    public MessageObject(String messageId, String senderId, String message, ArrayList<String> mediaUrlList, String imageUrl, String creator,String chat) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.mediaUrlList = mediaUrlList;
        this.imageUrl = imageUrl;
        this.creator = creator;
        this.chat = chat;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMessageId() {
        return messageId;
    }
    public String getSenderId() {
        return senderId;
    }
    public String getMessage() {
        return message;
    }
    public ArrayList<String> getMediaUrlList() {
        return mediaUrlList;
    }
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMediaUrlList(ArrayList<String> mediaUrlList) {
        this.mediaUrlList = mediaUrlList;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public MessageObject() {
    }
}
