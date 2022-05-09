package com.chuthuong.lthstore.model;

import com.google.gson.annotations.SerializedName;

public class ProductDetailColor {
    @SerializedName("_id")
    private String id;
    private String color;
    private int amount;

    public ProductDetailColor() {
    }

    public ProductDetailColor(String id, String color, int amount) {
        this.id = id;
        this.color = color;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
