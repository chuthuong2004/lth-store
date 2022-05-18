package com.chuthuong.lthstore.response;

import com.chuthuong.lthstore.model.Order;

public class OrderResponse {
    private boolean success;
    private String message;
    private Order order;

    public OrderResponse() {
    }

    public OrderResponse(boolean success, String message, Order order) {
        this.success = success;
        this.message = message;
        this.order = order;
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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
