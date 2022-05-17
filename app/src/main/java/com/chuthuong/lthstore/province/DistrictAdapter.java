package com.chuthuong.lthstore.province;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chuthuong.lthstore.R;

import java.util.List;

public class DistrictAdapter extends ArrayAdapter<District> {
    public DistrictAdapter(@NonNull Context context, int resource, @NonNull List<District> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_province_selected,parent,false );
        TextView txtProvince = convertView.findViewById(R.id.txt_province_selected);
        District province = this.getItem(position);
        txtProvince.setText(province.getName());
        if(province!=null){
            txtProvince.setText(province.getName());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_province,parent,false );
        TextView txtProvince = convertView.findViewById(R.id.txt_province);
        District province = this.getItem(position);
        if(province!=null){
            txtProvince.setText(province.getName());
        }
        return convertView;
    }
}
