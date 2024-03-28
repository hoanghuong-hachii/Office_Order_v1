package com.example.officeorder.Response;

public class AccessTokenResponse {
    private String accessToken;
    private String token;
    private String expirationTime;

    public String getAccessToken() {
        return accessToken;
    }

    public String getToken() {
        return token;
    }

    public String getExpirationTime() {
        return expirationTime;
    }
}