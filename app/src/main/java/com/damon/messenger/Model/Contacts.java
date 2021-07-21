package com.damon.messenger.Model;

public class Contacts {

    public String name ,status,image,contact,receiver,sender,id,search,uid,phone,code,email;
    public Boolean selector = false;
    public Boolean escribiendo = false;

    public Contacts(){

    }

    public Boolean getEscribiendo() {
        return escribiendo;
    }

    public void setEscribiendo(Boolean escribiendo) {
        this.escribiendo = escribiendo;
    }

    public Contacts(String name, String status, String image, String contact, String receiver, String sender, String id, String search, String uid, String phone, String code, String email) {
        this.name = name;
        this.status = status;
        this.email = email;
        this.image = image;
        this.contact = contact;
        this.receiver = receiver;
        this.sender = sender;
        this.id = id;
        this.search = search;
        this.uid = uid;
        this.phone = phone;
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getSelector() {
        return selector;
    }

    public void setSelector(Boolean selector) {
        this.selector = selector;
    }
}
