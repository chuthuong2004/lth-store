package com.chuthuong.lthstore.model;

import com.google.gson.annotations.SerializedName;

public class ProductImage {
    @SerializedName("_id")
    private String id;
    private String img;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public ProductImage() {
    }

    public ProductImage(String id, String img) {
        this.id = id;
        this.img = img;
    }
}
