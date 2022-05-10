package com.chuthuong.lthstore.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

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
import com.chuthuong.lthstore.adapter.ViewPagerAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.fragments.HomeFragment;
import com.chuthuong.lthstore.fragments.OrderFragment;
import com.chuthuong.lthstore.fragments.ProductFragment;
import com.chuthuong.lthstore.fragments.ProfileFragment;
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
    Fragment homeFragment, orderFragment, productFragment, profileFragment;

    ViewPager mViewPager;
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
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        showToken();

        homeFragment = new HomeFragment();
        loadFragment(homeFragment);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.setOffscreenPageLimit(2); //só lượng page load
    }

    private void addEvents() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mnu_home_navigation:
                        mViewPager.setCurrentItem(0);
                        HomeFragment homeFragment1 = (HomeFragment) mViewPager.getAdapter().instantiateItem(mViewPager, 0);
                        homeFragment1.reloadData();
//                        homeFragment = new HomeFragment();
//                        loadFragment(homeFragment);
                        break;
                    case R.id.mnu_product_navigation:
                        mViewPager.setCurrentItem(1);
//                        productFragment = new ProductFragment();
//                        loadFragment(productFragment);
                        break;
                    case R.id.mnu_order_navigation:
                        mViewPager.setCurrentItem(2);
//                        orderFragment = new OrderFragment();
//                        loadFragment(orderFragment);
                        break;
                    case R.id.mnu_profile_navigation:
                        mViewPager.setCurrentItem(3);
//                        profileFragment = new ProfileFragment();
//                        loadFragment(profileFragment);
                        break;
                }
                return true;
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.mnu_home_navigation).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.mnu_product_navigation).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.mnu_order_navigation).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.mnu_profile_navigation).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
        mViewPager = findViewById(R.id.view_pager_home);
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
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
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
}