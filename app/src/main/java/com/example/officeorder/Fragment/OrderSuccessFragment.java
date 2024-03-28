package com.example.officeorder.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officeorder.Adapter.SearchAdapter;
import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.getToken;
import com.example.officeorder.MainActivity;
import com.example.officeorder.Model.Product;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Service.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderSuccessFragment extends Fragment {

    private String accessToken="";
    private Button btn_chitiet, btn_home;
    private RecyclerView rv_sp;
    private ApiService apiService;
    private SearchAdapter adapter;

    private VariableEnvironment variableEnvironment = new VariableEnvironment("v");

    private int idUser;
    public OrderSuccessFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_order_success, container, false);
        UserSingleton userSingleton = UserSingleton.getInstance();
        String id_User  = userSingleton.getUserId();

        apiService = ApiClient.getApiService();
        btn_chitiet = v.findViewById(R.id.btn_chi_tiet_don_hang);
        btn_home = v.findViewById(R.id.btn_home);

        btn_chitiet.setOnClickListener( item ->{

            Fragment fragment = new ManageOrderFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_Container, fragment, "profile_tag");
            transaction.addToBackStack("profile_tag");
            transaction.commit();
        });

        btn_home.setOnClickListener( item ->{

            startActivity(new Intent(getActivity(), MainActivity.class));
//            Fragment fragment = new HomeFragment();
//            FragmentTransaction transaction = getFragmentManager().beginTransaction();
//            transaction.replace(R.id.frame_layout, fragment);
//            transaction.commit();
        });
        fetchChobanData(v);
        return v;
    }

    public void fetchChobanData(View view){

        rv_sp = view.findViewById(R.id.rv_sp_tuongtu);
        adapter = new SearchAdapter(getContext(), getFragmentManager());
        rv_sp.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_sp.setLayoutManager(gridLayoutManager);

        rv_sp.setAdapter(adapter);
        ChobanDataTask chobanDataTask = new ChobanDataTask();
        chobanDataTask.fetchData();
    }
    public class ChobanDataTask {

        public void fetchData() {
            //=========Access Token=========
            UserSingleton userSingleton = UserSingleton.getInstance();
            getToken get = new getToken();
            get.test();
            accessToken = userSingleton.getAccessToken();
            //=======
            Call<List<Product>> call = apiService.getProducts("Bearer " + accessToken);
            call.enqueue(new retrofit2.Callback<List<Product>>() {
                @Override
                public void onResponse(Call<List<Product>> call, retrofit2.Response<List<Product>> response) {
                    if (response.isSuccessful()) {
                        List<Product> dataList = response.body();
                        if (dataList != null) {
                            adapter.setData(dataList);
                        }
                    } else {
                    }
                }
                @Override
                public void onFailure(Call<List<Product>> call, Throwable t) {
                }
            });
        }


    }
}