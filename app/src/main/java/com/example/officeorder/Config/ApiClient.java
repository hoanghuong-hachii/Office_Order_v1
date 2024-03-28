package com.example.officeorder.Config;

import com.example.officeorder.Service.ApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final VariableEnvironment variableEnvironment = new VariableEnvironment("");
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(variableEnvironment.Valiables())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static ApiService getApiService() {
        return retrofit.create(ApiService.class);
    }
}
