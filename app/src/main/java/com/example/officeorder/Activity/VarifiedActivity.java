package com.example.officeorder.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Model.Active;
import com.example.officeorder.R;
import com.example.officeorder.Service.ApiService;
import com.example.officeorder.utils.ActiveAcount;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class VarifiedActivity extends AppCompatActivity {
    private ApiService apiService;
    private LinearLayout ln_resend;
    private CountDownTimer countDownTimer;
    private TextView tv_thongbao,tv_counter,tv_otp;
    private String email="";
    private String username="";
    private VariableEnvironment variableEnvironment = new VariableEnvironment("");

    private Button btn_send,btn_xacminh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_varified);


        Intent intent = getIntent();
        if(intent != null){
            username = intent.getStringExtra("username");
            email = intent.getStringExtra("email");
        }
        apiService= ApiClient.getApiService();
        tv_counter = findViewById(R.id.tv_counter);
        tv_thongbao = findViewById(R.id.tv_thongbao);
        btn_send = findViewById(R.id.btn_send);
        ln_resend = findViewById(R.id.ln_resend);
        btn_xacminh = findViewById(R.id.btn_verified);
        tv_otp = findViewById(R.id.tv_otp);
        getOTP();
        btn_send.setOnClickListener( v->{

                ln_resend.setVisibility(View.VISIBLE);
                btn_send.setEnabled(false);
                getOTP();
                long totalTimeInMillis = 1 * 60 * 1000;

                countDownTimer = new CountDownTimer(totalTimeInMillis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long minutes = millisUntilFinished / 60000;
                        long seconds = (millisUntilFinished % 60000) / 1000;
                        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
                        tv_counter.setText(timeLeftFormatted);
                    }

                    @Override
                    public void onFinish() {
                        tv_counter.setText("00:00");
                        ln_resend.setVisibility(View.GONE);
                        btn_send.setEnabled(true);
                    }
                };
                countDownTimer.start();


        });

        btn_xacminh.setOnClickListener(v -> {
            if (tv_otp != null && !tv_otp.getText().toString().trim().isEmpty()) {
                verifiedOTP();
            } else {
                tv_otp.setError("Vui lòng nhập OTP");
            }
        });
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

    private void verifiedOTP(){
        String otpnum = tv_otp.getText().toString();
        test(email,otpnum);

    }

    private void test(String email , String otp){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(variableEnvironment.Valiables())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<String> call = apiService.validateOtp(email, Integer.parseInt(otp));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String result = response.body();
                    Log.e("TEST OTP", "onResponse: "+ result );

                    if(result.equals("Xác minh thành công")){
                        createActiveAccount(username,true);
                        showSuccessDialog("Đăng ký thành công, Vui lòng đăng nhập lại!");
                    }
                    else {
                        showErrorMessage("OTP không hợp lệ hoặc đã hết hạn, Vui lòng thử lại!");
                    }
                } else {
                    Log.e("TEST OTP", "onResponse: FAIL " );
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("TEST OTP", "Failure: "+ t.getMessage() );
            }
        });
    }

    private void createActiveAccount(String email, boolean active) {
        Active activeAccount = new Active();
        activeAccount.setEmail(email);
        activeAccount.setActive(active);

        Call<Active> call = apiService.createActiveAccount(activeAccount);
        call.enqueue(new Callback<Active>() {
            @Override
            public void onResponse(Call<Active> call, Response<Active> response) {
                if (response.isSuccessful()) {
                    Active createdActiveAccount = response.body();
                    Log.e("POST ACTIVE", "onResponse: OK" + email );
                } else {

                }
            }

            @Override
            public void onFailure(Call<Active> call, Throwable t) {
                Log.e("POST ACTIVE", "onResponse: FAIL "+ t.getMessage() );
            }
        });
    }
    private void showSuccessDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thành công")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(VarifiedActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }
    private void showErrorMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lỗi")
                .setMessage(message)
                .setPositiveButton("Đóng", null)
                .show();
    }

    private void getOTP(){
        Call<Void> call = apiService.getOTP(email.toString());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("OTP generation successful");
                } else {
                    System.out.println("Failed to generate OTP. Response code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("Failed to generate OTP. Error: " + t.getMessage());
            }
        });
    }
}