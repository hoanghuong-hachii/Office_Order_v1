package com.example.officeorder.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductLikeSql {


    @SerializedName("idUser")
    @Expose
    private String idUser;
    @SerializedName("product")
    @Expose
    private Product product;
    @SerializedName("idProd")
    @Expose
    private String idProd;
    @SerializedName("idProdLike")
    @Expose
    private Integer idProdLike;
    private boolean isSelected;
    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getIdProd() {
        return idProd;
    }

    public void setIdProd(String idProd) {
        this.idProd = idProd;
    }

    public Integer getIdProdLike() {
        return idProdLike;
    }

    public void setIdProdLike(Integer idProdLike) {
        this.idProdLike = idProdLike;
    }



    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }



}
