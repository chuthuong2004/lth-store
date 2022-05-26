package com.chuthuong.lthstore.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.MyCartActivity;
import com.chuthuong.lthstore.activities.authActivities.LoginActivity;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.orders.OrderViewPagerAdapter;
import com.chuthuong.lthstore.response.CartResponse;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.utils.Util;
import com.chuthuong.lthstore.widget.CustomViewPager;
import com.chuthuong.lthstore.widget.CustomViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private View view;
    private ImageView imgCart;
    private TextView quantityCart;
    private ImageView search;
    private UserReaderSqlite userReaderSqlite;
    private User user;
    private CartResponse cartResponse;
    private RelativeLayout layoutOrder;
    private ConstraintLayout layoutNotUser;
    private TextView btnLogin;

    public OrderFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e("OrderFragment", "Fragment 3");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.e("Thuong","Reload FragmentOrder");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        userReaderSqlite = new UserReaderSqlite(getActivity(), "user.db", null, 1);

        Util.refreshToken(getActivity());
        user = userReaderSqlite.getUser();

        view = inflater.inflate(R.layout.fragment_order, container, false);
        addControls();
        addEvents();
        loadCart();
        tabLayout = view.findViewById(R.id.tab_layout_order);
        viewPager2 = view.findViewById(R.id.view_pager_order);
        OrderViewPagerAdapter orderViewPagerAdapter= new OrderViewPagerAdapter(getActivity());
        viewPager2.setAdapter(orderViewPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 1:
                    tab.setText("Đã đặt");
                    tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.processing));
                    break;
                case 2:
                    tab.setText("Đang giao");
                    break;
                case  3:
                    tab.setText("Đã giao");
                    break;
                case    4:
                    tab.setText("Đã hủy");
                    break;
                case 0:
                default:
                    tab.setText("Tất cả đơn hàng");
                    break;
            }
        }).attach();
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                orderViewPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadCart() {
        Util.refreshToken(getActivity());
        if (user != null) {
            callApiGetMyCart("Bearer " + user.getAccessToken());
        }
        else {
            layoutOrder.setVisibility(View.GONE);
            layoutNotUser.setVisibility(View.VISIBLE);
        }
    }

    private void callApiGetMyCart(String token) {
        String accept = "application/json;versions=1";
        ApiService.apiService.getMyCart(accept, token).enqueue(new Callback<CartResponse>() {
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
                setToast(getActivity(),"Lỗi server !");
            }
        });
    }
    private void setToast(Activity activity, String msg) {
        Toast toast = new Toast(activity);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_toast, (ViewGroup) activity.findViewById(R.id.layout_toast));
        TextView message = view.findViewById(R.id.message_toast);
        message.setText(msg);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
    private void addEvents() {
        imgCart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                Util.refreshToken(getActivity());
                if (user == null) {
                    openDialogRequestLogin();
                }
                // xử lý render cart
                else {
                    // start vô cart
                    Intent intent = new Intent(getActivity(), MyCartActivity.class);
                    intent.putExtra("my_cart", cartResponse);
                    intent.putExtra("title_my_cart", getResources().getString(R.string.strTitleMyCart));
                    startActivity(intent);
                }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
    }
    private void openDialogRequestLogin() {
        final Dialog dialog = new Dialog(getActivity());
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                Util.refreshToken(getActivity());

                dialog.dismiss();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        dialog.show();
    }
    private void addControls() {
        imgCart = view.findViewById(R.id.cart_img_toolbar);
        quantityCart = view.findViewById(R.id.quantity_cart_toolbar);
        search = view.findViewById(R.id.search_toolbar_img);
        layoutOrder = view.findViewById(R.id.layout_order);
        layoutNotUser = view.findViewById(R.id.layout_not_user);
        btnLogin = view.findViewById(R.id.btn_login);
    }
}