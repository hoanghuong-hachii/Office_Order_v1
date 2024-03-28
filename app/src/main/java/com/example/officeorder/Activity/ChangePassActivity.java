package com.example.officeorder.Activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.DatabaseHelper;
import com.example.officeorder.Config.HashUtils;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.getToken;
import com.example.officeorder.MainActivity;
import com.example.officeorder.Model.Active;
import com.example.officeorder.Model.Salt;
import com.example.officeorder.Model.UserProfile;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Request.LoginRequest;
import com.example.officeorder.Response.Data;
import com.example.officeorder.Response.LoginResponse;
import com.example.officeorder.Service.ApiService;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ChangePassActivity extends AppCompatActivity {
    private VariableEnvironment variableEnvironment = new VariableEnvironment("00");
    private String id_User;
    private Button btn_ok;
    private TextView tv_pwd, tv_re_pwd,tv_old;

    private String username="";
    private ApiService apiService;
    private String email,salt;
    private final int MAX_FAILED_ATTEMPTS = 3;
    private int failedAttempts = 0;
    private String userPasswordOld="",userPasswordnew="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        apiService= ApiClient.getApiService();
        Intent intent = getIntent();
        if(intent != null){
            email = intent.getStringExtra("email");
        }
        UserSingleton userSingleton = UserSingleton.getInstance();
        id_User  = userSingleton.getUserId();
        //getUsername();
        GetDataInfo();
        getPassOld();

        tv_old = findViewById(R.id.tv_pwdold);
        tv_pwd = findViewById(R.id.tv_pwd);
        tv_re_pwd = findViewById(R.id.tv_re_pwd);
        btn_ok = findViewById(R.id.btn_ok);

        btn_ok.setOnClickListener(v -> {
            String password = tv_pwd.getText().toString();
            String confirmPassword = tv_re_pwd.getText().toString();
            String oldpass = tv_old.getText().toString();
            getSaltByEmail(username);
            if (TextUtils.isEmpty(password)) {
                tv_pwd.setError("Mật khẩu không được để trống");
            } else if (TextUtils.isEmpty(confirmPassword)) {
                tv_re_pwd.setError("Mật khẩu xác nhận không được để trống");
            } else if (TextUtils.isEmpty(oldpass)) {
                tv_old.setError("Mật khẩu cũ không được để trống");
            } else if (!isValidPassword(password)) {
                tv_pwd.setError("Mật khẩu không đủ mạnh");
            } else if (!password.equals(confirmPassword)) {
                tv_re_pwd.setError("Mật khẩu xác nhận lại không trùng khớp");
            } else if (oldpass.equals(password)) {
                tv_pwd.setError("Mật khẩu mới không được trùng mật khẩu cũ");
            }
            else {
                if (userPasswordnew.equals(userPasswordOld)) {
                    resetPass();
                    showSuccessDialog("Mật khẩu đã được thay đổi thành công! Đăng nhập lại");
                } else {
                    tv_old.setError("Mật khẩu cũ không đúng");
                }
            }
        });
    }

    private void GetDataInfo() {
        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        ////================
        Call<UserProfile> call = apiService.getUserInfo(id_User,"Bearer "+ accessToken);

        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful()) {
                    UserProfile data = response.body();
                    // Data data = infoUser.getData();

                    if (data != null) {
                        username = data.getUserName();
                        String phone = data.getPhoneNumber();
                        String email1 = data.getEmail();
                        String address1 = data.getAddress();

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
    private void getPassOld(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(variableEnvironment.Valiables())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<String> call = apiService.getPasswordById(id_User);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {

                    String password = response.body();
                    userPasswordOld = password;
                    Log.e(TAG, "get Pass: "+ id_User +": "+password );
                } else {

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Handle the failure
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
                    Log.e("Login hash", "onResponse: OK");
                    salt = salts.getSalt();

                    Log.e("TAG", "SALT: " + salt);
                    //============

                    String email = username;
                    String password1 = tv_old.getText().toString();

                    userPasswordnew = HashUtils.hashPasswordWithSalt(password1, salt);
                    System.out.println("Password login: " + password1);
                    System.out.println("Salt login: " + salt);
                    System.out.println("Hashed Password ChangePass: " + userPasswordnew);

                    //=============
                } else {
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
    public void createSalt(String email, String salt) {

        ApiService apiService = ApiClient.getApiService();

        Salt newSalt = new Salt();
        newSalt.setEmail(email);
        newSalt.setSalt(salt);

        Call<Salt> call = apiService.createSalt(newSalt);

        call.enqueue(new Callback<Salt>() {
            @Override
            public void onResponse(Call<Salt> call, Response<Salt> response) {
                if (response.isSuccessful()) {
                    Salt createdSalt = response.body();
                    Log.e("Salt", "onResponse: OK");

                } else {
                    // Handle error
                    Log.e("Salt", "onResponse: Fail");
                }
            }

            @Override
            public void onFailure(Call<Salt> call, Throwable t) {
                // Handle failure
            }
        });
    }
    private void resetPass(){
        //========Hash password================
        String password1 = tv_pwd.getText().toString();

//        String salt = HashUtils.generateSalt();
        String passHash = HashUtils.hashPasswordWithSalt(password1, salt);

        System.out.println("Password: " + password1);
        System.out.println("Salt: " + salt);
        System.out.println("Hashed Password register: " + passHash);

        //createSalt(username,salt);
        //==================================
        Call<Void> call = apiService.changePassword(email, passHash);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.e("ChangePass", "OK code: " + response.code());
                } else {
                    Log.e("Error", "Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Error", "Request failed: " + t.getMessage());
            }
        });
    }

    private void getUsername() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(variableEnvironment.Valiables())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<String> call = apiService.getUsernameByEmail(username);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    username = response.body();

                } else {
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private void showSuccessDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thành công")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ChangePassActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }
}