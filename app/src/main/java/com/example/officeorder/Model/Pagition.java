package com.example.officeorder.Model;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pagition {

    @SerializedName("size")
    @Expose
    private Integer size;
    @SerializedName("content")
    @Expose
    private List<Product> content;
    @SerializedName("number")
    @Expose
    private Integer number;
    @SerializedName("first")
    @Expose
    private Boolean first;
    @SerializedName("last")
    @Expose
    private Boolean last;
    @SerializedName("numberOfElements")
    @Expose
    private Integer numberOfElements;
    @SerializedName("totalElements")
    @Expose
    private Integer totalElements;
    @SerializedName("totalPages")
    @Expose
    private Integer totalPages;

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<Product> getProduct() {
        return content;
    }

    public void setProduct(List<Product> content) {
        this.content = content;
    }

    public Integer getNumber() { 
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Boolean getFirst() {
        return first;
    }

    public void setFirst(Boolean first) {
        this.first = first;
    }

    public Boolean getLast() {
        return last;
    }

    public void setLast(Boolean last) {
        this.last = last;
    }

    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public Integer getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Integer totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

}