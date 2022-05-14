package com.chuthuong.lthstore.model;

import java.io.Serializable;
import java.util.List;

public class ListCategory implements Serializable {
    private List<Category> categories;
    private boolean success;
    private  int countDocuments;
    private int resultPerPage;

    public ListCategory() {
    }

    public ListCategory(List<Category> categories, boolean success, int countDocuments, int resultPerPage) {
        this.categories = categories;
        this.success = success;
        this.countDocuments = countDocuments;
        this.resultPerPage = resultPerPage;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
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
