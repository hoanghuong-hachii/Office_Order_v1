package com.example.officeorder.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductBillDetail {

    @SerializedName("idBill")
    @Expose
    private Integer idBill;
    @SerializedName("idUser")
    @Expose
    private String idUser;
    @SerializedName("userName")
    @Expose
    private String userName;
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
    @SerializedName("payableAmount")
    @Expose
    private Integer payableAmount;
    @SerializedName("shippingFee")
    @Expose
    private Integer shippingFee;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("productBillDTOS")
    @Expose
    private List<ProductBillDTO> productBillDTOS;

    public Integer getIdBill() {
        return idBill;
    }

    public void setIdBill(Integer idBill) {
        this.idBill = idBill;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public Integer getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(Integer payableAmount) {
        this.payableAmount = payableAmount;
    }

    public Integer getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(Integer shippingFee) {
        this.shippingFee = shippingFee;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<ProductBillDTO> getProductBillDTOS() {
        return productBillDTOS;
    }

    public void setProductBillDTOS(List<ProductBillDTO> productBillDTOS) {
        this.productBillDTOS = productBillDTOS;
    }

    @Override
    public String toString() {
        return "PostBillRequest{" +
                "idBill=" + idBill +
                ", idUser='" + idUser + '\'' +
                ", userName='" + userName + '\'' +
                ", numberPhoneCustomer='" + numberPhoneCustomer + '\'' +
                ", addressCustomer='" + addressCustomer + '\'' +
                ", dateTimeOrder='" + dateTimeOrder + '\'' +
                ", status='" + status + '\'' +
                ", totalPayment=" + totalPayment +
                ", payableAmount=" + payableAmount +
                ", shippingFee=" + shippingFee +
                ", note='" + note + '\'' +
                ", productBillDTOS=" + productBillDTOS +
                '}';
    }
}