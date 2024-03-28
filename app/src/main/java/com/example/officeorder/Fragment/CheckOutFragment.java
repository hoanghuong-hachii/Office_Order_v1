package com.example.officeorder.Fragment;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officeorder.Activity.SettingActivity;
import com.example.officeorder.Activity.WebViewActivity;
import com.example.officeorder.Adapter.CheckoutAdapter;
import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.getToken;
import com.example.officeorder.Model.CartProduct;
import com.example.officeorder.Model.ProductBillDTO;
import com.example.officeorder.Model.UserProfile;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Request.PostBillRequest;
import com.example.officeorder.Service.ApiService;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckOutFragment extends Fragment {
    private int totalPrice = 0;
    private ImageButton btnback;
    private ImageView btnaddress;
    private TextView tv_giatam,tv_amount,tv_totalprice,tv_feeship ,tv_hinhthuctt;
    private Button btnSubmit;
    private RecyclerView rvItemsInCart;
    private CheckoutAdapter adapter;
    private LinearLayout addinfo,ln_hinhthuctt;
    private TextView address,email,phonenumber,username;

    private boolean checkpaymentOnline;
    private int phiship,payment ;
    private VariableEnvironment variableEnvironment = new VariableEnvironment("d");
    private ArrayList<String> idProdList;
    private int amountPrice = 0;
    private ApiService apiService;
    private String accessToken="";
    private String idUser;

    public CheckOutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View v = inflater.inflate(R.layout.fragment_check_out, container, false);
         initID(v);

         checkpaymentOnline = false;

        tv_feeship.setText("23.000");

        apiService = ApiClient.getApiService();
        //
        UserSingleton userSingleton = UserSingleton.getInstance();
         idUser  = userSingleton.getUserId();

        //=========Access Token=========
        getToken get = new getToken();
        get.test();
        accessToken = userSingleton.getAccessToken();
        //=======
        GetDataInfo();
        adapter = new CheckoutAdapter(getContext(), getFragmentManager());

        rvItemsInCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvItemsInCart.setAdapter(adapter);
        getCartProducts();


        calculateTotalPrice();
        clickButton();
        return v;
    }

    private void getCartProducts() {
        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        ////================
        Call<List<CartProduct>> call = apiService.getShoppingCart(idUser,"Bearer " + accessToken);

        call.enqueue(new Callback<List<CartProduct>>() {
            @Override
            public void onResponse(Call<List<CartProduct>> call, Response<List<CartProduct>> response) {
                if (response.isSuccessful()) {
                    List<CartProduct> cartProductList = response.body();
                    totalPrice =0;
                    if (cartProductList != null) {

                        Log.e("Get gio hang", "checkOutFragment OK " );
                        adapter.setData(cartProductList);
                    }
                    else{

                    }
                } else {
                    // Xử lý khi request không thành công
                }
            }

            @Override
            public void onFailure(Call<List<CartProduct>> call, Throwable t) {
                Log.e("Get gio hang", "checkOutFragment FAIL "+ t.getMessage() );
            }
        });

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
                        String phone = data.getPhoneNumber();
                        String email1 = data.getEmail();
                        String address1 = data.getAddress();

                        username.setText(userName);
                        phonenumber.setText(phone);
                        email.setText(email1);
                        address.setText(address1);

                        Bundle bundle = getArguments();
                        Log.e("BULD", "onCreateView: "+bundle );
                        if (bundle != null) {
                            String fullname = bundle.getString("fullname");
                            String phone1 = bundle.getString("phone");
                            String email11 = bundle.getString("email");
                            String address11 = bundle.getString("address");
                            String province = bundle.getString("province");
                            String district = bundle.getString("district");
                            String ward = bundle.getString("ward");
                            if(fullname != null){
                                username.setText(fullname);
                            }
                            if(phone1 != null){
                                phonenumber.setText(phone1);
                            }
                            if(email11 != null){
                                email.setText(email11);
                            }
                            if(address11 != null){
                                address.setText(address11+", "+ward+", "+district +", "+province);
                            }
                        }
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

    private void calculateTotalPrice() {
        totalPrice = 0;

        phiship = 23000;
        totalPrice = getArguments().getInt("totalPrice");

        Log.e(TAG, "calculateTotalPrice: tong hang nhan duoc - "+ String.valueOf(totalPrice) );

        if(totalPrice > 200000){
            phiship = 0;
        }
        amountPrice = totalPrice + phiship;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String giatam = currencyFormatter.format(totalPrice);
        String totalfee = currencyFormatter.format(amountPrice);
        String tienship = currencyFormatter.format(phiship);

        tv_totalprice.setText(totalfee);
        tv_giatam.setText(giatam);
        tv_feeship.setText(tienship);
        tv_amount.setText(totalfee);
    }

    private void clickButton() {
        addinfo.setOnClickListener(item ->{

            Fragment searchFragment = new AddressFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_Container, searchFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        btnback.setOnClickListener(v -> {
                getActivity().onBackPressed();
        });

        btnSubmit.setOnClickListener(item->{

            //Toast.makeText(getContext(),"Đặt hàng thành công!",Toast.LENGTH_SHORT).show();

            if(checkpaymentOnline==true){
                thanhtoanOnline();

                postBill();
                deleteShoppingCartByUserId(idUser);

            }
            else{
                postBill();
                deleteShoppingCartByUserId(idUser);
                OrderSuccessFragment fragment = new OrderSuccessFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_Container, fragment)
                        .commit();
            }
//            postBill();
//            deleteShoppingCartByUserId(idUser);
//            OrderSuccessFragment fragment = new OrderSuccessFragment();
//            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.main_Container, fragment)
//                    .commit();

        });

        ln_hinhthuctt.setOnClickListener( v -> {

            final String[] genders = {"Thanh toán khi nhận hàng", "Thanh toán online"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Phương thức thanh toán: ");
            builder.setItems(genders, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String selectedGender = genders[which];

                    if(selectedGender.equals("Thanh toán online")){
                        checkpaymentOnline = true;
                    }
                    tv_hinhthuctt.setText(selectedGender);
                }
            });
            builder.show();
        });
    }

    private void thanhtoanOnline() {
        String tv_info = "Test";
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return postRequest(variableEnvironment.Valiables()+"api/v9/pay/submitOrder", String.valueOf(amountPrice), tv_info.toString());
            }
            @Override
            protected void onPostExecute(String result) {
                System.out.println("Response: " + result);

                String url = result.replace("redirect:", "");
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("paymentUrl",url);
                startActivity(intent);
            }
        }.execute();

    }
    public static String postRequest(String url, String amount, String orderInfo) {
        try {
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new FormBody.Builder()
                    .build();

            String requestUrl = url + "?amount=" + amount + "&orderInfo=" + orderInfo;
            Log.e(TAG, "postRequest: " + requestUrl );
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .post(requestBody)
                    .addHeader("accept", "*/*")
                    .build();
            okhttp3.Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                return "Error: " + response.code();
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private void deleteShoppingCartByUserId(String idUser) {
        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        //=======
        Call<Void> call = apiService.deleteShoppingCartByUserId(idUser,"Bearer " + accessToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.e("DELETE SHOPPING CART", "onResponse: Successfully deleted shopping cart items for user with ID: " + idUser);
                } else {
                    Log.e("DELETE SHOPPING CART", "onResponse: Failed to delete shopping cart items for user with ID: " + idUser);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("DELETE SHOPPING CART", "onFailure: " + t.getMessage());
            }
        });
    }

    private void postBill() {


        UserSingleton userSingleton = UserSingleton.getInstance();
        String idUser  = userSingleton.getUserId();

        String numberPhoneCustomer = phonenumber.getText().toString();
        String addressCustomer = address.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());
        String dateTimeOrder = sdf.format(new Date());
        String status = "pending";

        PostBillRequest postBillRequest = new PostBillRequest();
        postBillRequest.setIdUser(idUser);
        postBillRequest.setUserName(username.getText().toString());
        postBillRequest.setNumberPhoneCustomer(numberPhoneCustomer);
        postBillRequest.setAddressCustomer(addressCustomer);
        postBillRequest.setDateTimeOrder(dateTimeOrder);
        postBillRequest.setPayableAmount(payment);
        postBillRequest.setShippingFee(phiship);
        //==================send=================ơ
        //=========Access Token=========
        //UserSingleton userSingleton = UserSingleton.getInstance();
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

                    if (cartProductList != null) {

                        List<ProductBillDTO> productBillList = new ArrayList<>();
                        for (CartProduct cartProduct : cartProductList) {
                            ProductBillDTO productBill = new ProductBillDTO();

                            productBill.setProductId(cartProduct.getIdProd());

                            productBill.setQuantity(cartProduct.getQuantityProd());
                            productBillList.add(productBill);
                        }
                        postBillRequest.setProductBillDTOS(productBillList);

                        //=======================
                        //=========Access Token=========
                        getToken get = new getToken();
                        get.test();
                        String accessToken = userSingleton.getAccessToken();
                        ////================
                        Call<Void> call1 = apiService.postBill(postBillRequest,"Bearer " + accessToken);
                        call1.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call1, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Log.e("POST BILL", "onResponse: PASS");
                                } else {
                                    Log.e("POST BILL", "onResponse: FAIL");
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e("POST BILL", "onFailure: " + t.getMessage());
                            }
                        });
                    }
                    else{

                    }
                } else {
                    // Xử lý khi request không thành công
                }
            }
            //======================
            @Override
            public void onFailure(Call<List<CartProduct>> call, Throwable t) {
                // Xử lý khi có lỗi xảy ra
            }
        });

    }

    private void initID(View v) {

        ln_hinhthuctt = v.findViewById(R.id.ln_hinhthuctt);
        tv_hinhthuctt = v.findViewById(R.id.tv_hinhthucthanhtoan);
        addinfo = v.findViewById(R.id.addinfo);
        btnaddress = v.findViewById(R.id.btnaddress);
        btnSubmit = v.findViewById(R.id.btnSubmit);
        tv_giatam = v.findViewById(R.id.giatam);
        tv_amount = v.findViewById(R.id.amount);
        tv_totalprice = v.findViewById(R.id.totalprice);
        btnback = v.findViewById(R.id.backButton);
        rvItemsInCart = v.findViewById(R.id.rvItemsInCart);
        username = v.findViewById(R.id.username);
        phonenumber = v.findViewById(R.id.phonenumber);
        email = v.findViewById(R.id.email);
        address = v.findViewById(R.id.address);
        tv_feeship = v.findViewById(R.id.fee_ship);
    }
}