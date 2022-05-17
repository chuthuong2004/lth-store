package com.chuthuong.lthstore.response;

import com.chuthuong.lthstore.model.Cart;

import java.io.Serializable;

public class CartResponse implements Serializable {
    private boolean success;
    private String message;
    private Cart cart;

    public CartResponse() {
    }

    public CartResponse(boolean success, String message, Cart cart) {
        this.success = success;
        this.message = message;
        this.cart = cart;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }
}
