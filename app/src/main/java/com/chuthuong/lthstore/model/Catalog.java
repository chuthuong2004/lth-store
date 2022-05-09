package com.chuthuong.lthstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Catalog {
    @SerializedName("_id")
    private String id;
    private String name;
    private List<Category> categories;
    private String createdAt;
    private String updatedAt;

    public Catalog() {
    }

    public Catalog(String id, String name, List<Category> categories, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.categories = categories;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
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
