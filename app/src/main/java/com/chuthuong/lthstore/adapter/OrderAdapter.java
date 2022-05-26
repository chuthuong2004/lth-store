package com.chuthuong.lthstore.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.ReviewProductActivity;
import com.chuthuong.lthstore.model.Cart;
import com.chuthuong.lthstore.model.Order;
import com.chuthuong.lthstore.response.ListOrderResponse;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    Context context;
    ListOrderResponse listOrder;
    MyItemOrderAdapter myItemOrderAdapter;
    Cart cart;

    public OrderAdapter(Context context, ListOrderResponse listOrder) {
        this.context = context;
        this.listOrder = listOrder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = listOrder.getOrders().get(position);
        holder.recItemOrder.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        myItemOrderAdapter = new MyItemOrderAdapter(context, order);
        holder.recItemOrder.setAdapter(myItemOrderAdapter);
        myItemOrderAdapter.notifyDataSetChanged();
        holder.txtNameOrder.setText(order.getUser());
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
        String formatTotalPriceOrder = formatter.format(order.getTotalPrice());
        holder.txtTotalPriceOrder.setText(formatTotalPriceOrder + "đ");
        holder.btnReOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "mua lại", Toast.LENGTH_SHORT).show();
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
