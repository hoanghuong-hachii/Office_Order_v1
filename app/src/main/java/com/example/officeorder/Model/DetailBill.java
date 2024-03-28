package com.example.officeorder.Model;

public class DetailBill {
    private int  idBill;
    private String phone;
    private String address;
    private String namePro;
    private int  totalfee;
    private String status;
    private String dv;
    private int idPro;
    private int quantity;

    public DetailBill(int idBill, String phone, String address, String namePro, int totalfee, String status, String dv, int idPro, int quantity) {
        this.idBill = idBill;
        this.phone = phone;
        this.address = address;
        this.namePro = namePro;
        this.totalfee = totalfee;
        this.status = status;
        this.dv = dv;
        this.idPro = idPro;
        this.quantity = quantity;
    }

    public String getNamePro() {
        return namePro;
    }

    public void setNamePro(String namePro) {
        this.namePro = namePro;
    }

    public int getIdBill() {
        return idBill;
    }

    public void setIdBill(int idBill) {
        this.idBill = idBill;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTotalfee() {
        return totalfee;
    }

    public void setTotalfee(int totalfee) {
        this.totalfee = totalfee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDv() {
        return dv;
    }

    public void setDv(String dv) {
        this.dv = dv;
    }

    public int getIdPro() {
        return idPro;
    }

    public void setIdPro(int idPro) {
        this.idPro = idPro;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
