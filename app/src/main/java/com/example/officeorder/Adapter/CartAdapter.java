package com.example.officeorder.Adapter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.getToken;
import com.example.officeorder.Fragment.ProductDetailFragment;
import com.example.officeorder.Model.CartProduct;
import com.example.officeorder.Model.Product;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Service.ApiService;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private int selectedItemPosition = -1;

    private int totalPrice;
    private boolean isLoad;
    private String productName, image,formattedDiscountedPrice;
    private VariableEnvironment variableEnvironment = new VariableEnvironment("some value");
    private Context context;
    private List<CartProduct> data;
    private OnClearClickListener onClearClickListener;
    private FragmentManager fragmentManager;
    private String accessToken="";
    private String id_User;
    private ApiService apiService;
    private OnQuantityChangeListener quantityChangeListener;

    private OnMinBtnCartClickListener minBtnCartClickListener;

    public CartAdapter(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public void setData(List<CartProduct> list) {
        this.data = list;
        notifyDataSetChanged();
    }
    public void setOnMinBtnCartClickListener(OnMinBtnCartClickListener listener) {
        this.minBtnCartClickListener = listener;
    }
    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.quantityChangeListener = listener;
    }
    public void setAllItemsChecked(boolean isChecked) {
        for (CartProduct product : data) {
            product.setSelected(isChecked);
        }
        notifyDataSetChanged();
    }


    public interface OnMinBtnCartClickListener {
        void onMinBtnCartClick(int position);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cartbuy, parent, false);
        return new CartViewHolder(view);
    }

    public CartProduct getItem(int position) {
        return data.get(position);

    }

    public interface OnQuantityChangeListener {
        void onQuantityDecrease(int position);
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
        //
       String idProduct = food.getIdProd();
       getDataProduct(idProduct,holder);

        holder.txtNumItems.setText(String.valueOf(food.getQuantityProd()));

        holder.txtNumItems.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Nhập số lượng");

            final EditText input = new EditText(v.getContext());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {

                String quantityStr = input.getText().toString();
                if (!quantityStr.isEmpty()) {

                    String idprod = food.getIdProd();
                    int quantity = Integer.parseInt(quantityStr);
                    float floatPrice = (float) food.getPrice();
                    holder.txtNumItems.setText(String.valueOf(quantity));
                    updateQuantityToServer(id_User, idprod, quantity,floatPrice,holder);
                }
            });

            // Thiết lập nút Cancel
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            // Hiển thị dialog
            builder.show();
        });

        holder.minBtnCart.setOnClickListener( v->{
            int currentQuantity = food.getQuantityProd();
            if (currentQuantity > 0) {


                int newQuantity = currentQuantity - 1;


                String idprod = food.getIdProd();

                float floatPrice = (float) food.getPrice();
                food.setQuantityProd(newQuantity);
                holder.txtNumItems.setText(String.valueOf(newQuantity));
                updateQuantityToServer(id_User, String.valueOf(idprod), newQuantity,floatPrice,holder);

            }
            if (currentQuantity == 1) {
                data.remove(food);
                deleteCartproduct(id_User,String.valueOf(idProduct),holder);
            }
        });

        holder.plusBtnCart.setOnClickListener( v->{
            int currentQuantity = food.getQuantityProd();

            int newQuantity = currentQuantity + 1;
            String idprod = food.getIdProd();

            float floatPrice = (float) food.getPrice();
            food.setQuantityProd(newQuantity);
            holder.txtNumItems.setText(String.valueOf(newQuantity));
            updateQuantityToServer(id_User, idprod, newQuantity,floatPrice,holder);


        });

        holder.imgCart.setOnClickListener(view -> {

            ProductDetailFragment fragment = new ProductDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("idProd", food.getIdProd());
            fragment.setArguments(bundle);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_Container, fragment, "del_tag");
            transaction.addToBackStack("del_tag");
            transaction.commit();
        });
        holder.txtTitle.setOnClickListener(view -> {

            ProductDetailFragment fragment = new ProductDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("idProd", food.getIdProd());
            fragment.setArguments(bundle);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_Container, fragment, "del_tag");
            transaction.addToBackStack("del_tag");
            transaction.commit();
        });
        holder.checkBox.setOnCheckedChangeListener(null);

        holder.checkBox.setChecked(food.isSelected());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            food.setSelected(isChecked);

        });
        //===================

    }

    private void getDataProduct(String idProd,CartViewHolder holder) {
        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        //=======
        retrofit2.Call<Product> call = apiService.getProductDetail(idProd,"Bearer " + accessToken);

        call.enqueue(new retrofit2.Callback<Product>() {
            @Override
            public void onResponse(retrofit2.Call<Product> call, retrofit2.Response<Product> response) {
                if (response.isSuccessful()) {
                    Product product = response.body();

                    Log.e(TAG, "onResponse: "+"OKay Cart" );
                    ((Activity) context).runOnUiThread(() -> {
                        if (product != null) {
                            productName = product.getProductName();
                            image = product.getImageAvatar();
                            formattedDiscountedPrice = product.getFormattedDiscountedPrice();

                            //setUI
                            holder.txtTitle.setText(productName);
                            String imageUrl = variableEnvironment.Valiables() + image;

                            Log.e("img_Cart: ", imageUrl);
                            //===========
                            String p = product.getFormattedPrice();
                            String priceString = p.replace(".", "").trim();
                            int price = Integer.parseInt(priceString);
                            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                            String formattedPriceOld = currencyFormatter.format(price);
                            holder.tv_priceOld.setText(formattedPriceOld);
                            //===========
                            String pp = product.getFormattedDiscountedPrice();
                            String ppriceString = pp.replace(".", "").trim();
                            int pprice = Integer.parseInt(ppriceString);
                            String formattedPrice = currencyFormatter.format(pprice);
                            holder.tv_price.setText(formattedPrice);

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

    public void deleteSelectedItems() {
        isLoad = false;
        List<CartProduct> selectedItems = new ArrayList<>();

        for (CartProduct item : data) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }

        }

        for (CartProduct selectedItem : selectedItems) {
            int position = data.indexOf(selectedItem);
            String idProduct = data.get(position).getIdProd();

            data.remove(selectedItem);
            notifyItemRemoved(position);
            deleteCartItem(id_User,idProduct);
        }

    }

    private void updateQuantityToServer(String idUser, String idProd, int quantityProd, float price,CartViewHolder holder) {
        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        //=======
        Call<Void> call = apiService.updateQuantityInServer(idUser, idProd, quantityProd, price,"Bearer " + accessToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.e("PUT", "onResponse: PASS");

                    if (minBtnCartClickListener != null) {
                        minBtnCartClickListener.onMinBtnCartClick(holder.getAdapterPosition());
                    }
                } else {
                    Log.e("PUT", "onResponse: FAIL");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("PUT", "onFailure: " + t.getMessage());
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

    public void setOnClearClickListener(OnClearClickListener listener) {
        this.onClearClickListener = listener;
    }

    public interface OnClearClickListener {
        void onClearClick(int position);
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        ImageView imgCart;
        TextView tv_priceOld,tv_price;
        TextView txtNumItems;
        ImageView minBtnCart;
        ImageView plusBtnCart;
        CheckBox checkBox;
        //ImageView clear;

        public CartViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txttitle);
            imgCart = itemView.findViewById(R.id.picCart);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_priceOld = itemView.findViewById(R.id.tv_price_old);
            txtNumItems = itemView.findViewById(R.id.numItems);
            minBtnCart = itemView.findViewById(R.id.minBtnCart);
            plusBtnCart = itemView.findViewById(R.id.plusBtnCart);
            checkBox = itemView.findViewById(R.id.chkitem);
            //clear = itemView.findViewById(R.id.clear);
        }
    }

    public void deleteCartproduct(String idUser,String idProd,CartViewHolder holder){
        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        //=======
        Call<Void> call = apiService.deleteCartItem(idUser, idProd,"Bearer " + accessToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    if (minBtnCartClickListener != null) {
                        minBtnCartClickListener.onMinBtnCartClick(holder.getAdapterPosition());
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure
            }
        });

    }
    public void deleteCartItem(String idUser,String idProd){
        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        //=======
        Call<Void> call = apiService.deleteCartItem(idUser, idProd,"Bearer " + accessToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                } else {

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure
            }
        });

    }


}
