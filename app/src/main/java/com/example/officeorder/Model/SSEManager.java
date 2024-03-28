package com.example.officeorder.Model;

import static android.content.ContentValues.TAG;

import android.util.Log;

import java.io.IOException;

import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SSEManager {

    private static final String SSE_ENDPOINT = "http://192.168.60.5/statusUpdates/2";

    private OkHttpClient client;

    public SSEManager() {
        client = new OkHttpClient.Builder()
                .eventListener(new EventListener() {
                    // Implement event listener if needed
                })
                .build();
    }

    public void listenForStatusUpdates() {
        Request request = new Request.Builder()
                .url(SSE_ENDPOINT)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, Response response) {
                if (response.isSuccessful()) {
                    ResponseBody response1 = response.body();

                    Log.e(TAG, "onResponse: "+ response1 );

                }
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

}
