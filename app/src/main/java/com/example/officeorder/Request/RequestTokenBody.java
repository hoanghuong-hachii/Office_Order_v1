package com.example.officeorder.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestTokenBody {

    @SerializedName("idUser")
    @Expose
    private String idUser;
    @SerializedName("tokenDevice")
    @Expose
    private String tokenDevice;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getTokenDevice() {
        return tokenDevice;
    }

    public void setTokenDevice(String tokenDevice) {
        this.tokenDevice = tokenDevice;
    }

}