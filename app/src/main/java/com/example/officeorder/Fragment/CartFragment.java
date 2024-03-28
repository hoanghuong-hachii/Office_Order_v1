package com.example.officeorder.Fragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officeorder.Adapter.CartAdapter;
import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.getToken;
import com.example.officeorder.Model.CartProduct;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Service.ApiService;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CartFragment extends Fragment {

    private VariableEnvironment variableEnvironment = new VariableEnvironment("s");
    //ArrayList<Food3> selectedItems = new ArrayList<>();

    private int totalPrice = 0;
    ArrayList<String> idProdList;
    private ImageButton backButton,deleteButton,clear;
    private RecyclerView rvItemsInCart;
    private CartAdapter adapter;
    //private SQLiteHelper dbHelper;
    private TextView tvtotal;
    private CheckBox ck_all;
    private  GetDataTask getDataTask = new GetDataTask();
    private  String idUser;
    private ApiService apiService;
    private String accessToken="";
    private String id_Prod;
    private Button btnSubmit;
    private boolean isLoad;
    public CartFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //FragmentCartBinding binding = FragmentCartBinding.inflate(inflater,container, false);
        View view =inflater.inflate(R.layout.fragment_cart, container, false);

        apiService = ApiClient.getApiService();
        //
        UserSingleton userSingleton = UserSingleton.getInstance();
        idUser  = userSingleton.getUserId();
        //=========Access Token=========
        getToken get = new getToken();
        get.test();
        accessToken = userSingleton.getAccessToken();
        //=======
        backButton = view.findViewById(R.id.backButton);
        deleteButton = view.findViewById(R.id.deleteButton);
        rvItemsInCart = view.findViewById(R.id.rvItemsInCart);
        tvtotal = view.findViewById(R.id.totalprice);
        ck_all = view.findViewById(R.id.checkbox_All);
        rvItemsInCart.setLayoutManager(new LinearLayoutManager(requireContext()));


        ck_all.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adapter.setAllItemsChecked(isChecked);
        });
        Bundle bundle = getArguments();
        if (bundle != null)  {
            idProdList = bundle.getStringArrayList("idProdList");
            //deletebyId();
        }

        btnSubmit = view.findViewById(R.id.btnSubmit);
        rvItemsInCart.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        adapter = new CartAdapter(getContext(), getFragmentManager());
        rvItemsInCart.setAdapter(adapter);

        getDataTask.fetchData(idUser);
        Handler handler = new Handler();
        deleteButton.setOnClickListener(v -> {
            Log.e(TAG, "onCreateView: Xóa " );
            adapter.deleteSelectedItems();

            handler.postDelayed(() -> {
                getDataTask.fetchData(idUser);
            }, 500);
        });

        adapter.setOnMinBtnCartClickListener(position -> {
            Log.e(TAG, "onCreateView: Load lại " );
            getDataTask.fetchData(idUser);
        });

        backButton.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        btnSubmit.setOnClickListener(v->{

            Log.e(TAG, "onCreateView: tong tien hang" + String.valueOf(totalPrice) );
            Bundle bundle1 = new Bundle();
            bundle1.putInt("totalPrice",totalPrice);
            Fragment fragment = new CheckOutFragment();
            fragment.setArguments(bundle1);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_Container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        });

        return view;
    }

    public class GetDataTask {

        public void fetchData(String idUser) {
            //=========Access Token=========
            UserSingleton userSingleton = UserSingleton.getInstance();
            getToken get = new getToken();
            get.test();
            String accessToken = userSingleton.getAccessToken();
            //=======
            Call<List<CartProduct>> call = apiService.getShoppingCart(idUser,"Bearer " + accessToken);

            call.enqueue(new Callback<List<CartProduct>>() {
                @Override
                public void onResponse(Call<List<CartProduct>> call, Response<List<CartProduct>> response) {
                    if (response.isSuccessful()) {
                        List<CartProduct> cartProductList = response.body();
                        totalPrice =0;
                        if (cartProductList != null) {

                            adapter.setData(cartProductList);

                            for (CartProduct cartProduct : cartProductList) {
                                int priceitem = cartProduct.getTotalPrice();
                                totalPrice += priceitem;
                            }

                            //tổng
                            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                            String formattedPrice = currencyFormatter.format(totalPrice);
                            tvtotal.setText(formattedPrice);
                            isLoad = false;
                        }
                        else{
                        }
                    } else {
                    }
                }
                @Override
                public void onFailure(Call<List<CartProduct>> call, Throwable t) {
                    // Xử lý khi có lỗi xảy ra
                }
            });


        }
    }

}