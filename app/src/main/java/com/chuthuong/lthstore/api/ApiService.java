package com.chuthuong.lthstore.api;

import android.os.Environment;

import com.chuthuong.lthstore.model.ShipmentDetail;
import com.chuthuong.lthstore.province.Province;
import com.chuthuong.lthstore.response.CartResponse;
import com.chuthuong.lthstore.response.ListCategoryResponse;
import com.chuthuong.lthstore.response.ListOrderResponse;
import com.chuthuong.lthstore.response.ListProductResponse;
import com.chuthuong.lthstore.response.ListReviewResponse;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.response.OrderResponse;
import com.chuthuong.lthstore.response.ProductResponse;
import com.chuthuong.lthstore.response.ReviewResponse;
import com.chuthuong.lthstore.response.UserResponse;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.ApiToken;
import com.chuthuong.lthstore.utils.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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

    // forgot password
    @FormUrlEncoded
    @POST("password/forgot")
    Call<ApiResponse> forgotPassword(@Field("email") String email);

    // update password
    @FormUrlEncoded
    @POST("password/update")
    Call<ApiResponse> resetPassword(@Header("Accept") String accept,
                                    @Header("token") String token,
                                    @Field("currentPassword") String currentPassword,
                                    @Field("newPassword") String newPassword,
                                    @Field("confirmPassword") String confirmPassword);

    // Logout
    @POST("auth/logout")
    Call<ApiResponse> logoutUser(@Header("token") String token);

    // Refresh token
    @FormUrlEncoded
    @POST("auth/refresh")
    Call<ApiToken> refreshToken(@Field("refreshToken") String refreshToken);

    // Get All Category
    @GET("categories")
    Call<ListCategoryResponse> getAllCategories();

    // get all products
    @GET("products")
    Call<ListProductResponse> getAllProducts(@Query("limit") int limit,
                                             @Query("page") int page,
                                             @Query("sort") String sort,
                                             @Query("search") String search,
                                             @Query("discount[gte]") int filterDiscount,
                                             @Query("price[gte]") int filterPrice);

    // product detail
    @GET("product/{id}")
    Call<ProductResponse> getProduct(@Path("id") String id);

    // get all reviews by product
    @GET("reviews/{idProduct}")
    Call<ListReviewResponse> getAllReviewByProduct(@Path("idProduct") String productID);

    // get all products by category
    @GET("products/{idCate}")
    Call<ListProductResponse> getAllProductByCategory(@Path("idCate") String categoryID,
                                                      @Query("limit") int limit,
                                                      @Query("page") int page);

    // get my cart
    @GET("cart/my-cart")
    Call<CartResponse> getMyCart(@Header("Accept") String accept,
                                 @Header("token") String token);


    // remove item from cart
    @PUT("cart/remove-item-from-cart/{id}")
    Call<CartResponse> removeItemFromCart(@Header("Accept") String accept,
                                          @Path("id") String cartItemID,
                                          @Header("token") String token);

    // add item to cart
    @FormUrlEncoded
    @POST("cart/add-to-cart")
    Call<CartResponse> addItemToCart(@Header("Accept") String accept,
                                     @Header("token") String token,
                                     @Field("product") String productID,
                                     @Field("size") String size,
                                     @Field("color") String color,
                                     @Field("quantity") int quantity);

    // update quantity of item in cart
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

    // update avatar
    @Multipart
    @PUT("me/update")
    Call<UserResponse> updateAvatar(@Header("token") String token, @Part MultipartBody.Part avatar);


    // add shipment detail
    @POST("me/shipment-detail")
    Call<UserResponse> addShipmentDetail(@Header("Accept") String accept,
                                         @Header("token") String token,
                                         @Body ShipmentDetail shipmentDetail);

    // update shipment detail
    @PUT("me/shipment-detail/{id}")
    Call<UserResponse> updateShipment(
            @Header("token") String token,
            @Path("id") String shipmentID,
            @Body ShipmentDetail shipmentDetail);

    // remove shipment detail
    @PUT("me/shipment-detail/remove/{id}")
    Call<UserResponse> removeShipment(
            @Header("token") String token,
            @Path("id") String shipmentID);


    // create new order
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

    // get all orders
    @GET("order/me")
    Call<ListOrderResponse> getMyOrder(@Header("Accept") String accept,
                                       @Header("token") String token,
                                       @Query("sort") String sort,
                                       @Query("orderStatus[regex]") String filterStatus);

    // get a order
    @GET("order/me/{id}")
    Call<OrderResponse> getAnOrderMe(@Header("token") String token,
                                     @Path("id") String id);

    // cancel order
    @FormUrlEncoded
    @PUT("order/cancel/{id}")
    Call<OrderResponse> cancelOrder(@Header("token") String token,
                                    @Path("id") String id,
                                    @Field("reason") String reason);

    // delete review
    @DELETE("review/{id}")
    Call<ApiResponse> deleteReview(@Header("token") String token,
                                   @Path("id") String id);

    // create new review
    @FormUrlEncoded
    @POST("review/new/{id}")
    Call<ReviewResponse> createNewReviews(@Header("token") String token,
                                   @Field("content") String content,
                                   @Field("product") String productID,
                                   @Field("star") float star,
                                   @Path("id") String orderItemID);

    @PUT("products/favorite/add/{id}")
    Call<ProductResponse> addFavorite(@Header("token") String token,
                                      @Path("id") String productID);
    @PUT("products/favorite/remove/{id}")
    Call<ProductResponse> removeFavorite(@Header("token") String token,
                                      @Path("id") String productID);
}
