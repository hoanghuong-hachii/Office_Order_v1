package com.example.officeorder.Response;

import com.google.gson.annotations.SerializedName;

public class ResponseBody {

    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }
}
