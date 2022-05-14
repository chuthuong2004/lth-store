package com.chuthuong.lthstore.model;

import java.io.Serializable;

public class ReviewInfoProductOrdered implements Serializable {
    private String size;
    private String color;
    private int quantity;

    public ReviewInfoProductOrdered() {
    }

    public ReviewInfoProductOrdered(String size, String color, int quantity) {
        this.size = size;
        this.color = color;
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
