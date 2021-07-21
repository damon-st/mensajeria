package com.damon.messenger.Model;

public class FondoModel {
    private String id, image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public FondoModel(String id, String image) {
        this.id = id;
        this.image = image;
    }

    public FondoModel() {
    }
}
