package com.chuthuong.lthstore.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CartItem implements Serializable {
    @SerializedName("_id")
    private String id;
    private Product product;
    private int quantity;
    private String size;
    private String color;

    public CartItem() {
    }

    public CartItem(String id, Product product, int quantity, String size, String color) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.size = size;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id='" + id + '\'' +
                ", product=" + product +
                ", quantity=" + quantity +
                ", size='" + size + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
