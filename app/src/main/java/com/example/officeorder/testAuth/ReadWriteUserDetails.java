package com.example.officeorder.testAuth;

public class ReadWriteUserDetails {
    public String username, email, phone, address, gender;

    public ReadWriteUserDetails() {
    }

    public ReadWriteUserDetails(String textusername, String textEmail, String textPhone, String textGender, String textaddress){
        this.username = textusername;
        this.email = textEmail;
        this.phone = textPhone;
        this.address = textaddress;
        this.gender = textGender;

    }
}
