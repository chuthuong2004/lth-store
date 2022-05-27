package com.chuthuong.lthstore.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.detailActivities.OrderDetailActivity;
import com.chuthuong.lthstore.adapter.ItemReviewAdapter;
import com.chuthuong.lthstore.adapter.MyItemOrderAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.Order;
import com.chuthuong.lthstore.model.OrderItem;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.response.OrderResponse;
import com.chuthuong.lthstore.response.ReviewModel;
import com.chuthuong.lthstore.response.ReviewResponse;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.utils.Util;
import com.chuthuong.lthstore.widget.CustomProgressDialog;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewProductActivity extends AppCompatActivity {

    ImageView imageViewBack;
    TextView txtConfirmReview;
    User user;
    private UserReaderSqlite userReaderSqlite;
    private RecyclerView recOrderItemReviews;
    private ItemReviewAdapter itemReviewAdapter;
    private Order order;
    public boolean isClickedReviews = false;
    private String orderID;
    public List<Float> listRatings;
    private int numberLoop;
    private CustomProgressDialog dialog;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_product);
        userReaderSqlite = new UserReaderSqlite(this, "user.db", null, 1);
        Util.refreshToken(this);
        user = userReaderSqlite.getUser();
        orderID = getIntent().getStringExtra("order_id");
        if (orderID != null) {
            callApiGetAnOrderMe(user.getAccessToken(), orderID);
        }
        addControls();
        addEvent();


    }

    private void callApiGetAnOrderMe(String accessToken, String orderID) {
        String token = "Bearer " + accessToken;
        ApiService.apiService.getAnOrderMe(token, orderID).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful()) {
                    order = response.body().getOrder();
                    recOrderItemReviews.setLayoutManager(new LinearLayoutManager(ReviewProductActivity.this, RecyclerView.VERTICAL, false));
                    itemReviewAdapter = new ItemReviewAdapter(ReviewProductActivity.this, order);
                    recOrderItemReviews.setAdapter(itemReviewAdapter);
                    itemReviewAdapter.notifyDataSetChanged();
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(ReviewProductActivity.this, apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Toast.makeText(ReviewProductActivity.this, "Lỗi server", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addEvent() {

        txtConfirmReview.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                dialog = new CustomProgressDialog(ReviewProductActivity.this);
                dialog.show();
                for (int i = 0; i < order.getOrderItems().size(); i++) {
                    numberLoop = i + 1;
                    OrderItem orderItem = order.getOrderItems().get(i);
                    String content = itemReviewAdapter.reviews.get(i);
                    Float star = itemReviewAdapter.rates.get(i);
                    callApiCreateReviews(user.getAccessToken(), orderItem.getProduct(), content, star, orderItem.getId());
                }
            }
        });
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void callApiCreateReviews(String accessToken, String productID, String content, float star, String orderItemID) {
        String token = "Bearer " + accessToken;

        ApiService.apiService.createNewReviews(token, content, productID, star, orderItemID).enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful()) {
                    if (numberLoop == order.getOrderItems().size()) {
                        dialog.dismiss();
                        setToast(ReviewProductActivity.this, response.body().getMessage());
                        finish();
                    }
                } else {
                    dialog.dismiss();
                    Gson gson = new Gson();
                    try {
                        ApiResponse apiResponse = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Log.e("lỗi", response.errorBody().toString());
                        setToast(ReviewProductActivity.this, apiResponse.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                setToast(ReviewProductActivity.this, "Lỗi server");
            }
        });
    }

    private void addControls() {
        txtConfirmReview = findViewById(R.id.txt_confirm_review);
        imageViewBack = findViewById(R.id.img_back_detail);
        recOrderItemReviews = findViewById(R.id.rec_item_review_order);
    }


//    private void loadProduct() {
//        txtNameProductReview.setText(orderItem.getName());
//        txtTypeProductReview.setText(orderItem.getColor() + ", " + orderItem.getSize());
//        Glide.with(this).load(orderItem.getImage()).into(imageProductReview);
//    }

    private void setToast(Activity activity, String msg) {
        Toast toast = new Toast(activity);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.layout_toast));
        TextView message = view.findViewById(R.id.message_toast);
        message.setText(msg);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}