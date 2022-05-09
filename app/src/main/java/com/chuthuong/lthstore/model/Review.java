package com.chuthuong.lthstore.model;

import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("_id")
    private String id;
    private String content;
    private Product product;
    private int star;
    private User user;
    private boolean deleted;
    private String createdAt;
    private String updatedAt;

    public Review(String id, String content, Product product, int star, User user, boolean deleted, String createdAt, String updatedAt) {
        this.id = id;
        this.content = content;
        this.product = product;
        this.star = star;
        this.user = user;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Review() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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
