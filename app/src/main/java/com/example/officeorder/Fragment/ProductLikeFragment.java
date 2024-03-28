package com.example.officeorder.Fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officeorder.Adapter.ProductLikeAdapter;
import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.getToken;
import com.example.officeorder.Model.ProductLikeSql;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Service.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ProductLikeFragment extends Fragment {
    private List<String> categories = new ArrayList<>();

    private boolean isEditing = false;
    private boolean isShowNgangHang = false;
    private TextView tv_chinhsua;
    private TextInputEditText searchView;
    private int[] clickCount = new int[15];
    private ImageButton btn_back,cardbtn;
    private RecyclerView recyclerView;
    private ProductLikeAdapter adapter;
    private VariableEnvironment variableEnvironment = new VariableEnvironment("value");
    private ApiService apiService;
    private String idUser;
    private Button btn_bothich;
    private TextView tv_nganhhang,tv_all, tv_giamgia;
    private LinearLayout ln_nganhhang;
    private PopupWindow popupWindow;

    private boolean isTvAllSelected = true;
    public ProductLikeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_product_like, container, false);

        UserSingleton userSingleton = UserSingleton.getInstance();
        idUser  = userSingleton.getUserId();

        apiService = ApiClient.getApiService();
        //====
        btn_back = v.findViewById(R.id.btn_back);
        tv_nganhhang =v.findViewById(R.id.tv_nganhhang);
        //ln_nganhhang = v.findViewById(R.id.ln_nganhhang);
        tv_chinhsua =v.findViewById(R.id.tv_chinhsua);
       btn_bothich = v.findViewById(R.id.btn_bothich);
        cardbtn = v.findViewById(R.id.cardbtn);
        recyclerView = v.findViewById(R.id.rvItemsInCart);
        adapter = new ProductLikeAdapter(getContext(), getFragmentManager());

        tv_all = v.findViewById(R.id.tv_all);
        tv_giamgia = v.findViewById(R.id.tv_giamgia);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        getProductLikeTask();

        btn_back.setOnClickListener( item ->{
            getActivity().onBackPressed();
        });

        cardbtn.setOnClickListener(item->{

            Fragment fragment = new CartFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_Container, fragment, "details_tag");
            transaction.addToBackStack("details_tag");
            transaction.commit();
        });

        tv_nganhhang.setOnClickListener(item -> {
            showPopup();
            isTvAllSelected = false;
            isShowNgangHang = !isShowNgangHang;
            int drawableId = (isShowNgangHang) ? R.drawable.arrow_up_2_thin : R.drawable.arrow_down_2_thin;
            tv_nganhhang.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableId, 0);
            tv_nganhhang.setBackgroundResource(R.drawable.red_border);
            tv_giamgia.setBackgroundColor(Color.parseColor("#6DDFDBDB"));
            tv_all.setBackgroundColor(Color.parseColor("#6DDFDBDB"));
        });

        btn_bothich.setOnClickListener(item -> {
            adapter.removeSelectedItems();
        });

        tv_chinhsua.setOnClickListener( item ->{
            isEditing = !isEditing;
            if (adapter != null) {
                boolean editing = !adapter.isEditing();
                adapter.setEditing(editing);
                if (isEditing) {
                    tv_chinhsua.setText("Hoàn tất");
                    btn_bothich.setVisibility(View.VISIBLE);
                } else {
                    tv_chinhsua.setText("Chỉnh sửa");
                    btn_bothich.setVisibility(View.GONE);
                }
            }
        });

        tv_all.setBackgroundResource(R.drawable.red_border);

        tv_all.setOnClickListener(view -> {
            isTvAllSelected = true;
            tv_all.setBackgroundResource(R.drawable.red_border);
            tv_nganhhang.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down_2_thin, 0);

            tv_giamgia.setBackgroundColor(Color.parseColor("#6DDFDBDB"));
            tv_nganhhang.setBackgroundColor(Color.parseColor("#6DDFDBDB"));
            getProductLikeTask();
        });

        tv_giamgia.setOnClickListener(view -> {
            isTvAllSelected = false;
            tv_giamgia.setBackgroundResource(R.drawable.red_border);
            tv_nganhhang.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down_2_thin, 0);

            tv_all.setBackgroundColor(Color.parseColor("#6DDFDBDB"));
            tv_nganhhang.setBackgroundColor(Color.parseColor("#6DDFDBDB"));
            getProductLikeHaveCouponsTask();
        });

        return v;
    }

    private void getProductLikeHaveCouponsTask() {
        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        ////================
        Call<List<ProductLikeSql>> call = apiService.getProductLikeHaveCoupon(idUser,"Bearer "+ accessToken);
        call.enqueue(new retrofit2.Callback<List<ProductLikeSql>>() {
            @Override
            public void onResponse(Call<List<ProductLikeSql>> call, retrofit2.Response<List<ProductLikeSql>> response) {
                if (response.isSuccessful()) {
                    List<ProductLikeSql> dataList = response.body();
                    if (dataList != null) {
                        adapter.setData(dataList);
                    }
                } else {
                }
            }
            @Override
            public void onFailure(Call<List<ProductLikeSql>> call, Throwable t) {
            }
        });
    }

    private void getProductLikeTask() {
        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        ////================
        Call<List<ProductLikeSql>> call = apiService.getProductLike(idUser,"Bearer "+accessToken);
        call.enqueue(new retrofit2.Callback<List<ProductLikeSql>>() {
            @Override
            public void onResponse(Call<List<ProductLikeSql>> call, retrofit2.Response<List<ProductLikeSql>> response) {
                if (response.isSuccessful()) {
                    List<ProductLikeSql> dataList = response.body();
                    if (dataList != null) {
                        adapter.setData(dataList);
                    }
                } else {
                }
            }
            @Override
            public void onFailure(Call<List<ProductLikeSql>> call, Throwable t) {
            }
        });
    }

    private void showPopup() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_nganhhang_layout, null);

        final LinearLayout dau,the,bdinh,hodan,kep,daokeo,file,ghim,bia,khay,giay,but;
        final Button btn_ok, btn_clear;
        dau = popupView.findViewById(R.id.id11);
        the = popupView.findViewById(R.id.id12);
        bdinh = popupView.findViewById(R.id.id21);
        hodan = popupView.findViewById(R.id.id22);
        kep = popupView.findViewById(R.id.id31);
        daokeo = popupView.findViewById(R.id.id32);
        file = popupView.findViewById(R.id.id41);
        ghim = popupView.findViewById(R.id.id42);
        bia = popupView.findViewById(R.id.id51);
        khay = popupView.findViewById(R.id.id52);
        giay = popupView.findViewById(R.id.id61);
        but = popupView.findViewById(R.id.id62);

        btn_clear = popupView.findViewById(R.id.btn_reset);
        btn_ok = popupView.findViewById(R.id.btn_ok);

        btn_ok.setOnClickListener(v->{
            getProductLikeByCategory(categories);
        });



        getProductLikeByCategory(categories);
        dau.setOnClickListener(v -> handleLinearLayoutClick(dau, 0));
        the.setOnClickListener(v -> handleLinearLayoutClick(the, 1));
        bdinh.setOnClickListener(v -> handleLinearLayoutClick(bdinh, 2));
        hodan.setOnClickListener(v -> handleLinearLayoutClick(hodan, 3));
        kep.setOnClickListener(v -> handleLinearLayoutClick(kep, 4));
        daokeo.setOnClickListener(v -> handleLinearLayoutClick(daokeo, 5));
        file.setOnClickListener(v -> handleLinearLayoutClick(file, 6));
        ghim.setOnClickListener(v -> handleLinearLayoutClick(ghim, 7));
        bia.setOnClickListener(v -> handleLinearLayoutClick(bia, 8));
        khay.setOnClickListener(v -> handleLinearLayoutClick(khay, 9));
        giay.setOnClickListener(v -> handleLinearLayoutClick(giay, 10));
        but.setOnClickListener(v -> handleLinearLayoutClick(but, 11));

        btn_clear.setOnClickListener(v->{

            LinearLayout[] linearLayouts = {
                    dau, the, bdinh, hodan, kep, daokeo, file, ghim, bia, khay, giay, but
            };

            for (LinearLayout layout : linearLayouts) {
                layout.setBackgroundColor(Color.parseColor("#4AD6D6BE"));
            }
            categories.clear();
            getProductLikeByCategory(categories);

        });
        int[] location = new int[2];
        tv_nganhhang.getLocationOnScreen(location);
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = popupView.getMeasuredWidth();

        int xOffset = location[0] - popupWidth / 2 + tv_nganhhang.getWidth() / 2;
        int yOffset = location[1] + tv_nganhhang.getHeight();

        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(popupView, Gravity.NO_GRAVITY, xOffset, yOffset);

        popupWindow.setOnDismissListener(() -> {
            categories.clear();
            tv_nganhhang.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down_2_thin, 0);

        });

        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setAnimationStyle(R.anim.popup_animation);
        //popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private String getCategoryNameByIndex(int index) {
        switch (index) {
            case 0:
                return "stamp";
            case 1:
                return "card";
            case 2:
                return "tape";
            case 3:
                return "glue";
            case 4:
                return "staple";
            case 5:
                return "cutlery";
            case 6:
                return "file";
            case 7:
                return "ghim";
            case 8:
                return "cover";
            case 9:
                return "shelf";
            case 10:
                return "paper";
            case 11:
                return "pen";

            default:
                return "";
        }
    }

    private void handleLinearLayoutClick(LinearLayout linearLayout, int index) {
        clickCount[index]++;
        String category = getCategoryNameByIndex(index);

        if (clickCount[index] % 2 == 1) {
            linearLayout.setBackgroundResource(R.drawable.red_border);
            // Add category to the list
            if (!categories.contains(category)) {
                categories.add(category);
            }
        } else {
            linearLayout.setBackgroundColor(Color.parseColor("#4AD6D6BE"));
            // Remove category from the list
            categories.remove(category);
        }
    }



    private void getProductLikeByCategory(List<String> categories){
        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        ////================
        Call<List<ProductLikeSql>> call = apiService.getProductLikesByUserAndCategories(idUser, categories,"Bearer "+accessToken);

        call.enqueue(new Callback<List<ProductLikeSql>>() {
            @Override
            public void onResponse(Call<List<ProductLikeSql>> call, Response<List<ProductLikeSql>> response) {
                if (response.isSuccessful()) {
                    List<ProductLikeSql> productLikes = response.body();

                    adapter.setData(productLikes);
                } else {
                    // Handle error
                }
            }

            @Override
            public void onFailure(Call<List<ProductLikeSql>> call, Throwable t) {
                // Handle failure
            }
        });
    }

}