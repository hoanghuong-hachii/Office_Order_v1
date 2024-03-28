package com.example.officeorder.Activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.officeorder.Fragment.OrderSuccessFragment;
import com.example.officeorder.MainActivity;
import com.example.officeorder.R;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class SuccessActivity extends AppCompatActivity {

    private Button btn_back;
    private TextView tv_amount, tv_magd, tv_datetime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        btn_back = findViewById(R.id.btn_back);
        tv_amount = findViewById(R.id.tv_amount);
        tv_datetime = findViewById(R.id.tv_timedate);
        tv_magd = findViewById(R.id.tv_magd);


        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        Log.e(TAG, "onCreate: "+ url );
        try {
            URI uri = new URI(url);
            String query = uri.getQuery();

            Map<String, String> queryParams = new HashMap<>();
            if (query != null) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=");
                    String key = keyValue[0];
                    String value = keyValue.length > 1 ? keyValue[1] : "";
                    queryParams.put(key, value);
                }
            }

            System.out.println("vnp_Amount: " + queryParams.get("vnp_Amount"));
            System.out.println("vnp_PayDate: " + queryParams.get("vnp_PayDate"));
            System.out.println("vnp_TransactionNo: " + queryParams.get("vnp_TransactionNo"));

            String amount1 = queryParams.get("vnp_Amount");
            String tranNo = queryParams.get("vnp_TransactionNo");
            String inputDate = queryParams.get("vnp_PayDate");

            int amount = Integer.parseInt(amount1)/100;

            DecimalFormat currencyFormat = new DecimalFormat("#,###đ");
            String formattedAmount = currencyFormat.format(amount);
            System.out.println("Số đã định dạng: " + formattedAmount);

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            try {
                Date date = inputFormat.parse(inputDate);
                String formattedDate = outputFormat.format(date);
                System.out.println("DATE TIME: " + formattedDate);
                //
                tv_amount.setText("Số tiền: " + formattedAmount);
                tv_magd.setText("Mã giao dịch: "+ tranNo);
                tv_datetime.setText("Thời gian thanh toán: "+ formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        btn_back.setOnClickListener(view -> {
            Intent i = new Intent(SuccessActivity.this,  MainActivity.class);
            startActivity(i);
        });

    }
}