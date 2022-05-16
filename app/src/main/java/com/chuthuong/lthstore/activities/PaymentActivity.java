package com.chuthuong.lthstore.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.adapter.MyCartAdapter;
import com.chuthuong.lthstore.adapter.MyPaymentAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.Cart;
import com.chuthuong.lthstore.model.CartResponse;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.widget.CustomProgressDialog;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    RecyclerView recyclerViewPaymentCart;
    MyPaymentAdapter myPaymentAdapter;
    TextView txtTotalPricePayment, txtTotalPriceHaveToPayment, txtPaymentOrder,totalQuantityItem;
    User user;
    Cart cart = null;
    private CartResponse cartResponse;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        user = MainActivity.getUser();
        getMyCart();
        addControls();
        addEvents();
        recyclerViewPaymentCart.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
    }

    private void addEvents() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txtPaymentOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToast(PaymentActivity.this,"Chuyển sang nhập thông tin");
            }
        });
    }

    private void loadAdapter(Cart cart) {
        myPaymentAdapter = new MyPaymentAdapter(this, cart);
        recyclerViewPaymentCart.setAdapter(myPaymentAdapter);
        myPaymentAdapter.notifyDataSetChanged();
        totalQuantityItem.setText(cart.getCartItems().size()+"");
        totalPrice(cart);
    }

    private void totalPrice(Cart cart) {
        int quantity = 0;
        for (int i = 0; i < cart.getCartItems().size(); i++) {
            int priceCurrent = cart.getCartItems().get(i).getProduct().getPrice()
                    - ((cart.getCartItems().get(i).getProduct().getPrice() * cart.getCartItems().get(i).getProduct().getDiscount()) / 100);
            int price = priceCurrent * cart.getCartItems().get(i).getQuantity();
            quantity += price;
        }
        NumberFormat formatter = new DecimalFormat("#,###");
        String formatterPriceProduct = formatter.format(quantity);
        txtTotalPricePayment.setText(formatterPriceProduct + "đ");
        txtTotalPriceHaveToPayment.setText(formatterPriceProduct + "đ");
    }

    private void addControls() {
        recyclerViewPaymentCart = findViewById(R.id.rec_cart_payment);
        txtTotalPriceHaveToPayment = findViewById(R.id.txt_total_price_have_to_pay);
        txtTotalPricePayment = findViewById(R.id.txt_total_price_payment);
        txtPaymentOrder = findViewById(R.id.txt_payment_order);
        totalQuantityItem = findViewById(R.id.txt_total_quantity_payment);
        back = findViewById(R.id.img_back_detail);
    }
    public void callApiGetMyCart(String accessToken) {
        String accept = "application/json;versions=1";
        ApiService.apiService.getMyCart(accept, accessToken).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    cartResponse = response.body();
                    cart = cartResponse.getCart();
                    loadAdapter(cart);
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(PaymentActivity.this, apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                setToast(PaymentActivity.this, "Lỗi server !");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        getMyCart();
    }

    private void getMyCart() {
        if (user != null) {
            callApiGetMyCart("Bearer " + user.getAccessToken());
        }
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