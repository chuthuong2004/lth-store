package com.chuthuong.lthstore.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
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
import com.chuthuong.lthstore.widget.CustomProgressDialog;
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
    LayoutInflater inflater;
    ViewGroup viewGroup;
    User user = null;
    private MyCartActivity myCartActivity;
    private CustomProgressDialog dialogUpdate;
    private CustomProgressDialog dialogRemoveItem;

    public MyCartAdapter(Context context, Cart cart) {
        this.context = context;
        this.cart = cart;
    }
    private void setToast(Context context, String msg) {
        Toast toast = new Toast(context.getApplicationContext());
        View view = inflater.inflate(R.layout.custom_toast, viewGroup.findViewById(R.id.layout_toast));
        TextView message = view.findViewById(R.id.message_toast);
        message.setText(msg);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        viewGroup = parent;
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        user = MainActivity.getUser();
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
                if (user!=null) {
                    dialogRemoveItem = new CustomProgressDialog(context);
                    dialogRemoveItem.show();
                    callApiRemoveItemFromCart(cartItems.getId(),"Bearer "+ user.getAccessToken());
                }
            }
        });
        holder.addQuantityProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(holder.quantityProductItem.getText().toString()) + 1;
                holder.quantityProductItem.setText(quantity+"");
                dialogUpdate = new CustomProgressDialog(context);
                dialogUpdate.show();
                callApiUpdateQuantityCart("Bearer "+ user.getAccessToken(), cartItems.getId(),quantity);
            }
        });
        holder.minusQuantityProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(holder.quantityProductItem.getText().toString()) - 1;
                holder.quantityProductItem.setText(quantity+"");
                dialogUpdate = new CustomProgressDialog(context);
                dialogUpdate.show();
                callApiUpdateQuantityCart("Bearer "+ user.getAccessToken(), cartItems.getId(),quantity);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToast(context.getApplicationContext(), "click me");
            }
        });
    }
    private void callApiUpdateQuantityCart(String token, String id, int quantity) {
        String accept = "application/json;versions=1";
        ApiService.apiService.updateQuantityCart(accept,token,id,quantity).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    CartResponse cartResponse = response.body();
                    dialogUpdate.dismiss();
                    Log.e("Cart", cartResponse.getMessage() );
                    if(cartResponse.getCart()!=null) {
                        myCartActivity.reloadAdapter(cartResponse.getCart());
                    }else {
                        myCartActivity.hideLayoutCart();
                    }
//                    setToast(context.getApplicationContext(), cartResponse.getMessage());
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
//                        Log.e("Lỗi",response.errorBody()+"");
                        setToast(context.getApplicationContext(), apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                setToast(context.getApplicationContext(), "Lỗi server !");
            }
        });
    }

    private void callApiRemoveItemFromCart(String id, String accessToken) {
        String accept = "application/json;versions=1";
        ApiService.apiService.removeItemFromCart(accept,id, accessToken).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    CartResponse cartResponse = response.body();
                    dialogRemoveItem.dismiss();
                    if(cartResponse.getCart()!=null) {
                        myCartActivity.reloadAdapter(cartResponse.getCart());
                    }else {
                        myCartActivity.hideLayoutCart();
                    }
//                    setToast(context.getApplicationContext(), cartResponse.getMessage());
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(context.getApplicationContext(), apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                setToast(context.getApplicationContext(), "Lỗi server !");
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