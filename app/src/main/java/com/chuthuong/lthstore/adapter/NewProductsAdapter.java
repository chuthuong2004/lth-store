package com.chuthuong.lthstore.adapter;

import android.annotation.SuppressLint;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.authActivities.LoginActivity;
import com.chuthuong.lthstore.activities.detailActivities.ProductDetailActivity;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.response.ListProductResponse;
import com.chuthuong.lthstore.model.Product;
import com.chuthuong.lthstore.response.ProductResponse;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.utils.Util;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewProductsAdapter extends RecyclerView.Adapter<NewProductsAdapter.ViewHolder> {
    private Context context;
    private ListProductResponse listProductResponse;
    private UserReaderSqlite userReaderSqlite;
    private User user;
    private Product productReceive;

    public NewProductsAdapter(Context context, ListProductResponse listProductResponse) {
        this.context = context;
        this.listProductResponse = listProductResponse;
    }

    @NonNull
    @Override
    public NewProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewProductsAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.new_products, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull NewProductsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        userReaderSqlite = new UserReaderSqlite(context, "user.db", null, 1);
        Util.refreshToken(context);
        user = userReaderSqlite.getUser();
        Product product = listProductResponse.getProducts().get(position);
        productReceive = product;
        Glide.with(context).load(product.getImages().get(0).getImg()).into(holder.imageView);
        holder.name.setText(product.getName());
        holder.discount.setText("-" + product.getDiscount() + "%");
        NumberFormat formatter = new DecimalFormat("#,###");
        String formatterPriceProduct = formatter.format(product.getPrice() - (product.getPrice() * product.getDiscount() / 100));
        holder.price.setText(formatterPriceProduct + "đ");
        holder.quantitySold.setText(product.getQuantitySold() + "");
        Float rate = product.getRate();
        holder.ratingBar.setRating(rate);
        loadFavorite(product, holder);
        if (product.getDiscount() != 0) {
            String formatterCurrentPriceProduct = formatter.format(product.getPrice());
            holder.currentPrice.setText(formatterCurrentPriceProduct + "đ");
        } else {
            holder.currentPrice.setVisibility(View.GONE);
        }
        holder.imgFavoriteNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    if(holder.imgFavoriteNewProduct.getTag().equals("liked")) {
                        callApiRemoveFavorite(user.getAccessToken(), product.getId(), holder);
                    }else {
                        callApiAddFavorite(user.getAccessToken(), product.getId(), holder);
                    }
                } else {
                    openDialogRequestLogin();
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                context.startActivity(intent);
            }
        });
    }

    private void openDialogRequestLogin() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_login);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        if (Gravity.CENTER == Gravity.CENTER) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }
        TextView cancel = dialog.findViewById(R.id.dialog_cancel);
        TextView login = dialog.findViewById(R.id.dialog_login);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }
        });
        dialog.show();
    }

    private void callApiRemoveFavorite(String accessToken, String id, ViewHolder holder) {
        String token = "Bearer "+ accessToken;
        ApiService.apiService.removeFavorite(token, id).enqueue(new Callback<ProductResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if(response.isSuccessful()) {
                    productReceive = response.body().getProduct();
                    loadFavorite(productReceive, holder);
                }else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Toast.makeText(context, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(context, "Lỗi server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callApiAddFavorite(String accessToken, String id, ViewHolder holder) {
        String token = "Bearer "+ accessToken;
        ApiService.apiService.addFavorite(token, id).enqueue(new Callback<ProductResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if(response.isSuccessful()) {
                    productReceive = response.body().getProduct();
                    loadFavorite(productReceive, holder);
                }else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Toast.makeText(context, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(context, "Lỗi server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFavorite(Product product, ViewHolder holder) {
        holder.likeCount.setText(product.getLikeCount() + "");
        if(user!=null){
            if(product.getFavorites().contains(user.getId())) {
                holder.imgFavoriteNewProduct.setImageResource(R.drawable.ic_baseline_favorite_24);
                holder.imgFavoriteNewProduct.setTag("liked");
            }else {
                holder.imgFavoriteNewProduct.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                holder.imgFavoriteNewProduct.setTag("not favorite");
            }
        }
    }

    @Override
    public int getItemCount() {
        if (listProductResponse != null) {
            return listProductResponse.getProducts().size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, imgFavoriteNewProduct;
        RatingBar ratingBar;
        TextView name, currentPrice, price, discount, likeCount, quantitySold;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_new_product);
            name = itemView.findViewById(R.id.name_product_new);
            price = itemView.findViewById(R.id.new_product_price);
            discount = itemView.findViewById(R.id.new_product_discount);
            currentPrice = itemView.findViewById(R.id.new_product_current_price);
            likeCount = itemView.findViewById(R.id.new_product_like_count);
            quantitySold = itemView.findViewById(R.id.new_product_sold);
            ratingBar = itemView.findViewById(R.id.new_product_rating_bar);
            imgFavoriteNewProduct = itemView.findViewById(R.id.img_favorite_new_product);
        }
    }
}
