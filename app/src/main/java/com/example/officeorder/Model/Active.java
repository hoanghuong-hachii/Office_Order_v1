package com.example.officeorder.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Active {

@SerializedName("id")
@Expose
private String id;
@SerializedName("email")
@Expose
private String email;
@SerializedName("active")
@Expose
private Boolean active;

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

public Boolean getActive() {
return active;
}

public void setActive(Boolean active) {
this.active = active;
}

}