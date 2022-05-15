package com.chuthuong.lthstore.api;

import com.chuthuong.lthstore.model.Cart;
import com.chuthuong.lthstore.model.CartResponse;
import com.chuthuong.lthstore.model.ListCategory;
import com.chuthuong.lthstore.model.ListProduct;
import com.chuthuong.lthstore.model.ListReview;
import com.chuthuong.lthstore.model.ListUser;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
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
            .baseUrl("http://web-api-chuthuong.herokuapp.com/api/v2/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    // Lấy tất cả user
//    @Headers("Content-Type: application/json")
    @Headers({"application-id: MY-APPLICATION-ID",
            "secret-key: MY-SECRET-KEY",
            "application-type: REST"})
    @GET("admin/users")
    Call<ListUser> getAllUser(@Header("Accept") String accept,
                              @Header("token") String token);

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
    Call<ListCategory> getAllCategories();

    @GET("products")
    Call<ListProduct> getAllProducts(@Query("limit") String limit,
                                     @Query("page") String page,
                                     @Query("sort") String sort,
                                     @Query("discount[gte]") String filterDiscount);

    @GET("reviews/{idProduct}")
    Call<ListReview> getAllReviewByProduct(@Path("idProduct") String productID);

    @GET("products/{idCate}")
    Call<ListProduct> getAllProductByCategory(@Path("idCate") String categoryID);

    @GET("cart/my-cart")
    Call<CartResponse> getMyCart(@Header("Accept") String accept,
                                 @Header("token") String token);

    @PUT("cart/remove-item-from-cart/{id}")
    Call<CartResponse> removeItemFromCart(@Header("Accept") String accept,
                                 @Path("id") String itemID,
                                 @Header("token") String token);
}
