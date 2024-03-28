package com.example.officeorder.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officeorder.Adapter.SearchAdapter;
import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Model.Product;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Service.ApiService;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class UuDaiFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private ImageButton btnback, btncard;
    private TextView btnsearch;
    private ApiService apiService;
    private String accessToken = "";
    private VariableEnvironment variableEnvironment = new VariableEnvironment("value");
    public UuDaiFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dau_muc, container, false);
        recyclerView = v.findViewById(R.id.rvvergestable);
        btnback = v.findViewById(R.id.btnback);
        btncard = v.findViewById(R.id.btncard);
        btnsearch = v.findViewById(R.id.btnsearch);

        UserSingleton userSingleton = UserSingleton.getInstance();
        String id_User  = userSingleton.getUserId();
        accessToken = userSingleton.getAccessToken();
        apiService = ApiClient.getApiService();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new SearchAdapter(getContext(), getFragmentManager());
        recyclerView.setAdapter(adapter);

        ChobanDataTask chobanDataTask = new ChobanDataTask();
        chobanDataTask.fetchData();
        btnback.setOnClickListener(item-> {
            Fragment searchFragment = new HomeFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_Container, searchFragment);
            transaction.commit();

        });

        btnsearch.setOnClickListener(item->{
            Log.e("Keyword", "Search ");
            Fragment searchFragment = new SearchFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_Container, searchFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        });

        btncard.setOnClickListener(item-> {
            Fragment fragment = new CartFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_Container, fragment, "verges_tag");
            transaction.addToBackStack("verges_tag");
            transaction.commit();

        });

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    public class ChobanDataTask {

        private Executor executor = Executors.newSingleThreadExecutor();

        public void fetchData() {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Call<List<Product>> call = apiService.getProductCoupon("Bearer " + accessToken);
                    call.enqueue(new retrofit2.Callback<List<Product>>() {
                        @Override
                        public void onResponse(Call<List<Product>> call, retrofit2.Response<List<Product>> response) {
                            if (response.isSuccessful()) {
                                List<Product> dataList = response.body();
                                if (dataList != null) {
                                    adapter.setData(dataList);
                                }
                            } else {
                                // Handle unsuccessful response
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Product>> call, Throwable t) {
                            // Handle failure
                        }
                    });
                }
            });
        }

    }

}