package com.example.officeorder.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductBill {

    @SerializedName("idProdBill")
    @Expose
    private Integer idProdBill;
    @SerializedName("bill")
    @Expose
    private Object bill;
    @SerializedName("idBill1")
    @Expose
    private Integer idBill1;
    @SerializedName("idUser")
    @Expose
    private Integer idUser;
    @SerializedName("idProd")
    @Expose
    private Integer idProd;
    @SerializedName("priceProd")
    @Expose
    private Integer priceProd;
    @SerializedName("quantityProd")
    @Expose
    private Integer quantityProd;
    @SerializedName("totalPriceProd")
    @Expose
    private Integer totalPriceProd;

    public Integer getIdProdBill() {
        return idProdBill;
    }

    public void setIdProdBill(Integer idProdBill) {
        this.idProdBill = idProdBill;
    }

    public Object getBill() {
        return bill;
    }

    public void setBill(Object bill) {
        this.bill = bill;
    }

    public Integer getIdBill1() {
        return idBill1;
    }

    public void setIdBill1(Integer idBill1) {
        this.idBill1 = idBill1;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public Integer getIdProd() {
        return idProd;
    }

    public void setIdProd(Integer idProd) {
        this.idProd = idProd;
    }

    public Integer getPriceProd() {
        return priceProd;
    }

    public void setPriceProd(Integer priceProd) {
        this.priceProd = priceProd;
    }

    public Integer getQuantityProd() {
        return quantityProd;
    }

    public void setQuantityProd(Integer quantityProd) {
        this.quantityProd = quantityProd;
    }

    public Integer getTotalPriceProd() {
        return totalPriceProd;
    }

    public void setTotalPriceProd(Integer totalPriceProd) {
        this.totalPriceProd = totalPriceProd;
    }

    @Override
    public String toString() {
        return "ProductBillDetail{" +
                "idProdBill=" + idProdBill +
                ", bill=" + bill +
                ", idBill1=" + idBill1 +
                ", idUser=" + idUser +
                ", idProd=" + idProd +
                ", priceProd=" + priceProd +
                ", quantityProd=" + quantityProd +
                ", totalPriceProd=" + totalPriceProd +
                '}';
    }
}