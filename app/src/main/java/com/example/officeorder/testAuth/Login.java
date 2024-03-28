package com.example.officeorder.testAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.officeorder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText edtEmail, edtPass;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_login);

        edtEmail = findViewById(R.id.edtEmailLogin);
        edtPass = findViewById(R.id.edtPasswordLogin);
        progressBar = findViewById(R.id.progressBarLogin);
        authProfile = FirebaseAuth.getInstance();

        Button btnLogin = findViewById(R.id.buttonLogin);

        Button logout = findViewById(R.id.btn_out);

        logout.setOnClickListener(v->{
            signOut();
        });
        btnLogin.setOnClickListener(v->{
            String textEmail = edtEmail.getText().toString();
            String textPwd = edtPass.getText().toString();

            if(TextUtils.isEmpty(textEmail)){
                edtEmail.setError("Vui lòng nhập email");
                edtEmail.requestFocus();
            }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                edtEmail.setError("Vui lòng nhập lại email");
                edtEmail.requestFocus();
            }else if(TextUtils.isEmpty(textPwd)){
                edtPass.setError("Vui lòng nhập password");
                edtPass.requestFocus();
            }else{
                progressBar.setVisibility(View.VISIBLE);
                loginUser(textEmail, textPwd);
            }
        });
    }

    private void signOut(){
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        firebaseUser.sendEmailVerification();
        authProfile.signOut();
        //showAlertDialog();
    }
    private void check(){
        if(authProfile.getCurrentUser() != null){
            Toast.makeText(Login.this, "Already logged in", Toast.LENGTH_SHORT).show();
            // Start the UserProfile
            startActivity(new Intent(Login.this, UserProfile.class));
            finish();
        }
        else{
            Toast.makeText(Login.this, "You can log in now", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginUser(String email, String pwd) {
        authProfile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser firebaseUser = authProfile.getCurrentUser();

                    if (firebaseUser.isEmailVerified()) {
                        Log.e("Auth_firebase", "onComplete: Xác minh từ firebase thành công" );

                        startActivity(new Intent(Login.this, UserProfile.class));
                        finish();
                    } else {
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlertDialog();
                    }

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        edtEmail.setError("Người dùng không tồn tại hoặc không còn hợp lệ. Vui lòng đăng ký lại.");
                        edtEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        edtEmail.setError("Thông tin không hợp lệ. Vui lòng kiểm tra và nhập lại. ");
                        edtEmail.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
        private void showAlertDialog() {
            //setup the Alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            builder.setTitle("Email chưa được xác minh");
            builder.setMessage("Hãy xác minh email của bạn ngay bây giờ. Bạn không thể đăng nhập nếu không xác minh email.");
            //Open Email apps if User clicks/taps Continue button
            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            //create the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

}