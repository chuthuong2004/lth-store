package com.chuthuong.lthstore.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.chuthuong.lthstore.response.ListProductResponse;
import com.chuthuong.lthstore.model.Product;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class SameProductDetailAdapter extends RecyclerView.Adapter<SameProductDetailAdapter.ViewHolder> {
    private Context context;
    private ListProductResponse listProductResponse;

    public SameProductDetailAdapter(Context context, ListProductResponse listProductResponse) {
        this.context = context;
        this.listProductResponse = listProductResponse;
    }

    @NonNull
    @Override
    public SameProductDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SameProductDetailAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.same_product_detail_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SameProductDetailAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Product product = listProductResponse.getProducts().get(position);
        Glide.with(context).load(product.getImages().get(0).getImg()).into(holder.imageView);
        holder.name.setText(product.getName());
        holder.discount.setText("-" + product.getDiscount() + "%");
        NumberFormat formatter = new DecimalFormat("#,###");
        String formatterPriceProduct = formatter.format(product.getPrice() - (product.getPrice() * product.getDiscount() / 100));
        holder.price.setText(formatterPriceProduct + "đ");
        holder.likeCount.setText(product.getLikeCount() + "");
        holder.quantitySold.setText(product.getQuantitySold() + "");
        Float rate = product.getRate();
        holder.ratingBar.setRating(rate);
        if (product.getDiscount() != 0) {
            String formatterCurrentPriceProduct = formatter.format(product.getPrice());
            holder.currentPrice.setText(formatterCurrentPriceProduct + "đ");
        } else {
            holder.currentPrice.setText("");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listProductResponse != null) {
            return listProductResponse.getProducts().size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        RatingBar ratingBar;
        TextView name, currentPrice, price, discount, likeCount, quantitySold;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_same_product_item);
            name = itemView.findViewById(R.id.name_product_same_detail);
            price = itemView.findViewById(R.id.same_product_price);
            discount = itemView.findViewById(R.id.same_product_discount);
            currentPrice = itemView.findViewById(R.id.same_product_current_price);
            likeCount = itemView.findViewById(R.id.same_product_like_count);
            quantitySold = itemView.findViewById(R.id.same_product_sold);
            ratingBar = itemView.findViewById(R.id.same_product_rating_bar);
        }
    }
}
