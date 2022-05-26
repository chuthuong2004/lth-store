package com.chuthuong.lthstore.activities.detailActivities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.MainActivity;
import com.chuthuong.lthstore.activities.MyCartActivity;
import com.chuthuong.lthstore.activities.authActivities.LoginActivity;
import com.chuthuong.lthstore.adapter.ShowAllAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.fragments.SearchFragment;
import com.chuthuong.lthstore.response.CartResponse;
import com.chuthuong.lthstore.response.ListProductResponse;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.utils.Util;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowAllActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ShowAllAdapter showAllAdapter;
    ListProductResponse listProductResponse;
    private User user;
    private CartResponse cartResponse;
    private ImageView imgCart;
    private TextView quantityCart;
    private UserReaderSqlite userReaderSqlite;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        addControls();
        addEvents();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        final Object obj = getIntent().getSerializableExtra("list_see_all");
        String title = getIntent().getStringExtra("title_see_all");
        if (obj instanceof ListProductResponse) {
            listProductResponse = (ListProductResponse) obj;
            showAllAdapter = new ShowAllAdapter(ShowAllActivity.this, listProductResponse);
            TextView txtTitle = findViewById(R.id.title_see_all);
            txtTitle.setText(title);
            recyclerView.setAdapter(showAllAdapter);
            showAllAdapter.notifyDataSetChanged();
        }
        ImageView imgBack, imgSearch;
        imgBack = findViewById(R.id.img_back_detail);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
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
                    intent.putExtra("title_my_cart", getResources().getString(R.string.strTitleMyCart));
                    startActivity(intent);
                }
            }
        });

        Util.refreshToken(ShowAllActivity.this);
        user = userReaderSqlite.getUser();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addControls() {
        userReaderSqlite = new UserReaderSqlite(this, "user.db", null, 1);
        Util.refreshToken(this);
        user = userReaderSqlite.getUser();
        quantityCart = findViewById(R.id.quantity_cart_toolbar);
        imgCart = findViewById(R.id.cart_img_toolbar);
        recyclerView = findViewById(R.id.show_all_rec);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {
        Util.refreshToken(ShowAllActivity.this);
        user = userReaderSqlite.getUser();
        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadCart() {
        Util.refreshToken(ShowAllActivity.this);
        user = userReaderSqlite.getUser();

        if (user != null) {
            callApiGetMyCart("Bearer " + user.getAccessToken());
        }
    }

    private void callApiGetMyCart(String accessToken) {
        String accept = "application/json;versions=1";
        ApiService.apiService.getMyCart(accept, accessToken).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    cartResponse = response.body();
                    if (cartResponse.getCart().getCartItems() != null) {
                        int size = cartResponse.getCart().getCartItems().size();
                        if (size > 0) {
                            quantityCart.setText(size + "");
                            quantityCart.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        quantityCart.setVisibility(View.GONE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                setToast(ShowAllActivity.this,"Lỗi server !");
            }
        });
    }
    private void setToast(Activity activity, String msg) {
        Toast toast = new Toast(activity);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.layout_toast));
        TextView message = view.findViewById(R.id.message_toast);
        message.setText(msg);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        Util.refreshToken(this);
        user = userReaderSqlite.getUser();
        loadCart();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        Util.refreshToken(this);
        user = userReaderSqlite.getUser();
        super.onResume();
    }
}