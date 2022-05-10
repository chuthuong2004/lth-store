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
import com.chuthuong.lthstore.model.ListProduct;
import com.chuthuong.lthstore.model.Product;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FlashSaleProductAdapter extends RecyclerView.Adapter<FlashSaleProductAdapter.ViewHolder> {
    private Context context;
    private ListProduct listProduct;

    public FlashSaleProductAdapter(Context context, ListProduct listProduct) {
        this.context = context;
        this.listProduct = listProduct;
    }

    @NonNull
    @Override
    public FlashSaleProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FlashSaleProductAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.flash_sale_product_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = listProduct.getProducts().get(position);
        Glide.with(context).load(product.getImages().get(0).getImg()).into(holder.imageView);
        holder.name.setText(product.getName());
        holder.discount.setText("-" + product.getDiscount() + "%");
        NumberFormat formatter = new DecimalFormat("#,###");
        String formatterPriceProduct = formatter.format(product.getPrice() - (product.getPrice() * product.getDiscount() / 100));
        holder.price.setText(formatterPriceProduct + "đ");
        holder.likeCount.setText(product.getLikeCount() + "");
        holder.quantitySold.setText(product.getQuantitySold()+"");
        if (product.getDiscount() != 0) {
            String formatterCurrentPriceProduct = formatter.format(product.getPrice());
            holder.currentPrice.setText(formatterCurrentPriceProduct + "đ");
        } else {
            holder.currentPrice.setText("");
        }
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
        TextView name, currentPrice, price, discount, likeCount, quantitySold;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.flash_sale_product_img);
            name = itemView.findViewById(R.id.flash_sale_product_name);
            price = itemView.findViewById(R.id.flash_sale_product_price);
            discount = itemView.findViewById(R.id.flash_sale_product_discount);
            currentPrice = itemView.findViewById(R.id.flash_sale_product_current_price);
            likeCount = itemView.findViewById(R.id.flash_sale_product_like_count);
            quantitySold = itemView.findViewById(R.id.flash_sale_product_sold);
        }
    }
}
