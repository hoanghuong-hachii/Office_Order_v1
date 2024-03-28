package com.example.officeorder.Config;

import static android.content.ContentValues.TAG;
import android.util.Log;
import com.example.officeorder.Model.UserSingleton;

public class getToken {
    public void test() {
        ApiManager.init();
        UserSingleton userSingleton = UserSingleton.getInstance();
        String token = userSingleton.getToken();

        Log.e(TAG, "Test: " + token);

        ApiManager.refreshToken(token, new ApiManager.OnTokenRefreshListener() {
            @Override
            public void onTokenRefreshed(String accessToken) {
                // Handle the refreshed access token
                Log.e("TestActivity", "Access Token: " + accessToken);
                UserSingleton userSingleton = UserSingleton.getInstance();
                userSingleton.setAccessToken(accessToken);
            }

            @Override
            public void onTokenRefreshFailed() {
                // Handle token refresh failure
                Log.e("TestActivity", "Token refresh failed");
            }
        });
    }
}
