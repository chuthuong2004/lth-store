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

public class NewProductsAdapter extends RecyclerView.Adapter<NewProductsAdapter.ViewHolder> {

    private Context context;
    private ListProduct productList;

    public NewProductsAdapter(Context context, ListProduct productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public NewProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.new_products, parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull NewProductsAdapter.ViewHolder holder, int position) {
        Product product = productList.getProducts().get(position);
        Glide.with(context).load(product.getImages().get(0).getImg()).into(holder.newImg);
        holder.newName.setText(product.getName());

        NumberFormat formatter = new DecimalFormat("#,###");
        String formatterPriceProduct = formatter.format(product.getPrice());
        holder.newPrice.setText(formatterPriceProduct+"đ");
    }

    @Override
    public int getItemCount() {
        return productList.getProducts().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView newImg;
        TextView newName, newPrice;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            newImg = itemView.findViewById(R.id.new_img);
            newName = itemView.findViewById(R.id.new_product_name);
            newPrice = itemView.findViewById(R.id.new_price);
        }
    }
}
