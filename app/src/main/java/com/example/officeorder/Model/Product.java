package com.example.officeorder.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("idProd")
    @Expose
    private String idProd;
    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("origin")
    @Expose
    private String origin;
    @SerializedName("detail")
    @Expose
    private String detail;
    @SerializedName("productName")
    @Expose
    private String productName;
    @SerializedName("purchasePrice")
    @Expose
    private Integer purchasePrice;
    @SerializedName("unitPrice")
    @Expose
    private Double unitPrice;
    @SerializedName("retailPrice")
    @Expose
    private Integer retailPrice;
    @SerializedName("unitName")
    @Expose
    private String unitName;

    @SerializedName("quantityImported")
    @Expose
    private Integer quantityImported;
    @SerializedName("quantitySold")
    @Expose
    private Integer quantitySold;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("orderQuantity")
    @Expose
    private Integer orderQuantity;
    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("coupons")
    @Expose
    private Integer coupons;
    @SerializedName("imageAvatar")
    @Expose
    private String imageAvatar;
    @SerializedName("imageQR")
    @Expose
    private String imageQR;
    @SerializedName("formattedDiscountedPrice")
    @Expose
    private String formattedDiscountedPrice;
    @SerializedName("formattedPrice")
    @Expose
    private String formattedPrice;

    public String getIdProd() {
        return idProd;
    }

    public void setIdProd(String idProd) {
        this.idProd = idProd;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Integer purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(Integer retailPrice) {
        this.retailPrice = retailPrice;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }


    public Integer getQuantityImported() {
        return quantityImported;
    }

    public void setQuantityImported(Integer quantityImported) {
        this.quantityImported = quantityImported;
    }

    public Integer getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(Integer quantitySold) {
        this.quantitySold = quantitySold;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(Integer orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getCoupons() {
        return coupons;
    }

    public void setCoupons(Integer coupons) {
        this.coupons = coupons;
    }

    public String getImageAvatar() {
        return imageAvatar;
    }

    public void setImageAvatar(String imageAvatar) {
        this.imageAvatar = imageAvatar;
    }

    public String getImageQR() {
        return imageQR;
    }

    public void setImageQR(String imageQR) {
        this.imageQR = imageQR;
    }

    public String getFormattedDiscountedPrice() {
        return formattedDiscountedPrice;
    }

    public void setFormattedDiscountedPrice(String formattedDiscountedPrice) {
        this.formattedDiscountedPrice = formattedDiscountedPrice;
    }

    public String getFormattedPrice() {
        return formattedPrice;
    }

    public void setFormattedPrice(String formattedPrice) {
        this.formattedPrice = formattedPrice;
    }

    @Override
    public String toString() {
        return "Product{" +
                "idProd='" + idProd + '\'' +
                ", productName='" + productName + '\'' +
                ", quantitySold=" + quantitySold +
                '}';
    }
}