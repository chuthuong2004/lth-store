package com.chuthuong.lthstore.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.province.District;
import com.chuthuong.lthstore.province.DistrictAdapter;
import com.chuthuong.lthstore.province.Province;
import com.chuthuong.lthstore.province.ProvinceAdapter;
import com.chuthuong.lthstore.province.Ward;
import com.chuthuong.lthstore.province.WardAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipmentDetailActivity extends AppCompatActivity {
    Spinner spinnerProvince, spinnerDistrict, spinnerWard;
    ProvinceAdapter provinceAdapter;
    DistrictAdapter districtAdapter;
    WardAdapter wardAdapter;

    List<Province> provinces = null;
    List<District> districts = null;
    List<Ward> wards = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment_detail);
        addControls();
        callApiGetProvince();

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
    private void addControls() {
        spinnerProvince = findViewById(R.id.spinner_province);
        spinnerDistrict = findViewById(R.id.spinner_districts);
        spinnerWard = findViewById(R.id.spinner_wards);
    }
    private void loadAdapter(){
        provinceAdapter = new ProvinceAdapter(ShipmentDetailActivity.this,R.layout.activity_shipment_detail,provinces);
        spinnerProvince.setAdapter(provinceAdapter);
        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ShipmentDetailActivity.this, provinceAdapter.getItem(position).getName(), Toast.LENGTH_SHORT).show();
                districts = provinceAdapter.getItem(position).getDistricts();
                districtAdapter = new DistrictAdapter(ShipmentDetailActivity.this,R.layout.activity_shipment_detail,districts);
                spinnerDistrict.setAdapter(districtAdapter);
                spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(ShipmentDetailActivity.this, districtAdapter.getItem(position).getName(), Toast.LENGTH_SHORT).show();
                        wards = districtAdapter.getItem(position).getWards();
                        wardAdapter = new WardAdapter(ShipmentDetailActivity.this,R.layout.activity_shipment_detail,wards);
                        spinnerWard.setAdapter(wardAdapter);
                        spinnerWard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(ShipmentDetailActivity.this, wardAdapter.getItem(position).getName(), Toast.LENGTH_SHORT).show();
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