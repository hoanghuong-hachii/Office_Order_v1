package com.example.officeorder.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Service.ApiService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private ApiService apiService;
    private String accessToken="";
    private String id_User;
    private DatabaseReference databaseReference;
    private VariableEnvironment variableEnvironment = new VariableEnvironment("s");
    private Context mContext;
    private  boolean checklike = false;
    private List<Product> mListFood2;
    private FragmentManager mFragmentManager;

    private boolean isLoadingAdded = false;


    // Định nghĩa lớp để thực thi tải ảnh trong nền
    private class PicassoTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView imageView;

        public PicassoTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            try {
                // Tải ảnh bằng Picasso trong nền
                return Picasso.get().load(imageUrl).get();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Picasso", "Error loading image: " + e.getMessage());
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                // Cập nhật ImageView sau khi tải xong ảnh
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public SearchAdapter(Context mContext, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.mFragmentManager = fragmentManager;
    }
    public void addAll(List<Product> newData) {
        if (mListFood2 != null) {
            mListFood2.addAll(newData);
            notifyDataSetChanged();
        }
    }
    public void setData(List<Product> list) {
        this.mListFood2 = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item2, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Product food2 = mListFood2.get(position);
        if (food2 == null) {
            return;
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("productLike");

        apiService = ApiClient.getApiService();

        UserSingleton userSingleton = UserSingleton.getInstance();
        id_User  = userSingleton.getUserId();

        //=========Access Token=========
        getToken get = new getToken();
        get.test();
        accessToken = userSingleton.getAccessToken();
        //=======

        checklike = false;

        holder.tv_name.setText(food2.getProductName());
        holder.tv_price.setText(food2.getFormattedDiscountedPrice());

        holder.tv_priceInit.setText(food2.getFormattedPrice());
        holder.coupon.setText("- " + food2.getCoupons() + ".0%");
        holder.tv_slban.setText("Đã bán "+ food2.getQuantitySold());

        int coupons = food2.getCoupons();

        if (coupons==0) {
            holder.picpromo.setVisibility(View.GONE);
            holder.coupon.setVisibility(View.GONE);
            holder.tv_priceInit.setVisibility(View.GONE);
        } else {
            holder.picpromo.setVisibility(View.VISIBLE);
            holder.coupon.setVisibility(View.VISIBLE);
            holder.tv_priceInit.setVisibility(View.VISIBLE);
        }
        String imagePath = food2.getImageAvatar();
        String imageUrl =  variableEnvironment.Valiables()+imagePath;

//        Picasso.get()
//                .load(imageUrl)
//                .into(holder.img_food2);

        // Tạo một đối tượng PicassoTask để tải ảnh trong nền
        PicassoTask picassoTask = new PicassoTask(holder.img_food2);
        picassoTask.execute(imageUrl);

        Log.e("IMAGE Search Adapter", imageUrl);
     //   Log.e("IMAGE", imageUrl);

        String id_Pro1 = food2.getIdProd();

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

                String id_Pro = food2.getIdProd();
                String productPrice = food2.getFormattedDiscountedPrice();
                String priceString = productPrice.replace(".", "").replace("đ", "").trim();

                int price = Integer.parseInt(priceString);
                Log.e("TAG", "onClick: "+price );

                sendAddToCartRequest(id_User, id_Pro, price);
            }
        });

    }
    public void addLoadingFooter() {
        if (mListFood2 != null) {
            isLoadingAdded = true;
            mListFood2.add(null);
            notifyItemInserted(mListFood2.size() - 1);
        }
    }
    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mListFood2.size() - 1;
        if (position >= 0) {
            mListFood2.remove(position);
            notifyItemRemoved(position);
        }
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
        private TextView coupon,tv_slban;
        private ImageView picpromo;
       // private ImageButton imageButton_like;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            img_food2 = itemView.findViewById(R.id.imgFood);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_priceInit = itemView.findViewById(R.id.tv_price_old);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            coupon = itemView.findViewById(R.id.coupon);
            tv_slban =itemView.findViewById(R.id.tv_sl_ban);
            picpromo = itemView.findViewById(R.id.picpromo);
          //  imageButton_like = itemView.findViewById(R.id.imagebtn_like);
        }
    }

    public interface OnAddButtonClickListener {
        void onAddButtonClick();
    }
    private void sendAddToCartRequest(String id_User, String id_Pro, int productPrice) {
        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        //=======
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
                    Toast.makeText(mContext, "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ADD Cart", "onFailure: " + t.getMessage());
                // Hiển thị thông báo lỗi
                Toast.makeText(mContext, "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }


}

