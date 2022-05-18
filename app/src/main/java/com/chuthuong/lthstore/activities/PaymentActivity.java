package com.chuthuong.lthstore.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.authActivities.LoginActivity;
import com.chuthuong.lthstore.adapter.MyPaymentAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.Cart;
import com.chuthuong.lthstore.model.Order;
import com.chuthuong.lthstore.model.ShipmentDetail;
import com.chuthuong.lthstore.response.CartResponse;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.response.OrderResponse;
import com.chuthuong.lthstore.response.UserResponse;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    RecyclerView recyclerViewPaymentCart;
    MyPaymentAdapter myPaymentAdapter;
    TextView txtTotalPricePayment, txtTotalPriceHaveToPayment, txtPaymentOrder, totalQuantityItem, shippingPrice;
    TextView txtNamePayment, txtPhonePayment, txtAddressPayment, txtProvincePayment;
    ImageView editShipmentDetail;
    CardView layoutShipment;
    User user;
    Cart cart = null;
    private CartResponse cartResponse;
    private ImageView back;
    private ShipmentDetail shipmentDetail;
    private int priceShipping;
    private int quantityPriceCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        callApiMyAccount("Bearer " + LoginActivity.user.getAccessToken());
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
                if (layoutShipment.getVisibility() == View.GONE) {
                    startActivity(new Intent(PaymentActivity.this, ShipmentDetailActivity.class));
                } else {
                    callApiCreateOrder("Bearer "+LoginActivity.user.getAccessToken(), shipmentDetail, false,priceShipping );
                }

            }
        });
        editShipmentDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToast(PaymentActivity.this, "Chuyển sang sổ địa chỉ !");
            }
        });
    }

    private void callApiCreateOrder(String token, ShipmentDetail shipmentDetail, boolean isPaid, int shippingPrice) {
        String fullName, phone, province, district,ward,address;
        fullName = shipmentDetail.getFullName();
        phone =shipmentDetail.getPhone();
        province= shipmentDetail.getProvince();
        district =shipmentDetail.getDistrict();
        ward = shipmentDetail.getWard();
        address = shipmentDetail.getAddress();
        String accept = "application/json;versions=1";
        Log.e("OK", fullName+", "+phone+", "+province+", "+district+", "+ward+", "+address+", "+isPaid+", "+shippingPrice);
        ApiService.apiService.createOrder(accept,token,fullName,phone,province,district,ward,address,isPaid,shippingPrice).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful()) {
                    OrderResponse orderResponse = response.body();
                    Order order = orderResponse.getOrder();
                    setToast(PaymentActivity.this,  orderResponse.getMessage());
                    finish();
                    startActivity(new Intent(PaymentActivity.this,MainActivity.class));
                }else {
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
            public void onFailure(Call<OrderResponse> call, Throwable t) {

                setToast(PaymentActivity.this, "Lỗi Server !");
            }
        });
    }

    private void loadAdapter(Cart cart) {
        myPaymentAdapter = new MyPaymentAdapter(this, cart);
        recyclerViewPaymentCart.setAdapter(myPaymentAdapter);
        myPaymentAdapter.notifyDataSetChanged();
        totalQuantityItem.setText(cart.getCartItems().size() + "");
        setTotalPrice(cart);
    }

    private void callApiMyAccount(String token) {
        String accept = "application/json;versions=1";
        ApiService.apiService.getMyAccount(accept, token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    UserResponse userResponse = response.body();
                    user = userResponse.getUser();
                    getMyCart();
                    setShipmentDetail();
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
            public void onFailure(Call<UserResponse> call, Throwable t) {
                setToast(PaymentActivity.this, "Lỗi Server !");
            }
        });

    }

    private void setShipmentDetail() {
        NumberFormat formatter = new DecimalFormat("#,###");
        shipmentDetail = findDefaultShipmentDetail(user);
        if (shipmentDetail == null) {
            shippingPrice.setText("0đ");
            layoutShipment.setVisibility(View.GONE);
        } else {
            priceShipping = getShippingPrice(shipmentDetail.getProvince(), shipmentDetail.getWard());

            String formatterPriceShipping = formatter.format(priceShipping);
            shippingPrice.setText(formatterPriceShipping + "đ");
            layoutShipment.setVisibility(View.VISIBLE);
            txtNamePayment.setText(shipmentDetail.getFullName());
            txtPhonePayment.setText(shipmentDetail.getPhone());
            txtAddressPayment.setText(shipmentDetail.getAddress());
            txtProvincePayment.setText(shipmentDetail.getWard() + ", " + shipmentDetail.getDistrict() + ", " + shipmentDetail.getProvince());
        }
        String totalPricePayment = formatter.format(quantityPriceCart + priceShipping);
        txtTotalPriceHaveToPayment.setText(totalPricePayment + "đ");
    }

    private ShipmentDetail findDefaultShipmentDetail(User user) {
        List<ShipmentDetail> shipmentDetails = user.getShipmentDetails();
        if (shipmentDetails == null) return null;
        if (shipmentDetails.size() == 0) return shipmentDetails.get(0);
        for (int i = 0; i < shipmentDetails.size(); i++) {
            if (shipmentDetails.get(i).isDefault() == true) {
                return shipmentDetails.get(i);
            }
        }
        return shipmentDetails.get(0);
    }

    private void setTotalPrice(Cart cart) {
        int quantity = 0;
        for (int i = 0; i < cart.getCartItems().size(); i++) {
            int priceCurrent = cart.getCartItems().get(i).getProduct().getPrice()
                    - ((cart.getCartItems().get(i).getProduct().getPrice() * cart.getCartItems().get(i).getProduct().getDiscount()) / 100);
            int price = priceCurrent * cart.getCartItems().get(i).getQuantity();
            quantity += price;
        }
        quantityPriceCart = quantity;
        NumberFormat formatter = new DecimalFormat("#,###");
        String formatterPriceProduct = formatter.format(quantity);
        txtTotalPricePayment.setText(formatterPriceProduct + "đ");
        setShipmentDetail();
    }

    private int getShippingPrice(String province, String ward) {
        if (province.contains("Hồ Chí Minh")) {
            if (ward.contains("Hiệp Bình Phước")) return 0;
            return 10000;
        }
        return 30000;
    }

    private void addControls() {
        recyclerViewPaymentCart = findViewById(R.id.rec_cart_payment);
        txtTotalPriceHaveToPayment = findViewById(R.id.txt_total_price_have_to_pay);
        txtTotalPricePayment = findViewById(R.id.txt_total_price_payment);
        txtPaymentOrder = findViewById(R.id.txt_payment_order);
        totalQuantityItem = findViewById(R.id.txt_total_quantity_payment);
        back = findViewById(R.id.img_back_detail);
        txtNamePayment = findViewById(R.id.txt_name_payment);
        txtPhonePayment = findViewById(R.id.txt_phone_payment);
        txtAddressPayment = findViewById(R.id.txt_address_payment);
        txtProvincePayment = findViewById(R.id.txt_province_payment);
        txtNamePayment = findViewById(R.id.txt_name_payment);
        editShipmentDetail = findViewById(R.id.edit_shipment_detail);
        layoutShipment = findViewById(R.id.layout_address);
        shippingPrice = findViewById(R.id.text_view_shipping_price);
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
        callApiMyAccount("Bearer " + LoginActivity.user.getAccessToken());
    }

    private void getMyCart() {
        if (user != null) {
            callApiGetMyCart("Bearer " + LoginActivity.user.getAccessToken());
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