package com.chuthuong.lthstore.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.MyCartActivity;
import com.chuthuong.lthstore.activities.ReviewProductActivity;
import com.chuthuong.lthstore.activities.detailActivities.OrderDetailActivity;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.Cart;
import com.chuthuong.lthstore.model.Order;
import com.chuthuong.lthstore.model.OrderItem;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.response.CartResponse;
import com.chuthuong.lthstore.response.ListOrderResponse;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.utils.Util;
import com.chuthuong.lthstore.widget.CustomProgressDialog;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    Context context;
    ListOrderResponse listOrder;
    MyItemOrderAdapter myItemOrderAdapter;
    private CustomProgressDialog dialogAddItem;
    private int numberLoop;
    private UserReaderSqlite userReaderSqlite;
    private User userToken;
    private LayoutInflater inflater;
    private ViewGroup viewGroup;
    private Dialog dialogReOrder;

    public OrderAdapter(Context context, ListOrderResponse listOrder) {
        this.context = context;
        this.listOrder = listOrder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        viewGroup = parent;
        return new OrderAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = listOrder.getOrders().get(position);
        holder.recItemOrder.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        myItemOrderAdapter = new MyItemOrderAdapter(context, order);
        holder.recItemOrder.setAdapter(myItemOrderAdapter);
        myItemOrderAdapter.notifyDataSetChanged();
        holder.txtNameOrder.setText(order.getId());
        holder.txtQuantityOrder.setText(order.getOrderItems().size() + "");
        switch (order.getOrderStatus()) {
            case "Shipping":
                holder.txtStatusOrder.setText("Đang vận chuyển");
                holder.txtStatusOrder.setTextColor(context.getResources().getColor(R.color.shipping));
                holder.btnReview.setVisibility(View.GONE);
                holder.btnReOrder.setVisibility(View.GONE);
                holder.btnCancelOrder.setVisibility(View.GONE);
                break;
            case "Delivery":
                holder.txtStatusOrder.setText("Đang giao");
                holder.txtStatusOrder.setTextColor(context.getResources().getColor(R.color.delivery));
                break;
            case "Delivered":
                holder.txtStatusOrder.setText("Đã giao");
                holder.txtStatusOrder.setTextColor(context.getResources().getColor(R.color.delivered));
                if (order.isCommented()) {
                    holder.btnReOrder.setVisibility(View.VISIBLE);
                    holder.btnReview.setVisibility(View.GONE);
                } else {
                    holder.btnReOrder.setVisibility(View.GONE);
                    holder.btnReview.setVisibility(View.VISIBLE);
                }
                holder.btnCancelOrder.setVisibility(View.GONE);
                break;

            case "Canceled":
                holder.txtStatusOrder.setText("Đã hủy");
                holder.txtStatusOrder.setTextColor(context.getResources().getColor(R.color.cancel_order));
                holder.btnCancelOrder.setVisibility(View.GONE);
                holder.btnReview.setVisibility(View.GONE);
                holder.btnReOrder.setVisibility(View.VISIBLE);

                break;
            case "Processing":
            default:
                holder.txtStatusOrder.setText("Chờ xác nhận");
                holder.txtStatusOrder.setTextColor(context.getResources().getColor(R.color.processing));
                holder.btnCancelOrder.setVisibility(View.GONE);
                holder.btnReview.setVisibility(View.GONE);
                holder.btnReOrder.setVisibility(View.GONE);
                break;
        }
        NumberFormat formatter = new DecimalFormat("#,###");
        String formatTotalPriceOrder = formatter.format(order.getTotalPrice()+order.getShippingPrice());
        holder.txtTotalPriceOrder.setText(formatTotalPriceOrder + "đ");
        holder.btnReOrder.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                userReaderSqlite = new UserReaderSqlite(context, "user.db", null, 1);
                Util.refreshToken(context);
                userToken = userReaderSqlite.getUser();
                openDialogReOrder(order);
            }
        });
        holder.btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "hủy đơn", Toast.LENGTH_SHORT).show();
            }
        });
        holder.btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "đánh giá", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ReviewProductActivity.class);
                intent.putExtra("order_id",  order.getId());
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("order_id", order.getId());
                context.startActivity(intent);
            }
        });
    }
    private void openDialogReOrder(Order order) {
        dialogReOrder = new Dialog(context);
        dialogReOrder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogReOrder.setContentView(R.layout.layout_dialog_re_order);
        Window window = dialogReOrder.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;
        window.setAttributes(windowAttributes);
        if (Gravity.BOTTOM == Gravity.BOTTOM) {
            dialogReOrder.setCancelable(true);
        } else {
            dialogReOrder.setCancelable(false);
        }

        // load adapter
        RecyclerView recItemReOder = dialogReOrder.findViewById(R.id.rec_order_item_re_order);
        recItemReOder.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        myItemOrderAdapter = new MyItemOrderAdapter(context, order);
        recItemReOder.setAdapter(myItemOrderAdapter);
        myItemOrderAdapter.notifyDataSetChanged();

        // xử lý tắt dialog
        ImageView closeDialog = dialogReOrder.findViewById(R.id.close_dialog_re_order);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogReOrder.dismiss();
            }
        });

        // xử lý mua lại
        TextView btnOk = dialogReOrder.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddItem = new CustomProgressDialog(context);
                dialogAddItem.show();
                numberLoop = 0;
                for (int i = 0; i < order.getOrderItems().size(); i++) {
                    OrderItem orderItem = order.getOrderItems().get(i);
                    callApiAddItemToCart(order,userToken.getAccessToken(), orderItem.getProduct(), orderItem.getSize(), orderItem.getColor(), orderItem.getQuantity());
                }

            }
        });
        dialogReOrder.show();
    }

    private void callApiAddItemToCart(Order order,String accessToken, String productID, String size, String color, int quantity) {
        String token = "Bearer "+accessToken;
        String accept = "application/json;versions=1";
        ApiService.apiService.addItemToCart(accept, token, productID, size, color, quantity).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    numberLoop +=1;
                    if(numberLoop==order.getOrderItems().size()) {
                        dialogAddItem.dismiss();
                        context.startActivity(new Intent(context, MyCartActivity.class));
                        dialogReOrder.dismiss();
                    }
                } else {
                    dialogAddItem.dismiss();
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(context, apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                setToast(context, "Lỗi server !");
            }
        });
    }
    private void setToast(Context context, String msg) {
        Toast toast = new Toast(context);
        View view = inflater.inflate(R.layout.custom_toast, (ViewGroup) viewGroup.findViewById(R.id.layout_toast));
        TextView message = view.findViewById(R.id.message_toast);
        message.setText(msg);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public int getItemCount() {

        if (listOrder.getOrders() != null && listOrder != null) {
            return listOrder.getOrders().size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recItemOrder;
        TextView txtNameOrder, txtQuantityOrder, txtTotalPriceOrder, txtStatusOrder, btnReview, btnReOrder, btnCancelOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recItemOrder = itemView.findViewById(R.id.rec_item_order);
            txtNameOrder = itemView.findViewById(R.id.txt_name_order);
            txtQuantityOrder = itemView.findViewById(R.id.txt_quantity_product_order);
            txtTotalPriceOrder = itemView.findViewById(R.id.txt_total_price_order);
            txtStatusOrder = itemView.findViewById(R.id.txt_status_order);
            btnReview = itemView.findViewById(R.id.txt_review);
            btnReOrder = itemView.findViewById(R.id.txt_re_order);
            btnCancelOrder = itemView.findViewById(R.id.txt_cancel_order);
        }
    }
}
