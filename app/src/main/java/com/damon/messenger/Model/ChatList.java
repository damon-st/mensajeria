package com.damon.messenger.Model;

public class ChatList {

    public String id,nombre,users,image,creador;

    public ChatList(String id,String nombre,String users,String image,String creador) {
        this.id = id;
        this.nombre = nombre;
        this.users = users;
        this.image =image;
        this.creador = creador;
    }

    public String getCreador() {
        return creador;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public ChatList() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
