package com.chuthuong.lthstore.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.authActivities.ChangePasswordActivity;
import com.chuthuong.lthstore.activities.authActivities.LoginActivity;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.fragments.HomeFragment;
import com.chuthuong.lthstore.fragments.OrderFragment;
import com.chuthuong.lthstore.fragments.ProductFragment;
import com.chuthuong.lthstore.model.ListUser;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.ApiToken;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    User user = null;
    String nameSharePreference = "account";
    ImageView avt;
    ArrayAdapter<String> userAdapter;
    Fragment homeFragment, orderFragment, productFragment ;
    BottomNavigationView bottomNavigationView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        addEvents();
        Intent intentReceiveFromUser = getIntent();
        user = (User) intentReceiveFromUser.getSerializableExtra("user");
        if (user == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        showToken();

        homeFragment = new HomeFragment();
        loadFragment(homeFragment);
    }

    private void addEvents() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mnu_home_navigation:
                        homeFragment = new HomeFragment();
                        loadFragment(homeFragment);
                        Toast.makeText(MainActivity.this, "Trang chủ", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.mnu_product_navigation:
                        productFragment = new ProductFragment();
                        loadFragment(productFragment);
                        Toast.makeText(MainActivity.this, "Sản phẩm", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.mnu_order_navigation:
                        orderFragment = new OrderFragment();
                        loadFragment(orderFragment);
                        Toast.makeText(MainActivity.this, "Đơn hàng", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.mnu_profile_navigation:
                        Toast.makeText(MainActivity.this, "Tài khoản", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences preferences = getSharedPreferences(nameSharePreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("refreshToken", "");
        editor.putString("accessToken", "");
        editor.commit(); // xác nhận lưu
    }

    private void loadFragment(Fragment homeFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.home_container, homeFragment);
        transaction.commit();
    }

    private void addControls() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public ApiToken getToken(String nameSharePreference) {
        SharedPreferences preferences = getSharedPreferences(nameSharePreference, MODE_PRIVATE);
        String refreshToken = preferences.getString("refreshToken", "");
        String accessToken = preferences.getString("accessToken", "");
        ApiToken apiToken = new ApiToken(accessToken, refreshToken);
        return apiToken;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showToken() {
        ApiToken apiToken = getToken("account");
        String accessToken = apiToken.getAccessToken();
        if (accessToken == "") {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String[] verifyTokens = accessToken.split("\\.");
            String header = new String(decoder.decode(verifyTokens[0]));
            String payload = new String(decoder.decode(verifyTokens[1]));
            String p = payload.split(",")[3];
            String newPayload = p.substring(0, p.length() - 1);
            long exp = Long.parseLong(newPayload.split(":")[1]);
            long date = Calendar.getInstance().getTimeInMillis();
            Log.e("Payload", exp + "");
            Date date1 = new Date(exp);
            SimpleDateFormat df2 = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                df2 = new SimpleDateFormat("DD/MM/YYYY hh:mm:ss");
            }
            String date2 = df2.format(date1);
            Log.e("Payload", newPayload + "");
            Log.e("Date", date + "");
            Log.e("Date2", date2 + "");

            Toast.makeText(this, payload + "=" + date, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuChangePassword:
                Intent intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
            case R.id.mnuLogout:
                callApiLogout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callApiLogout() {
        if (user == null) {
            Intent intentReceiveFromUser = getIntent();
            user = (User) intentReceiveFromUser.getSerializableExtra("user");
        }
        Log.e("accessToken", user.getAccessToken());
        String token = user.getAccessToken();
        String accessToken = "Bearer " + token;
        String access = "application/json;versions=1";
        ApiService.apiService.logoutUser(access, accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Log.e("Message", response.body().getMessage() + "");
                    SharedPreferences preferences = getSharedPreferences(nameSharePreference, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("refreshToken", "");
                    editor.putString("accessToken", "");
                    editor.commit(); // xác nhận lưu
                    Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Log.e("Message", apiError.getMessage());
                        Toast.makeText(MainActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("Lỗi server ", t.toString());
                Toast.makeText(MainActivity.this, "lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAllUser(View view) {
        ApiToken apiToken = getToken("account");
        String accessToken = "Bearer " + apiToken.getAccessToken();
        String access = "application/json;versions=1";
        callApiGetAllUser(access, accessToken);
    }

    private void callApiGetAllUser(String access, String accessToken) {
        ApiService.apiService.getAllUser(access, accessToken).enqueue(new Callback<ListUser>() {
            @Override
            public void onResponse(Call<ListUser> call, Response<ListUser> response) {
                if (response.isSuccessful()) {
                    ListUser users = response.body();
//                    Log.e("ID:", users.getId());
//                    Log.e("Username:", user.getUsername());
//                    Log.e("Email:", user.getEmail());
//                    Log.e("IsAdmin:", user.isAdmin() + "");
//                    Log.e("createdAt:", user.getCreatedAt());
//                    Log.e("updatedAt:", user.getUpdatedAt());
//                    Log.e("accessToken:", user.getAccessToken());
//                    Log.e("refreshToken:", user.getRefreshToken());
                    Log.e("users:", users.getUsers().get(0).toString());
                    String avtURL = users.getUsers().get(0).getAvatar();
                    Log.e("AVT", avtURL);
                    Glide.with(MainActivity.this).load(avtURL).into(avt);
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Log.e("Message", apiError.getMessage());
//                        Toast.makeText(MainActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ListUser> call, Throwable t) {
                Log.e("Main", t.getMessage());
            }
        });
    }

}