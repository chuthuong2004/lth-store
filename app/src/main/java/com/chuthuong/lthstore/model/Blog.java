package com.chuthuong.lthstore.model;

import com.google.gson.annotations.SerializedName;

public class Blog {
    @SerializedName("_id")
    private String id;
    private String title;
    private String content;
    private String author;
    private String attachment;
    private int likeCount;
    private boolean deleted;
    private String createdAt;
    private String updatedAt;
    private String slug;

    public Blog(String id, String title, String content, String author, String attachment, int likeCount, boolean deleted, String createdAt, String updatedAt, String slug) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.attachment = attachment;
        this.likeCount = likeCount;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.slug = slug;
    }

    public Blog() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
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
}
