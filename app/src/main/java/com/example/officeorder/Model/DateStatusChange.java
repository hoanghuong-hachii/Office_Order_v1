package com.example.officeorder.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DateStatusChange {

    @SerializedName("idDateStatusChange")
    @Expose
    private Integer idDateStatusChange;
    @SerializedName("dateTimeOrder")
    @Expose
    private String dateTimeOrder;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("idBill")
    @Expose
    private Integer idBill;
    @SerializedName("idInventory")
    @Expose
    private Object idInventory;

    public Integer getIdDateStatusChange() {
        return idDateStatusChange;
    }

    public void setIdDateStatusChange(Integer idDateStatusChange) {
        this.idDateStatusChange = idDateStatusChange;
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

    public Integer getIdBill() {
        return idBill;
    }

    public void setIdBill(Integer idBill) {
        this.idBill = idBill;
    }

    public Object getIdInventory() {
        return idInventory;
    }

    public void setIdInventory(Object idInventory) {
        this.idInventory = idInventory;
    }

}
