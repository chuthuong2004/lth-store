package com.chuthuong.lthstore.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.MainActivity;
import com.chuthuong.lthstore.activities.MyCartActivity;
import com.chuthuong.lthstore.activities.detailActivities.ProductDetailActivity;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.Cart;
import com.chuthuong.lthstore.model.CartItem;
import com.chuthuong.lthstore.response.CartResponse;
import com.chuthuong.lthstore.model.ProductDetail;
import com.chuthuong.lthstore.model.ProductDetailColor;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.widget.CustomProgressDialog;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {

    public Context context;
    Cart cart;
    List<CartItem> cartItems;
    LayoutInflater inflater;
    ViewGroup viewGroup;
    User user = null;
    private MyCartActivity myCartActivity;
    private CustomProgressDialog dialogUpdate;
    private CustomProgressDialog dialogRemoveItem;
    private UserReaderSqlite userReaderSqlite;

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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        userReaderSqlite = new UserReaderSqlite(context, "user.db", null, 1);
        user = userReaderSqlite.getUser();
        myCartActivity = (MyCartActivity) context;
        CartItem cartItem = cart.getCartItems().get(cart.getCartItems().size() - 1 - position);
        Glide.with(context).load(cartItem.getProduct().getImages().get(0).getImg()).into(holder.imgProductItem);
        holder.nameProductItem.setText(cartItem.getProduct().getName());
        holder.colorProductItem.setText(cartItem.getColor());
        holder.sizeProductItem.setText(cartItem.getSize());
        NumberFormat formatter = new DecimalFormat("#,###");
        String formatterPriceProduct = formatter.format(cartItem.getProduct().getPrice() - (cartItem.getProduct().getPrice() * cartItem.getProduct().getDiscount() / 100));
        holder.priceProductItem.setText(formatterPriceProduct + "đ");
        if (cartItem.getProduct().getDiscount() != 0) {
            String formatterCurrentPriceProduct = formatter.format(cartItem.getProduct().getPrice());
            holder.currentPriceProductItem.setText(formatterCurrentPriceProduct + "đ");
        } else {
            holder.currentPriceProductItem.setText("");
        }
        holder.quantityProductItem.setText(cartItem.getQuantity() + "");
        int quantity = Integer.parseInt(holder.quantityProductItem.getText().toString());
        if (quantity < 2) {
            holder.minusQuantityProduct.setEnabled(true);
            holder.minusQuantityProduct.setBackgroundTintList(context.getResources().getColorStateList(R.color.grey_2));
        }
        List<ProductDetailColor> productDetailColors = findProductDetail(cartItem.getProduct().getDetail(), cartItem.getSize());
        int amountProduct = findAmountBySizeColor(productDetailColors, cartItem.getColor());
        if (quantity >= amountProduct) {
            holder.addQuantityProduct.setEnabled(true);
            holder.maxQuantity.setVisibility(View.VISIBLE);
            holder.maxQuantity.setText("Chỉ còn " + amountProduct + " sản phẩm");
            holder.addQuantityProduct.setBackgroundTintList(context.getResources().getColorStateList(R.color.grey_2));
        } else {
            holder.maxQuantity.setVisibility(View.GONE);
        }

        holder.removeItemFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    dialogRemoveItem = new CustomProgressDialog(context);
                    dialogRemoveItem.show();
                    callApiRemoveItemFromCart(cartItem.getId(), "Bearer " + user.getAccessToken());
                }
            }
        });
        holder.addQuantityProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(holder.quantityProductItem.getText().toString());
                if (quantity < amountProduct) {
                    quantity += 1;
                    holder.quantityProductItem.setText(quantity + "");
                    dialogUpdate = new CustomProgressDialog(context);
                    dialogUpdate.show();
                    callApiUpdateQuantityCart("Bearer " + user.getAccessToken(), cartItem.getId(), quantity);
                }

            }
        });
        holder.minusQuantityProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(holder.quantityProductItem.getText().toString());
                if (quantity > 1) {
                    quantity -= 1;
                    holder.quantityProductItem.setText(quantity + "");
                    dialogUpdate = new CustomProgressDialog(context);
                    dialogUpdate.show();
                    callApiUpdateQuantityCart("Bearer " + user.getAccessToken(), cartItem.getId(), quantity);
                }

            }
        });
        holder.quantityProductItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    int qty = Integer.parseInt(holder.quantityProductItem.getText().toString());

                    if (qty >amountProduct) {
                        holder.maxQuantity.setVisibility(View.VISIBLE);
                        holder.maxQuantity.setText("Chỉ còn " + amountProduct + " sản phẩm");
                        holder.addQuantityProduct.setBackgroundTintList(context.getResources().getColorStateList(R.color.grey_2));
                        holder.quantityProductItem.setText(amountProduct + "");
                        holder.minusQuantityProduct.setBackgroundTintList(context.getResources().getColorStateList(R.color.pink));
                    }
                    else if (qty < 2) {
                        holder.maxQuantity.setVisibility(View.GONE);
                        holder.addQuantityProduct.setBackgroundTintList(context.getResources().getColorStateList(R.color.pink));
                        holder.minusQuantityProduct.setBackgroundTintList(context.getResources().getColorStateList(R.color.grey_2));
                    }
                    else if(qty< amountProduct && qty> 1) {
                        holder.maxQuantity.setVisibility(View.GONE);
                        holder.addQuantityProduct.setBackgroundTintList(context.getResources().getColorStateList(R.color.pink));
                        holder.minusQuantityProduct.setBackgroundTintList(context.getResources().getColorStateList(R.color.pink));
                    }
                }else {
                    holder.quantityProductItem.setText("1");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), ProductDetailActivity.class);
                intent.putExtra("product_id", cartItem.getProduct().getId());
                context.startActivity(intent);
            }
        });
    }

    private List<ProductDetailColor> findProductDetail(List<ProductDetail> productDetails, String query) {
        for (int i = 0; i < productDetails.size(); i++) {
            if (productDetails.get(i).getSize().equals(query)) {
                return productDetails.get(i).getDetailColor();
            }
        }
        return null;
    }

    private int findAmountBySizeColor(List<ProductDetailColor> productDetailColors, String query) {
        for (int i = 0; i < productDetailColors.size(); i++) {
            if (productDetailColors.get(i).getColor().equals(query)) {
                return productDetailColors.get(i).getAmount();
            }
        }
        return 0;
    }

    private void callApiUpdateQuantityCart(String token, String id, int quantity) {
        String accept = "application/json;versions=1";
        ApiService.apiService.updateQuantityCart(accept, token, id, quantity).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    CartResponse cartResponse = response.body();
                    dialogUpdate.dismiss();
                    Log.e("Cart", cartResponse.getMessage());
                    if (cartResponse.getCart() != null) {
                        myCartActivity.reloadAdapter(cartResponse.getCart());
                    } else {
                        myCartActivity.hideLayoutCart();
                    }
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        dialogUpdate.dismiss();
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
        ApiService.apiService.removeItemFromCart(accept, id, accessToken).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    CartResponse cartResponse = response.body();
                    dialogRemoveItem.dismiss();
                    if (cartResponse.getCart() != null) {
                        myCartActivity.reloadAdapter(cartResponse.getCart());
                    } else {
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
        if (cart.getCartItems() != null) {
            return cart.getCartItems().size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProductItem, removeItemFromCart;
        TextView nameProductItem, colorProductItem, sizeProductItem, currentPriceProductItem, priceProductItem,
                addQuantityProduct, minusQuantityProduct, maxQuantity;
        EditText quantityProductItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProductItem = itemView.findViewById(R.id.img_cart_item);
            nameProductItem = itemView.findViewById(R.id.cart_item_name);
            colorProductItem = itemView.findViewById(R.id.color_cart_item);
            sizeProductItem = itemView.findViewById(R.id.cart_item_size);
            currentPriceProductItem = itemView.findViewById(R.id.cart_product_current_price);
            priceProductItem = itemView.findViewById(R.id.cart_item_price);
            addQuantityProduct = itemView.findViewById(R.id.txt_add_quantity);
            minusQuantityProduct = itemView.findViewById(R.id.txt_minus_quantity);
            quantityProductItem = itemView.findViewById(R.id.edt_quantity_product);
            removeItemFromCart = itemView.findViewById(R.id.remove_item_from_cart);
            maxQuantity = itemView.findViewById(R.id.txt_max_quantity);
        }
    }
}