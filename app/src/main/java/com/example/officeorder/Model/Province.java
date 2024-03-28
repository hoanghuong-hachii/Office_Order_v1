package com.example.officeorder.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Province {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("division_type")
    @Expose
    private String divisionType;
    @SerializedName("codename")
    @Expose
    private String codename;
    @SerializedName("phone_code")
    @Expose
    private Integer phoneCode;
    @SerializedName("districts")
    @Expose
    private List<District> districts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDivisionType() {
        return divisionType;
    }

    public void setDivisionType(String divisionType) {
        this.divisionType = divisionType;
    }

    public String getCodename() {
        return codename;
    }

    public void setCodename(String codename) {
        this.codename = codename;
    }

    public Integer getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(Integer phoneCode) {
        this.phoneCode = phoneCode;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }

}