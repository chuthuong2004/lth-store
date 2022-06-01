package com.chuthuong.lthstore.activities.detailActivities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.MainActivity;
import com.chuthuong.lthstore.activities.MyCartActivity;
import com.chuthuong.lthstore.activities.PaymentActivity;
import com.chuthuong.lthstore.activities.ReviewProductActivity;
import com.chuthuong.lthstore.adapter.MyItemOrderAdapter;
import com.chuthuong.lthstore.adapter.OrderAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.Order;
import com.chuthuong.lthstore.model.OrderItem;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.response.CartResponse;
import com.chuthuong.lthstore.response.OrderResponse;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.utils.Util;
import com.chuthuong.lthstore.widget.CustomProgressDialog;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    private ImageView back, backToHome;
    private UserReaderSqlite userReaderSqlite;
    private User userToken;
    private Order order;
    private RecyclerView recOrderItem;
    private MyItemOrderAdapter myItemOrderAdapter;
    private TextView nameDetailOrder, phoneDetailOrder, addressDetailOrder;
    private TextView btnCancel, btnReview, btnReOrder;
    private TextView quantityItem, txtTotal, totalPrice, totalPriceItem, priceShipping, reasonCancel;
    private ConstraintLayout layoutPrice;
    private TextView idOrder, statusOrder;
    private TextView dateOrder, dateConfirm, dateDelivery, dateDelivered, dateCanceled;
    private TextView txtDateConfirm, txtDateDelivery, txtDateDelivered, txtDateCanceled;
    private CustomProgressDialog dialogAddItem;
    private int numberLoop;
    private String reasonCancelChecked = "";
    private CustomProgressDialog dialogCancel;
    private TextView btnContactWithShop;
    private String orderID;
    private NestedScrollView nestedScrollViewItem;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        orderID = getIntent().getStringExtra("order_id");
        userReaderSqlite = new UserReaderSqlite(this, "user.db", null, 1);
        Util.refreshToken(this);
        userToken = userReaderSqlite.getUser();
        addControls();
        addEvents();
    }

    private void callApiGetAnOrderMe(String accessToken, String orderID) {
        String token = "Bearer " + accessToken;
        ApiService.apiService.getAnOrderMe(token, orderID).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful()) {
                    order = response.body().getOrder();
                    loadData(order);
                } else {
                    Gson gson = new Gson();
                    try {
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                setToast(OrderDetailActivity.this, "Lỗi server");
            }
        });
    }

    private void loadData(Order order) {
        if(order.getOrderItems().size()>=3) {
            nestedScrollViewItem.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 550));
        }
        recOrderItem.setLayoutManager(new LinearLayoutManager(OrderDetailActivity.this, RecyclerView.VERTICAL, false));
        myItemOrderAdapter = new MyItemOrderAdapter(OrderDetailActivity.this, order);
        recOrderItem.setAdapter(myItemOrderAdapter);
        myItemOrderAdapter.notifyDataSetChanged();
        nameDetailOrder.setText(order.getShippingInfo().getFullName());
        phoneDetailOrder.setText(order.getShippingInfo().getPhone());
        addressDetailOrder.setText(order.getShippingInfo().getAddress() + ", "
                + order.getShippingInfo().getWard() + ", " + order.getShippingInfo().getDistrict() + ", "
                + order.getShippingInfo().getProvince());
        NumberFormat formatter = new DecimalFormat("#,###");
        String formatterTotalPriceItem = formatter.format(order.getTotalPrice());
        totalPriceItem.setText(formatterTotalPriceItem + " đ");
        String formatterPriceShipping = formatter.format(order.getShippingPrice());
        priceShipping.setText(formatterPriceShipping + " đ");
        String formatterTotalPrice = formatter.format(order.getTotalPrice() + order.getShippingPrice());
        totalPrice.setText(formatterTotalPrice + " đ");
        quantityItem.setText(order.getOrderItems().size() + "");
        idOrder.setText(order.getId());
        switch (order.getOrderStatus()) {
            case "Shipping":
                statusOrder.setText("Đang vận chuyển");
                statusOrder.setTextColor(getResources().getColor(R.color.shipping));
                break;
            case "Delivery":
                statusOrder.setText("Đang giao");
                statusOrder.setTextColor(getResources().getColor(R.color.delivery));
                break;
            case "Delivered":
                statusOrder.setText("Đã giao");
                statusOrder.setTextColor(getResources().getColor(R.color.delivered));
                break;
            case "Canceled":
                statusOrder.setText("Đã hủy");
                statusOrder.setTextColor(getResources().getColor(R.color.orange));
                break;
            case "Processing":
            default:
                statusOrder.setText("Chờ xác nhận");
                statusOrder.setTextColor(getResources().getColor(R.color.processing));
                break;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String fmtDateOrder = sdf.format(order.getCreatedAt());
        dateOrder.setText(fmtDateOrder);
        if (order.getOrderStatus().equals("Canceled")) {
            reasonCancel.setVisibility(View.VISIBLE);
            if (order.getCanceledReason() != null) {
                reasonCancel.setText("Lý do hủy: " + order.getCanceledReason());
            }
            String fmtDateCanceled = sdf.format(order.getCanceledAt());
            txtDateCanceled.setVisibility(View.VISIBLE);
            dateCanceled.setVisibility(View.VISIBLE);
            dateCanceled.setText(fmtDateCanceled);
        }

        if (order.getOrderStatus().equals("Processing")) {
            btnCancel.setVisibility(View.VISIBLE);
        }
        if (order.isCommented() || order.getOrderStatus().equals("Canceled")) {
            btnReOrder.setVisibility(View.VISIBLE);
        }
        if (order.getOrderStatus().equals("Shipping")) {
            // thời gian giao hàng cho vận chuyển
            String fmtDateShipping = sdf.format(order.getShippingAt());
            dateConfirm.setVisibility(View.VISIBLE);
            dateConfirm.setText(fmtDateShipping);
            txtDateConfirm.setVisibility(View.VISIBLE);
            btnContactWithShop.setVisibility(View.VISIBLE);
        }
        if (order.getOrderStatus().equals("Delivery")) {
            // thời gian giao hàng
            String fmtDateDelivery = sdf.format(order.getDeliveryAt());
            dateDelivery.setVisibility(View.VISIBLE);
            dateDelivery.setText(fmtDateDelivery);
            txtDateDelivery.setVisibility(View.VISIBLE);

            // thời gian giao hàng cho vận chuyển
            String fmtDateShipping = sdf.format(order.getShippingAt());
            dateConfirm.setVisibility(View.VISIBLE);
            dateConfirm.setText(fmtDateShipping);
            txtDateConfirm.setVisibility(View.VISIBLE);
            btnContactWithShop.setVisibility(View.VISIBLE);
        }
        if (order.getOrderStatus().equals("Delivered")) {
            if (order.isCommented()) {
                btnReview.setVisibility(View.GONE);
                btnReOrder.setVisibility(View.VISIBLE);
            } else {
                btnReview.setVisibility(View.VISIBLE);
                btnReOrder.setVisibility(View.GONE);
            }
            // thời gian giao hàng thành công
            String fmtDateDelivered = sdf.format(order.getDeliveredAt());
            dateDelivered.setVisibility(View.VISIBLE);
            dateDelivered.setText(fmtDateDelivered);
            txtDateDelivered.setVisibility(View.VISIBLE);

            // thời gian giao hàng
            String fmtDateDelivery = sdf.format(order.getDeliveryAt());
            dateDelivery.setVisibility(View.VISIBLE);
            dateDelivery.setText(fmtDateDelivery);
            txtDateDelivery.setVisibility(View.VISIBLE);

            // thời gian giao hàng cho vận chuyển
            String fmtDateShipping = sdf.format(order.getShippingAt());
            dateConfirm.setVisibility(View.VISIBLE);
            dateConfirm.setText(fmtDateShipping);
            txtDateConfirm.setVisibility(View.VISIBLE);
        }
    }

    private void setToast(Activity activity, String msg) {
        Toast toast = new Toast(activity);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_toast, (ViewGroup) activity.findViewById(R.id.layout_toast));
        TextView message = view.findViewById(R.id.message_toast);
        message.setText(msg);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    private void addEvents() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderDetailActivity.this, MainActivity.class));
            }
        });
        totalPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutPrice.getVisibility() == View.GONE) {
                    layoutPrice.setVisibility(View.VISIBLE);
                    totalPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_keyboard_arrow_up_24, 0);
                } else {
                    layoutPrice.setVisibility(View.GONE);
                    totalPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_keyboard_arrow_down_24, 0);
                }
            }
        });
        txtTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutPrice.getVisibility() == View.GONE) {
                    layoutPrice.setVisibility(View.VISIBLE);
                    totalPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_keyboard_arrow_up_24, 0);
                } else {
                    layoutPrice.setVisibility(View.GONE);
                    totalPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_keyboard_arrow_down_24, 0);
                }
            }
        });
        btnReOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogReOrder();
            }
        });
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this, ReviewProductActivity.class);
                intent.putExtra("order_id", order.getId());
                startActivity(intent);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogCancel();
            }
        });
        btnContactWithShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToast(OrderDetailActivity.this, "Chức năng chat với shop đang cập nhật !");
            }
        });
    }

    private void openDialogCancel() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_cancel_order);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;
        window.setAttributes(windowAttributes);
        if (Gravity.BOTTOM == Gravity.BOTTOM) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }
        EditText edtReason = dialog.findViewById(R.id.edt_reason);
        AppCompatRadioButton rad1, rad2, rad3, rad4, rad5, rad6, rad7;
        rad1 = dialog.findViewById(R.id.rad_1);
        rad2 = dialog.findViewById(R.id.rad_2);
        rad3 = dialog.findViewById(R.id.rad_3);
        rad4 = dialog.findViewById(R.id.rad_4);
        rad5 = dialog.findViewById(R.id.rad_5);
        rad6 = dialog.findViewById(R.id.rad_6);
        rad7 = dialog.findViewById(R.id.rad_7);
        RadioGroup radioGroup = dialog.findViewById(R.id.rad_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rad_1:
                        reasonCancelChecked = rad1.getText().toString();
                        edtReason.setVisibility(View.GONE);
                        break;

                    case R.id.rad_2:
                        reasonCancelChecked = rad2.getText().toString();
                        edtReason.setVisibility(View.GONE);
                        break;
                    case R.id.rad_3:
                        reasonCancelChecked = rad3.getText().toString();
                        edtReason.setVisibility(View.GONE);
                        break;
                    case R.id.rad_4:
                        reasonCancelChecked = rad4.getText().toString();
                        edtReason.setVisibility(View.GONE);
                        break;
                    case R.id.rad_5:
                        reasonCancelChecked = rad5.getText().toString();
                        edtReason.setVisibility(View.GONE);
                        break;
                    case R.id.rad_6:
                        reasonCancelChecked = rad6.getText().toString();
                        edtReason.setVisibility(View.GONE);
                        break;
                    case R.id.rad_7:
                        edtReason.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        // xử lý mua lại
        TextView btnConfirm = dialog.findViewById(R.id.btn_confirm_cancel);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checked = radioGroup.getCheckedRadioButtonId();
                if (checked > 0) {
                    if (edtReason.getVisibility() == View.VISIBLE && edtReason.getText().toString() != null) {
                        reasonCancelChecked = edtReason.getText().toString();
                    }
                    dialogCancel = new CustomProgressDialog(OrderDetailActivity.this);
                    dialogCancel.show();
                    callApiCancelOrder(userToken.getAccessToken(), order.getId(), reasonCancelChecked);
                }
            }
        });
        dialog.show();
    }

    private void callApiCancelOrder(String accessToken, String id, String reason) {
        String token = "Bearer " + accessToken;
        ApiService.apiService.cancelOrder(token, id, reason).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful()) {
                    dialogCancel.dismiss();
                    finish();
                } else {
                    dialogCancel.dismiss();
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(OrderDetailActivity.this, apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                setToast(OrderDetailActivity.this, "Lỗi server !");
            }
        });
    }

    private void openDialogReOrder() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_re_order);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;
        window.setAttributes(windowAttributes);
        if (Gravity.BOTTOM == Gravity.BOTTOM) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }
        NestedScrollView nestedScrollView = dialog.findViewById(R.id.nestedScrollView_item_re_order);
        if(order.getOrderItems().size()>=2) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 550);
            nestedScrollView.setLayoutParams(layoutParams);
        }
        // load adapter
        RecyclerView recItemReOder = dialog.findViewById(R.id.rec_order_item_re_order);
        recItemReOder.setLayoutManager(new LinearLayoutManager(OrderDetailActivity.this, RecyclerView.VERTICAL, false));
        myItemOrderAdapter = new MyItemOrderAdapter(OrderDetailActivity.this, order);
        recItemReOder.setAdapter(myItemOrderAdapter);
        myItemOrderAdapter.notifyDataSetChanged();

        // xử lý tắt dialog
        ImageView closeDialog = dialog.findViewById(R.id.close_dialog_re_order);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // xử lý mua lại
        TextView btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddItem = new CustomProgressDialog(OrderDetailActivity.this);
                dialogAddItem.show();
                numberLoop = 0;
                for (int i = 0; i < order.getOrderItems().size(); i++) {
                    OrderItem orderItem = order.getOrderItems().get(i);
                    int quantity = orderItem.getQuantity();
                    callApiAddItemToCart(userToken.getAccessToken(), orderItem.getProduct(), orderItem.getSize(), orderItem.getColor(), quantity);
                }

            }
        });
        dialog.show();
    }

    private void callApiAddItemToCart(String accessToken, String productID, String size, String color, int quantity) {
        String token = "Bearer " + accessToken;
        Log.e("toke", size + ", " + color + ", " + quantity);
        String accept = "application/json;versions=1";
        ApiService.apiService.addItemToCart(accept, token, productID, size, color, quantity).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    numberLoop += 1;
                    if (numberLoop == order.getOrderItems().size()) {
                        dialogAddItem.dismiss();
                        finish();
                        startActivity(new Intent(OrderDetailActivity.this, MyCartActivity.class));
                    }
                } else {
                    dialogAddItem.dismiss();
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(OrderDetailActivity.this, apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                setToast(OrderDetailActivity.this, "Lỗi server !");
            }
        });
    }

    private void addControls() {
        back = findViewById(R.id.img_back_detail);
        backToHome = findViewById(R.id.back_to_home);
        recOrderItem = findViewById(R.id.rec_order_item);
        nameDetailOrder = findViewById(R.id.txt_name_detail_order);
        phoneDetailOrder = findViewById(R.id.txt_phone_detail_order);
        addressDetailOrder = findViewById(R.id.txt_address_detail_order);
        reasonCancel = findViewById(R.id.txt_reason_cancel);
        btnCancel = findViewById(R.id.btn_cancel_order_detail);
        btnReview = findViewById(R.id.btn_review_order_detail);
        btnReOrder = findViewById(R.id.btn_re_order_detail);
        quantityItem = findViewById(R.id.quantity_item);
        txtTotal = findViewById(R.id.txt_total);
        totalPrice = findViewById(R.id.total_price);
        totalPriceItem = findViewById(R.id.total_price_item);
        priceShipping = findViewById(R.id.price_shipping);
        layoutPrice = findViewById(R.id.layout_price);
        idOrder = findViewById(R.id.id_order);
        statusOrder = findViewById(R.id.status_order);
        dateOrder = findViewById(R.id.time_order);
        dateConfirm = findViewById(R.id.time_confirm);
        txtDateConfirm = findViewById(R.id.txt_time_confirm);
        dateDelivery = findViewById(R.id.time_delivery);
        txtDateDelivery = findViewById(R.id.txt_time_delivery);
        dateDelivered = findViewById(R.id.time_delivered);
        txtDateDelivered = findViewById(R.id.txt_time_delivered);
        dateCanceled = findViewById(R.id.time_canceled);
        txtDateCanceled = findViewById(R.id.txt_time_canceled);
        btnContactWithShop = findViewById(R.id.btn_contact_with_shop);
        nestedScrollViewItem = findViewById(R.id.nestedScrollView_item);


    }

    @Override
    protected void onStart() {
        super.onStart();
        callApiGetAnOrderMe(userToken.getAccessToken(), orderID);
    }
}