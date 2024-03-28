package com.example.officeorder.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CartProduct {

    @SerializedName("idShCart")
    @Expose
    private String idShCart;
    @SerializedName("idUser")
    @Expose
    private String idUser;
    @SerializedName("idProd")
    @Expose
    private String idProd;
    @SerializedName("quantityProd")
    @Expose
    private Integer quantityProd;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("totalPrice")
    @Expose
    private Integer totalPrice;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getIdShCart() {
        return idShCart;
    }

    public void setIdShCart(String idShCart) {
        this.idShCart = idShCart;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdProd() {
        return idProd;
    }

    public void setIdProd(String idProd) {
        this.idProd = idProd;
    }

    public Integer getQuantityProd() {
        return quantityProd;
    }

    public void setQuantityProd(Integer quantityProd) {
        this.quantityProd = quantityProd;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

}