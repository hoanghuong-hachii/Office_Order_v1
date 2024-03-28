package com.example.officeorder.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.MotionEffect;

import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.DbAESHelper;
import com.example.officeorder.Config.EmailMaskingExample;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.core_AES;
import com.example.officeorder.Config.getToken;
import com.example.officeorder.Model.Salt;
import com.example.officeorder.Model.UserData;
import com.example.officeorder.Model.UserProfile;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Service.ApiService;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Arrays;

import javax.crypto.Cipher;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingActivity extends AppCompatActivity {

    private VariableEnvironment variableEnvironment = new VariableEnvironment("d");

    private static final int PICK_IMAGE_REQUEST = 1;

    private boolean clickAvatar = false;
    private boolean clickBackground = false;
    Button btn_save;
    private boolean changeAvatar = false;
    private boolean changeBackground = false;
    ImageButton backButton;
    TextView tv_name, tv_gioitinh,  tv_sdt, tv_email;
    ImageView iv_background, iv_avatar,iv_ten, iv_gt, iv_ns, iv_sdt, iv_email,iv_doimk;
    private String idUser;
    private ApiService apiService;

    //==================================
    private DbAESHelper db = new DbAESHelper(this);
    private static core_AES cipherAES;
    private static byte[] aesKey;
    private KeyStore keyStore;
    private String keyaesEn="";
    private byte[] decryptKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        UserSingleton userSingleton = UserSingleton.getInstance();
         idUser  = userSingleton.getUserId();

        apiService = ApiClient.getApiService();
        //===================

        iv_doimk = findViewById(R.id.iv_doimk);
        btn_save = findViewById(R.id.btn_luu);
        backButton = findViewById(R.id.backButton);
        tv_name = findViewById(R.id.tv_name);
        tv_gioitinh = findViewById(R.id.tv_gioitinh);
       // tv_diachi = findViewById(R.id.tv_address);
        tv_sdt = findViewById(R.id.tv_sdt);
        tv_email = findViewById(R.id.tv_email);
        iv_avatar = findViewById(R.id.iv_avatar);
        iv_background = findViewById(R.id.iv_background);
        iv_ten = findViewById(R.id.iv_ten);
        iv_gt = findViewById(R.id.iv_gt);
        iv_sdt = findViewById(R.id.iv_sdt);
        iv_email = findViewById(R.id.iv_email);
        //iv_diachi = findViewById(R.id.iv_diachi);


        String stringValue = (changeAvatar) ? "true" : "false";

        Log.e("Tthai avatar init: ",stringValue);
        GetDataInfo();

        changeData();

        //===============AES=============================
        initKeyStore();
        getEncryptKey(tv_name.getText().toString());
    }
    private static String byteArrayToHexString(byte[] byteArray) {
        StringBuilder hexStringBuilder = new StringBuilder(2 * byteArray.length);
        for (byte b : byteArray) {
            hexStringBuilder.append(String.format("%02X", b));
        }
        return hexStringBuilder.toString();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }
    private void getEncryptKey(String key){
        Call<Salt> call = apiService.getSaltByEmail(key);
        call.enqueue(new Callback<Salt>() {
            @Override
            public void onResponse(Call<Salt> call, Response<Salt> response) {
                if (response.isSuccessful()) {
                    Salt salts = response.body();
                    String key = salts.getSalt();

                    keyaesEn = salts.getSalt();

                    decryptString(keyaesEn, tv_name.getText().toString());
                    //Log.e("get keyaes", "keyAES: " + key);
                }
            }
            @Override
            public void onFailure(Call<Salt> call, Throwable t) {
                // Handle failure
            }
        });
    }



    private void decryptString(String cipherText, String alias) {
        try {
            PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null)).getPrivateKey();

            if (privateKey != null && cipherText != null) {
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);

                try {
                    byte[] decryptText = cipher.doFinal(Base64.decode(cipherText, Base64.DEFAULT));
                    decryptKey = cipherText.getBytes();
                    Log.e(MotionEffect.TAG, "decryptStringKey: " + new String(decryptText));

                    aesKey = decryptKey;
                    byte[] iv = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

                    core_AES core_aes = new core_AES(aesKey, iv);
                    String decryptEmail = byteArrayToString(core_aes.CBC_decrypt(tv_email.getText().toString().getBytes()));
                    EmailMaskingExample emailMaskingExample = new EmailMaskingExample();
                    String result = emailMaskingExample.maskEmail(decryptEmail);
                    tv_email.setText(result);
                    //tv_email.setText(decryptEmail);
                    System.out.println(byteArrayToString(core_aes.CBC_decrypt(tv_email.getText().toString().getBytes())));

                    //=========

                } catch (Exception e) {
                    Log.d(MotionEffect.TAG, "Error decoding Base64: " + e.getMessage());
                }
            } else {
                Log.d(MotionEffect.TAG, "PrivateKey or cipherText is null");
            }
        } catch (Exception e) {
            Log.d(MotionEffect.TAG, e.getMessage());
        }
    }

    private void initKeyStore() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
        } catch (Exception e) {
            Log.d(MotionEffect.TAG, e.getMessage());
        }
    }
    private void GetDataInfo() {
        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        ////================
        Call<UserProfile> call = apiService.getUserInfo(idUser,"Bearer "+ accessToken);

        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful()) {
                    UserProfile data = response.body();
                   // Data data = infoUser.getData();

                    if (data != null) {
                        String userName = data.getUserName();
                        String phone = data.getPhoneNumber();
                        String email1 = data.getEmail();
                        String address1 = data.getAddress();

                        tv_name.setText(userName);
                        tv_sdt.setText(phone);
                        tv_email.setText(email1);
                       // tv_diachi.setText(address1);
                        tv_gioitinh.setText(data.getGender());
                        giaima();
                        //thay  doi thong tin===============
                        Intent i = getIntent();
                        if (i != null) {
                            String newName = i.getStringExtra("name");
                            String newSdt = i.getStringExtra("sdt");
                            String newEmail = i.getStringExtra("email");
                            String newDiachi = i.getStringExtra("diachi");
                            if (newName != null) {
                                tv_name.setText(newName);
                            }
                            if (newEmail != null) {
                                tv_email.setText(newEmail);
                            }
//                            }
                            if(newSdt != null){
                                tv_sdt.setText(newSdt);
                            }
                        }

                        String avatar = data.getAvatar();
                        String background = data.getBackground();

                        String avatarUrl = variableEnvironment.Valiables() + avatar;

                        Picasso.get()
                                .load(avatarUrl)
                                .into(iv_avatar);
                        String backgroundUrl = variableEnvironment.Valiables() + background;

                        Picasso.get()
                                .load(backgroundUrl)
                                .into(iv_background);
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

    private void changeData() {
        iv_gt.setOnClickListener( v -> {

            final String[] genders = {"Nam", "Nữ", "Khác"};
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setItems(genders, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String selectedGender = genders[which];
                    tv_gioitinh.setText(selectedGender);
                }
            });
            builder.show();
        });


        iv_doimk.setOnClickListener( v->{
            Intent intent = new Intent(SettingActivity.this,VerifiedChangePassActivity.class);
            intent.putExtra("username", tv_name.getText());
            intent.putExtra("email", tv_email.getText());
            startActivity(intent);
        });

        iv_sdt.setOnClickListener(v -> {
            String oldsdt = tv_sdt.getText().toString();
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Sửa số điện thoại");

            final EditText input = new EditText(v.getContext());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setText(oldsdt);
            builder.setView(input);
            builder.setPositiveButton("OK", (dialog, which) -> {

                String sdt = input.getText().toString();
                if (!sdt.isEmpty()) {
                    tv_sdt.setText(sdt);
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        iv_ten.setOnClickListener(v -> {
            String oldName = tv_name.getText().toString();
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Sửa tên");

            final EditText input = new EditText(v.getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setText(oldName);
            builder.setView(input);
            builder.setPositiveButton("OK", (dialog, which) -> {

                String sdt = input.getText().toString();
                if (!sdt.isEmpty()) {
                    tv_name.setText(sdt);
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });
        iv_email.setOnClickListener(v -> {
            String oldemail = tv_email.getText().toString();
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Sửa Email");

            final EditText input = new EditText(v.getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setText(oldemail);
            builder.setView(input);
            builder.setPositiveButton("OK", (dialog, which) -> {

                String sdt = input.getText().toString();
                if (!sdt.isEmpty()) {
                    tv_email.setText(sdt);
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });
        backButton.setOnClickListener( v -> {
            finish();
        });

        btn_save.setOnClickListener( v -> {

            String newUserName = tv_name.getText().toString();
            String newPhoneNumber = tv_sdt.getText().toString();
            String newEmail = tv_email.getText().toString();
            String newGioitinh = tv_gioitinh.getText().toString();
           // String newDiachi = tv_diachi.getText().toString();

            UserData updatedUserData = new UserData();
            updatedUserData.setName(newUserName);
            updatedUserData.setSdt(newPhoneNumber);
            updatedUserData.setEmail(newEmail);
            updatedUserData.setGioitinh(newGioitinh);
            //updatedUserData.setDiachi(newDiachi);

            if (changeAvatar) {

                Log.e("Trang thai","cap nhat anh");
                Bitmap avatarBitmap = ((BitmapDrawable) iv_avatar.getDrawable()).getBitmap();
                uploadAvatar(avatarBitmap, "avatar.jpeg");
                changeAvatar = false;
            }
            if(changeBackground){
                Log.e("Trang thai","cap nhat background");
                Bitmap avatarBitmap = ((BitmapDrawable) iv_background.getDrawable()).getBitmap();
                uploadBackground(avatarBitmap, "background.jpeg");
                changeBackground = false;
            }

            updateUserInfo(updatedUserData);
            finish();
        });
        iv_avatar.setOnClickListener(v -> {

            clickAvatar = true;
            changeAvatar = true;
            String stringValue = (changeAvatar) ? "true" : "false";

            Log.e("Tthai avatar Change: ",stringValue);

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        iv_background.setOnClickListener(v -> {

            clickBackground = true;
            changeBackground = true;
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });


    }
    private static String byteArrayToString(byte[] byteArray) {
        StringBuilder stringBuilder = new StringBuilder(byteArray.length);
        for (byte b : byteArray) {
            stringBuilder.append((char) b);
        }
        return stringBuilder.toString();
    }
    private void giaima(){
        byte[] iv = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        DbAESHelper dbAESHelper = new DbAESHelper(this);
        byte[] retrievedKey = dbAESHelper.getKeyByName(tv_name.getText().toString());
        String a = tv_email.getText().toString();
        core_AES aes = new core_AES(retrievedKey,iv);
        System.out.println(byteArrayToString(aes.CBC_decrypt(a.getBytes())));
        String s = byteArrayToString(aes.CBC_decrypt(a.getBytes()));

        //EmailMaskingExample emailMaskingExample = new EmailMaskingExample();
       // String result = emailMaskingExample.maskEmail(s);
        tv_email.setText("****@gmail.com");
    }
    private void showSuccessDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thành công")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                if(clickAvatar){
                    iv_avatar.setImageBitmap(bitmap);
                    clickAvatar = false;
                }
                else if(clickBackground) {
                    iv_background.setImageBitmap(bitmap);
                    clickBackground = false;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadAvatar(Bitmap bitmap, String imageName) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), byteArrayOutputStream.toByteArray());
        MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", imageName, requestBody);

        // Gọi API để upload avatar
        Call<ResponseBody> call = apiService.uploadAvatar(idUser, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                } else {
                   // runOnUiThread(() -> Toast.makeText(SettingActivity.this, "Lỗi, Vui lòng thử lại", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //runOnUiThread(() -> Toast.makeText(SettingActivity.this, "Kiểm tra kết nối!", Toast.LENGTH_SHORT).show());
            }
        });
    }
    private void uploadBackground(Bitmap bitmap, String imageName) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), byteArrayOutputStream.toByteArray());
        MultipartBody.Part body = MultipartBody.Part.createFormData("background", imageName, requestBody);
//=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        // Gọi API để upload avatar
        Call<ResponseBody> call = apiService.uploadBackground(idUser, body,"Bearer " + accessToken);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                } else {
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //runOnUiThread(() -> Toast.makeText(SettingActivity.this, "Kiểm tra kết nối!", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateUserInfo(UserData userData) {

        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        Call<Void> call = apiService.updateUser(
                idUser,
                userData.getName(),
                userData.getEmail(),
                userData.getSdt(),
                userData.getGioitinh(),
                "Bearer " + accessToken
        );

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                 runOnUiThread(() -> Toast.makeText(SettingActivity.this, "Lỗi, Vui lòng thử lại", Toast.LENGTH_SHORT).show());
            }
        });
    }


}