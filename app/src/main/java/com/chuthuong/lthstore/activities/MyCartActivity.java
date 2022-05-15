package com.chuthuong.lthstore.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.adapter.MyCartAdapter;
import com.chuthuong.lthstore.model.Cart;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MyCartActivity extends AppCompatActivity {
    RecyclerView recyclerViewCart;
    MyCartAdapter myCartAdapter;
    TextView totalPrice, titleMyCart;
    ImageView imgBack, imgHome, imgChat, imgSearch;
    int quantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);
        addControls();
        addEvents();
        Intent intent = getIntent();
        Cart cart = (Cart) intent.getSerializableExtra("my_cart");
        String title = intent.getStringExtra("title_my_cart");
        titleMyCart.setText(title);
        Log.e("Nhận Cart", cart.toString());
        recyclerViewCart = findViewById(R.id.rec_cart);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(MyCartActivity.this, RecyclerView.VERTICAL, false));
        myCartAdapter = new MyCartAdapter(this, cart);
        recyclerViewCart.setAdapter(myCartAdapter);
        myCartAdapter.notifyDataSetChanged();
        totalPrice.setText(totalPrice(cart)+"đ");
    }
    private String totalPrice(Cart cart){
        quantity=0;
        for (int i = 0; i < cart.getCartItems().size(); i++) {
            int priceCurrent = cart.getCartItems().get(i).getProduct().getPrice()
                    - ((cart.getCartItems().get(i).getProduct().getPrice() * cart.getCartItems().get(i).getProduct().getDiscount()) / 100);
            int price = priceCurrent * cart.getCartItems().get(i).getQuantity();
            quantity += price;
        }
        NumberFormat formatter = new DecimalFormat("#,###");
        String formatterPriceProduct = formatter.format(quantity);
        return formatterPriceProduct;
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
                Toast.makeText(MyCartActivity.this, "Chức năng chat đang cập nhật !", Toast.LENGTH_SHORT).show();
            }
        });
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyCartActivity.this, "Chức năng tìm kiếm đang cập nhật !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void reloadAdapter(Cart cart) {

        totalPrice.setText(totalPrice(cart) + "đ");
        myCartAdapter = new MyCartAdapter(this, cart);
        recyclerViewCart.setAdapter(myCartAdapter);
        myCartAdapter.notifyDataSetChanged();
        Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
        Log.e("new",cart.getCartItems().size()+"");
//        onRestart();
    }

    private void addControls() {
        totalPrice = findViewById(R.id.txt_total_price_cart);
        imgBack = findViewById(R.id.img_back_my_cart);
        imgHome = findViewById(R.id.img_home);
        imgChat = findViewById(R.id.img_chat);
        imgSearch = findViewById(R.id.search_img);
        titleMyCart = findViewById(R.id.title_my_cart);
    }
}