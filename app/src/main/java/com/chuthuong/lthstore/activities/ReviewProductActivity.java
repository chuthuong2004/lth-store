package com.chuthuong.lthstore.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.OrderItem;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.response.ReviewModel;
import com.chuthuong.lthstore.response.ReviewResponse;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.utils.Util;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewProductActivity extends AppCompatActivity {

    ImageView imageProductReview, imageViewBack;
    TextView txtNameProductReview, txtTypeProductReview, txtConfirmReview;
    RatingBar ratingBarReviewProduct;
    EditText editTextFeelings;
    String productID;
    OrderItem orderItem;
    ReviewResponse reviewResponse;
    User user;
    private UserReaderSqlite userReaderSqlite;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_product);
        Util.refreshToken(this);
        userReaderSqlite = new UserReaderSqlite(this, "user.db", null, 1);
        user = userReaderSqlite.getUser();
        orderItem = (OrderItem) getIntent().getSerializableExtra("order_item");
        productID = orderItem.getProduct();

        addControls();
        loadProduct();
        addEvent();


    }


    private void addEvent() {
        ratingBarReviewProduct.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                switch ((int) ratingBarReviewProduct.getRating()) {
                    case 1:
                        editTextFeelings.setHint("Hãy chia sẻ vì sao sản phẩm này không tốt nhé");
                        break;
                    case 2:
                        editTextFeelings.setHint("Hãy chia sẻ vì sao bạn không thích sản phẩm này nhé");
                        break;
                    case 3:
                        editTextFeelings.setHint("Hãy chia sẻ vì sao bạn chưa thật sự thích sản phẩm này nhé");
                        break;
                    case 4:
                        editTextFeelings.setHint("Hãy chia sẻ ví sao bạn thích sản phẩm này nhé");
                        break;
                    case 5:
                    default:
                        editTextFeelings.setHint("Hãy chia sẻ những điều bạn thích về sản phẩm này nhé");
                        break;

                }
                Log.e("Sao nè", ratingBarReviewProduct.getRating() + "");
            }
        });
        txtConfirmReview.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (user != null) {
                    Util.refreshToken(ReviewProductActivity.this);
                    user = userReaderSqlite.getUser();
                    setToast(ReviewProductActivity.this, "Đánh giá " + ratingBarReviewProduct.getRating() + " sao" + "\nNội dung " + editTextFeelings.getText());
                    addReview(userReaderSqlite.getUser().getAccessToken(), productID, editTextFeelings.getText().toString(),
                            (int) ratingBarReviewProduct.getRating(), orderItem.getId());
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

    private void addReview(String accessToken, String productID, String content, int star, String orderItemID) {
        String token = "Bearer " + accessToken;
        String accept = "application/json;versions=1";

        ApiService.apiService.addReview(accept, token, content, productID, star, orderItemID).enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful()) {
//                    reviewResponse=response.body();
                    ReviewModel review = response.body().getReview();
                    Log.e("review", review.getContent());
                    setToast(ReviewProductActivity.this, response.body().getMessage());
                    onBackPressed();
                } else {
                    Log.e("Code", response.code() + "");
                    Log.e("Code", response.errorBody().toString() + "");
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
        txtNameProductReview = findViewById(R.id.txt_name_product_review);
        txtTypeProductReview = findViewById(R.id.txt_type_product);
        imageProductReview = findViewById(R.id.img_product_review);
        ratingBarReviewProduct = findViewById(R.id.rating_bar_review_product);
        editTextFeelings = findViewById(R.id.edit_text_feelings);
        txtConfirmReview = findViewById(R.id.txt_confirm_review);
        imageViewBack = findViewById(R.id.img_back_detail);
    }


    private void loadProduct() {
        txtNameProductReview.setText(orderItem.getName());
        txtTypeProductReview.setText(orderItem.getColor() + ", " + orderItem.getSize());
        Glide.with(this).load(orderItem.getImage()).into(imageProductReview);
    }

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