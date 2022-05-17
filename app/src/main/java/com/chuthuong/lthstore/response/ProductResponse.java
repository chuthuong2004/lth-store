package com.chuthuong.lthstore.response;

import com.chuthuong.lthstore.model.Product;

public class ProductResponse {
    private boolean success;
    private Product product;

    public ProductResponse() {
    }

    public ProductResponse(boolean success, Product product) {
        this.success = success;
        this.product = product;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
