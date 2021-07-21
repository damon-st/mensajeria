package com.damon.messenger.Model;

import java.io.Serializable;

public class UserObject implements Serializable {

    private String  uid,
            name,
            phone,
            notificationKey,
    image,status,userState ,data,state,time,search,id,contact,receiver,sender,code
   , email
            ;

    private Boolean selected = false;


    public UserObject(String uid, String name, String email){
        this.uid = uid;
        this.name = name;
        this.email = email;
    }

    public UserObject(String uid){
        this.uid = uid;
    }

    public UserObject(String uid, String email, String name, String phone, String notificationKey, String image, String status, String userState, String data, String state, String time, String search, String id, String contact, String receiver, String sender, String code, Boolean selected) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.notificationKey = notificationKey;
        this.image = image;
        this.status = status;
        this.userState = userState;
        this.data = data;
        this.state = state;
        this.time = time;
        this.search = search;
        this.id = id;
        this.contact = contact;
        this.receiver = receiver;
        this.sender = sender;
        this.code = code;
        this.selected = selected;
        this.email = email;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUid() {
        return uid;
    }
    public String getPhone() {
        return phone;
    }
    public String getName() {
        return name;
    }
    public String getNotificationKey() {
        return notificationKey;
    }
    public Boolean getSelected() {
        return selected;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setNotificationKey(String notificationKey) {
        this.notificationKey = notificationKey;
    }
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
