package com.chuthuong.lthstore.response;

import com.chuthuong.lthstore.model.Product;

import java.io.Serializable;
import java.util.List;

public class ListProductResponse implements Serializable {
    private List<Product> products;
    private boolean success;
    private  int documents;
    private  int countDocuments;
    private int resultPerPage;

    public ListProductResponse() {
    }

    public ListProductResponse(List<Product> products, boolean success,int documents, int countDocument, int resultPerPage) {
        this.products = products;
        this.success = success;
        this.documents = documents;
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
    public int getDocument() {
        return documents;
    }

    public void setDocument(int documents) {
        this.documents = documents;
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
