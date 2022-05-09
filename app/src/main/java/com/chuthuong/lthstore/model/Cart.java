package com.chuthuong.lthstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Cart {
    @SerializedName("_id")
    private String id;
    private String user;
    private List<CartItem> cartItems;
    private String createdAt;
    private String updatedAt;

    public Cart(String id, String user, List<CartItem> cartItems, String createdAt, String updatedAt) {
        this.id = id;
        this.user = user;
        this.cartItems = cartItems;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Cart() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
