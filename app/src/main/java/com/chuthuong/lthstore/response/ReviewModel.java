package com.chuthuong.lthstore.response;

import com.chuthuong.lthstore.model.ReviewInfoProductOrdered;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class ReviewModel implements Serializable {
    @SerializedName("_id")
    private String id;
    private String content;
    private String product;
    private int star;
    private String user;
    private boolean deleted;
    private Date createdAt;
    private Date updatedAt;
    @SerializedName("infoProductOrdered")
    private ReviewInfoProductOrdered reviewInfoProductOrdered;

    public ReviewModel() {
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

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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

    public ReviewInfoProductOrdered getReviewInfoProductOrdered() {
        return reviewInfoProductOrdered;
    }

    public void setReviewInfoProductOrdered(ReviewInfoProductOrdered reviewInfoProductOrdered) {
        this.reviewInfoProductOrdered = reviewInfoProductOrdered;
    }

    public ReviewModel(String id, String content, String product, int star, String user, boolean deleted, Date createdAt, Date updatedAt, ReviewInfoProductOrdered reviewInfoProductOrdered) {
        this.id = id;
        this.content = content;
        this.product = product;
        this.star = star;
        this.user = user;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reviewInfoProductOrdered = reviewInfoProductOrdered;
    }

    @Override
    public String toString() {
        return "ReviewRes{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", product='" + product + '\'' +
                ", star=" + star +
                ", user='" + user + '\'' +
                ", deleted=" + deleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", reviewInfoProductOrdered=" + reviewInfoProductOrdered +
                '}';
    }}