package com.damon.messenger.Activitys;

public class VideoModel {
    private String  senderID,receiverID,ringing,calling,id;

    public VideoModel() {
    }

    public VideoModel(String senderID, String receiverID, String ringing, String calling,String id) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.ringing = ringing;
        this.calling = calling;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getRinging() {
        return ringing;
    }

    public void setRinging(String ringing) {
        this.ringing = ringing;
    }

    public String getCalling() {
        return calling;
    }

    public void setCalling(String calling) {
        this.calling = calling;
    }
}
