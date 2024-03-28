package com.example.officeorder.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.getToken;
import com.example.officeorder.Model.CartProduct;
import com.example.officeorder.Model.Product;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Service.ApiService;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CartViewHolder> {

    private VariableEnvironment variableEnvironment = new VariableEnvironment("d");
    private Context context;
    private List<CartProduct> data;

    private String accessToken="";
    private String id_User;
    private ApiService apiService;
    private FragmentManager fragmentManager;

    public CheckoutAdapter(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public void setData(List<CartProduct> list) {
        this.data = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.checkout_item, parent, false);
        return new CartViewHolder(view);
    }


    public CartProduct getItem(int position) {
        return data.get(position);

    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartProduct food = data.get(position);

        if (food == null) {
            return;
        }
        UserSingleton userSingleton = UserSingleton.getInstance();
        id_User  = userSingleton.getUserId();

        apiService = ApiClient.getApiService();
        String idProduct = food.getIdProd();
        getDataProduct(idProduct,holder);
        //
        holder.tv_quantity.setText("x"+String.valueOf(food.getQuantityProd()));

        //=========Access Token=========
        getToken get = new getToken();
        get.test();
        accessToken = userSingleton.getAccessToken();
        //=======
    }

    private void getDataProduct(String idProd, CartViewHolder holder) {
        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        ////================
        retrofit2.Call<Product> call = apiService.getProductDetail(idProd,"Bearer " + accessToken);

        call.enqueue(new retrofit2.Callback<Product>() {
            @Override
            public void onResponse(retrofit2.Call<Product> call, retrofit2.Response<Product> response) {
                if (response.isSuccessful()) {
                    Product product = response.body();

                    ((Activity) context).runOnUiThread(() -> {
                        if (product != null) {

                            //setUI
                            holder.txtTitle.setText(product.getProductName());
                            String imageUrl = variableEnvironment.Valiables()+product.getImageAvatar();



                            Log.e("img_Cart: ", imageUrl);
                            //===========
                            String p = product.getFormattedPrice();
                            String priceString = p.replace(".", "").trim();
                            int price = Integer.parseInt(priceString);
                            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                            String formattedPriceOld = currencyFormatter.format(price);
                            holder.tv_priceold.setText(formattedPriceOld);
                            //===========
                            String pp = product.getFormattedDiscountedPrice();
                            String ppriceString = pp.replace(".", "").trim();
                            int pprice = Integer.parseInt(ppriceString);
                            String formattedPrice = currencyFormatter.format(pprice);
                            holder.tv_price.setText(formattedPrice);
                            //============

                            Picasso.get()
                                    .load(imageUrl)
                                    .into(holder.imgCart);
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

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        ImageView imgCart;
        TextView tv_priceold,tv_price;
        TextView tv_quantity;
        public CartViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.tv_title);
            imgCart = itemView.findViewById(R.id.iv_avatar);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_priceold = itemView.findViewById(R.id.tv_price_old);
            tv_quantity = itemView.findViewById(R.id.tv_quantity);
        }
    }


}

