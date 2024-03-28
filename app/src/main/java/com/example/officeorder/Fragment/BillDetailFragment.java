package com.example.officeorder.Fragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officeorder.Adapter.DetailBillAdapter;
import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.getToken;
import com.example.officeorder.Model.DateStatusChange;
import com.example.officeorder.Model.ProductBillDTO;
import com.example.officeorder.Model.ProductBillDetail;
import com.example.officeorder.Model.UserProfile;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Request.PostBillRequest;
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


public class BillDetailFragment extends Fragment {

    private DetailBillAdapter adapter;
    private String accessToken="";
    private VariableEnvironment variableEnvironment = new VariableEnvironment("s");

    ArrayList<String> idProdList;
    private ImageButton backButton,deleteButton,clear;
    private RecyclerView rvItemsInCart;
    private int idBill;
    private String idUser;
    private TableRow tb_huy,tb_gh, tb_ht;
    private ApiService apiService;
    private Button btn_Reorder, btn_huydon;
    private TextView tamtinh, phiship, tongtra, tv_time_dathang, tv_time_giaohang, tv_time_hoanthanh;
    private TextView tv_id, tv_name, tv_diachi, tv_sdt, tv_time,tv_time_huy;
    public BillDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_bill_detail, container, false);

        apiService = ApiClient.getApiService();
        idBill = 0;
        int total=0;
        Bundle arguments = getArguments();
        if (arguments != null) {
            idBill = arguments.getInt("bill_id");
        }
        Log.e("ID", "ID: "+idBill );
        UserSingleton userSingleton = UserSingleton.getInstance();
        idUser  = userSingleton.getUserId();


        tb_gh = view.findViewById(R.id.tb_gh);
        tb_huy = view.findViewById(R.id.tb_huy);
        tb_ht = view.findViewById(R.id.tb_ht);
        btn_Reorder = view.findViewById(R.id.btn_Reorder);
        btn_huydon = view.findViewById(R.id.btn_huydon);

        btn_huydon.setOnClickListener( v->{

            CancellBill();
        });
        btn_Reorder.setOnClickListener( v->{
            sendToCart_ReOder();

        });
        rvItemsInCart = view.findViewById(R.id.rvItemsInCart);

        backButton = view.findViewById(R.id.btn_back);
        backButton.setOnClickListener( v->{
            getActivity().onBackPressed();
        });
        rvItemsInCart.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new DetailBillAdapter(getContext(), getFragmentManager());
        rvItemsInCart.setAdapter(adapter);
        GetDataTask();
        //==========
        tamtinh = view.findViewById(R.id.tv_giatam);
        phiship = view.findViewById(R.id.tv_ship);
        tongtra = view.findViewById(R.id.total);
        tv_time_hoanthanh = view.findViewById(R.id.tv_time_hoanthanh);
        tv_time_giaohang = view.findViewById(R.id.tv_time_giaohang);
        tv_time_huy = view.findViewById(R.id.tv_time_huy);

        //=============

        //=============
        tv_name = view.findViewById(R.id.tv_username);
        tv_id = view.findViewById(R.id.tv_orderID);
        tv_sdt = view.findViewById(R.id.tv_phonenumber);
        tv_diachi = view.findViewById(R.id.tv_address);
        tv_time =view.findViewById(R.id.tv_time);

        String idBillStr = String.valueOf(idBill);
        tv_id.setText("#" +idBillStr);

        getUserName();

        return view;
    }

    private void CancellBill() {
        String status = "Cancelled";
//=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        ////================
        Call<Void> call = apiService.updateStatus( idBill,status,"Bearer " + accessToken);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(),"Hủy đơn hàng thành công",Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                } else {

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure
                // Network error or other errors
            }
        });
    }

    private void getDateStatusBill() {
        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        ////================
        Call<List<DateStatusChange>> call = apiService.getStatusChanges(idBill,"Bearer " + accessToken);
        call.enqueue(new Callback<List<DateStatusChange>>() {
            @Override
            public void onResponse(Call<List<DateStatusChange>> call, Response<List<DateStatusChange>> response) {
                if (response.isSuccessful()) {
                    List<DateStatusChange> statusChanges = response.body();

                    for (DateStatusChange statusChange : statusChanges) {
                       String status = statusChange.getStatus();
                        if(status.equals("Successful_delivery")){
                            tv_time_hoanthanh.setText(statusChange.getDateTimeOrder() );
                        }
                        if(status.equals("Shipped")){
                            tv_time_giaohang.setText(statusChange.getDateTimeOrder());
                        }
                        if(status.equals("Cancelled")){
                            tv_time_huy.setText(statusChange.getDateTimeOrder());
                        }
                        Log.d("StatusChange", statusChange.getStatus());
                    }
                } else {
                    Log.e("APIError", "Failed to fetch status changes");
                }
            }

            @Override
            public void onFailure(Call<List<DateStatusChange>> call, Throwable t) {

                Log.e("APIError", "Network error: " + t.getMessage());
            }
        });
    }

    private void getUserName() {
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
                    //  Data data = infoUser.getData();
                    if (data != null) {
                        String userName = data.getUserName();
                        tv_name.setText(userName);
                    }
                } else {
                }
            }
            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
            }
        });
    }

    private void sendToCart_ReOder (){
//=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        ////================
        Call<List<PostBillRequest>> call = apiService.searchProdBill(idBill, idUser,"Bearer " + accessToken);
        call.enqueue(new Callback<List<PostBillRequest>>() {
            @Override
            public void onResponse(Call<List<PostBillRequest>> call, Response<List<PostBillRequest>> response) {
                if (response.isSuccessful()) {
                    List<PostBillRequest> prodBillList = response.body();

                    for(PostBillRequest productBillDetail : prodBillList){

                        List<ProductBillDTO> productBillDTOList = productBillDetail.getProductBillDTOS();

                        for (ProductBillDTO productBillDTO : productBillDTOList) {
                            String idProduct = productBillDTO.getProductId();
                            //int idProduct = proList.getIdProd();
                            int price = productBillDTO.getRetailPrice();
                            sendAddToCartRequest(idUser, idProduct, price);

                        }

                    }

                    Fragment fragment = new CartFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_Container, fragment, "details_tag");
                    transaction.addToBackStack("details_tag");
                    transaction.commit();
                    Log.e(TAG, "onResponse: " + prodBillList.toString() );

                } else {
                }
            }
            @Override
            public void onFailure(Call<List<PostBillRequest>> call, Throwable t) {
                Log.e(TAG, "onFailure: "+ t.getMessage() );
            }
        });
    }
    private void sendAddToCartRequest(String id_User, String id_Pro, int productPrice) {

        Call<Void> call = apiService.addToCart(id_User, id_Pro, productPrice,"Bearer " + accessToken);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.e("ADD Cart", "onResponse: OKAY ADD CART");

                } else {
                    Log.e("ADD Cart", "onResponse: FAIL ADD CART");

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ADD Cart", "onFailure: " + t.getMessage());

            }
        });
    }

    private void GetDataTask (){
//=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        ////================
        Call<List<ProductBillDetail>> call = apiService.detailProdBill(idBill, idUser,"Bearer " + accessToken);
        call.enqueue(new Callback<List<ProductBillDetail>>() {
            @Override
            public void onResponse(Call<List<ProductBillDetail>> call, Response<List<ProductBillDetail>> response) {
                if (response.isSuccessful()) {
                    List<ProductBillDetail> prodBillList = response.body();
                    Log.e(TAG, "onResponse: "+ prodBillList.toString() );
                    if(prodBillList!=null && !prodBillList.isEmpty()){

                        for(ProductBillDetail postBillRequest : prodBillList){
                            int idbill = postBillRequest.getIdBill();
                            if(idbill==idBill){
                                tv_time.setText(postBillRequest.getDateTimeOrder());
                                tv_diachi.setText(postBillRequest.getAddressCustomer());
                                tv_sdt.setText(postBillRequest.getNumberPhoneCustomer());

                                String status = postBillRequest.getStatus();
                                if(status.equals("Pending")){
                                    btn_Reorder.setVisibility(View.GONE);
                                    btn_huydon.setVisibility(View.VISIBLE);
                                    tb_gh.setVisibility(View.GONE);
                                    tb_ht.setVisibility(View.GONE);
                                    tb_huy.setVisibility(View.GONE);
                                }
                                else if(status.equals("Shipped")){
                                    tb_gh.setVisibility(View.VISIBLE);
                                    tb_ht.setVisibility(View.GONE);
                                    tb_huy.setVisibility(View.GONE);
                                }

                                else if(status.equals("Cancelled")){
                                    tb_gh.setVisibility(View.GONE);
                                    tb_ht.setVisibility(View.GONE);
                                    tb_huy.setVisibility(View.VISIBLE);
                                }
                                else{
                                    tb_gh.setVisibility(View.VISIBLE);
                                    tb_ht.setVisibility(View.VISIBLE);
                                    tb_huy.setVisibility(View.GONE);
                                }

                                getDateStatusBill();


                                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                                String tienShip = currencyFormatter.format(postBillRequest.getShippingFee());
                                String tienTamtinh = currencyFormatter.format(postBillRequest.getTotalPayment());
                                String tienTong = currencyFormatter.format(postBillRequest.getPayableAmount());
                                tamtinh.setText(tienTamtinh);
                                phiship.setText(tienShip);
                                tongtra.setText(tienTong);

                                List<ProductBillDTO> productBillDTOList = postBillRequest.getProductBillDTOS();
                                if (!productBillDTOList.isEmpty()) {
                                    adapter.setData(productBillDTOList);
                                }
                            }
                        }
                    }

                } else {

                }
            }
            @Override
            public void onFailure(Call<List<ProductBillDetail>> call, Throwable t) {
                Log.e(TAG, "onFailure: "+ t.getMessage() );
            }
        });
    }
}