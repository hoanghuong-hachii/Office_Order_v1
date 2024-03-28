package com.example.officeorder.Service;



import android.util.Log;

import com.example.officeorder.Config.getToken;
import com.example.officeorder.Model.Active;
import com.example.officeorder.Model.Bill;
import com.example.officeorder.Model.CartProduct;
import com.example.officeorder.Model.DateStatusChange;
import com.example.officeorder.Model.InfoUser;
import com.example.officeorder.Model.Pagition;
import com.example.officeorder.Model.Product;
import com.example.officeorder.Model.ProductBillDetail;
import com.example.officeorder.Model.ProductLikeSql;
import com.example.officeorder.Model.Salt;
import com.example.officeorder.Model.User;
import com.example.officeorder.Model.UserProfile;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.Request.LoginRequest;
import com.example.officeorder.Request.PostBillRequest;
import com.example.officeorder.Request.ProductFilterRequest;
import com.example.officeorder.Request.RefreshTokenRequest;
import com.example.officeorder.Request.RequestTokenBody;
import com.example.officeorder.Response.AccessTokenResponse;
import com.example.officeorder.Response.LoginResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    //===================User===============
    @GET("api/v2/users/id")
    Call<UserProfile> getUserInfo(
            @Query("idUser") String userId,

    @Header("Authorization") String authorization);

    @POST("api/v2/users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @Multipart
    @PUT("api/v2/users/updateUser/{userId}")
    Call<ResponseBody> uploadAvatar(
            @Path("userId") String userId,
            @Part MultipartBody.Part image);

    @Multipart
    @PUT("api/v2/users/updateUser/{userId}")
    Call<ResponseBody> uploadBackground(
            @Path("userId") String userId,
            @Part MultipartBody.Part image,
            @Header("Authorization") String authorization

    );

    @PUT("api/v2/users/updateUser/{userId}")
    Call<InfoUser> updateAddress(
            @Path("userId") String userId,
            @Query("address") String address,
            @Header("Authorization") String authorization
    );
    @PUT("api/v2/users/updateUser/{userId}")
    Call<Void> updateUser(
            @Path("userId") String userId,
            @Query("userName") String userName,
            @Query("email") String email,
            @Query("phoneNumber") String phoneNumber,
            @Query("gender") String gender,
            @Header("Authorization") String authorization
    );

    @POST("api/v2/users/signupUser")
    Call<Void> signUp(@Body User data);


    //=================Product================
    @GET("api/v1/Products/roleUser/productStock")
    Call<List<Product>> getProducts(
            @Header("Authorization") String authorization
    );

    @POST("api/v1/Products/roleUser/filter")
    Call<Pagition> getPagitionProduct(
            @Query("page") int page,
            @Query("size") int size,
            @Body ProductFilterRequest  productFilterRequest,
            @Header("Authorization") String authorization
    );

    @GET("api/v1/Products/roleUser/{id}")
    Call<Product> getProductDetail(
            @Path("id") String productId,
            @Header("Authorization") String authorization
    );

    @GET("api/v1/Products/roleUser/priceAndFilter")
    Call<List<Product>> searchProduct(
            @Query("productName") String productName,
            @Query("sortDirection") String sortDirection,
            @Query("startPrice") String startPrice,
            @Query("endPrice") String endPrice,
            @Header("Authorization") String authorization
    );

    @GET("api/v1/Products/roleUser/searchProduct")
    Call<List<Product>> searchProductsByCategory(
            @Query("categoryName") String categoryName,
            @Header("Authorization") String authorization
    );

    @GET("api/v1/Products/roleUser/coupons")
    Call<List<Product>> getProductCoupon(
            @Header("Authorization") String authorization
    );

    //=============Danh sach yeu thich=======
    @POST("api/v6/ProdLike")
    Call<Void> addProductLike(
            @Query("idUser") String userId,
            @Query("idProd") String productId,
            @Header("Authorization") String authorization);

    @DELETE("api/v6/ProdLike")
    Call<Void> deleteProductLike(
            @Query("idUser") String userId,
            @Query("idProd") String productId,
            @Header("Authorization") String authorization);

    @GET("api/v6/ProdLike/user/{idUser}")
    Call<List<ProductLikeSql>> getProductLike(
            @Path("idUser") String userId,
            @Header("Authorization") String authorization);

    @GET("api/v6/ProdLike/user/{idUser}/product-likes-with-coupons")
    Call<List<ProductLikeSql>> getProductLikeHaveCoupon(
            @Path("idUser") String idUser,
            @Header("Authorization") String authorization);

    @GET("/api/v6/ProdLike/user/{idUser}/product-likes-by-categories")
    Call<List<ProductLikeSql>> getProductLikesByUserAndCategories(
            @Path("idUser") String idUser,
            @Query("categories") List<String> categories,
            @Header("Authorization") String authorization
    );

    //=================Giỏ hàng================
    @POST("api/v4/shoppingCart")
    Call<Void> addToCart(
            @Query("idUser") String userId,
            @Query("idProd") String productId,
            @Query("price") int productPrice,
            @Header("Authorization") String authorization
    );
    @GET("api/v4/shoppingCart/user/{userId}")
    Call<List<CartProduct>> getShoppingCart(@Path("userId") String userId,
                                            @Header("Authorization") String authorization
    );

    @PUT("api/v4/shoppingCart")
    Call<Void> updateQuantityInServer(
            @Query("idUser") String idUser,
            @Query("idProd") String idProd,
            @Query("quantityProd") int quantityProd,
            @Query("price") float price,
            @Header("Authorization") String authorization
    );
    @DELETE("api/v4/shoppingCart")
    Call<Void> deleteCartItem(@Query("idUser") String idUser,
                              @Query("idProd") String idProd,
                              @Header("Authorization") String authorization
    );

    @DELETE("/api/v4/shoppingCart/user/{idUser}")
    Call<Void> deleteShoppingCartByUserId(@Path("idUser") String idUser,
                                          @Header("Authorization") String authorization
    );
    //====================Bill==============================
    @POST("api/v5/Bill/bills")
    Call<Void> postBill(
            @Body PostBillRequest postBillRequest,
            @Header("Authorization") String authorization
    );

    @GET("api/v5/Bill/search")
    Call<List<PostBillRequest>> getBills(
            @Query("idUser") String idUser,
            @Query("status") String status,
            @Header("Authorization") String authorization);

    @GET("api/v5/Bill/search")
    Call<List<PostBillRequest>> searchProdBill(
            @Query("idBill1") int idBill1,
            @Query("idUser") String idUser,
            @Header("Authorization") String authorization);

    @GET("api/v5/Bill/search")
    Call<List<ProductBillDetail>> detailProdBill(
            @Query("idBill1") int idBill1,
            @Query("idUser") String idUser,
            @Header("Authorization") String authorization);

    @GET("api/v5/Bill/search")
    Call<List<Bill>> getInfoBills(
            @Query("idUser") int idBill,
            @Query("status") String idUser,
            @Header("Authorization") String authorization);

    @PUT("/api/v5/Bill/updateStatus/{id}")
    Call<Void> updateStatus(@Path("id") int id,
                            @Query("status") String status,
                            @Header("Authorization") String authorization);

    @GET("api/v5/Bill/GetStatus/{billId}")
    Call<List<DateStatusChange>> getStatusChanges(
            @Path("billId") int billId,
            @Header("Authorization") String authorization);


    //============FireBase============
    @POST("/register-device")
    Call<Void> registerDevice(
            @Body RequestTokenBody requestTokenBody);
    //======Salt=====
    @POST("/api/salts")
    Call<Salt> createSalt(@Body Salt salt);

    @GET("/api/salts/{email}")
    Call<Salt> getSaltByEmail(@Path("email") String email);

    //=========Token========
    @POST("/api/v2/users/refreshToken")
    Call<AccessTokenResponse> refreshToken(@Body RefreshTokenRequest refreshTokenRequest);

    //=========OTP==============
    @GET("/api/testOTP/generateOtp")
    Call<Void> getOTP(
            @Query("email") String email
    );
    @GET("/api/testOTP/validateOtp")
    Call<String> validateOtp(
            @Query("email") String email,
            @Query("otpnum") int otpnum);

    @GET("/api/v2/users/getUsernameByEmail")
    Call<String> getUsernameByEmail(@Query("email") String email);
    @GET("api/v2/users/changePassword")
    Call<Void> changePassword(
            @Query("email") String email,
            @Query("newPassword") String newPassword
    );

    //=========Active======
    @POST("/api/active")
    Call<Active> createActiveAccount(
            @Body Active activeAccount);
    @GET("/api/active/{email}")
    Call<Active> getActiveAccountByEmail(
            @Path("email") String email);

    @PUT("/api/active/{email}")
    Call<Active> updateActiveStatus(
            @Path("email") String email,
            @Query("active") boolean active);

    @GET("/api/v2/users/getPasswordById")
    Call<String> getPasswordById(
            @Query("idUser") String idUser);
}
