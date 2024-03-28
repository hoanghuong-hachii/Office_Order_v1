package com.example.officeorder.Fragment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.officeorder.Adapter.PaginationAdapter;
import com.example.officeorder.Adapter.SearchAdapter;
import com.example.officeorder.Adapter.SlideAdapter;
import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.ApiManager;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.getToken;
import com.example.officeorder.Model.Pagition;
import com.example.officeorder.Model.Product;
import com.example.officeorder.Model.SlideItem;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Request.ProductFilterRequest;
import com.example.officeorder.Request.RequestTokenBody;
import com.example.officeorder.Service.ApiService;
import com.example.officeorder.utils.PaginationScrollListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HomeFragment extends Fragment {

    private DatabaseReference databaseReference;
    private SearchAdapter popularAdapter,chobanAdapter;
    //private PaginationAdapter chobanAdapter;
    private VariableEnvironment variableEnvironment = new VariableEnvironment("v");
    private ApiService apiService;
    private Button btn_SeeMorechoban, btn_SeeMoreuudai;
    private String id_User;
    private ViewPager2 viewPager2;
    private TextView editTextSearch;
    private Handler slideHandler = new Handler();
    private ImageView iv_dau_muc,iv_spgiay, iv_filehs, iv_the,iv_bangdinh,iv_biamau;
    private ImageView iv_but, iv_ghim, iv_hodan, iv_kep, iv_khayke, iv_daokeo;

    private String accessToken = "";
    private LinearLayout loadingProgressBar;
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

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Test();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("proLike");

        getFormWidgets(view);
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);
        //========token---------
        getToken get = new getToken();
        get.test();

        UserSingleton userSingleton = UserSingleton.getInstance();
        id_User  = userSingleton.getUserId();

        accessToken = userSingleton.getAccessToken();
        apiService = ApiClient.getApiService();

        ButtonEvent();

    }

    @Override
    public void onStart() {
        super.onStart();

        getToken();
        //loadingProgressBar.setVisibility(View.VISIBLE);
        fetchChobanData(getView());
        fetchPopularData(getView());
       // fetchHotData(getView());
        AdvertisesSlide();

    }

    public void fetchChobanData(View view){

        RecyclerView rvChoban = view.findViewById(R.id.rvChoban);
        chobanAdapter = new SearchAdapter(getContext(), getChildFragmentManager());
        rvChoban.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
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
        loadFirstPage();

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
                    //progressBar.setVisibility(View.GONE);
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
                    //progressBar.setVisibility(View.GONE);
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

    public class Testphantrang {
        private Executor executor = Executors.newSingleThreadExecutor();

        public void fetchData() {
            executor.execute(new Runnable() {
                @Override
                public void run() {

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
                    Call<Pagition> call = apiService.getPagitionProduct(page,size,productFilterRequest,"Bearer " + accessToken);
                    call.enqueue(new retrofit2.Callback<Pagition>() {
                        @Override
                        public void onResponse(Call<Pagition> call, retrofit2.Response<Pagition> response) {
                            if (response.isSuccessful()) {
                                Log.e("PHANTRANG", "onResponse: OK");
                                isLoading = false;
                                Pagition pagition = response.body();
                                totalPage = pagition.getTotalPages();
                                List<Product> dataList = pagition.getProduct();
                                if (dataList != null) {
                                    chobanAdapter.setData(dataList);
                                }

                                isLastPage = currentPage == totalPage -1;
                            } else {
                            }
                        }
                        @Override
                        public void onFailure(Call<Pagition> call, Throwable t) {
                            // Handle failure
                            Log.e("PHANTRANG", "onFailure: "+ t.getMessage() );
                        }
                    });
                }
            });
        }

        public void loadMoreData() {

            if (!isLastPage) {
                currentPage++;
                fetchData();
            }
        }
    }

    public void fetchPopularData(View view){

        popularAdapter = new SearchAdapter(getContext(), getFragmentManager());
        RecyclerView rvPopular = view.findViewById(R.id.rvPopular);
        rvPopular.setAdapter(popularAdapter);
        rvPopular.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        PopularDataTask popularDataTask = new PopularDataTask();
        popularDataTask.fetchData();
    }

    public void getFormWidgets(View view){
        viewPager2 = view.findViewById(R.id.viewPager2);
        editTextSearch = view.findViewById(R.id.editTextsearch);
        iv_hodan = view.findViewById(R.id.btn_keodan);
        //iv_dau_muc = view.findViewById(R.id.btn_dau_mucdau);
        iv_bangdinh =view.findViewById(R.id.btn_bangdinh_catbangdinh);
        iv_but =view.findViewById(R.id.btn_but);
        iv_the =view.findViewById(R.id.btn_baodungthe);
        iv_daokeo =view.findViewById(R.id.btn_daotro);
        iv_biamau =view.findViewById(R.id.btn_biamau_mika);
        iv_dau_muc =view.findViewById(R.id.btn_dau_mucdau);
        iv_ghim =view.findViewById(R.id.btn_ghimcai);
        viewPager2 = view.findViewById(R.id.viewPager2);
        iv_kep = view.findViewById(R.id.btn_kepbuom);
        iv_khayke = view.findViewById(R.id.btn_khay);
        iv_spgiay = view.findViewById(R.id.btn_spgiay);
        iv_filehs = view.findViewById(R.id.btn_filehoso);
        btn_SeeMoreuudai = view.findViewById(R.id.btn_SeeMoreuudai);
        btn_SeeMorechoban = view.findViewById(R.id.btn_SeeMorechoban);
    }

    public void AdvertisesSlide(){

        List<SlideItem> slideItems = new ArrayList<>();
        slideItems.add(new SlideItem(R.drawable.img_slide1));
        slideItems.add(new SlideItem(R.drawable.ic_slide2));
        slideItems.add(new SlideItem(R.drawable.ic_slide3));
        slideItems.add(new SlideItem(R.drawable.ic_slide4));
        slideItems.add(new SlideItem(R.drawable.ic_slide5));
        viewPager2.setAdapter(new SlideAdapter(slideItems, viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(5);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
            float absPosition = Math.abs(position);
        });
        viewPager2.setPageTransformer(compositePageTransformer);

        // Initialize slideHandler and post a delayed runnable to auto-scroll ViewPager2
        slideHandler = new Handler(Looper.getMainLooper());
        ViewPager2 finalViewPager = viewPager2;
        slideHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finalViewPager.setCurrentItem((finalViewPager.getCurrentItem() + 1) % slideItems.size(), true);
                slideHandler.postDelayed(this, 2000);
            }
        }, 3000);
    }

    public class ChobanDataTask {

        private Executor executor = Executors.newSingleThreadExecutor();

        public void fetchData() {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    //=========Access Token=========
                    UserSingleton userSingleton = UserSingleton.getInstance();
                    getToken get = new getToken();
                    get.test();
                    String accessToken = userSingleton.getAccessToken();
                    ////================
                    Call<List<Product>> call = apiService.getProductCoupon("Bearer " + accessToken);
                    call.enqueue(new retrofit2.Callback<List<Product>>() {
                        @Override
                        public void onResponse(Call<List<Product>> call, retrofit2.Response<List<Product>> response) {
                            if (response.isSuccessful()) {
                                List<Product> dataList = response.body();
                                if (dataList != null) {
                                    chobanAdapter.setData(dataList);
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

    public class TestDataTask {

        private Executor executor = Executors.newSingleThreadExecutor();

        public void fetchData() {
            executor.execute(new Runnable() {
                @Override
                public void run() {

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
                    Call<Pagition> call = apiService.getPagitionProduct(page,size,productFilterRequest,"Bearer " + accessToken);
                    call.enqueue(new retrofit2.Callback<Pagition>() {
                        @Override
                        public void onResponse(Call<Pagition> call, retrofit2.Response<Pagition> response) {
                            if (response.isSuccessful()) {
                                Pagition pagition = response.body();
                                totalPage = pagition.getTotalPages();
                                List<Product> dataList = pagition.getProduct();
                                if (dataList != null) {
                                    chobanAdapter.setData(dataList);
                                }
                            } else {

                            }
                        }
                        @Override
                        public void onFailure(Call<Pagition> call, Throwable t) {
                            // Handle failure
                        }
                    });
                }
            });
        }
    }
    public class PopularDataTask {

        public void fetchData() {
            //=========Access Token=========
            UserSingleton userSingleton = UserSingleton.getInstance();
            getToken get = new getToken();
            get.test();
            String accessToken = userSingleton.getAccessToken();
            ////================
            Call<List<Product>> call = apiService.getProductCoupon("Bearer " + accessToken);
            call.enqueue(new Callback<List<Product>>() {
                @Override
                public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                    if (response.isSuccessful()) {
                        Log.e(TAG, "onResponse: "+ "Coupon" );
                        List<Product> dataList = response.body();
                        if (dataList != null) {
                            popularAdapter.setData(dataList);
                        }
                    } else {
                    }
                }

                @Override
                public void onFailure(Call<List<Product>> call, Throwable t) {
                    Log.e("Get coupon", "onFailure: "+ t.getMessage() );
                }
            });
        }


    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d(TAG, "TOKEN FCM = "+token);

                        sendRegistrationToServer(token);
                    }

                    private void sendRegistrationToServer(String token) {

                        RequestTokenBody tokenDevice = new RequestTokenBody();
                        tokenDevice.setTokenDevice(token);
                        tokenDevice.setIdUser(id_User);

                        Call<Void> call = apiService.registerDevice(tokenDevice);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Log.e(TAG, "onResponse: OK " );

                                } else {
                                    Log.e(TAG, "onResponse: Fail " );

                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e(TAG, "onResponse: Fail "+t.getMessage() );
                            }
                        });
                    }
                });
    }
    public void ButtonEvent(){

        editTextSearch.setOnClickListener(v-> {
                // startActivity(new Intent(requireContext(), SearchableActivity.class));
            Fragment fragment = new SearchFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_Container, fragment, "home");
            transaction.addToBackStack("home");
            transaction.commit();


        });
        iv_dau_muc.setOnClickListener( v -> {
            swipeFragmentLeft(new DauMucFragment());
        });

        btn_SeeMorechoban.setOnClickListener( v->{
            swipeFragmentLeft(new ChoBanFragment());
        });
        btn_SeeMoreuudai.setOnClickListener( v->{
            swipeFragmentLeft(new UuDaiFragment());
        });

        iv_khayke.setOnClickListener( v -> {
            swipeFragmentLeft(new KhayFragment());
        });

        iv_hodan.setOnClickListener( v -> {
            swipeFragmentLeft(new HoleFragment());
        });
        iv_kep.setOnClickListener( v -> {
            swipeFragmentLeft(new KepFragment());
        });

        iv_biamau.setOnClickListener( v -> {
            swipeFragmentLeft(new BiaFragment());
        });

        iv_spgiay.setOnClickListener( v -> {
            swipeFragmentLeft(new PaperFragment());
        });

        iv_bangdinh.setOnClickListener( v -> {
            swipeFragmentLeft(new BangDinhFragment());
        });

        iv_but.setOnClickListener( v -> {
            swipeFragmentLeft(new PenFragment());
        });

        iv_daokeo.setOnClickListener( v -> {
            swipeFragmentLeft(new DaoFragment());
        });

        iv_filehs.setOnClickListener( v -> {
            swipeFragmentLeft(new FileFragment());
        });

        iv_ghim.setOnClickListener( v -> {
            swipeFragmentLeft(new GhimFragment());
        });

        iv_the.setOnClickListener( v -> {
            swipeFragmentLeft(new CardFragment());
        });
    }

    public void swipeFragmentLeft(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
               // .setCustomAnimations(R.anim.silde_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(R.id.main_Container, fragment,"home")
                .addToBackStack("home")
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove the slideHandler callbacks to prevent memory leaks
        if (slideHandler != null) {
            slideHandler.removeCallbacksAndMessages(null);
            slideHandler = null;
        }
    }

    private void Test(){
        ApiManager.init();
        UserSingleton userSingleton = UserSingleton.getInstance();
        String token = userSingleton.getToken();
        Log.e(TAG, "Test: "+token );
        ApiManager.refreshToken(token, new ApiManager.OnTokenRefreshListener() {
            @Override
            public void onTokenRefreshed(String accessToken) {
                // Handle the refreshed access token
                Log.e("TestActivity", "Access Token: " + accessToken);
            }

            @Override
            public void onTokenRefreshFailed() {
                // Handle token refresh failure
                Log.e("TestActivity", "Token refresh failed");
            }
        });
    }
}
