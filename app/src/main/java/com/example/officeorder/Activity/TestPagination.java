package com.example.officeorder.Activity;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.officeorder.Adapter.PaginationAdapter;
import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.getToken;
import com.example.officeorder.Model.Pagition;
import com.example.officeorder.Model.Product;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Request.ProductFilterRequest;
import com.example.officeorder.Service.ApiService;
import com.example.officeorder.utils.PaginationScrollListener;

import java.util.List;

import retrofit2.Call;

public class TestPagination extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    PaginationAdapter chobanAdapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView rvChoban;
    ProgressBar progressBar;


    private ApiService apiService;
    //=========PHÂN TRANG=============
    private int totalPage = 10;
    private int page = 0;
    private int size = 8;
    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 5;
    private int currentPage = PAGE_START;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_pagination);

        rvChoban = (RecyclerView) findViewById(R.id.main_recycler);
        progressBar = (ProgressBar) findViewById(R.id.main_progress);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.main_swiperefresh);

        apiService = ApiClient.getApiService();

        chobanAdapter = new PaginationAdapter(this, getSupportFragmentManager());
        rvChoban.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvChoban.setLayoutManager(gridLayoutManager);
        rvChoban.setItemAnimator(new DefaultItemAnimator());
        rvChoban.setAdapter(chobanAdapter);
        rvChoban.addOnScrollListener(new PaginationScrollListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        //loadFirstPage();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadFirstPage();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                loadFirstPage();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");

        currentPage=0;
        ProductFilterRequest productFilterRequest = new ProductFilterRequest();
        productFilterRequest.setProductName("");
        productFilterRequest.setPriceFrom(0);
        productFilterRequest.setPriceTo(0);
        productFilterRequest.setCategoryName("");

        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        ////================
        Call<Pagition> call = apiService.getPagitionProduct(currentPage,size,productFilterRequest,"Bearer " + accessToken);
        call.enqueue(new retrofit2.Callback<Pagition>() {
            @Override
            public void onResponse(Call<Pagition> call, retrofit2.Response<Pagition> response) {
                if (response.isSuccessful()) {
                    Log.e("PHANTRANG", "onResponse: OK");

                    Pagition pagition = response.body();
                    progressBar.setVisibility(View.GONE);
                    TOTAL_PAGES = pagition.getTotalPages();
                    List<Product> dataList = pagition.getProduct();
                    if (dataList != null) {
                        chobanAdapter.setData(dataList);

                        if (currentPage <= TOTAL_PAGES) chobanAdapter.addLoadingFooter();
                        else isLastPage = true;
                    }
                }
            }
            @Override
            public void onFailure(Call<Pagition> call, Throwable t) {
                // Handle failure
                Log.e("PHANTRANG", "onFailure: "+ t.getMessage() );
            }
        });

    }
    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

        ProductFilterRequest productFilterRequest = new ProductFilterRequest();
        productFilterRequest.setProductName("");
        productFilterRequest.setPriceFrom(0);
        productFilterRequest.setPriceTo(0);
        productFilterRequest.setCategoryName("");

        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        ////================
        Call<Pagition> call = apiService.getPagitionProduct(currentPage,size,productFilterRequest,"Bearer " + accessToken);
        call.enqueue(new retrofit2.Callback<Pagition>() {
            @Override
            public void onResponse(Call<Pagition> call, retrofit2.Response<Pagition> response) {
                if (response.isSuccessful()) {
                    Log.e("PHANTRANG", "onResponse: OK");

                    chobanAdapter.removeLoadingFooter();
                    isLoading = false;
                    Pagition pagition = response.body();
                    progressBar.setVisibility(View.GONE);
                    totalPage = pagition.getTotalPages();
                    List<Product> dataList = pagition.getProduct();
                    if (dataList != null) {
                        //chobanAdapter.setData(dataList);
                        chobanAdapter.addAll(dataList);
                        if (currentPage != TOTAL_PAGES) chobanAdapter.addLoadingFooter();
                        else isLastPage = true;
                    }


                }
            }
            @Override
            public void onFailure(Call<Pagition> call, Throwable t) {
                // Handle failure
                Log.e("PHANTRANG", "onFailure: "+ t.getMessage() );
            }
        });

    }

}