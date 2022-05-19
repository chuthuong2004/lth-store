package com.chuthuong.lthstore.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.adapter.MyCartAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.Cart;
import com.chuthuong.lthstore.response.CartResponse;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.utils.Util;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCartActivity extends AppCompatActivity {
    RecyclerView recyclerViewCart;
    MyCartAdapter myCartAdapter;
    TextView totalPrice, titleMyCart, findProductInCart, buyNow;
    ConstraintLayout layoutCart, layoutCartEmpty;
    ImageView imgBack, imgHome, imgChat, imgSearch;
    CartResponse cartResponse = null;
    private User user = null;
    private Cart cart = null;
    private UserReaderSqlite userReaderSqlite;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);

        userReaderSqlite = new UserReaderSqlite(MyCartActivity.this, "user.db", null, 1);
        Util.refreshToken(this);
        user = userReaderSqlite.getUser();
        String title = getIntent().getStringExtra("title_my_cart");
        addControls();
        addEvents();
        loadCart();
        titleMyCart.setText(title);

    }


    private void renderCart(Cart mCart) {
        if (mCart != null) {
            cart = mCart;
            recyclerViewCart = findViewById(R.id.rec_cart);
            recyclerViewCart.setLayoutManager(new LinearLayoutManager(MyCartActivity.this, RecyclerView.VERTICAL, false));
            myCartAdapter = new MyCartAdapter(this, mCart);
            recyclerViewCart.setAdapter(myCartAdapter);
            myCartAdapter.notifyDataSetChanged();
            setTextPrice(mCart);
        } else {
            hideLayoutCart();
        }
    }

    private void loadCart() {
        if (user != null) {
            callApiGetMyCart("Bearer " + user.getAccessToken());
        }
    }

    public void callApiGetMyCart(String accessToken) {
        String accept = "application/json;versions=1";
        ApiService.apiService.getMyCart(accept, accessToken).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    cartResponse = response.body();
                    renderCart(cartResponse.getCart());
                } else {
                    try {
                        hideLayoutCart();
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                setToast(MyCartActivity.this, "Lỗi server !");
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        userReaderSqlite = new UserReaderSqlite(this, "user.db", null, 1);
        Util.refreshToken(this);
        loadCart();

    }

    public void hideLayoutCart() {
        layoutCart.setVisibility(View.GONE);
        layoutCartEmpty.setVisibility(View.VISIBLE);
    }

    private void setTextPrice(Cart cart) {
        int quantity = 0;
        for (int i = 0; i < cart.getCartItems().size(); i++) {
            int priceCurrent = cart.getCartItems().get(i).getProduct().getPrice()
                    - ((cart.getCartItems().get(i).getProduct().getPrice() * cart.getCartItems().get(i).getProduct().getDiscount()) / 100);
            int price = priceCurrent * cart.getCartItems().get(i).getQuantity();
            quantity += price;
        }
        NumberFormat formatter = new DecimalFormat("#,###");
        String formatterPriceProduct = formatter.format(quantity);
        totalPrice.setText(formatterPriceProduct + "đ");
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
    private void addEvents() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyCartActivity.this, MainActivity.class));
            }
        });
        imgChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToast(MyCartActivity.this, "Chức năng chat đang cập nhật !");
            }
        });
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToast(MyCartActivity.this, "Chức năng tìm kiếm đang cập nhật !");
            }
        });
        findProductInCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyCartActivity.this, PaymentActivity.class));
            }
        });
    }

    public void reloadAdapter(Cart cart) {

        setTextPrice(cart);
        myCartAdapter = new MyCartAdapter(this, cart);
        recyclerViewCart.setAdapter(myCartAdapter);
        myCartAdapter.notifyDataSetChanged();
    }
    private void addControls() {
        totalPrice = findViewById(R.id.txt_total_price_cart);
        imgBack = findViewById(R.id.img_back_my_cart);
        imgHome = findViewById(R.id.img_home);
        imgChat = findViewById(R.id.img_chat);
        imgSearch = findViewById(R.id.search_img);
        titleMyCart = findViewById(R.id.title_my_cart);
        layoutCart = findViewById(R.id.layout_cart);
        layoutCartEmpty = findViewById(R.id.layout_cart_empty);
        findProductInCart = findViewById(R.id.txt_find_product_in_cart);
        buyNow = findViewById(R.id.txt_buy_product);
    }
}