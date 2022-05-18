package com.chuthuong.lthstore.api;

import android.os.Environment;

import com.chuthuong.lthstore.model.ShipmentDetail;
import com.chuthuong.lthstore.province.Province;
import com.chuthuong.lthstore.response.CartResponse;
import com.chuthuong.lthstore.response.ListCategoryResponse;
import com.chuthuong.lthstore.response.ListProductResponse;
import com.chuthuong.lthstore.response.ListReviewResponse;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.response.OrderResponse;
import com.chuthuong.lthstore.response.ProductResponse;
import com.chuthuong.lthstore.response.UserResponse;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    // api
    ApiService apiService = new Retrofit.Builder()
            .baseUrl(Util.URI_API)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);
    ApiService apiProvince = new Retrofit.Builder()
            .baseUrl(Util.URI_API_PROVINCES)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @GET("?depth=3")
    Call<List<Province>> getProvinces();

    // Đăng nhập
    @FormUrlEncoded
    @POST("auth/login")
    Call<User> loginUser(@Field("username") String username, @Field("password") String password);

    // Đăng kí
    @FormUrlEncoded
    @POST("auth/register")
    Call<ApiResponse> createUser(@Field("username") String username,
                                 @Field("email") String email,
                                 @Field("password") String password);

    // Quên mật khẩu
    @FormUrlEncoded
    @POST("password/forgot")
    Call<ApiResponse> forgotPassword(@Field("email") String email);

    // Đổi mật khẩu
    @FormUrlEncoded
    @POST("password/update")
    Call<ApiResponse> resetPassword(@Header("Accept") String accept,
                                    @Header("token") String token,
                                    @Field("currentPassword") String currentPassword,
                                    @Field("newPassword") String newPassword,
                                    @Field("confirmPassword") String confirmPassword);

    // Đăng xuất
    @POST("auth/logout")
    Call<ApiResponse> logoutUser(@Header("Accept") String accept,
                                 @Header("token") String token);

    // Refresh token
    @FormUrlEncoded
    @POST("auth/refresh")
    Call<ApiResponse> refreshToken(@Field("refreshToken") String refreshToken);

    // Get All Category
    @GET("categories")
    Call<ListCategoryResponse> getAllCategories();

    @GET("products")
    Call<ListProductResponse> getAllProducts(@Query("limit") String limit,
                                             @Query("page") String page,
                                             @Query("sort") String sort,
                                             @Query("discount[gte]") String filterDiscount);

    @GET("product/{id}")
    Call<ProductResponse> getProduct(@Path("id") String id);

    @GET("reviews/{idProduct}")
    Call<ListReviewResponse> getAllReviewByProduct(@Path("idProduct") String productID);

    @GET("products/{idCate}")
    Call<ListProductResponse> getAllProductByCategory(@Path("idCate") String categoryID);

    @GET("cart/my-cart")
    Call<CartResponse> getMyCart(@Header("Accept") String accept,
                                 @Header("token") String token);

    @PUT("cart/remove-item-from-cart/{id}")
    Call<CartResponse> removeItemFromCart(@Header("Accept") String accept,
                                          @Path("id") String cartItemID,
                                          @Header("token") String token);

    // Đổi mật khẩu
    @FormUrlEncoded
    @POST("cart/add-to-cart")
    Call<CartResponse> addItemToCart(@Header("Accept") String accept,
                                     @Header("token") String token,
                                     @Field("product") String productID,
                                     @Field("size") String size,
                                     @Field("color") String color,
                                     @Field("quantity") int quantity);

    // Đổi mật khẩu
    @FormUrlEncoded
    @PUT("cart/{id}")
    Call<CartResponse> updateQuantityCart(@Header("Accept") String accept,
                                          @Header("token") String token,
                                          @Path("id") String cartItemID,
                                          @Field("quantity") int quantity);

    // get profile
    @GET("me/profile")
    Call<UserResponse> getMyAccount(@Header("Accept") String accept,
                                    @Header("token") String token);

    @POST("me/shipment-detail")
    Call<UserResponse> addShipmentDetail(@Header("Accept") String accept,
                                         @Header("token") String token,
                                         @Body ShipmentDetail shipmentDetail);

    @FormUrlEncoded
    @POST("order/new")
    Call<OrderResponse> createOrder(@Header("Accept") String accept,
                                    @Header("token") String token,
                                    @Field("fullName") String fullName,
                                    @Field("phone") String phone,
                                    @Field("province") String province,
                                    @Field("district") String district,
                                    @Field("ward") String ward,
                                    @Field("address") String address,
                                    @Field("isPaid") boolean isPaid,
                                    @Field("shippingPrice") int shippingPrice);
}
