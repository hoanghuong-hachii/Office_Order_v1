package com.example.officeorder.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.getToken;
import com.example.officeorder.Model.UserProfile;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Service.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InfoAddressActivity extends AppCompatActivity {

    private VariableEnvironment variableEnvironment = new VariableEnvironment("");
    private ApiService userService;
    private TextView tinh, quan, xa, pho, tv_name, tv_phone;
    private Button btn_thaydoi;
    private ImageView iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_address);

        iv_back =findViewById(R.id.backButton);
        tinh = findViewById(R.id.tinh);
        quan = findViewById(R.id.huyen);
        xa = findViewById(R.id.xa);
        pho = findViewById(R.id.pho);
        tv_name = findViewById(R.id.tv_name);
        tv_phone = findViewById(R.id.tv_phone);

        btn_thaydoi = findViewById(R.id.btn_change);

        GetDataInfo();

        iv_back.setOnClickListener( v->{
            finish();
        });
        btn_thaydoi.setOnClickListener( v->{
            startActivity(new Intent(InfoAddressActivity.this, AddressEditActivity.class));
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        GetDataInfo();
    }

    private void GetDataInfo() {

        UserSingleton userSingleton = UserSingleton.getInstance();
        String id_User  = userSingleton.getUserId();

        userService = ApiClient.getApiService();
        ///
        //=========Access Token=========
        //UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        ////================
        Call<UserProfile> call = userService.getUserInfo(id_User,"Bearer "+ accessToken);

        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful()) {
                    UserProfile data = response.body();
                    Log.e(TAG, "onResponse: " + data.toString() );
                    if (data != null) {

                        String address = data.getAddress();
                        tv_name.setText(data.getUserName());
                        tv_phone.setText(data.getPhoneNumber());
                        String[] addressParts = address.split(", ");

                        if (addressParts.length >= 4) {
                            String street = addressParts[0];
                            String ward = addressParts[1];
                            String district = addressParts[2];
                            String province = addressParts[3];

                            tinh.setText(province);
                            quan.setText(district);
                            xa.setText(ward);
                            pho.setText(street);

                        } else {
                            System.err.println("Invalid address format");
                        }
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                // Handle failure
            }
        });
    }
}