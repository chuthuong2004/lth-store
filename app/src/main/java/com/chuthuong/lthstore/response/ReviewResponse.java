package com.chuthuong.lthstore.response;
import java.io.Serializable;
public class ReviewResponse implements Serializable {
    private boolean success;
    private String message;
    private ReviewModel review;

    public ReviewResponse() {
    }

    public ReviewResponse(boolean success, String message, ReviewModel review) {
        this.success = success;
        this.message = message;
        this.review = review;
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

    public ReviewModel getReview() {
        return review;
    }

    public void setReview(ReviewModel review) {
        this.review = review;
    }

    @Override
    public String toString() {
        return "ReviewResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", review=" + review +
                '}';
    }
}