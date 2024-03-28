package com.example.officeorder.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officeorder.Adapter.BillAdapter;
import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.getToken;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Request.PostBillRequest;
import com.example.officeorder.Service.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProcessingFragment extends Fragment {

    private RecyclerView rvItemsInCart;
    private BillAdapter adapter;
    private String id_User;
    private ApiService apiService;
    private VariableEnvironment variableEnvironment = new VariableEnvironment("d");

    public ProcessingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wait, container, false);

        UserSingleton userSingleton = UserSingleton.getInstance();
        id_User  = userSingleton.getUserId();

        Log.e("USERR", "onClick: "+id_User);

        apiService = ApiClient.getApiService();
        rvItemsInCart = v.findViewById(R.id.rv);

        rvItemsInCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new BillAdapter(getContext(), getFragmentManager());
        rvItemsInCart.setAdapter(adapter);
        fetchData(id_User);


        return v;
    }

    private void fetchData(String idUser) {
//=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        ////================
        Call<List<PostBillRequest>> call = apiService.getBills(idUser, "Shipped","Bearer " + accessToken);
        call.enqueue(new Callback<List<PostBillRequest>>() {
            @Override
            public void onResponse(Call<List<PostBillRequest>> call, Response<List<PostBillRequest>> response) {
                if (response.isSuccessful()) {
                    List<PostBillRequest> bills = response.body();
                    if (bills != null) {
                        adapter.setData(bills);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<PostBillRequest>> call, Throwable t) {
                // Handle failure
            }
        });
    }
}