package com.example.officeorder.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Bill {

    @SerializedName("idBill")
    @Expose
    private Integer idBill;
    @SerializedName("idUser")
    @Expose
    private Integer idUser;
    @SerializedName("numberPhoneCustomer")
    @Expose
    private String numberPhoneCustomer;
    @SerializedName("addressCustomer")
    @Expose
    private String addressCustomer;
    @SerializedName("dateTimeOrder")
    @Expose
    private String dateTimeOrder;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("totalPayment")
    @Expose
    private Integer totalPayment;
    @SerializedName("productBills")
    @Expose
    private List<ProductBill> productBills;

    public Integer getIdBill() {
        return idBill;
    }

    public void setIdBill(Integer idBill) {
        this.idBill = idBill;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getNumberPhoneCustomer() {
        return numberPhoneCustomer;
    }

    public void setNumberPhoneCustomer(String numberPhoneCustomer) {
        this.numberPhoneCustomer = numberPhoneCustomer;
    }

    public String getAddressCustomer() {
        return addressCustomer;
    }

    public void setAddressCustomer(String addressCustomer) {
        this.addressCustomer = addressCustomer;
    }

    public String getDateTimeOrder() {
        return dateTimeOrder;
    }

    public void setDateTimeOrder(String dateTimeOrder) {
        this.dateTimeOrder = dateTimeOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(Integer totalPayment) {
        this.totalPayment = totalPayment;
    }

    public List<ProductBill> getProductBills() {
        return productBills;
    }

    public void setProductBills(List<ProductBill> productBills) {
        this.productBills = productBills;
    }

}
