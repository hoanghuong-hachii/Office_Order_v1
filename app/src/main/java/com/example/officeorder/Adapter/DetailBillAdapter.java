package com.example.officeorder.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.getToken;
import com.example.officeorder.Fragment.ProductDetailFragment;
import com.example.officeorder.Model.Product;
import com.example.officeorder.Model.ProductBillDTO;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Service.ApiService;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailBillAdapter extends RecyclerView.Adapter<DetailBillAdapter.SearchViewHolder> {
    private ApiService apiService;
    private VariableEnvironment variableEnvironment = new VariableEnvironment("s");
    private Context mContext;
    private List<ProductBillDTO> mListFood2;
    private FragmentManager mFragmentManager;
    private String accessToken="";
    public DetailBillAdapter(Context mContext, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.mFragmentManager = fragmentManager;
    }

    public void setData(List<ProductBillDTO> list) {
        this.mListFood2 = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chitietbill, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        ProductBillDTO food2 = mListFood2.get(position);
        if (food2 == null) {
            return;
        }

        apiService = ApiClient.getApiService();
        //
        String idProduct = food2.getProductId();
        getProductDetail(String.valueOf(idProduct),holder);
        Integer totalPrice = food2.getTotalPriceProd();
        int price = (totalPrice != null) ? totalPrice : 0;

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String tonggia = currencyFormatter.format(totalPrice);
        int quantity = food2.getQuantity();
        holder.tv_quantityProduct.setText("x"+String.valueOf(quantity));
        //holder.tv_totalPrice.setText(tonggia);

        holder.ln_product.setOnClickListener( v ->{
            ProductDetailFragment fragment = new ProductDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("idProd", idProduct);
            fragment.setArguments(bundle);
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.replace(R.id.main_Container, fragment, "detailBill_tag");
            transaction.addToBackStack("detailBill_tag");
            transaction.commit();
        });

        //int id_Pro1 = mListFood2.get(holder.getAdapterPosition()).getIdProd();

        UserSingleton userSingleton = UserSingleton.getInstance();
        String id_User1  = userSingleton.getUserId();
        //=========Access Token=========
        getToken get = new getToken();
        get.test();
        accessToken = userSingleton.getAccessToken();
        //=======
    }

    @Override
    public int getItemCount() {
        if (mListFood2 != null) {
            return mListFood2.size();
        }
        return 0;
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_totalPrice, tv_quantityProduct;
        private ImageView iv_anh;
        private LinearLayout ln_product;
        private TextView tv_price,tv_priceOld;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            ln_product = itemView.findViewById(R.id.lngia);
            tv_name = itemView.findViewById(R.id.tv_title);
            tv_quantityProduct = itemView.findViewById(R.id.tv_quantity);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_priceOld = itemView.findViewById(R.id.tv_price_old);
            iv_anh = itemView.findViewById(R.id.iv_avatar);

        }
    }

    private void getProductDetail(String idProd, SearchViewHolder holder) {
        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        //=======
        Call<Product> call = apiService.getProductDetail(idProd,"Bearer " + accessToken);

        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Product product = response.body();

                        if (product != null) {
                            String productName = product.getProductName();
                            String avatar = product.getImageAvatar();
                            holder.tv_name.setText(productName);
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

                            String url = variableEnvironment.Valiables() + avatar;

                            Picasso.get()
                                    .load(url)
                                    .into(holder.iv_anh);
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
}

