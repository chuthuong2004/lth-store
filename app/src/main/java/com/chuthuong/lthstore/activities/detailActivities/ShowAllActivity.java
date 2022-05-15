package com.chuthuong.lthstore.activities.detailActivities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.MainActivity;
import com.chuthuong.lthstore.activities.MyCartActivity;
import com.chuthuong.lthstore.activities.authActivities.ChangePasswordActivity;
import com.chuthuong.lthstore.activities.authActivities.LoginActivity;
import com.chuthuong.lthstore.adapter.PopularProductAdapter;
import com.chuthuong.lthstore.adapter.ShowAllAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.fragments.SearchFragment;
import com.chuthuong.lthstore.model.CartResponse;
import com.chuthuong.lthstore.model.ListCategory;
import com.chuthuong.lthstore.model.ListProduct;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowAllActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ShowAllAdapter showAllAdapter;
    ListProduct listProduct;
    private User user;
    private CartResponse cart;
    private ImageView imgCart;
    private TextView quantityCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);
        addControls();
        addEvents();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        final Object obj = getIntent().getSerializableExtra("list_see_all");
        String title = getIntent().getStringExtra("title_see_all");
        Log.e("title", title);
        if (obj instanceof ListProduct) {
            listProduct = new ListProduct();
            listProduct = (ListProduct) obj;
            showAllAdapter = new ShowAllAdapter(ShowAllActivity.this, listProduct);
            TextView txtTitle = findViewById(R.id.title_see_all);
            txtTitle.setText(title);
            recyclerView.setAdapter(showAllAdapter);
            showAllAdapter.notifyDataSetChanged();
        }
        ImageView imgBack, imgSearch;
        imgBack = findViewById(R.id.img_back_detail);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgSearch = findViewById(R.id.search_img);
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment searchFragment = new SearchFragment();
                fragmentTransaction.add(R.id.search_container, searchFragment);
//                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_out_right,
//                        R.anim.slide_out_right);
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, 0);
                fragmentTransaction.show(searchFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    private void addEvents() {
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user == null) {
                    openDialogRequestLogin();
                }
                // xử lý render cart
                else {
                    // start vô cart
                    Intent intent = new Intent(ShowAllActivity.this, MyCartActivity.class);
                    intent.putExtra("my_cart", cart.getCart());
                    intent.putExtra("title_my_cart", getResources().getString(R.string.strTitleMyCart));
                    startActivity(intent);
                }
            }
        });
        loadCart();
    }

    private void openDialogRequestLogin() {
        final Dialog dialog = new Dialog(this);
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
                Intent intent = new Intent(ShowAllActivity.this, LoginActivity.class);
                intent.putExtra("intent", "ProductActivity");
                startActivity(intent);
            }
        });
        dialog.show();
    }

    private void addControls() {
        user = LoginActivity.user;
        quantityCart = findViewById(R.id.quantity_cart_toolbar);
        imgCart = findViewById(R.id.cart_img_toolbar);
        recyclerView = findViewById(R.id.show_all_rec);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Thoát", Toast.LENGTH_SHORT).show();
    }

    private void loadCart() {
        if (user == null) {
            Toast.makeText(ShowAllActivity.this, "Không có user", Toast.LENGTH_SHORT).show();
        } else {
            callApiGetMyCart("Bearer " + user.getAccessToken());

        }
    }

    private void callApiGetMyCart(String accessToken) {
        Toast.makeText(this, accessToken, Toast.LENGTH_SHORT).show();
        Log.e("ACCESS", accessToken);
        String accept = "application/json;versions=1";
        ApiService.apiService.getMyCart(accept, accessToken).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    cart = response.body();
                    int size = cart.getCart().getCartItems().size();
                    quantityCart.setText(size + "");
                    quantityCart.setVisibility(View.VISIBLE);
                    Log.e("cart", cart.getCart().toString());
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Log.e("Message", apiError.getMessage());
                        Toast.makeText(ShowAllActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Log.e("Lỗi server ", t.toString());
                Toast.makeText(ShowAllActivity.this, "lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }

}