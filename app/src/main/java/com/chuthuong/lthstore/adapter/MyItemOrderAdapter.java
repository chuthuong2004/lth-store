package com.chuthuong.lthstore.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.ReviewProductActivity;
import com.chuthuong.lthstore.activities.detailActivities.OrderDetailActivity;
import com.chuthuong.lthstore.model.Order;
import com.chuthuong.lthstore.model.OrderItem;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MyItemOrderAdapter extends RecyclerView.Adapter<MyItemOrderAdapter.ViewHolder> {
    private Context context;
    private Order order;

    public MyItemOrderAdapter(Context context, Order order) {
        this.context = context;
        this.order = order;
    }

    @NonNull
    @Override
    public MyItemOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyItemOrderAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyItemOrderAdapter.ViewHolder holder, int position) {
        OrderItem orderItem = order.getOrderItems().get(position);
        Glide.with(context).load(orderItem.getImage()).into(holder.imageViewItemOrder);
        holder.nameItemOrder.setText(orderItem.getName());
        holder.quantityItemOrder.setText(orderItem.getQuantity()+"");
        holder.txtSizeColor.setText(orderItem.getSize()+", "+ orderItem.getColor());
        NumberFormat formatter = new DecimalFormat("#,###");
        String formatTotalCurrentPriceOrder = formatter.format(orderItem.getPrice());
        holder.currentPrice.setText(formatTotalCurrentPriceOrder+"đ");
        String formatTotalPriceOrder = formatter.format(orderItem.getPrice()-(orderItem.getPrice()*orderItem.getDiscount()/100));
        holder.priceItemOrder.setText(formatTotalPriceOrder+"đ");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("order_id", order.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if ( order.getOrderItems() != null) {
            return  order.getOrderItems().size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewItemOrder;
        private TextView nameItemOrder, priceItemOrder, quantityItemOrder, txtSizeColor, currentPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewItemOrder = itemView.findViewById(R.id.image_order_item);
            nameItemOrder = itemView.findViewById(R.id.txt_order_item_name);
            priceItemOrder = itemView.findViewById(R.id.txt_price_item_order);
            quantityItemOrder = itemView.findViewById(R.id.txt_quantity_item_order);
            txtSizeColor = itemView.findViewById(R.id.txt_size_color);
            currentPrice = itemView.findViewById(R.id.txt_price_current_item_order);

        }
    }
}
