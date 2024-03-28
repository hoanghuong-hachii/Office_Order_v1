package com.example.officeorder.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.MotionEffect;

import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.ApiManager;
import com.example.officeorder.Config.DatabaseHelper;
import com.example.officeorder.Config.DbAESHelper;
import com.example.officeorder.Config.HashUtils;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.core_AES;
import com.example.officeorder.MainActivity;
import com.example.officeorder.Model.Active;
import com.example.officeorder.Model.Salt;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Request.LoginRequest;
import com.example.officeorder.Response.Data;
import com.example.officeorder.Response.LoginResponse;
import com.example.officeorder.Service.ApiService;
import com.example.officeorder.utils.ActiveAcount;
import com.google.android.material.textfield.TextInputEditText;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText username, password;
    private Button btnlogin;
    private String salt;
    private String id_User;
    private TextView tvsignup,tv_forget;
    private VariableEnvironment variableEnvironment = new VariableEnvironment("s");
    private ApiService apiService;
    private final int MAX_FAILED_ATTEMPTS = 3;
    private int failedAttempts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btnlogin = findViewById(R.id.btnSubmit);

        apiService = ApiClient.getApiService();
        tvsignup = findViewById(R.id.tvsignup);
        tv_forget = findViewById(R.id.tv_forget);

        tvsignup.setOnClickListener( view ->{
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });
        btnlogin.setOnClickListener(view -> {



            String email = username.getText().toString();

            if (failedAttempts < MAX_FAILED_ATTEMPTS) {
                getSaltByEmail(email);
            } else if(failedAttempts >= MAX_FAILED_ATTEMPTS){
                updateActiveStatus(email,false);
                showErrorMessage("Tài khoản của bạn đã bị khóa");

            }

        });
        tv_forget.setOnClickListener( v -> {
            startActivity(new Intent(LoginActivity.this,ForgetPwdActivity.class));
        });

        //=====================

    }

    private void updateActiveStatus(String email, boolean active) {

        Call<Active> call = apiService.updateActiveStatus(email, active);
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
                Log.e("PUT ACTIVE", "onFailure: "+ t.getMessage() );
            }
        });
    }
    public void getSaltByEmail(String email) {

        Call<Salt> call = apiService.getSaltByEmail(email);

        call.enqueue(new Callback<Salt>() {
            @Override
            public void onResponse(Call<Salt> call, Response<Salt> response) {
                if (response.isSuccessful()) {
                    Salt salts = response.body();
                    Log.e("Login hash", "onResponse: OK" );
                    salt = salts.getSalt();

                    Log.e("TAG", "SALT: "+salt );
                    //============

                    String email = username.getText().toString();
                    String password1 = password.getText().toString();

                    String userPassword = HashUtils.hashPasswordWithSalt(password1, salt);

                    System.out.println("Password login: " + password1);
                    System.out.println("Salt login: " + salt);
                    System.out.println("Hashed Password Login: " + userPassword);

                    //============
                    Call<Active> call1 = apiService.getActiveAccountByEmail(email);
                    call1.enqueue(new Callback<Active>() {
                        @Override
                        public void onResponse(Call<Active> call1, Response<Active> response) {
                            if (response.isSuccessful()) {
                                Active activeAccount = response.body();

                                boolean check = activeAccount.getActive();

                                Log.e("GET ACTIVE", "onResponse: OK"+ check );
                                Log.d("GET ACTIVE", " Active email: "+email +" :"+ check );
                                //===========================LOgin============

                                //============

                                LoginRequest loginRequest = new LoginRequest(email, userPassword);
                                Call<LoginResponse> call2 = apiService.loginUser(loginRequest);
                                call2.enqueue(new Callback<LoginResponse>() {
                                    @Override
                                    public void onResponse(Call<LoginResponse> call2, Response<LoginResponse> response) {
                                        if (response.isSuccessful()) {
                                            LoginResponse loginResponse = response.body();
                                            if (loginResponse != null) {

                                                Data dt = loginResponse.getData();
                                                id_User = dt.getIdUser();

                                                UserSingleton userSingleton = UserSingleton.getInstance();
                                                userSingleton.setUserId(id_User);
                                                userSingleton.setToken(dt.getToken());
                                                userSingleton.setAccessToken(dt.getAccessToken());
                                                Log.e(TAG, "AccessToken: "+ dt.getAccessToken() );
                                                Log.e(TAG, "Token: " +dt.getToken() );

                                                DatabaseHelper databaseHelper = new DatabaseHelper(LoginActivity.this);
                                                boolean isInserted = databaseHelper.insertUserId(id_User);

                                                if (isInserted) {
                                                    //     Toast.makeText(LoginActivity.this, "User ID đã lưu SQLite", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT) .show();
                                                }

                                                //if (loginResponse.isStatus() && loginResponse.getMessage().equals("Login Success")) {
                                                if (check==true && loginResponse.getMessage().equals("Login Success")) {

                                                    runOnUiThread(() -> {
                                                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                                        finish();
                                                    });
                                                }else if (check==false){
                                                    showErrorMessage("Tài khoản của bạn đã bị khóa");
                                                }
                                                else if(check==true && !loginResponse.getMessage().equals("Login Success")){
                                                    failedAttempts++;
                                                    showErrorMessage("Bạn đã sai mật khẩu "+ failedAttempts+"/"+ MAX_FAILED_ATTEMPTS+" lần!");
                                                }

                                                else {
                                                    failedAttempts++;
                                                    showErrorMessage("Bạn đã sai mật khẩu "+ failedAttempts+"/"+ MAX_FAILED_ATTEMPTS+" lần!");
                                                }
                                            } else {
                                                // Đăng nhập không thành công
                                                Toast.makeText(LoginActivity.this, "Lỗi mạng! Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            // Đã xảy ra lỗi
                                            Toast.makeText(LoginActivity.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<LoginResponse> call2, Throwable t) {
                                        failedAttempts++;
                                        Log.e("Lỗi",t.getMessage());
                                        showErrorMessage("Bạn đã sai mật khẩu "+ failedAttempts+"/"+ MAX_FAILED_ATTEMPTS+" lần!");
                                        //Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "onResponse: "+t );
                                    }
                                });
                                //=============
                            } else {
                            }
                        }

                        @Override
                        public void onFailure(Call<Active> call1, Throwable t) {
                            // Handle network errors or failures
                        }
                    });

                } else {
                    // Handle error
                    Log.e("Login hash", "onResponse: Fail" );
                }
            }

            @Override
            public void onFailure(Call<Salt> call, Throwable t) {
                // Handle failure
            }
        });
    }
    private void showErrorMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lỗi")
                .setMessage(message)
                .setPositiveButton("Đóng", null)
                .show();
    }
}