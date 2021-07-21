package com.damon.messenger.Model;

import java.util.Date;

public class Messages {

    private String from, message, type,to,messageID,time,date,name,sender,receiver,type_responder,msg_responder_nombre_responder,msg_sender_responder,msgImage;
    private boolean isseen;
    private int position;
    private long fecha;


    public Messages() {
    }

    public Messages(String from, String message, String type, String to, String messageID, String time, String date, String name, String sender, String receiver, String type_responder, String msg_responder_nombre_responder, String msg_sender_responder, String msgImage, boolean isseen, int position, long fecha) {
        this.from = from;
        this.message = message;
        this.type = type;
        this.to = to;
        this.messageID = messageID;
        this.time = time;
        this.date = date;
        this.name = name;
        this.sender = sender;
        this.receiver = receiver;
        this.type_responder = type_responder;
        this.msg_responder_nombre_responder = msg_responder_nombre_responder;
        this.msg_sender_responder = msg_sender_responder;
        this.msgImage = msgImage;
        this.isseen = isseen;
        this.position = position;
        this.fecha = fecha;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getType_responder() {
        return type_responder;
    }

    public void setType_responder(String type_responder) {
        this.type_responder = type_responder;
    }

    public String getMsg_responder_nombre_responder() {
        return msg_responder_nombre_responder;
    }

    public void setMsg_responder_nombre_responder(String msg_responder_nombre_responder) {
        this.msg_responder_nombre_responder = msg_responder_nombre_responder;
    }

    public String getMsg_sender_responder() {
        return msg_sender_responder;
    }

    public void setMsg_sender_responder(String msg_sender_responder) {
        this.msg_sender_responder = msg_sender_responder;
    }

    public String getMsgImage() {
        return msgImage;
    }

    public void setMsgImage(String msgImage) {
        this.msgImage = msgImage;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public Date getFechaTransfor(){
        Date date = new Date();
        date.setTime(getFecha());
        return date;
    }
}
