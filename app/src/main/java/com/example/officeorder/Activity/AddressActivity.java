package com.example.officeorder.Activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.getToken;
import com.example.officeorder.Model.District;
import com.example.officeorder.Model.Province;
import com.example.officeorder.Model.UserProfile;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.Model.Ward;
import com.example.officeorder.R;
import com.example.officeorder.Service.AddressService;
import com.example.officeorder.Service.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddressActivity extends AppCompatActivity {

    private AddressService apiService;
    private ApiService userService;
    private Spinner provinceSpinner;
    private Spinner districtSpinner;
    private Spinner wardSpinner;
    private Button btnSubmit;
    private VariableEnvironment variableEnvironment = new VariableEnvironment("");
    private ImageView backButton;
    private EditText editTextAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://provinces.open-api.vn/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(AddressService.class);

        //============
        provinceSpinner = findViewById(R.id.provinceSpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        wardSpinner = findViewById(R.id.wardSpinner);
        backButton = findViewById(R.id.backButton);
        editTextAddress = findViewById(R.id.editTextAddress);

        btnSubmit = findViewById(R.id.btnSubmit);


        fetchProvinces();
        //GetDataInfo();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (    editTextAddress.getText().toString().isEmpty() ||
                        provinceSpinner.getSelectedItem() == null ||
                        districtSpinner.getSelectedItem() == null ||
                        wardSpinner.getSelectedItem() == null) {

                    Toast.makeText(AddressActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {

                    Intent intent = new Intent(AddressActivity.this, SignUpActivity.class);
                    Bundle bundle = new Bundle();


                    bundle.putString("address", editTextAddress.getText().toString() );

                    String selectedProvince = (String) provinceSpinner.getSelectedItem();
                    String selectedDistrict = (String) districtSpinner.getSelectedItem();
                    String selectedWard = (String) wardSpinner.getSelectedItem();
                    bundle.putString("province", selectedProvince);
                    bundle.putString("district", selectedDistrict);
                    bundle.putString("ward", selectedWard);

                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (
                        editTextAddress.getText().toString().isEmpty() ||
                                provinceSpinner.getSelectedItem() == null ||
                                districtSpinner.getSelectedItem() == null ||
                                wardSpinner.getSelectedItem() == null) {

                    Toast.makeText(AddressActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {

                    Bundle bundle = new Bundle();

                    bundle.putString("address", editTextAddress.getText().toString());

                    String selectedProvince = (String) provinceSpinner.getSelectedItem();
                    String selectedDistrict = (String) districtSpinner.getSelectedItem();
                    String selectedWard = (String) wardSpinner.getSelectedItem();
                    bundle.putString("province", selectedProvince);
                    bundle.putString("district", selectedDistrict);
                    bundle.putString("ward", selectedWard);

                    Intent intent = new Intent(AddressActivity.this, SignUpActivity.class);

                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

    }

    private void GetDataInfo() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(variableEnvironment.Valiables())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        int idUser;
        UserSingleton userSingleton = UserSingleton.getInstance();
        String id_User  = userSingleton.getUserId();

        userService = retrofit.create(ApiService.class);
        //
       //=========Access Token=========
       // UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        ////================
        Call<UserProfile> call = userService.getUserInfo(id_User,"Bearer "+ accessToken);

        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful()) {
                    UserProfile data = response.body();


                    if (data != null) {

                        String address = data.getAddress();

                        String[] addressParts = address.split(", ");

                        if (addressParts.length >= 4) {
                            String street = addressParts[0];
                            String ward = addressParts[1];
                            String district = addressParts[2];
                            String province = addressParts[3];

                            provinceSpinner.setTag(province);
                            districtSpinner.setTag(district);
                            wardSpinner.setTag(ward);
                            editTextAddress.setText(street);

                        } else {
                            System.err.println("Invalid address format");
                        }

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
    private void fetchProvinces() {
        Call<List<Province>> call = apiService.getProvinces();
        call.enqueue(new Callback<List<Province>>() {
            @Override
            public void onResponse(Call<List<Province>> call, Response<List<Province>> response) {
                if (response.isSuccessful()) {
                    List<Province> provinces = response.body();
                    List<String> provincesName = new ArrayList<>();
                    final List<String> provinceCodes = new ArrayList<>();
                    for(Province province : provinces){

                    String provinceName = province.getName();
                    int provinceCode = province.getCode();
                    String proCode = String.valueOf(provinceCode);

                    provincesName.add(provinceName);
                    provinceCodes.add(proCode);

                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddressActivity.this,
                            android.R.layout.simple_spinner_item, provincesName);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    provinceSpinner.setAdapter(adapter);

                    provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedProvince = provincesName.get(position);
                            Log.e( "onItemSelected: ", selectedProvince);
                            String selectedProvinceCode = provinceCodes.get(position);
                            fetchDistricts(selectedProvinceCode);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Province>> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void fetchDistricts(String selectedProvinceCode) {
        Call<List<District>> call = apiService.getDistricts();
        call.enqueue(new Callback<List<District>>() {
            @Override
            public void onResponse(Call<List<District>> call, Response<List<District>> response) {
                if (response.isSuccessful()) {
                    List<District> districts = response.body();
                    List<String> districtsName = new ArrayList<>();
                    final List<String> districtCodes = new ArrayList<>();
                    for (District district : districts) {

                        String districtName = district.getName();
                        int districtCodeInt = district.getCode();
                        String districtCode = String.valueOf(districtCodeInt);
                        int districtProvinceCodeInt = district.getProvinceCode();
                        String districtProvinceCode = String.valueOf(districtProvinceCodeInt);
                        if (districtProvinceCode.equals(selectedProvinceCode)) {
                            districtsName.add(districtName);
                            districtCodes.add(districtCode);
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddressActivity.this,
                            android.R.layout.simple_spinner_item, districtsName);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    districtSpinner.setAdapter(adapter);

                    districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedDistrict = districtsName.get(position);
                            Log.e(TAG, "onItemSelected: "+ selectedDistrict );
                            String selectedDistrictCode = districtCodes.get(position);
                            fetchWards(selectedDistrictCode);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    districtSpinner.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<List<District>> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void fetchWards(String selectedDistrictCode) {
        Call<List<Ward>> call = apiService.getWards();
        call.enqueue(new Callback<List<Ward>>() {
            @Override
            public void onResponse(Call<List<Ward>> call, Response<List<Ward>> response) {
                if (response.isSuccessful()) {
                    List<Ward> wards = response.body();
                    //=============
                    List<String> wardsName = new ArrayList<>();
                    for (Ward ward : wards) {

                        String wardName = ward.getName();
                        int wardDistrictCodeInt = ward.getDistrictCode();
                        String wardDistrictCode = String.valueOf(wardDistrictCodeInt);
                        if (wardDistrictCode.equals(selectedDistrictCode)) {
                            wardsName.add(wardName);
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddressActivity.this,
                            android.R.layout.simple_spinner_item, wardsName);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    wardSpinner.setAdapter(adapter);

                    wardSpinner.setEnabled(true);
                }
            }
            @Override
            public void onFailure(Call<List<Ward>> call, Throwable t) {
                // Handle failure
            }
        });
    }
}