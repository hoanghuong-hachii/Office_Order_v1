package com.example.officeorder.Request;

import com.google.gson.annotations.SerializedName;

public class ProductFilterRequest {
    @SerializedName("productName")
    private String productName;

    @SerializedName("priceFrom")
    private int priceFrom;

    @SerializedName("priceTo")
    private int priceTo;

    @SerializedName("categoryName")
    private String categoryName;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(int priceFrom) {
        this.priceFrom = priceFrom;
    }

    public int getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(int priceTo) {
        this.priceTo = priceTo;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
