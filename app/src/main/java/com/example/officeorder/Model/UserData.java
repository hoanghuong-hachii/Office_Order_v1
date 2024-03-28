package com.example.officeorder.Model;

public class UserData {
    private static UserData instance;
    private String idUser;
    private String name ;
    private String gioitinh;
    //private String diachi;
    private String sdt;
    private String email;

    public UserData(String idUser, String name, String gioitinh,  String sdt, String email) {
        this.idUser = idUser;
        this.name = name;
        this.gioitinh = gioitinh;
        //this.diachi = diachi;
        this.sdt = sdt;
        this.email = email;
    }

    public UserData() {
    }

    public static UserData getInstance() {
        if (instance == null) {
            instance = new UserData();
        }
        return instance;
    }

    public static void setInstance(UserData instance) {
        UserData.instance = instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGioitinh() {
        return gioitinh;
    }

    public void setGioitinh(String gioitinh) {
        this.gioitinh = gioitinh;
    }

//    public String getDiachi() {
//        return diachi;
//    }
//
//    public void setDiachi(String ngaysinh) {
//        this.diachi = ngaysinh;
//    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
}
