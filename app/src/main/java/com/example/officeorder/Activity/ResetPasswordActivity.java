package com.example.officeorder.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.HashUtils;
import com.example.officeorder.Model.Salt;
import com.example.officeorder.R;
import com.example.officeorder.Service.ApiService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private Button btn_ok;
    private TextView tv_pwd, tv_re_pwd;

    private String username="";
    private ApiService apiService;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        apiService= ApiClient.getApiService();
        Intent intent = getIntent();
        if(intent != null){
            email = intent.getStringExtra("email");
        }

        getUsername();
        tv_pwd = findViewById(R.id.tv_pwd);
        tv_re_pwd = findViewById(R.id.tv_re_pwd);
        btn_ok = findViewById(R.id.btn_ok);

        btn_ok.setOnClickListener(v -> {
            String password = tv_pwd.getText().toString();
            String confirmPassword = tv_re_pwd.getText().toString();

            if (isValidPassword(password) && password.equals(confirmPassword)) {
                resetPass();
                showSuccessDialog("Mật khẩu đã được thay đổi thành công! Đăng nhập lại");
            } else if(!isValidPassword(password)){
                tv_pwd.setError("Mật khẩu không đủ mạnh");
            }
            else if(!password.equals(confirmPassword)){
                tv_re_pwd.setError("Mật khẩu xác nhận lại không trùng khớp");
            }
        });
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

        String salt = HashUtils.generateSalt();

        String passHash = HashUtils.hashPasswordWithSalt(password1, salt);

        System.out.println("Password: " + password1);
        System.out.println("Salt: " + salt);
        System.out.println("Hashed Password register: " + passHash);

        createSalt(username,salt);
        //==================================
        Call<Void> call = apiService.changePassword(email, passHash);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

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

        Call<String> call = apiService.getUsernameByEmail(email);
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
                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }
}