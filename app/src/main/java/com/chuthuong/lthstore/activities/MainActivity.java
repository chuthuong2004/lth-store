package com.chuthuong.lthstore.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.authActivities.ChangePasswordActivity;
import com.chuthuong.lthstore.activities.authActivities.LoginActivity;
import com.chuthuong.lthstore.adapter.ViewPagerAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.fragments.HomeFragment;
import com.chuthuong.lthstore.response.CartResponse;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.ApiToken;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.utils.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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

    int i = 0;
    User user = null;
    String nameSharePreference = "account";
    ImageView avt;
    ArrayAdapter<String> userAdapter;
    Fragment homeFragment, orderFragment, productFragment, profileFragment;
    ViewPager mViewPager;
    BottomNavigationView bottomNavigationView;
    CartResponse cartResponse = null;
    UserReaderSqlite userReaderSqlite;
    TextView quantityCart;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userReaderSqlite = new UserReaderSqlite(this, "user.db", null, 1);
        addControls();
        addEvents();
        homeFragment = new HomeFragment();
        Util.refreshToken(this);
        user = userReaderSqlite.getUser();
        loadFragment(homeFragment);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.setOffscreenPageLimit(3); //só lượng page load

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
//                        loadFragment(productFragment);123
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Util.refreshToken(MainActivity.this);
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        Util.refreshToken(MainActivity.this);
                        bottomNavigationView.getMenu().findItem(R.id.mnu_home_navigation).setChecked(true);
                        break;
                    case 1:
                        Util.refreshToken(MainActivity.this);
                        bottomNavigationView.getMenu().findItem(R.id.mnu_product_navigation).setChecked(true);
                        break;
                    case 2:
                        Util.refreshToken(MainActivity.this);
                        bottomNavigationView.getMenu().findItem(R.id.mnu_order_navigation).setChecked(true);
                        break;
                    case 3:
                        Util.refreshToken(MainActivity.this);
                        bottomNavigationView.getMenu().findItem(R.id.mnu_profile_navigation).setChecked(true);
                        break;
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPageScrollStateChanged(int state) {
                Util.refreshToken(MainActivity.this);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadFragment(Fragment homeFragment) {
        Util.refreshToken(MainActivity.this);
        user = userReaderSqlite.getUser();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.home_container, homeFragment);
        transaction.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addControls() {
        Util.refreshToken(MainActivity.this);
        user = userReaderSqlite.getUser();
        mViewPager = findViewById(R.id.view_pager_home);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        quantityCart = findViewById(R.id.quantity_cart_toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void showToken() {
//        ApiToken apiToken = getToken("account");
//        String accessToken = apiToken.getAccessToken();
//        if (accessToken == "") {
////            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//        } else {
//            Base64.Decoder decoder = Base64.getUrlDecoder();
//            String[] verifyTokens = accessToken.split("\\.");
//            String header = new String(decoder.decode(verifyTokens[0]));
//            String payload = new String(decoder.decode(verifyTokens[1]));
//            String p = payload.split(",")[3];
//            String newPayload = p.substring(0, p.length() - 1);
//            long exp = Long.parseLong(newPayload.split(":")[1]);
//            long date = Calendar.getInstance().getTimeInMillis();
//            Date date1 = new Date(exp);
//            SimpleDateFormat df2 = null;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                df2 = new SimpleDateFormat("DD/MM/YYYY hh:mm:ss");
//            }
//            String date2 = df2.format(date1);
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuChangePassword:
                Intent intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.mnuLogout:
//                callApiLogout();
                break;
        }
        return super.onOptionsItemSelected(item);
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
    protected void onRestart() {
        Util.refreshToken(this);
        user = userReaderSqlite.getUser();
        super.onRestart();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
    }
}