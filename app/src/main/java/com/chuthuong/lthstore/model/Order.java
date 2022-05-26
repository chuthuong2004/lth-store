package com.chuthuong.lthstore.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    @SerializedName("_id")
    private String id;
    private OrderShippingInfo shippingInfo;
    private List<OrderItem> orderItems;
    private String user;
    private int taxPrice;
    private int shippingPrice;
    private int totalPrice;
    private String orderStatus;
    private String canceledReason;
    private boolean commented;
    private Date createdAt;
    private Date updatedAt;
    private Date canceledAt;
    private Date shippingAt;
    private Date deliveryAt;
    private Date deliveredAt;

    public Order() {
    }

    public Order(String id, OrderShippingInfo shippingInfo, List<OrderItem> orderItems, String user, int taxPrice, int shippingPrice, int totalPrice, String orderStatus, String canceledReason, boolean commented, Date createdAt, Date updatedAt, Date canceledAt, Date shippingAt, Date deliveryAt, Date deliveredAt) {
        this.id = id;
        this.shippingInfo = shippingInfo;
        this.orderItems = orderItems;
        this.user = user;
        this.taxPrice = taxPrice;
        this.shippingPrice = shippingPrice;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.canceledReason = canceledReason;
        this.commented = commented;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.canceledAt = canceledAt;
        this.shippingAt = shippingAt;
        this.deliveryAt = deliveryAt;
        this.deliveredAt = deliveredAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OrderShippingInfo getShippingInfo() {
        return shippingInfo;
    }

    public void setShippingInfo(OrderShippingInfo shippingInfo) {
        this.shippingInfo = shippingInfo;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getTaxPrice() {
        return taxPrice;
    }

    public void setTaxPrice(int taxPrice) {
        this.taxPrice = taxPrice;
    }

    public int getShippingPrice() {
        return shippingPrice;
    }

    public void setShippingPrice(int shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCanceledReason() {
        return canceledReason;
    }

    public void setCanceledReason(String canceledReason) {
        this.canceledReason = canceledReason;
    }

    public boolean isCommented() {
        return commented;
    }

    public void setCommented(boolean commented) {
        this.commented = commented;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(Date canceledAt) {
        this.canceledAt = canceledAt;
    }

    public Date getShippingAt() {
        return shippingAt;
    }

    public void setShippingAt(Date shippingAt) {
        this.shippingAt = shippingAt;
    }

    public Date getDeliveryAt() {
        return deliveryAt;
    }

    public void setDeliveryAt(Date deliveryAt) {
        this.deliveryAt = deliveryAt;
    }

    public Date getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Date deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", shippingInfo=" + shippingInfo +
                ", orderItems=" + orderItems +
                ", user='" + user + '\'' +
                ", taxPrice=" + taxPrice +
                ", shippingPrice=" + shippingPrice +
                ", totalPrice=" + totalPrice +
                ", orderStatus='" + orderStatus + '\'' +
                ", canceledReason='" + canceledReason + '\'' +
                ", commented=" + commented +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", canceledAt=" + canceledAt +
                ", shippingAt=" + shippingAt +
                ", deliveryAt=" + deliveryAt +
                ", deliveredAt=" + deliveredAt +
                '}';
    }
}