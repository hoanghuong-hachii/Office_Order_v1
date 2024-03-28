package com.example.officeorder.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductBillDTO {

    @SerializedName("idProductBill")
    @Expose
    private Integer idProductBill;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("retailPrice")
    @Expose
    private Integer retailPrice;
    @SerializedName("unitName")
    @Expose
    private String unitName;
    @SerializedName("totalPriceProd")
    @Expose
    private Integer totalPriceProd;
    @SerializedName("productId")
    @Expose
    private String productId;
    @SerializedName("productName")
    @Expose
    private String productName;
    @SerializedName("discount")
    @Expose
    private Integer discount;
    @SerializedName("status")
    @Expose
    private String status;

    public Integer getIdProductBill() {
        return idProductBill;
    }

    public void setIdProductBill(Integer idProductBill) {
        this.idProductBill = idProductBill;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public Integer getTotalPriceProd() {
        return totalPriceProd;
    }

    public void setTotalPriceProd(Integer totalPriceProd) {
        this.totalPriceProd = totalPriceProd;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}