package com.example.officeorder.Config;

import android.os.AsyncTask;

import com.example.officeorder.Model.Product;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.Service.ApiService;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiAsyncTask extends AsyncTask<Void, Void, List<Product>> {

    private ApiService apiService;
    private Callback<List<Product>> callback;
    private String accessToken="";
    public ApiAsyncTask(ApiService apiService, Callback<List<Product>> callback) {
        this.apiService = apiService;
        this.callback = callback;
    }

    @Override
    protected List<Product> doInBackground(Void... voids) {
        try {
            // Thực hiện yêu cầu API và trả về danh sách sản phẩm
            //=========Access Token=========
            UserSingleton userSingleton = UserSingleton.getInstance();
            getToken get = new getToken();
            get.test();
            accessToken = userSingleton.getAccessToken();
            //=======
            Call<List<Product>> call = apiService.getProducts("Bearer " + accessToken);
            Response<List<Product>> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Product> dataList) {
        if (dataList != null) {
            callback.onResponse(null, Response.success(dataList));
        } else {
            callback.onFailure(null, new Throwable("Request failed"));
        }
    }
}

