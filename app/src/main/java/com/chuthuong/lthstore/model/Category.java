package com.chuthuong.lthstore.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Category implements Serializable {
    @SerializedName("_id")
    private String id;
    private String name;
    private String catalog;
    private List<String> products;
    private String createdAt;
    private String updatedAt;
    private String slug;
    private String imageCate;

    public Category() {
    }

    public Category(String id, String name, String catalog, List<String> products, String createdAt, String updatedAt, String slug, String imageCate) {
        this.id = id;
        this.name = name;
        this.catalog = catalog;
        this.products = products;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.slug = slug;
        this.imageCate = imageCate;
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

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getImageCate() {
        return imageCate;
    }

    public void setImageCate(String imageCate) {
        this.imageCate = imageCate;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", catalog='" + catalog + '\'' +
                ", products=" + products +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", slug='" + slug + '\'' +
                ", imageCate='" + imageCate + '\'' +
                '}';
    }
}
