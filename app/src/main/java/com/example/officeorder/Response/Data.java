package com.example.officeorder.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

@SerializedName("idUser")
@Expose
private String idUser;
@SerializedName("accessToken")
@Expose
private String accessToken;
@SerializedName("token")
@Expose
private String token;
@SerializedName("expirationTime")
@Expose
private String expirationTime;

public String getIdUser() {
return idUser;
}

public void setIdUser(String idUser) {
this.idUser = idUser;
}

public String getAccessToken() {
return accessToken;
}

public void setAccessToken(String accessToken) {
this.accessToken = accessToken;
}

public String getToken() {
return token;
}

public void setToken(String token) {
this.token = token;
}

public String getExpirationTime() {
return expirationTime;
}

public void setExpirationTime(String expirationTime) {
this.expirationTime = expirationTime;
}

}