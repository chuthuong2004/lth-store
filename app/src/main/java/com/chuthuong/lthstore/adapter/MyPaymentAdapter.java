package com.chuthuong.lthstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.model.Cart;
import com.chuthuong.lthstore.model.CartItem;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MyPaymentAdapter extends RecyclerView.Adapter<MyPaymentAdapter.ViewHolder> {

    private Context context;
    private Cart cart;

    public MyPaymentAdapter(Context context, Cart cart) {
        this.context = context;
        this.cart = cart;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyPaymentAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_payment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItems = cart.getCartItems().get(position);
        Glide.with(context).load(cartItems.getProduct().getImages().get(0).getImg()).into(holder.imageViewItemCartPayment);
        holder.nameItemCartPayment.setText(cartItems.getProduct().getName());
        holder.colorItemCartPayment.setText(cartItems.getColor());
        holder.sizeItemCartPayment.setText(cartItems.getSize());
        holder.quantityItemCartPayment.setText(cartItems.getQuantity() + "");
        NumberFormat formatter = new DecimalFormat("#,###");
        String formatterPriceProduct = formatter.format(cartItems.getProduct().getPrice() - (cartItems.getProduct().getPrice() * cartItems.getProduct().getDiscount() / 100));
        holder.priceItemCartPayment.setText(formatterPriceProduct + "đ");
    }

    @Override
    public int getItemCount() {
        if (cart != null) {
            return cart.getCartItems().size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewItemCartPayment;
        private TextView nameItemCartPayment, colorItemCartPayment, priceItemCartPayment, sizeItemCartPayment, quantityItemCartPayment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewItemCartPayment = itemView.findViewById(R.id.img_cart_item_payment);
            nameItemCartPayment = itemView.findViewById(R.id.cart_item_name_payment);
            colorItemCartPayment = itemView.findViewById(R.id.cart_item_payment_color);
            priceItemCartPayment = itemView.findViewById(R.id.cart_item_payment_price);
            sizeItemCartPayment = itemView.findViewById(R.id.cart_item_payment_size);
            quantityItemCartPayment = itemView.findViewById(R.id.cart_item_payment_quantity);
        }
    }
}