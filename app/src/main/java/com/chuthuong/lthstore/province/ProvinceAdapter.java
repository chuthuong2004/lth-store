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

public class ProvinceAdapter extends ArrayAdapter<Province> {
    public ProvinceAdapter(@NonNull Context context, int resource, @NonNull List<Province> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_province_selected,parent,false );
        TextView txtProvince = convertView.findViewById(R.id.txt_province_selected);
        Province province = this.getItem(position);
        txtProvince.setText(province.getName());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_province,parent,false );
        TextView txtProvince = convertView.findViewById(R.id.txt_province);
        Province province = this.getItem(position);
        if(province!=null){
            txtProvince.setText(province.getName());
        }
        return convertView;
    }
}
