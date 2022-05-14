package com.chuthuong.lthstore.model;

import java.io.Serializable;
import java.util.List;

public class ListReview implements Serializable {
    private List<Review> reviews;
    private boolean success;
    private  int countDocuments;
    private int resultPerPage;

    public ListReview() {
    }

    public ListReview(List<Review> reviews, boolean success, int countDocuments, int resultPerPage) {
        this.reviews = reviews;
        this.success = success;
        this.countDocuments = countDocuments;
        this.resultPerPage = resultPerPage;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCountDocuments() {
        return countDocuments;
    }

    public void setCountDocuments(int countDocuments) {
        this.countDocuments = countDocuments;
    }

    public int getResultPerPage() {
        return resultPerPage;
    }

    public void setResultPerPage(int resultPerPage) {
        this.resultPerPage = resultPerPage;
    }
}
