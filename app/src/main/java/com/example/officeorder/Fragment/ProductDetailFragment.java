package com.example.officeorder.Fragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officeorder.Adapter.SearchAdapter;
import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.getToken;
import com.example.officeorder.MainActivity;
import com.example.officeorder.Model.Product;
import com.example.officeorder.Model.ProductLike;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Service.ApiService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ProductDetailFragment extends Fragment {
    private DatabaseReference databaseReference;
    private SearchAdapter adapter;
    private TextView tvpriceInit,tvcoupon;
    private ImageView info_product_image1;
    private ImageButton backButton;
    private ImageButton homeButton;
    private ImageButton cardbutton;
    private Button btnthemvaogio;
    private  String idProd, IIdUser;
    private boolean checklike ;
    private ImageView img_like;
    private TextView tvTitle;
    private TextView tvPrice,tv_daban;
    private TextView brand,origin,weight,txtMota;
    private VariableEnvironment variableEnvironment = new VariableEnvironment("value");
    private ApiService apiService;

    private String accessToken="";
    private Bitmap bitmapQR;
    public ProductDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product_detail, container, false);

        apiService = ApiClient.getApiService();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("productLike");
        //
        initId(v);
        UserSingleton userSingleton = UserSingleton.getInstance();
        String id_User  = userSingleton.getUserId();

        Log.d("USERR", "onClick: "+id_User);

        //=========Access Token=========
        getToken get = new getToken();
        get.test();
        accessToken = userSingleton.getAccessToken();
        //=======

        IIdUser = id_User;
        getProductDetail(idProd);
        BrandProduct(v);
        ClickButtonListener(v);
        return v;
    }


    private void ClickButtonListener(View rootView) {

//        qrcode.setOnClickListener( item->{
//            showQRCodeImage();
//        });

        btnthemvaogio.setOnClickListener(item ->{
//                String productName = tvTitle.getText().toString();
            String productPrice = tvPrice.getText().toString();
//                int productQuantity = 1;
//                Toast.makeText(getContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();

            UserSingleton userSingleton = UserSingleton.getInstance();
            String id_User  = userSingleton.getUserId();

            Log.d("USERR", "onClick: "+id_User);

            String priceString = productPrice.replace(".", "").replace("đ", "").trim();

            int price = Integer.parseInt(priceString);
            //Log.e("TAG", "onClick: "+productPrice );

            sendAddToCartRequest(id_User, String.valueOf(idProd), price);

        });

        homeButton.setOnClickListener(item ->{
            startActivity(new Intent(getActivity(), MainActivity.class));
        });

        cardbutton.setOnClickListener(item->{

            Fragment fragment = new CartFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_Container, fragment, "details_tag");
            transaction.addToBackStack("details_tag");
            transaction.commit();
        });

        backButton.setOnClickListener(item->{
            getActivity().onBackPressed();

        });

        String uniqueKey =  IIdUser+ "_" + idProd;

        checklike = false;
        databaseReference.child(uniqueKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    checklike = true;
                    img_like.setImageResource(R.drawable.ic_img_heart_fill);

                } else {
                    checklike = false;
                    img_like.setImageResource(R.drawable.ic_heart);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });

        img_like.setOnClickListener(v -> {
            String id_Pro = idProd;
            String idUser = IIdUser;

            if (!checklike) {
                img_like.setImageResource(R.drawable.ic_img_heart_fill);
                addProLikeFirebase(idUser, id_Pro);
                sendAddToProductLikeRequest(idUser,id_Pro);
                checklike=true;

            } else {
                img_like.setImageResource(R.drawable.ic_heart);
                deleteProductLikeInFireBase(idUser,id_Pro);
                deleteProductLike(idUser,id_Pro);
                checklike = false;
            }

        });
        //=======
    }

    private void sendAddToProductLikeRequest(String id_User, String id_Pro) {
        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        ////================
        Call<Void> call = apiService.addProductLike(id_User, id_Pro,"Bearer "+accessToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ADD like", "onResponse: OKAY ADD like");

                    Toast.makeText(getContext(), "Đã thêm vào Danh sách yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("ADD like", "onResponse: FAIL ADD like");

                    Toast.makeText(getContext(), "Lỗi khi thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("ADD like", "onFailure: "+ t.getMessage());
                    Toast.makeText(getContext(), "Lỗi kết nối! vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteProductLike(String id_User, String id_Pro) {
        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        ////================
        Call<Void> call = apiService.deleteProductLike(id_User, id_Pro,"Bearer "+ accessToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ADD like", "onResponse: OKAY Delete");
                    Toast.makeText(getContext(), "Đã bỏ khỏi Danh sách yêu thích", Toast.LENGTH_SHORT).show();

                } else {
                   }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("delete like", "onFailure: "+ t.getMessage());
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
                    // Hiển thị thông báo thành công
                    Toast.makeText(getContext(), "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("ADD Cart", "onResponse: FAIL ADD CART");
                    // Hiển thị thông báo lỗi
                    Toast.makeText(getContext(), "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ADD Cart", "onFailure: " + t.getMessage());
                // Hiển thị thông báo lỗi
                Toast.makeText(getContext(), "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showQRCodeImage() {

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.qrcode_layout);

        ImageView dialogQRCodeImageView = dialog.findViewById(R.id.img_qrcode);
        ImageView backQR = dialog.findViewById(R.id.backQR);
        LinearLayout scanQR = dialog.findViewById(R.id.scannQR);

        dialogQRCodeImageView.setImageBitmap(bitmapQR);

        backQR.setOnClickListener(item -> {
            dialog.dismiss();

        });

        scanQR.setOnClickListener(item -> {

            Bitmap bitmap = ((BitmapDrawable) dialogQRCodeImageView.getDrawable()).getBitmap();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            byte[] luminance = new byte[width * height];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = bitmap.getPixel(x, y);
                    int red = (pixel >> 16) & 0xff;
                    int green = (pixel >> 8) & 0xff;
                    int blue = pixel & 0xff;
                    luminance[y * width + x] = (byte) ((red + green + blue) / 3);
                }
            }

            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(luminance, width, height, 0, 0, width, height, false);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            QRCodeReader reader = new QRCodeReader();

            try {
                Result result = reader.decode(binaryBitmap);

                String qrCodeText = result.getText();
                Toast.makeText(getContext(), "QR Code: " + qrCodeText, Toast.LENGTH_SHORT).show();

                if (qrCodeText.startsWith("http://") || qrCodeText.startsWith("https://")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(qrCodeText));
                    startActivity(browserIntent);
                } else {
                    Toast.makeText(getContext(), "Invalid URL", Toast.LENGTH_SHORT).show();
                }
            } catch (NotFoundException e) {
                e.printStackTrace();
            } catch (ChecksumException e) {
                e.printStackTrace();
            } catch (FormatException e) {
                e.printStackTrace();
            }

        });

        dialog.show();

    }
    private void getProductDetail(String idProd) {

        Call<Product> call = apiService.getProductDetail(idProd,"Bearer " + accessToken);

        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Product product = response.body();

                    Log.e(TAG, "onResponse() returned: " +product.toString() );
                    getActivity().runOnUiThread(() -> {
                        if (product != null) {
                            String productName = product.getProductName();
                            String formattedPrice = product.getFormattedPrice();
                            String image = product.getImageAvatar();
                            String formattedDiscountedPrice = product.getFormattedDiscountedPrice();
                            int coupons = product.getCoupons();
                            String brand1 = product.getBrand();
                            String origin1 = product.getOrigin();
                            String description = product.getDetail();
                            String urlQR = product.getImageQR();
                            String dv = product.getUnitName();
                            int daban = product.getQuantitySold();
                            Log.e(TAG, "ĐÃ BÁN: "+ daban );

                            tv_daban.setText("   Đã bán "+ daban);
                            tvTitle.setText(productName);
                            tvPrice.setText(formattedDiscountedPrice + " đ");
                            tvpriceInit.setText(formattedPrice + "đ");
                            if (coupons==0) {
                                tvcoupon.setText("");
                                tvpriceInit.setVisibility(View.GONE);
                            } else {
                                tvcoupon.setText("Giảm "+coupons +"%");
                            }
                            brand.setText(brand1);
                            origin.setText(origin1);
                            weight.setText(dv);
                            txtMota.setText(description);

                            String imageUrl = variableEnvironment.Valiables() + image;
                            //String imageQR = variableEnvironment.Valiables() + urlQR;

                            Picasso.get()
                                    .load(variableEnvironment.Valiables()+image)
                                    .into(info_product_image1);

//                            Picasso.get()
//                                    .load(imageQR)
//                                    .into(qrcode);

                        }
                    });
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                // Handle failure
            }
        });

    }

    private void deleteProductLikeInFireBase(String idUser, String id_pro) {

        Log.e(TAG, "deleteProductLikeInFireBase:OK " );
        String uniqueKey = idUser + "_" + id_pro;
        databaseReference.child(uniqueKey).removeValue();

    }

    private void addProLikeFirebase(String idUser, String idProd) {
        ProductLike contact = new ProductLike(idUser,idProd);
        //String key = databaseReference.push().getKey();
        Log.e(TAG, "addProLikeFirebase:  OK" );
        String key = idUser + "_" + idProd;
        databaseReference.child(key).setValue(contact);
    }
    private void initId(View rootView) {
        img_like = rootView.findViewById(R.id.img_like);
        idProd = getArguments().getString("idProd", "");
        info_product_image1 = rootView.findViewById(R.id.info_product_image1);
        tvcoupon = rootView.findViewById(R.id.info_product_discount);
        tvpriceInit = rootView.findViewById(R.id.info_product_discount_price1);
        //qrcode = rootView.findViewById(R.id.qrcode);
        brand = rootView.findViewById(R.id.brand);
        origin = rootView.findViewById(R.id.origin);
        weight = rootView.findViewById(R.id.weight);
        txtMota = rootView.findViewById(R.id.txtMota);
        btnthemvaogio = rootView.findViewById(R.id.btnthemvaogio);
        tv_daban = rootView.findViewById(R.id.tv_daban);

        RecyclerView recyclerView =  rootView.findViewById(R.id.rvDescription);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        backButton = rootView.findViewById(R.id.backButton);
        homeButton = rootView.findViewById(R.id.homeButton);
        cardbutton = rootView.findViewById(R.id.cardbtn);
        tvPrice = rootView.findViewById(R.id.txtFoodPrice);
        tvTitle = rootView.findViewById(R.id.txtFoodName);

        btnthemvaogio = rootView.findViewById(R.id.btnthemvaogio);

    }

    public void BrandProduct(View view){

        adapter = new SearchAdapter(getContext(), getFragmentManager());
        RecyclerView rvPopular = view.findViewById(R.id.rvDescription);
        rvPopular.setAdapter(adapter);
        rvPopular.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        BrandDataTask hotDataTask = new BrandDataTask();
        hotDataTask.fetchData();
    }
    public class BrandDataTask {

        public void fetchData() {
            //=========Access Token=========
            UserSingleton userSingleton = UserSingleton.getInstance();
            getToken get = new getToken();
            get.test();
            accessToken = userSingleton.getAccessToken();
            //=======
            Call<List<Product>> call = apiService.getProductCoupon("Bearer " + accessToken);
            call.enqueue(new Callback<List<Product>>() {
                @Override
                public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
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