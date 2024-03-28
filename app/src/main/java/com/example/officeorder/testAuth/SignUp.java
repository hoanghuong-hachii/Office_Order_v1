package com.example.officeorder.testAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.officeorder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    private EditText edtuserName, edtpass, edtemail, edtphone, edtaddress ;
    private RadioGroup groupBtnGender;
    private RadioButton radioSelected;
    private ProgressBar progressBar;
    private Button signup;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_sign_up);
        progressBar = findViewById(R.id.progressBar);
        edtuserName = findViewById(R.id.edtUsername);
        edtpass = findViewById(R.id.edtPassword);
        edtemail = findViewById(R.id.edtEmail);
        edtphone = findViewById(R.id.edtPhone);
        edtaddress = findViewById(R.id.edtAddress);
        groupBtnGender = findViewById(R.id.radioBtnGender);
        groupBtnGender.clearCheck();

        signup = findViewById(R.id.btnRegister);

        edtpass.setOnClickListener(v->{
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

        });

        signup.setOnClickListener(v->{
            int idSelected = groupBtnGender.getCheckedRadioButtonId();
            radioSelected = findViewById(idSelected);

            String usename = edtuserName.getText().toString();
            String password = edtpass.getText().toString();
            String email = edtemail.getText().toString();
            String numberPhone = edtphone.getText().toString();
            String address = edtaddress.getText().toString();
            String textGender = radioSelected.getText().toString();

            String moblideRegex = "[0][0-9]{9}";
            Matcher mobileMatcher;
            Pattern mobilePattern = Pattern.compile(moblideRegex);
            mobileMatcher = mobilePattern.matcher(numberPhone);

            if(TextUtils.isEmpty(usename)){
                edtuserName.setError("User name is required");
                edtuserName.requestFocus();
            }else if(TextUtils.isEmpty(password)){
                edtpass.setError("Password is required");
                edtpass.requestFocus();
            }else if(TextUtils.isEmpty(email)){
                edtemail.setError("Email is required");
                edtemail.requestFocus();
            }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                edtemail.setError("Valid email is required");
                edtemail.requestFocus();
            } else if(groupBtnGender.getCheckedRadioButtonId() == -1){
                radioSelected.setError("Gender is required");
                radioSelected.requestFocus();
            } else if (!mobileMatcher.find()) {
                edtphone.setError("Mobile is valid");
                edtphone.requestFocus();
            } else if(TextUtils.isEmpty(numberPhone)){
                edtphone.setError("Mobile is required");
                edtphone.requestFocus();
            }else if(numberPhone.length() != 10){
                edtphone.setError("Số điện thoại có 10 chữ số");
                edtphone.requestFocus();
            }else if(password.length() < 6){
                edtpass.setError("Mật khẩu phải có ít nhất 6 chữ số");
                edtpass.requestFocus();
            }
            else {
                progressBar.setVisibility(View.VISIBLE);
                registerUser(usename, password, email, numberPhone, address, textGender);
            }
        });
    }

    private void registerUser(String usename, String password, String email, String numberPhone, String address, String textGender) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            progressBar.setVisibility(View.INVISIBLE);
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(usename, email, numberPhone, textGender, address);

                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        // send verification email
                                        firebaseUser.sendEmailVerification();
                                        Toast.makeText(SignUp.this, "Đăng ký thành công, Vui lòng xác minh email!", Toast.LENGTH_SHORT).show();

                                    }else {
                                        Toast.makeText(SignUp.this, "Đăng ký thất bại, Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }

                                }
                            });

                        }else{
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e){
                                edtpass.setError("Your password is too weak. Kindly use a mix of alphabets, numbers and special chẩcters");
                                edtpass.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                edtpass.setError("Your email is invalid or already is use. Kindly re-enter.");
                                edtpass.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e){
                                edtpass.setError("User is already registered with this email. Use another email.");
                                edtpass.requestFocus();
                            } catch (Exception e){
                                Log.e("Loi", e.getMessage());
                                Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }
}