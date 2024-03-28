package com.example.officeorder.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class District {

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
    @SerializedName("province_code")
    @Expose
    private Integer provinceCode;
    @SerializedName("wards")
    @Expose
    private List<Ward> wards;

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

    public Integer getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(Integer provinceCode) {
        this.provinceCode = provinceCode;
    }

    public List<Ward> getWards() {
        return wards;
    }

    public void setWards(List<Ward> wards) {
        this.wards = wards;
    }

}


