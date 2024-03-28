package com.example.officeorder.Adapter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.getToken;
import com.example.officeorder.Fragment.ProductDetailFragment;
import com.example.officeorder.Model.Product;
import com.example.officeorder.Model.ProductLikeSql;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Service.ApiService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductLikeAdapter extends RecyclerView.Adapter<ProductLikeAdapter.SearchViewHolder> {
    private ApiService apiService;
    private String accessToken="";
    private String id_User;
    private String id_Prod;
    private String price;
    private DatabaseReference databaseReference;
    private VariableEnvironment variableEnvironment = new VariableEnvironment("s");
    private Context mContext;
    private  boolean checklike = false;
    private boolean isEditing = false;
    private List<ProductLikeSql> mListFood2;
    private FragmentManager mFragmentManager;
    public ProductLikeAdapter(Context mContext, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.mFragmentManager = fragmentManager;
    }



    public void setEditing(boolean editing) {
        isEditing = editing;
        notifyDataSetChanged();
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setData(List<ProductLikeSql> list) {
        this.mListFood2 = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_like, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        ProductLikeSql food2 = mListFood2.get(position);
        if (food2 == null) {
            return;
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("productLike");

        apiService = ApiClient.getApiService();

        UserSingleton userSingleton = UserSingleton.getInstance();
        id_User  = userSingleton.getUserId();

        id_Prod = food2.getIdProd();
        getInfoProduct(id_Prod,holder);

        //=========Access Token=========
        getToken get = new getToken();
        get.test();
        accessToken = userSingleton.getAccessToken();
        //=======
        //
        if (isEditing) {
            holder.checkbox_xoa.setVisibility(View.VISIBLE);
        } else {
            holder.checkbox_xoa.setVisibility(View.GONE);
        }

        holder.checkbox_xoa.setChecked(food2.isSelected());

        holder.checkbox_xoa.setOnCheckedChangeListener((buttonView, isChecked) -> {
            food2.setSelected(isChecked);
        });
        holder.img_food2.setOnClickListener(view -> {


            ProductDetailFragment fragment = new ProductDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("idProd", food2.getIdProd());
            fragment.setArguments(bundle);
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.replace(R.id.main_Container, fragment, "del_tag");
            transaction.addToBackStack("del_tag");
            transaction.commit();
        });

        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                UserSingleton userSingleton = UserSingleton.getInstance();
                String id_User  = userSingleton.getUserId();

                Log.d("USERR", "onClick: "+id_User);

                //Toast.makeText(mContext, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();

                String productPrice = price;
                String priceString = productPrice.replace(".", "").replace("đ", "").trim();

                int price = Integer.parseInt(priceString);
                Log.e("TAG", "onClick: "+price );
                String idProduct = id_Prod;

                sendAddToCartRequest(id_User, idProduct, price);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (mListFood2 != null) {
            return mListFood2.size();
        }
        return 0;
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_food2;
        private TextView tv_name;
        private TextView tv_price, tv_priceInit;
        private Button btnAdd;
        private TextView coupon;
        private ImageView picpromo;
        private CheckBox checkbox_xoa;

        // private ImageButton imageButton_like;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            img_food2 = itemView.findViewById(R.id.imgFood);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_priceInit = itemView.findViewById(R.id.tv_price_old);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            coupon = itemView.findViewById(R.id.coupon);
            picpromo = itemView.findViewById(R.id.picpromo);
            checkbox_xoa = itemView.findViewById(R.id.checkbox_xoa);
        }
    }

    public interface OnAddButtonClickListener {
        void onAddButtonClick();
    }
    private void sendAddToCartRequest(String id_User, String id_Pro, int productPrice) {

        Call<Void> call = apiService.addToCart(id_User, id_Pro, productPrice,"Bearer " + accessToken);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.e("ADD Cart", "onResponse: OKAY ADD CART");
                    // Hiển thị thông báo thành công
                    Toast.makeText(mContext, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("ADD Cart", "onResponse: FAIL ADD CART");
                    // Hiển thị thông báo lỗi
                    Toast.makeText(mContext, "Lỗi ! Vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ADD Cart", "onFailure: " + t.getMessage());
                // Hiển thị thông báo lỗi
                Toast.makeText(mContext, "Kiểm tra kết nối và thử lại", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void removeSelectedItems() {
        List<ProductLikeSql> selectedItems = new ArrayList<>();
        for (ProductLikeSql item : mListFood2) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }

        mListFood2.removeAll(selectedItems);
        notifyDataSetChanged();
        deleteProductLikeInFireBase(id_User,String.valueOf(id_Prod));
        deleteProductLike(id_User,String.valueOf(id_Prod));
    }

    private void getInfoProduct(String idProd, SearchViewHolder holder) {

        Call<Product> call = apiService.getProductDetail(idProd,"Bearer " + accessToken);

        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Product product = response.body();

                    if (product != null) {
                        holder.tv_name.setText(product.getProductName());
                        price = product.getFormattedDiscountedPrice();
                        holder.tv_price.setText(product.getFormattedDiscountedPrice());

                        //
                        holder.tv_priceInit.setText(product.getFormattedPrice());
                        holder.coupon.setText("- " + product.getCoupons() + ".0%");

                        int coupons = product.getCoupons();

                        if (coupons==0) {
                            holder.picpromo.setVisibility(View.GONE);
                            holder.coupon.setVisibility(View.GONE);
                            holder.tv_priceInit.setVisibility(View.GONE);
                        } else {
                            holder.picpromo.setVisibility(View.VISIBLE);
                            holder.coupon.setVisibility(View.VISIBLE);
                            holder.tv_priceInit.setVisibility(View.VISIBLE);
                        }
                        String imagePath = product.getImageAvatar();
                        String imageUrl = variableEnvironment.Valiables() + imagePath;

                        Picasso.get()
                                .load(variableEnvironment.Valiables()+imagePath)
                                .into(holder.img_food2);

                        Log.e("IMAGE", imagePath);

                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                // Handle failure
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
        Call<Void> call = apiService.deleteProductLike(id_User, id_Pro,"Bearer "+accessToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ADD like", "onResponse: OKAY Delete");

                } else {
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("delete like", "onFailure: "+ t.getMessage());
            }
        });
    }

    private void deleteProductLikeInFireBase(String idUser, String id_pro) {

        Log.e(TAG, "deleteProductLikeInFireBase:OK " );
        String uniqueKey = idUser + "_" + id_pro;
        databaseReference.child(uniqueKey).removeValue();

    }
}

