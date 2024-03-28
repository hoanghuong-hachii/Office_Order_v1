package com.example.officeorder.Fragment;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.officeorder.Activity.InfoAddressActivity;
import com.example.officeorder.Activity.LoginActivity;
import com.example.officeorder.Activity.SettingActivity;
import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.DatabaseHelper;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.getToken;
import com.example.officeorder.Model.UserProfile;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Service.ApiService;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileFragment extends Fragment {

    ImageView iv_avatar;
    TextView tv_tentaikhoan;
    private String idUser;
    private VariableEnvironment variableEnvironment = new VariableEnvironment("d");
    ImageView iv_setting;
    Button logout;
    ImageView iv_background;
    private ApiService apiService;
    private TableRow quanly,yeuthich,diachi;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        apiService = ApiClient.getApiService();
        iv_background = v.findViewById(R.id.iv_background);
        quanly = v.findViewById(R.id.row1);
        yeuthich =v.findViewById(R.id.row2);
        iv_avatar = v.findViewById(R.id.iv_avatar);
        tv_tentaikhoan = v.findViewById(R.id.tv_tentaikhoan);

        diachi =v.findViewById(R.id.row4);
        UserSingleton userSingleton = UserSingleton.getInstance();
        idUser  = userSingleton.getUserId();

        GetDataInfo();

        iv_setting =v.findViewById(R.id.iv_setting);
        logout = v.findViewById(R.id.logout);
//        chinhsach =v.findViewById(R.id.row3);

        iv_setting.setOnClickListener( item ->{
            Intent intent = new Intent(getActivity(), SettingActivity.class);
            startActivity(intent);
        });

        diachi.setOnClickListener( item->{
            Intent intent = new Intent(getActivity(), InfoAddressActivity.class);
            startActivity(intent);
        });

        quanly.setOnClickListener(item->{

            Fragment fragment = new ManageOrderFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_Container, fragment, "profile_tag");
            transaction.addToBackStack("profile_tag");
            transaction.commit();

        });

        yeuthich.setOnClickListener(item -> {

            Fragment fragment = new ProductLikeFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_Container, fragment, "profile_tag");
            transaction.addToBackStack("profile_tag");
            transaction.commit();

        });


        logout.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                    .setPositiveButton("OK", (dialog, which) -> {
                        logoutAndExit();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return v;
    }

    private void logoutAndExit() {

        // Trong phương thức logout hoặc khi đăng xuất
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        boolean isDeleted = databaseHelper.deleteUserId();

        if (isDeleted) {
            Log.e(TAG, "logoutAndExit: OK" );
        } else {
            Log.e(TAG, "logoutAndExit: Failed" );
        }


        startActivity(new Intent(getActivity(), LoginActivity.class));
        //getActivity().finish();
        System.exit(0);
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

                    if (data != null) {
                        String userName = data.getUserName();
                        String avatar = data.getAvatar();
                        String background = data.getBackground();
                        tv_tentaikhoan.setText(userName);

                        String imageUrl = variableEnvironment.Valiables() + avatar;
                        String backgroundUrl = variableEnvironment.Valiables() + background;

                        Picasso.get()
                                .load(imageUrl)
                                .into(iv_avatar);
                        Picasso.get()
                                .load(backgroundUrl)
                                 .into(iv_background);
                    }
                } else {
                    // Handle unsuccessful response
                }
            }
            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                // Handle failure
            }
        });
    }
}