package com.example.officeorder.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.officeorder.R;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new myWebViewClient());

        Intent intent = getIntent();
        String url = intent.getStringExtra("paymentUrl");
        Log.e("Web ", url );

        webView.loadUrl(url);



    }

    private class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri uri = request.getUrl();
            String url = uri.toString();
            if (url.contains("vnp_TransactionStatus=00")) {

                Log.e("UL", "shouldOverrideUrlLoading: "+url );
                Log.e("STATUS", "shouldOverrideUrlLoading: OK");
                Intent successIntent = new Intent(WebViewActivity.this, SuccessActivity.class);
                successIntent.putExtra("url",url);
                startActivity(successIntent);

                // Trả về true để ngăn WebView xử lý URL tiếp theo
                return true;
            }

            // Nếu URL không chứa "ordersuccess", cho WebView xử lý bình thường
            return super.shouldOverrideUrlLoading(view, request);
        }

    }
}