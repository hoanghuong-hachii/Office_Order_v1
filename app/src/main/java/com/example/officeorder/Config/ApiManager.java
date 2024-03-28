package com.example.officeorder.Config;

import android.util.Log;

import com.example.officeorder.Request.RefreshTokenRequest;
import com.example.officeorder.Response.AccessTokenResponse;
import com.example.officeorder.Service.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {
    private static final VariableEnvironment v = new VariableEnvironment("");
    private static ApiService apiService;

    public static void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(v.Valiables())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static void refreshToken(String refreshToken, final OnTokenRefreshListener listener) {
        if (apiService == null) {
            Log.e("ApiManager", "ApiService not initialized. Call init() first.");
            return;
        }

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(refreshToken);
        Call<AccessTokenResponse> call = apiService.refreshToken(refreshTokenRequest);

        call.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                if (response.isSuccessful()) {
                    AccessTokenResponse accessTokenResponse = response.body();
                    if (listener != null) {
                        listener.onTokenRefreshed(accessTokenResponse.getAccessToken());
                    }
                } else {
                    Log.e("ApiManager", "Error: " + response.code());
                    if (listener != null) {
                        listener.onTokenRefreshFailed();
                    }
                }
            }

            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                Log.e("ApiManager", "Error: " + t.getMessage());
                if (listener != null) {
                    listener.onTokenRefreshFailed();
                }
            }
        });
    }

    public interface OnTokenRefreshListener {
        void onTokenRefreshed(String accessToken);

        void onTokenRefreshFailed();
    }
}
