package com.chuthuong.lthstore.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.detailActivities.ProductDetailActivity;
import com.chuthuong.lthstore.model.ListProduct;
import com.chuthuong.lthstore.model.Product;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class PopularProductAdapter extends RecyclerView.Adapter<PopularProductAdapter.ViewHolder> {
    private Context context;
    private ListProduct listProduct;

    public PopularProductAdapter(Context context, ListProduct listProduct) {
        this.context = context;
        this.listProduct = listProduct;
    }

    @NonNull
    @Override
    public PopularProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_product_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PopularProductAdapter.ViewHolder holder, int position) {
        Product product = listProduct.getProducts().get(position);
        Glide.with(context).load(product.getImages().get(0).getImg()).into(holder.imageView);
        holder.name.setText(product.getName());
        holder.discount.setText("-" + product.getDiscount() + "%");
        NumberFormat formatter = new DecimalFormat("#,###");
        String formatterPriceProduct = formatter.format(product.getPrice() - (product.getPrice() * product.getDiscount() / 100));
        holder.price.setText(formatterPriceProduct + "đ");
        holder.likeCount.setText(product.getLikeCount()+"");
        holder.quantitySold.setText(product.getQuantitySold()+"");
        if (product.getDiscount() != 0) {
            String formatterCurrentPriceProduct = formatter.format(product.getPrice());
            holder.currentPrice.setText(formatterCurrentPriceProduct + "đ");
        }
        else {
            holder.currentPrice.setText("");
        }
        Float rate = product.getRate(); /// 3.2 * 10
        holder.ratingBar.setRating(rate);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product_detail", product);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listProduct != null) {
            return listProduct.getProducts().size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        RatingBar ratingBar;
        TextView name, currentPrice, price, discount, likeCount, quantitySold;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.popular_product_img);
            name = itemView.findViewById(R.id.popular_product_name);
            price = itemView.findViewById(R.id.popular_product_price);
            discount = itemView.findViewById(R.id.popular_product_discount);
            currentPrice = itemView.findViewById(R.id.popular_product_current_price);
            likeCount = itemView.findViewById(R.id.popular_product_like_count);
            quantitySold = itemView.findViewById(R.id.popular_product_sold);
            ratingBar = itemView.findViewById(R.id.popular_product_rating_bar);
        }
    }
}
