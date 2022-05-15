package com.chuthuong.lthstore.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.MainActivity;
import com.chuthuong.lthstore.activities.MyCartActivity;
import com.chuthuong.lthstore.activities.authActivities.LoginActivity;
import com.chuthuong.lthstore.activities.detailActivities.ProductDetailActivity;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.Account;
import com.chuthuong.lthstore.model.Cart;
import com.chuthuong.lthstore.model.CartItem;
import com.chuthuong.lthstore.model.CartResponse;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.ApiToken;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {

    public Context context;
    Cart cart;
    public boolean isChanged = false;
    private MyCartActivity myCartActivity;
    public MyCartAdapter(Context context, Cart cart) {
        this.context = context;
        this.cart = cart;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        myCartActivity = (MyCartActivity) context;
        CartItem cartItems = cart.getCartItems().get(position);
        Glide.with(context).load(cartItems.getProduct().getImages().get(0).getImg()).into(holder.imgProductItem);
        holder.nameProductItem.setText(cartItems.getProduct().getName());
        holder.colorProductItem.setText(cartItems.getColor());
        holder.sizeProductItem.setText(cartItems.getSize());
        NumberFormat formatter = new DecimalFormat("#,###");
        String formatterPriceProduct = formatter.format(cartItems.getProduct().getPrice() - (cartItems.getProduct().getPrice() * cartItems.getProduct().getDiscount() / 100));
        holder.priceProductItem.setText(formatterPriceProduct + "đ");
        if (cartItems.getProduct().getDiscount() != 0) {
            String formatterCurrentPriceProduct = formatter.format(cartItems.getProduct().getPrice());
            holder.currentPriceProductItem.setText(formatterCurrentPriceProduct + "đ");
        } else {
            holder.currentPriceProductItem.setText("");
        }
        holder.quantityProductItem.setText(cartItems.getQuantity()+"");
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, ProductDetailActivity.class);
//                intent.putExtra("product_detail", cartItems.getProduct().);
//
//                context.startActivity(intent);
//            }
//        });
        holder.removeItemFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = MainActivity.getUser();
                if (user!=null) {
                    callApiRemoveItemFromCart(cartItems.getId(),"Bearer "+ user.getAccessToken(),holder.getAdapterPosition());
                    Toast.makeText(context.getApplicationContext(), "xóa item", Toast.LENGTH_SHORT).show();
                }else    {
                    Toast.makeText(context.getApplicationContext(), "Không có user", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void callApiRemoveItemFromCart(String id, String accessToken, int pos) {
        String accept = "application/json;versions=1";
        ApiService.apiService.removeItemFromCart(accept,id, accessToken).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    CartResponse cartResponse = response.body();
                    cart = cartResponse.getCart();
//                    isChanged = true;
                    myCartActivity.reloadAdapter(cart);
//                    notifyDataSetChanged();
                    Toast.makeText(context.getApplicationContext(), cartResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Log.e("Message", apiError.getMessage());
                        Toast.makeText(context.getApplicationContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Log.e("Lỗi server ", t.toString());
                Toast.makeText(context, "lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (cart.getCartItems()!=null) {
            return  cart.getCartItems().size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProductItem, removeItemFromCart;
        TextView nameProductItem, colorProductItem , sizeProductItem,currentPriceProductItem,priceProductItem, addQuantityProduct, minusQuantityProduct,quantityProductItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProductItem = itemView.findViewById(R.id.img_cart_item);
            nameProductItem = itemView.findViewById(R.id.cart_item_name);
            colorProductItem=itemView.findViewById(R.id.color_cart_item);
            sizeProductItem =itemView.findViewById(R.id.cart_item_size);
            currentPriceProductItem =itemView.findViewById(R.id.cart_product_current_price);
            priceProductItem =itemView.findViewById(R.id.cart_item_price);
            addQuantityProduct=itemView.findViewById(R.id.txt_add_quantity);
            minusQuantityProduct=itemView.findViewById(R.id.txt_minus_quantity);
            quantityProductItem=itemView.findViewById(R.id.txt_quantity_product);
            removeItemFromCart =itemView.findViewById(R.id.remove_item_from_cart);
        }
    }
}