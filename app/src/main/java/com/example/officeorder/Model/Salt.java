package com.example.officeorder.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Salt {

@SerializedName("id")
@Expose
private String id;
@SerializedName("email")
@Expose
private String email;
@SerializedName("salt")
@Expose
private String salt;

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
}

public String getEmail() {
return email;
}

public void setEmail(String email) {
this.email = email;
}

public String getSalt() {
return salt;
}

public void setSalt(String salt) {
this.salt = salt;
}

}
