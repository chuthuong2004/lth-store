package com.chuthuong.lthstore.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProductDetail implements Serializable {
    @SerializedName("_id")
    private String id;
    private String size;
    private List<ProductDetailColor> detailColor;

    public ProductDetail() {
    }

    public ProductDetail(String id, String size, List<ProductDetailColor> detailColor) {
        this.id = id;
        this.size = size;
        this.detailColor = detailColor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public List<ProductDetailColor> getDetailColor() {
        return detailColor;
    }

    public void setDetailColor(List<ProductDetailColor> detailColor) {
        this.detailColor = detailColor;
    }
}
