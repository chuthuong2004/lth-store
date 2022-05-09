package com.chuthuong.lthstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product {
    @SerializedName("_id")
    private String id;
    private String name;
    private String title;
    private int price;
    private int discount;
    private String desProduct;
    private List<ProductDetail> detail;
    private String category;
    private List<ProductImage> images;
    private int likeCount;
    private List<String> keywords;
    private List<Review> reviews;
    private boolean deleted;
    private String createdAt;
    private String updatedAt;
    private String slug;

    public Product() {
    }

    public Product(String id, String name, String title, int price, int discount, String desProduct, List<ProductDetail> detail, String category, List<ProductImage> images, int likeCount, List<String> keywords, List<Review> reviews, boolean deleted, String createdAt, String updatedAt, String slug) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.price = price;
        this.discount = discount;
        this.desProduct = desProduct;
        this.detail = detail;
        this.category = category;
        this.images = images;
        this.likeCount = likeCount;
        this.keywords = keywords;
        this.reviews = reviews;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.slug = slug;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getDesProduct() {
        return desProduct;
    }

    public void setDesProduct(String desProduct) {
        this.desProduct = desProduct;
    }

    public List<ProductDetail> getDetail() {
        return detail;
    }

    public void setDetail(List<ProductDetail> detail) {
        this.detail = detail;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                ", desProduct='" + desProduct + '\'' +
                ", detail=" + detail +
                ", category='" + category + '\'' +
                ", images=" + images +
                ", likeCount=" + likeCount +
                ", keywords=" + keywords +
                ", reviews=" + reviews +
                ", deleted=" + deleted +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }
}
