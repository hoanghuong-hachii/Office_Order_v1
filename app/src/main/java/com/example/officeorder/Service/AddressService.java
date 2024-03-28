package com.example.officeorder.Service;

import com.example.officeorder.Model.District;
import com.example.officeorder.Model.Province;
import com.example.officeorder.Model.Ward;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AddressService {
    @GET("p/")
    Call<List<Province>> getProvinces();

    @GET("d/")
    Call<List<District>> getDistricts();

    @GET("w/")
    Call<List<Ward>> getWards();
}
