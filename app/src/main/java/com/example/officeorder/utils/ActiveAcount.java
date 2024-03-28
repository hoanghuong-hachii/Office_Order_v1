package com.example.officeorder.utils;

import android.util.Log;

import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Model.Active;
import com.example.officeorder.Service.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveAcount {

    private boolean active = false;
    private ApiService activeAccountService;
    public void createActiveAccount(String email, boolean active) {
        activeAccountService = ApiClient.getApiService();

        Active activeAccount = new Active();
        activeAccount.setEmail(email);
        activeAccount.setActive(active);

        Call<Active> call = activeAccountService.createActiveAccount(activeAccount);
        call.enqueue(new Callback<Active>() {
            @Override
            public void onResponse(Call<Active> call, Response<Active> response) {
                if (response.isSuccessful()) {
                    Active createdActiveAccount = response.body();
                    Log.e("POST ACTIVE", "onResponse: OK"+ email );
                } else {

                }
            }

            @Override
            public void onFailure(Call<Active> call, Throwable t) {
                Log.e("POST ACTIVE", "onResponse: FAIL "+ t.getMessage() );
            }
        });
    }

    public boolean getActive(){
        return active;
    }
    public void getActiveAccountByEmail(String email) {
        activeAccountService = ApiClient.getApiService();

        Call<Active> call = activeAccountService.getActiveAccountByEmail(email);
        call.enqueue(new Callback<Active>() {
            @Override
            public void onResponse(Call<Active> call, Response<Active> response) {
                if (response.isSuccessful()) {
                    Active activeAccount = response.body();

                    active = activeAccount.getActive();

                    Log.e("GET ACTIVE", "onResponse: OK"+ active );
                } else {
                }
            }

            @Override
            public void onFailure(Call<Active> call, Throwable t) {
                // Handle network errors or failures
            }
        });
    }

    private void updateActiveStatus(String email, boolean active) {
        activeAccountService = ApiClient.getApiService();

        Call<Active> call = activeAccountService.updateActiveStatus(email, active);
        call.enqueue(new Callback<Active>() {
            @Override
            public void onResponse(Call<Active> call, Response<Active> response) {
                if (response.isSuccessful()) {
                    Active updatedActiveAccount = response.body();
                    Log.e("PUT ACTIVE", "onResponse: OK"+updatedActiveAccount );
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<Active> call, Throwable t) {
                // Handle network errors or failures
            }
        });
    }

}
