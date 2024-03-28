package com.example.officeorder.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.example.officeorder.Fragment.BillDetailFragment;
import com.example.officeorder.Model.Product;
import com.example.officeorder.Model.ProductBillDTO;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Request.PostBillRequest;
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

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.CartViewHolder> {

    private VariableEnvironment variableEnvironment= new VariableEnvironment("s");
    private Context context;
    private List<PostBillRequest> data;
    private ApiService apiService;
    private int idBill;
    private String idUser;
    private String firstProductId = "";

    private String accessToken = "";
    private FragmentManager fragmentManager;

    public BillAdapter(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new CartViewHolder(view);
    }

    public void setData(List<PostBillRequest> list) {
        this.data = list;
        notifyDataSetChanged();
    }

    public PostBillRequest getItem(int position) {
        return data.get(position);

    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        PostBillRequest food = data.get(position);

        apiService = ApiClient.getApiService();
        int idbill = food.getIdBill();
        idBill = idbill;
        UserSingleton userSingleton = UserSingleton.getInstance();
        idUser  = userSingleton.getUserId();

        //=========Access Token=========
        getToken get = new getToken();
        get.test();
        accessToken = userSingleton.getAccessToken();
        //=======
        List<ProductBillDTO> productBillDTOList = food.getProductBillDTOS();
        if (!productBillDTOList.isEmpty()) {
            ProductBillDTO firstProduct = productBillDTOList.get(0);
            String idProduct = firstProduct.getProductId();
            holder.tv_quantity.setText("x" + firstProduct.getQuantity().toString());

            getProductDetail(idProduct, holder);

            int cnt =0;
            for(ProductBillDTO productBillDTO : productBillDTOList){
                cnt++;
            }
            if(cnt==1){
                holder.ln_xemthem.setVisibility(View.GONE);
            }
            holder.tv_soluong.setText(String.valueOf(cnt)+ " sản phẩm");

        }
        //=============

        int fee_ship = 23000;
        int price  = food.getTotalPayment() ;
        int tongtra = price + fee_ship;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = currencyFormatter.format(tongtra);
        holder.tv_thanhtien.setText(formattedPrice);

        holder.chitiet.setOnClickListener(item->{

            BillDetailFragment fragment = new BillDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("bill_id", idbill);
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_Container, fragment,"bill");
            fragmentTransaction.addToBackStack("bill");
            fragmentTransaction.commit();

        });
        holder.ln_xt.setOnClickListener(item->{

            BillDetailFragment fragment = new BillDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("bill_id", idbill);
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_Container, fragment,"bill");
            fragmentTransaction.addToBackStack("bill");
            fragmentTransaction.commit();

        });

        holder.ln_xemthem.setOnClickListener(item->{

            BillDetailFragment fragment = new BillDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("bill_id", idbill);
            bundle.putInt("tongtra",tongtra);
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_Container, fragment,"bill");
            fragmentTransaction.addToBackStack("bill");
            fragmentTransaction.commit();

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
        TextView tv_Title;
        LinearLayout chitiet,ln_xemthem, ln_xt;
        TextView tv_thanhtien;
        TextView tv_priceold,tv_price,tv_quantity, tv_soluong;
        ImageView iv_avatar;

        public CartViewHolder(View itemView) {
            super(itemView);
            tv_Title = itemView.findViewById(R.id.tv_title);
            tv_thanhtien = itemView.findViewById(R.id.tv_thanhtien);
            tv_priceold = itemView.findViewById(R.id.tv_price_old);
            tv_price = itemView.findViewById(R.id.tv_price);
            chitiet = itemView.findViewById(R.id.chitiet);
            tv_quantity = itemView.findViewById(R.id.tv_quantity);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            tv_soluong = itemView.findViewById(R.id.tv_soluongsp);
            ln_xemthem = itemView.findViewById(R.id.ln_xemthem);
            ln_xt = itemView.findViewById(R.id.ln_chitiet);
        }
    }
    private void getProductDetail(String idProd, CartViewHolder holder) {

        Call<Product> call = apiService.getProductDetail(idProd,"Bearer " + accessToken);

        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Product product = response.body();

                    if (product != null) {
                        String productName = product.getProductName();
                        String avatar = product.getImageAvatar();
                        holder.tv_Title.setText(productName);

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
                        String url = variableEnvironment.Valiables() + avatar;

                        Log.e("IMAGE BillAdpter", url );

                        Picasso.get()
                                .load(url)
                                .into(holder.iv_avatar);
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