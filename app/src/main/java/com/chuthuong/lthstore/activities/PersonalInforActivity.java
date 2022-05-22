package com.chuthuong.lthstore.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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

import com.bumptech.glide.Glide;
import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.authActivities.LoginActivity;
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
public class PersonalInforActivity extends AppCompatActivity {
    TextView txtNamePersonal, txtEmailPersonal, txtAddressPersonal, txtEditPersonal,txtPhonePersonal;
    ImageView imgBackPersonal, imgPersonal, backToHome;
    User user = null;
    private UserReaderSqlite userReaderSqlite;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_infor);
        addControl();
        addEvent();
        userReaderSqlite = new UserReaderSqlite(this, "user.db", null, 1);
        Util.refreshToken(this);
        if (userReaderSqlite.getUser() != null) {
            callApiMyAccount(userReaderSqlite.getUser().getAccessToken());
        }
    }
    private void render() {
        txtEmailPersonal.setText(user.getEmail());
        Glide.with(this).load(user.getAvatar()).into(imgPersonal);
        ShipmentDetail shipmentDetail = findDefaultShipmentDetail(user);
        if(shipmentDetail!=null){
            txtNamePersonal.setText(shipmentDetail.getFullName());
            String address = shipmentDetail.getAddress()+", "+shipmentDetail.getWard()+", "+shipmentDetail.getDistrict()+", "+shipmentDetail.getProvince();
            txtAddressPersonal.setText(address);
            if(shipmentDetail.getPhone()!=null){
                txtPhonePersonal.setText(shipmentDetail.getPhone());
            }
        }
    }

    private void addEvent() {
        imgBackPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onBackPressed();
            }
        });
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonalInforActivity.this, MainActivity.class));
            }
        });
        txtEditPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToast(PersonalInforActivity.this, "OK");
                startActivity(new Intent(PersonalInforActivity.this,EditPersonalProfile.class));
            }
        });
    }

    private void addControl() {
        txtNamePersonal = findViewById(R.id.txt_user_name_personal);
        imgBackPersonal = findViewById(R.id.img_back_detail);
        imgPersonal = findViewById(R.id.image_personal_information);
        txtEmailPersonal = findViewById(R.id.txt_email_personal_information);
        txtAddressPersonal = findViewById(R.id.txt_address_personal_information);
        txtEditPersonal = findViewById(R.id.txt_edit_personal);
        txtPhonePersonal=findViewById(R.id.txt_number_phone_personal_information);
        backToHome = findViewById(R.id.home_img_toolbar);
    }

    private void callApiMyAccount(String accessToken) {
        String token = "Bearer " + accessToken;
        String accept = "application/json;versions=1";
        ApiService.apiService.getMyAccount(accept, token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    UserResponse userResponse = response.body();
                    user = userResponse.getUser();
                    render();
                    Log.e("user", user.toString());
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(PersonalInforActivity.this, apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                setToast(PersonalInforActivity.this, "Lỗi server !");
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
    protected void onRestart() {
        userReaderSqlite = new UserReaderSqlite(this, "user.db", null, 1);
        Util.refreshToken(this);
        super.onRestart();
    }
    private ShipmentDetail findDefaultShipmentDetail(User user) {
        List<ShipmentDetail> shipmentDetails = user.getShipmentDetails();
        if (shipmentDetails == null) return null;
        for (int i = 0; i < shipmentDetails.size(); i++) {
            if (shipmentDetails.get(i).isDefault() == true) {
                return shipmentDetails.get(i);
            }
        }
        return shipmentDetails.get(0);
    }
}