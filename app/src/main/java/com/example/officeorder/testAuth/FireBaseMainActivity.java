package com.example.officeorder.testAuth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.officeorder.R;

public class FireBaseMainActivity extends AppCompatActivity {

    Button btnLogin, btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_base_main);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v->{
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        });

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(v->{
            Intent intent = new Intent(this, SignUp.class);
            startActivity(intent);
        });
    }
}