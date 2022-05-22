package com.chuthuong.lthstore.activities.shipmentActivities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.MainActivity;
import com.chuthuong.lthstore.adapter.ListShipmentDetailAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.ShipmentDetail;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.response.UserResponse;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.utils.Util;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListShipmentDetailActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ListShipmentDetailAdapter listShipmentDetailAdapter;
    List<ShipmentDetail> shipmentDetails;
    private UserReaderSqlite userReaderSqlite;
    private User userToken;
    private User user;
    private TextView addAddress;
    private ImageView imgAddAddress;
    private ImageView back;
    private ImageView backToHome;
    public boolean isChanged=false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_shipment_detail);
        boolean isChangeAddress = getIntent().getBooleanExtra("change_address",false);
        if(isChangeAddress== true) {
            isChanged=true;
        }
        setToast(this, isChanged+"");
        addControls();
        addEvents();
        userReaderSqlite = new UserReaderSqlite(this, "user.db", null, 1);
        Util.refreshToken(this);
        userToken = userReaderSqlite.getUser();
        if(userToken!=null) {
            callApiMyAccount("Bearer " + userToken.getAccessToken());
        }

    }

    private void addEvents() {
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListShipmentDetailActivity.this, MainActivity.class));
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListShipmentDetailActivity.this, ShipmentDetailActivity.class));
            }
        });
        imgAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListShipmentDetailActivity.this,ShipmentDetailActivity.class));
            }
        });
    }

    private void addControls() {
        addAddress = findViewById(R.id.txt_add_address);
        imgAddAddress = findViewById(R.id.img_add_address);
        back = findViewById(R.id.img_back_detail);
        backToHome = findViewById(R.id.home_img_toolbar);
    }

    public void loadAdapter(){
        shipmentDetails = user.getShipmentDetails();
        recyclerView = findViewById(R.id.rec_list_shipment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        listShipmentDetailAdapter = new ListShipmentDetailAdapter(this,shipmentDetails);
        recyclerView.setAdapter(listShipmentDetailAdapter);
        listShipmentDetailAdapter.notifyDataSetChanged();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void reloadAdapter(){
        Util.refreshToken(this);
        userToken = userReaderSqlite.getUser();
        if(userToken!=null) {
            callApiMyAccount("Bearer " + userToken.getAccessToken());
        }
    }
    private void callApiMyAccount(String token){
        String accept ="application/json;versions=1";
        ApiService.apiService.getMyAccount(accept,token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful()){
                    UserResponse userResponse = response.body();
                    user = userResponse.getUser();
                    loadAdapter();
                }else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(ListShipmentDetailActivity.this, apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                setToast(ListShipmentDetailActivity.this, "Lỗi server !");
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
        userToken = userReaderSqlite.getUser();
        if(userToken!=null) {
            callApiMyAccount("Bearer " + userToken.getAccessToken());
        }
    }
}