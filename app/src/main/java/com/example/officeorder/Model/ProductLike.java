package com.example.officeorder.Model;

public class ProductLike {
    private String idUser;
    private String idProduct;

    private String key;
    public ProductLike() {
    }

    public ProductLike(String idUser, String idProduct) {
        this.idUser = idUser;
        this.idProduct = idProduct;

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }
}
