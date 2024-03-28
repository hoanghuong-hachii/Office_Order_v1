package com.example.officeorder.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.officeorder.R;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddressFragment extends Fragment {
    private OkHttpClient client;
    private Spinner provinceSpinner;
    private Spinner districtSpinner;
    private Spinner wardSpinner;
    private Button btnSubmit;
    private ImageView backButton;
    private EditText fullname,phone,email,editTextAddress;


    public AddressFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new OkHttpClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_address, container, false);

        provinceSpinner = view.findViewById(R.id.provinceSpinner);
        districtSpinner = view.findViewById(R.id.districtSpinner);
        wardSpinner = view.findViewById(R.id.wardSpinner);
        backButton = view.findViewById(R.id.backButton);
        fullname = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        email = view.findViewById(R.id.email);
        editTextAddress = view.findViewById(R.id.editTextAddress);

        btnSubmit = view.findViewById(R.id.btnSubmit);
        fetchProvinces();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra các trường có được điền đầy đủ hay không
                if (fullname.getText().toString().isEmpty() ||
                        phone.getText().toString().isEmpty() ||
                        email.getText().toString().isEmpty() ||
                        editTextAddress.getText().toString().isEmpty() ||
                        provinceSpinner.getSelectedItem() == null ||
                        districtSpinner.getSelectedItem() == null ||
                        wardSpinner.getSelectedItem() == null) {
                    // Hiển thị thông báo hoặc thông báo lỗi tùy ý
                    Toast.makeText(getActivity(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    // Tạo Bundle và đặt dữ liệu vào
                    Bundle bundle = new Bundle();
                    bundle.putString("fullname", fullname.getText().toString());
                    bundle.putString("phone", phone.getText().toString());
                    bundle.putString("email", email.getText().toString());
                    bundle.putString("address", editTextAddress.getText().toString());
                    // Lấy dữ liệu từ Spinner
                    String selectedProvince = (String) provinceSpinner.getSelectedItem();
                    String selectedDistrict = (String) districtSpinner.getSelectedItem();
                    String selectedWard = (String) wardSpinner.getSelectedItem();
                    bundle.putString("province", selectedProvince);
                    bundle.putString("district", selectedDistrict);
                    bundle.putString("ward", selectedWard);

                    // Tạo instance của fragment OrderDetailFragment
                    CheckOutFragment orderDetailFragment = new CheckOutFragment();
                    // Đặt bundle vào fragment
                    orderDetailFragment.setArguments(bundle);

                    // Thực hiện chuyển fragment
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_Container, orderDetailFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra các trường có được điền đầy đủ hay không
                if (fullname.getText().toString().isEmpty() ||
                        phone.getText().toString().isEmpty() ||
                        email.getText().toString().isEmpty() ||
                        editTextAddress.getText().toString().isEmpty() ||
                        provinceSpinner.getSelectedItem() == null ||
                        districtSpinner.getSelectedItem() == null ||
                        wardSpinner.getSelectedItem() == null) {
                    // Hiển thị thông báo hoặc thông báo lỗi tùy ý
                    Toast.makeText(getActivity(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    // Tạo Bundle và đặt dữ liệu vào
                    Bundle bundle = new Bundle();
                    bundle.putString("fullname", fullname.getText().toString());
                    bundle.putString("phone", phone.getText().toString());
                    bundle.putString("email", email.getText().toString());
                    bundle.putString("address", editTextAddress.getText().toString());
                    // Lấy dữ liệu từ Spinner
                    String selectedProvince = (String) provinceSpinner.getSelectedItem();
                    String selectedDistrict = (String) districtSpinner.getSelectedItem();
                    String selectedWard = (String) wardSpinner.getSelectedItem();
                    bundle.putString("province", selectedProvince);
                    bundle.putString("district", selectedDistrict);
                    bundle.putString("ward", selectedWard);

                    // Tạo instance của fragment OrderDetailFragment
                    CheckOutFragment orderDetailFragment = new CheckOutFragment();
                    // Đặt bundle vào fragment
                    orderDetailFragment.setArguments(bundle);

                    // Thực hiện chuyển fragment
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_Container, orderDetailFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });


        return view;
    }

    private void fetchProvinces() {
        Request request = new Request.Builder()
                .url("https://provinces.open-api.vn/api/p/")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonData = response.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray provincesArray = new JSONArray(jsonData);

                                List<String> provinces = new ArrayList<>();
                                final List<String> provinceCodes = new ArrayList<>(); // Mảng lưu trữ các code tương ứng
                                for (int i = 0; i < provincesArray.length(); i++) {
                                    JSONObject provinceObject = provincesArray.getJSONObject(i);
                                    String provinceName = provinceObject.getString("name");
                                    String provinceCode = provinceObject.getString("code");
                                    provinces.add(provinceName);
                                    provinceCodes.add(provinceCode); // Thêm code vào mảng
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                                        android.R.layout.simple_spinner_item, provinces);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                provinceSpinner.setAdapter(adapter);

                                provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        String selectedProvince = provinces.get(position);
                                        String selectedProvinceCode = provinceCodes.get(position); // Lấy code tương ứng với tỉnh thành
                                        // Thực hiện các tác vụ khi chọn một tỉnh thành
                                        fetchDistricts(selectedProvinceCode);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    private void fetchDistricts(String selectedProvinceCode) {
        Request request = new Request.Builder()
                .url("https://provinces.open-api.vn/api/d/")
                .build();

        client.newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonData = response.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray districtsArray = new JSONArray(jsonData);

                                List<String> districts = new ArrayList<>();
                                final List<String> districtCodes = new ArrayList<>(); // Mảng lưu trữ các code tương ứng
                                for (int i = 0; i < districtsArray.length(); i++) {
                                    JSONObject districtObject = districtsArray.getJSONObject(i);
                                    String districtName = districtObject.getString("name");
                                    String districtCode = districtObject.getString("code");
                                    String districtProvinceCode = districtObject.getString("province_code");
                                    if (districtProvinceCode.equals(selectedProvinceCode)) {
                                        districts.add(districtName);
                                        districtCodes.add(districtCode); // Thêm code vào mảng
                                    }
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                                        android.R.layout.simple_spinner_item, districts);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                districtSpinner.setAdapter(adapter);

                                districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        String selectedDistrict = districts.get(position);
                                        String selectedDistrictCode = districtCodes.get(position); // Lấy code tương ứng với quận/huyện
                                        // Thực hiện các tác vụ khi chọn một quận/huyện
                                        fetchWards(selectedDistrictCode);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                                districtSpinner.setEnabled(true);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    private void fetchWards(String selectedDistrictCode) {
        Request request = new Request.Builder()
                .url("https://provinces.open-api.vn/api/w/")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse( Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonData = response.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray wardsArray = new JSONArray(jsonData);

                                List<String> wards = new ArrayList<>();
                                for (int i = 0; i < wardsArray.length(); i++) {
                                    JSONObject wardObject = wardsArray.getJSONObject(i);
                                    String wardName = wardObject.getString("name");
                                    String wardDistrictCode = wardObject.getString("district_code");
                                    if (wardDistrictCode.equals(selectedDistrictCode)) {
                                        wards.add(wardName);
                                    }
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                                        android.R.layout.simple_spinner_item, wards);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                wardSpinner.setAdapter(adapter);

                                wardSpinner.setEnabled(true);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }


}
