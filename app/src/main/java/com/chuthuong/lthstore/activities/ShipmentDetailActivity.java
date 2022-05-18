package com.chuthuong.lthstore.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.authActivities.LoginActivity;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.ShipmentDetail;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.province.District;
import com.chuthuong.lthstore.province.DistrictAdapter;
import com.chuthuong.lthstore.province.Province;
import com.chuthuong.lthstore.province.ProvinceAdapter;
import com.chuthuong.lthstore.province.Ward;
import com.chuthuong.lthstore.province.WardAdapter;
import com.chuthuong.lthstore.response.UserResponse;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipmentDetailActivity extends AppCompatActivity {
    Spinner spinnerProvince, spinnerDistrict, spinnerWard;
    ProvinceAdapter provinceAdapter;
    DistrictAdapter districtAdapter;
    WardAdapter wardAdapter;
    SwitchCompat switchCompat;
    List<Province> provinces = null;
    List<District> districts = null;
    List<Ward> wards = null;
    private User user;
    private ImageView back;
    EditText fullName, phone, address;
    TextView confirm;
    private String txtProvince;
    private String txtDistrict;
    private String txtWard;
    private ImageView backToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment_detail);
        addControls();
        callApiGetProvince();
        addEvents();

    }

    private void addEvents() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShipmentDetail shipmentDetail = new ShipmentDetail();
                shipmentDetail.setAddress(address.getText().toString());
                shipmentDetail.setDefault(switchCompat.isChecked());
                shipmentDetail.setDistrict(txtDistrict);
                shipmentDetail.setFullName(fullName.getText().toString());
                shipmentDetail.setPhone(phone.getText().toString());
                shipmentDetail.setProvince(txtProvince);
                shipmentDetail.setWard(txtWard);
                callApiAddShipmentDetail(shipmentDetail, "Bearer "+ LoginActivity.user.getAccessToken());
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShipmentDetailActivity.this,MainActivity.class));
            }
        });

    }
    private void callApiAddShipmentDetail(ShipmentDetail shipmentDetail, String token){
        String accept = "application/json;versions=1";
        ApiService.apiService.addShipmentDetail(accept,token,shipmentDetail).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful()) {
                    UserResponse userResponse = response.body();
                    user = userResponse.getUser();
                    Log.e("user", user.getUsername());
                    setToast(ShipmentDetailActivity.this, userResponse.getMessage());
                    finish();
                }
                else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(ShipmentDetailActivity.this,apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                setToast(ShipmentDetailActivity.this,"Lỗi server !");
            }
        });
    }
    private void callApiGetProvince(){
        ApiService.apiProvince.getProvinces().enqueue(new Callback<List<Province>>() {
            @Override
            public void onResponse(Call<List<Province>> call, Response<List<Province>> response) {
                if (response.isSuccessful()) {
                    provinces=  response.body();
                    loadAdapter();
                }
            }

            @Override
            public void onFailure(Call<List<Province>> call, Throwable t) {

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
    private void addControls() {
        spinnerProvince = findViewById(R.id.spinner_province);
        spinnerDistrict = findViewById(R.id.spinner_districts);
        spinnerWard = findViewById(R.id.spinner_wards);
        switchCompat = findViewById(R.id.switchCompat);
        fullName = findViewById(R.id.edit_text_name_receiver);
        phone = findViewById(R.id.edit_text_number_phone_receiver   );
        address = findViewById(R.id.text_view_address);
        confirm = findViewById(R.id.text_view_confirm);
        back = findViewById(R.id.img_back_detail);
        backToHome = findViewById(R.id.home_img_toolbar);
    }
    private void loadAdapter(){
        provinceAdapter = new ProvinceAdapter(ShipmentDetailActivity.this,R.layout.activity_shipment_detail,provinces);
        spinnerProvince.setAdapter(provinceAdapter);
        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtProvince = provinceAdapter.getItem(position).getName();
                districts = provinceAdapter.getItem(position).getDistricts();
                districtAdapter = new DistrictAdapter(ShipmentDetailActivity.this,R.layout.activity_shipment_detail,districts);
                spinnerDistrict.setAdapter(districtAdapter);
                spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        txtDistrict = districtAdapter.getItem(position).getName();
                        wards = districtAdapter.getItem(position).getWards();
                        wardAdapter = new WardAdapter(ShipmentDetailActivity.this,R.layout.activity_shipment_detail,wards);
                        spinnerWard.setAdapter(wardAdapter);
                        spinnerWard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                txtWard = wardAdapter.getItem(position).getName();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
}