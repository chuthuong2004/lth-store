package com.chuthuong.lthstore.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.shipmentActivities.ListShipmentDetailActivity;
import com.chuthuong.lthstore.activities.shipmentActivities.ShipmentDetailActivity;
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
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.utils.Util;
import com.chuthuong.lthstore.widget.CustomProgressDialog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//import vn.momo.momo_partner.AppMoMoLib;

public class PaymentActivity extends AppCompatActivity {
    RecyclerView recyclerViewPaymentCart;
    MyPaymentAdapter myPaymentAdapter;
    TextView txtTotalPricePayment, txtTotalPriceHaveToPayment, txtPaymentOrder, totalQuantityItem, shippingPrice;
    TextView txtNamePayment, txtPhonePayment, txtAddressPayment, txtProvincePayment;
    ImageView editShipmentDetail;
    CardView layoutShipment;
    User user, userToken;
    Cart cart = null;
    private CartResponse cartResponse;
    private ImageView back;
    private ShipmentDetail shipmentDetail;
    private int priceShipping;
    private int quantityPriceCart;
    private UserReaderSqlite userReaderSqlite;
    private CustomProgressDialog dialog;

    private String amount = "10000";
    private String fee = "0";
    int environment = 0;//developer default
    private String merchantName = "Thanh toán đơn hàng";
    private String merchantCode = "SCB01";
    private String merchantNameLabel = "Thương Chu";
    private String description = "Mua hàng online";



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        userReaderSqlite = new UserReaderSqlite(this, "user.db", null, 1);
        Util.refreshToken(this);
        userToken = userReaderSqlite.getUser();
        callApiMyAccount("Bearer " + userToken.getAccessToken());
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
                    dialog = new CustomProgressDialog(PaymentActivity.this);
                    dialog.show();
//                    AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION
//                    requestPayment();
                    callApiCreateOrder("Bearer "+userReaderSqlite.getUser().getAccessToken(), shipmentDetail, false,priceShipping );
                }

            }
        });
        editShipmentDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, ListShipmentDetailActivity.class);
                intent.putExtra("change_address", true);
                startActivity(intent);
            }
        });
    }
    //Get token through MoMo app
//    private void requestPayment() {
//        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT);
//        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN);
////        if (edAmount.getText().toString() != null && edAmount.getText().toString().trim().length() != 0)
////            amount = edAmount.getText().toString().trim();
//        amount = "10000";
//        Map<String, Object> eventValue = new HashMap<>();
//        //client Required
//        eventValue.put("merchantname", merchantName); //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
//        eventValue.put("merchantcode", merchantCode); //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
//        eventValue.put("amount", 10000); //Kiểu integer
//        eventValue.put("orderId", "orderId123456789"); //uniqueue id cho Bill order, giá trị duy nhất cho mỗi đơn hàng
//        eventValue.put("orderLabel", "Mã đơn hàng"); //gán nhãn
//
//        //client Optional - bill info
//        eventValue.put("merchantnamelabel", "Dịch vụ");//gán nhãn
//        eventValue.put("fee", 0); //Kiểu integer
//        eventValue.put("description", description); //mô tả đơn hàng - short description
//
//        //client extra data
//        eventValue.put("requestId",  merchantCode+"merchant_billId_"+System.currentTimeMillis());
//        eventValue.put("partnerCode", merchantCode);
//        //Example extra data
//        JSONObject objExtraData = new JSONObject();
//        try {
//            objExtraData.put("site_code", "008");
//            objExtraData.put("site_name", "CGV Cresent Mall");
//            objExtraData.put("screen_code", 0);
//            objExtraData.put("screen_name", "Special");
//            objExtraData.put("movie_name", "Kẻ Trộm Mặt Trăng 3");
//            objExtraData.put("movie_format", "2D");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        eventValue.put("extraData", objExtraData.toString());
//
//        eventValue.put("extra", "");
//        AppMoMoLib.getInstance().requestMoMoCallBack(this, eventValue);
//
//
//    }
    //Get token callback from MoMo app an submit to server side
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
//            if(data != null) {
//                if(data.getIntExtra("status", -1) == 0) {
//                    //TOKEN IS AVAILABLE
//                    Log.e("Thành công", data.getStringExtra("message"));
////                    tvMessage.setText("message: " + "Get token " + data.getStringExtra("message"));
//                    String token = data.getStringExtra("data"); //Token response
//                    String phoneNumber = data.getStringExtra("phonenumber");
//                    String env = data.getStringExtra("env");
//                    if(env == null){
//                        env = "app";
//                    }
//
//                    if(token != null && !token.equals("")) {
//                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
//                        // IF Momo topup success, continue to process your order
//                    } else {
//                        Log.e("Không thành công", "Ok");
////                        tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
//                    }
//                } else if(data.getIntExtra("status", -1) == 1) {
//                    //TOKEN FAIL
//                    String message = data.getStringExtra("message") != null?data.getStringExtra("message"):"Thất bại";
//                    Log.e("Thất bại", message);
////                    tvMessage.setText("message: " + message);
//                } else if(data.getIntExtra("status", -1) == 2) {
//                    //TOKEN FAIL
//                    Log.e("Thất bại", "message");
////                    tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
//                } else {
//                    //TOKEN FAIL
//                    Log.e("Thất bại", "message");
//                }
//            } else {
//                Log.e("Thất bại", "message");
//            }
//        } else {
//            Log.e("Thất bại", "message");
//        }
//    }
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
                    dialog.dismiss();
                    openDialogSuccessOrder();
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
    private void openDialogSuccessOrder() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_success_order);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(false);
        TextView btnContinue = dialog.findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentActivity.this, MainActivity.class));
                finish();
            }
        });
        dialog.show();
    }
    private void callApiMyAccount(String token) {
        String accept = "application/json;versions=1";
        ApiService.apiService.getMyAccount(accept, token).enqueue(new Callback<UserResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    UserResponse userResponse = response.body();
                    user = userResponse.getUser();

                    Util.refreshToken(PaymentActivity.this);
                    userToken = userReaderSqlite.getUser();
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
        SharedPreferences preferences = getSharedPreferences("shipment_detail", MODE_PRIVATE);
        String shipmentID = preferences.getString("shipment_id", "");

        NumberFormat formatter = new DecimalFormat("#,###");

        if(shipmentID!="") {
            for (int i = 0; i < user.getShipmentDetails().size(); i++) {
                if(user.getShipmentDetails().get(i).getId().equals(shipmentID)) {
                    shipmentDetail = user.getShipmentDetails().get(i);
                    break;
                }
            }
        }else {
            shipmentDetail = findDefaultShipmentDetail(user);
        }
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
        if (shipmentDetails.size() == 0) return null;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        Util.refreshToken(this);
        userToken = userReaderSqlite.getUser();
        callApiMyAccount("Bearer " + userToken.getAccessToken());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        Util.refreshToken(this);
        userToken = userReaderSqlite.getUser();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getMyCart() {
        if (user != null) {
            Util.refreshToken(this);
            userToken = userReaderSqlite.getUser();
            callApiGetMyCart("Bearer " + userReaderSqlite.getUser().getAccessToken());
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

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences preferences2 = getSharedPreferences("shipment_detail", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences2.edit();
        editor.putString("shipment_id", "");
        editor.commit(); // xác nhận lưu
    }
}