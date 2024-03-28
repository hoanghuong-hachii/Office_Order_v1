package com.example.officeorder.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.officeorder.Config.DatabaseHelper;
import com.example.officeorder.MainActivity;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Service.ApiService;
import com.example.officeorder.testAuth.FireBaseMainActivity;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IntroActivity extends AppCompatActivity {

    String userID;
    Button getstart;

    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://provinces.open-api.vn/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
        getstart  =findViewById(R.id.btnGetStarted);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        boolean check = databaseHelper.isUserIdEmpty();

        getstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check) {
                    startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                    finish();
                } else {
                    String userId = databaseHelper.getUserId();
                    UserSingleton userSingleton = UserSingleton.getInstance();
                    userSingleton.setUserId(userId);
                    startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                    finish();
                }

            }
        });

    }


}