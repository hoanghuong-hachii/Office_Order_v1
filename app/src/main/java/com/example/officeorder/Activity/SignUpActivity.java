package com.example.officeorder.Activity;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.lifecycle.ViewModelProvider;

import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.DatabaseHelper;
import com.example.officeorder.Config.DbAESHelper;
import com.example.officeorder.Config.HashUtils;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.core_AES;
import com.example.officeorder.MainActivity;
import com.example.officeorder.Model.Active;
import com.example.officeorder.Model.Salt;
import com.example.officeorder.Model.SignUpViewModel;
import com.example.officeorder.Model.User;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Request.LoginRequest;
import com.example.officeorder.Response.Data;
import com.example.officeorder.Response.LoginResponse;
import com.example.officeorder.Service.ApiService;
import com.example.officeorder.testAuth.ReadWriteUserDetails;
import com.example.officeorder.testAuth.SignUp;
import com.example.officeorder.utils.ActiveAcount;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Pair;
import android.widget.Toast;
import java.security.KeyStore;
public class SignUpActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefs";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String EMAIL_KEY = "email";
    private static final String PHONE_KEY = "phone";
    private TextInputLayout addresslayout;
    private TextInputEditText username, password, email, phone, editaddress;
    private VariableEnvironment variableEnvironment = new VariableEnvironment("s");

    private String usernameregiter="";
    private String emailregiter="";
    private SignUpViewModel signUpViewModel;
    //============================AES========================
    private static core_AES cipher;
    private static byte[] aesKey;
    private KeyStore keyStore;
    String encrypt_email = "";
    private KeyPair keyPair;
    private String alias = "1";
    private String encryptKeyAES= "";
    private ApiService apiService;
    private SecretKey secretKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.sdt);

        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        editaddress = findViewById(R.id.address);
        addresslayout = findViewById(R.id.addresslayout);
        editaddress.setOnClickListener(item->{
            saveValuesToSharedPreferences();
            Intent intent = new Intent(SignUpActivity.this, AddressActivity.class);
            startActivity(intent);
        });

        apiService = ApiClient.getApiService();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            loadSavedValues();
            String address = extras.getString("address");
            String province = extras.getString("province");
            String district = extras.getString("district");
            String ward = extras.getString("ward");

            editaddress.setText(address+", "+ ward+", "+ district+", "+ province );

        }

        Button btnsignup = findViewById(R.id.btnregister);

        btnsignup.setOnClickListener(item->{

            signUp();

        });

        TextView tvlogin = findViewById(R.id.tvlogin);
        tvlogin.setOnClickListener(item-> {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        });

        //getEncryptKey("Thuythuypwd");
    }

    private SecretKey decryptAESKey(String alias, String encryptedAESKeyString) {
        try {
            PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null)).getPrivateKey();

            if (privateKey != null && encryptedAESKeyString != null) {
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);

                try {
                    byte[] decryptedAESKey = cipher.doFinal(Base64.decode(encryptedAESKeyString, Base64.NO_WRAP));
                    Log.e(MotionEffect.TAG, "Decrypted AES Key Size: " + decryptedAESKey.length * 8 + " bits");
                    return new SecretKeySpec(decryptedAESKey, "AES");
                } catch (Exception e) {
                    Log.d(MotionEffect.TAG, "Error decoding Base64: " + e.getMessage());
                    return null;
                }
            } else {
                Log.d(MotionEffect.TAG, "PrivateKey or encryptedAESKeyString is null");
                return null;
            }
        } catch (Exception e) {
            Log.d(MotionEffect.TAG, e.getMessage());
            return null;
        }
    }
    private void loadSavedValues() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        username.setText(sharedPreferences.getString(USERNAME_KEY, ""));
        password.setText(sharedPreferences.getString(PASSWORD_KEY, ""));
        email.setText(sharedPreferences.getString(EMAIL_KEY, ""));
        phone.setText(sharedPreferences.getString(PHONE_KEY, ""));
    }

    private void saveValuesToSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_KEY, username.getText().toString());
        editor.putString(PASSWORD_KEY, password.getText().toString());
        editor.putString(EMAIL_KEY, email.getText().toString());
        editor.putString(PHONE_KEY, phone.getText().toString());
        editor.apply();
    }
    private void signUp() {
        String url = variableEnvironment.Valiables() + "api/v2/users/signup";

        String enteredUsername = username.getText().toString();
        String password1 = password.getText().toString();

        String enteredEmail = email.getText().toString();
        String enteredPhone = phone.getText().toString();
        String enteredAddress = editaddress.getText().toString();

        usernameregiter =  enteredUsername;
        emailregiter = enteredEmail;
        //========Hash password================
        //String password1 = "myPassword";

        String salt = HashUtils.generateSalt();
        String enteredPassword = HashUtils.hashPasswordWithSalt(password1, salt);

        System.out.println("Password: " + password1);
        System.out.println("Salt: " + salt);
        System.out.println("Hashed Password register: " + enteredPassword);

        createSalt(enteredUsername,salt);
        //==================================

        if (enteredUsername.length() < 5) {
            username.setError("Username phải có ít nhất 5 ký tự");
            return;
        }

        if (isValidPassword(enteredPassword)) {
            password.setError("Password phải có ít nhất 6 ký tự, chứa cả chữ hoa, chữ thường, số và ký tự đặc biệt!");
            return;
        }

        if (!isValidEmail(enteredEmail)) {
            email.setError("Email không hợp lệ");
            return;
        }

        if (enteredPhone.length() != 10) {
            phone.setError("Số điện thoại phải có 10 chữ số");
            return;
        }

        //======MÃ HÓA AES=========================

        initKeyStore();
        alias = username.getText().toString();

        createKey(alias);
        aesKey = generateAESKey();
        if (aesKey != null) {
            System.out.println("AES Key: " + bytesToHex(aesKey));
            // javaCryptoTest();
            encryptString(bytesToHex(aesKey), alias);
            //decryptString(encryptKeyAES, alias);

        }
        //===========================================
        createPwd(enteredUsername+"pwd",encryptKeyAES);

        System.out.println("AES 256 CBC");
        String text = email.getText().toString();

        text = fillBlock(text);
        byte[] inputText = text.getBytes();
        byte[] iv = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        cipher = new core_AES(aesKey, iv);
        byte[] encrypt = cipher.CBC_encrypt(inputText);
        encrypt_email = byteArrayToHexString(encrypt);


        System.out.println(byteArrayToString(cipher.CBC_decrypt(encrypt)));

        //===============================

        DbAESHelper dbAESHelper = new DbAESHelper(this);
        long a = dbAESHelper.addRecord(enteredUsername,aesKey);
        //==
        User data = new User();
        data.setUserName(enteredUsername);
        data.setPassword(enteredPassword);
        data.setEmail(enteredEmail);
        data.setPhoneNumber(enteredPhone);
        data.setAddress(enteredAddress);
        data.setGender("nu");
        data.setRoles("ROLE_USER");
        //"roles": "ROLE_USER"

        ApiService apiService = ApiClient.getApiService();

        Call<Void> call = apiService.signUp(data);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        showSuccessDialog("Vui lòng xác minh email!");

                    });
                } else {
                        showErrorMessage("Tài khoản đã tồn tại, Vui lòng đăng nhập!");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "onFailure: `"+ t.getMessage() );
                runOnUiThread(() -> {
                    Toast.makeText(SignUpActivity.this, "Lỗi! Vui lòng kiểm tra kết nối và thử lại", Toast.LENGTH_SHORT).show();
                });
            }
        });

    }

    private void getEncryptKey(String key){
        Call<Salt> call = apiService.getSaltByEmail(key);
        call.enqueue(new Callback<Salt>() {
            @Override
            public void onResponse(Call<Salt> call, Response<Salt> response) {
                if (response.isSuccessful()) {
                    Salt salts = response.body();
                    Log.e("get KEYaes", "onResponse: OK");
                    String key = salts.getSalt();

                    Log.e("get keyaes", "keyAES: " + key);
                }
            }
            @Override
            public void onFailure(Call<Salt> call, Throwable t) {
                // Handle failure
            }
        });
    }
    private static void javaCryptoTest() {
        System.out.println("\n~~~ javaCryptoTest ~~~\n");
        System.out.println("AES 256 CBC");
        String text = "otpt01720@gmail.com";

        text = fillBlock(text);
        byte[] inputText = text.getBytes();

        byte[] iv = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        cipher = new core_AES(aesKey, iv);

        System.out.println("=======ENCRYPT=========");
        byte[] encrypt = cipher.CBC_encrypt(inputText);
        String enText = byteArrayToHexString(encrypt);
        System.out.println(enText);
        System.out.println("=======DECRYPT=========");

        System.out.println(byteArrayToString(cipher.CBC_decrypt(encrypt)));

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
                        Intent intent = new Intent(SignUpActivity.this, VarifiedActivity.class);
                        intent.putExtra("username", usernameregiter);
                        intent.putExtra("email", emailregiter);
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
    public void createPwd(String email, String salt) {
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
                    Log.e("Key aes", "onResponse: OK");
                } else {
                    // Handle error
                    Log.e("Key aes", "onResponse: Fail");
                }
            }

            @Override
            public void onFailure(Call<Salt> call, Throwable t) {
                // Handle failure
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
    private boolean isValidEmail(String email) {
        // Kiểm tra định dạng email bằng regular expression
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    //===========================Mã hóa AES=======================================================================================
    private void createKey(String alias) {
        try {
            if (!keyStore.containsAlias(alias)) {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");

                KeyGenParameterSpec parameterSpec = new KeyGenParameterSpec.Builder(
                        alias,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        .setDigests(KeyProperties.DIGEST_SHA1)
                        .setUserAuthenticationRequired(false)
                        .build();

                keyPairGenerator.initialize(parameterSpec);
                keyPair = keyPairGenerator.generateKeyPair();
            } else {
                Toast.makeText(this, "Alias exists!!", Toast.LENGTH_SHORT).show();
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
    public byte[] generateAESKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            secretKey = keyGenerator.generateKey();
            byte[] keyBytes = secretKey.getEncoded();

            return keyBytes;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }
    private static String fillBlock(String text) {
        int spaceNum = text.getBytes().length%16==0?0:16-text.getBytes().length%16;
        for (int i = 0; i<spaceNum; i++) text += " ";
        return text;
    }
    private static String byteArrayToHexString(byte[] byteArray) {
        StringBuilder hexStringBuilder = new StringBuilder(2 * byteArray.length);
        for (byte b : byteArray) {
            hexStringBuilder.append(String.format("%02X", b));
        }
        return hexStringBuilder.toString();
    }

    private static String byteArrayToString(byte[] byteArray) {
        StringBuilder stringBuilder = new StringBuilder(byteArray.length);
        for (byte b : byteArray) {
            stringBuilder.append((char) b);
        }
        return stringBuilder.toString();
    }

    private void encryptString(String clearText, String alias) {
        try {
            RSAPublicKey publicKey = (RSAPublicKey) keyStore.getCertificate(alias).getPublicKey();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] cipherText = cipher.doFinal(clearText.getBytes());

            encryptKeyAES = Base64.encodeToString(cipherText, Base64.DEFAULT);
            Log.e(MotionEffect.TAG, "encryptStringKey: " + Base64.encodeToString(cipherText, Base64.DEFAULT));
        } catch (Exception e) {
            Log.d(MotionEffect.TAG, e.getMessage());
        }
    }

    private byte[] encryptAESKey(String alias, SecretKey aesKey) {
        try {
            RSAPublicKey publicKey = (RSAPublicKey) keyStore.getCertificate(alias).getPublicKey();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(aesKey.getEncoded());
        } catch (Exception e) {
            Log.d(MotionEffect.TAG, e.getMessage());
            return null;
        }
    }

    private String encodeToString(byte[] data) {
        return Base64.encodeToString(data, Base64.NO_WRAP); // Use NO_WRAP to avoid line breaks
    }


    private void decryptString(String cipherText, String alias) {
        try {
            PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null)).getPrivateKey();

            if (privateKey != null && cipherText != null) {
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);

                try {
                    byte[] decryptText = cipher.doFinal(Base64.decode(cipherText, Base64.DEFAULT));
                    if (decryptText.length >= 256 / 8) {
                        byte[] trimmedKey = Arrays.copyOf(decryptText, 256 / 8);
                        Log.e(MotionEffect.TAG, "decryptStringKey trimmedKey: " + new String(trimmedKey));
                        Log.e(MotionEffect.TAG, "Trimmed Decrypted Key Size: " + trimmedKey.length * 8 + " bits");
                    }
                        Log.e(MotionEffect.TAG, "decryptStringKey: " + new String(decryptText));
                    Log.e(MotionEffect.TAG, "Key Size giai ma " + decryptText.length * 8 + " bits");
                    //======================
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


}
