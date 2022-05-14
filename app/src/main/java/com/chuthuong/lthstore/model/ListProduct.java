package com.chuthuong.lthstore.model;

import java.io.Serializable;
import java.util.List;

public class ListProduct implements Serializable {
    private List<Product> products;
    private boolean success;
    private  int countDocuments;
    private int resultPerPage;

    public ListProduct() {
    }

    public ListProduct(List<Product> products, boolean success, int countDocument, int resultPerPage) {
        this.products = products;
        this.success = success;
        this.countDocuments = countDocument;
        this.resultPerPage = resultPerPage;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCountDocument() {
        return countDocuments;
    }

    public void setCountDocument(int countDocument) {
        this.countDocuments = countDocument;
    }

    public int getResultPerPage() {
        return resultPerPage;
    }

    public void setResultPerPage(int resultPerPage) {
        this.resultPerPage = resultPerPage;
    }

}
