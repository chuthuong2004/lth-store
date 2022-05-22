package com.chuthuong.lthstore.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.shipmentActivities.ListShipmentDetailActivity;
import com.chuthuong.lthstore.activities.shipmentActivities.ShipmentDetailActivity;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.ShipmentDetail;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.response.UserResponse;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.utils.Util;
import com.chuthuong.lthstore.widget.CustomProgressDialog;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListShipmentDetailAdapter extends RecyclerView.Adapter<ListShipmentDetailAdapter.ViewHolder> {
    Context context;
    List<ShipmentDetail> shipmentDetails;
    private UserReaderSqlite userReaderSqlite;
    private CustomProgressDialog dialogRemove;
    ListShipmentDetailActivity listShipmentDetailActivity;
    LayoutInflater inflater;
    ViewGroup viewGroup;
    public ListShipmentDetailAdapter(Context context, List<ShipmentDetail> shipmentDetails) {
        this.context = context;
        this.shipmentDetails = shipmentDetails;
    }

    @NonNull
    @Override
    public ListShipmentDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        viewGroup = parent;
        return new ListShipmentDetailAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_shipment_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListShipmentDetailAdapter.ViewHolder holder, int position) {
        listShipmentDetailActivity = (ListShipmentDetailActivity) context;
        ShipmentDetail shipmentDetail = shipmentDetails.get(position);
        holder.txtName.setText(shipmentDetail.getFullName());
        if (shipmentDetail.isDefault() == true) {
            holder.isDefault.setVisibility(View.VISIBLE);
        }
        holder.phone.setText(shipmentDetail.getPhone());
        holder.address.setText(shipmentDetail.getAddress() + ", " + shipmentDetail.getWard() + ", " + shipmentDetail.getDistrict() + ", " + shipmentDetail.getProvince());
        holder.del.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                userReaderSqlite = new UserReaderSqlite(context, "user.db", null, 1);
                Util.refreshToken(context);
                if(userReaderSqlite.getUser()!=null) {

                    dialogRemove = new CustomProgressDialog(context);
                    dialogRemove.show();
                    callApiRemoveShipment("Bearer " + userReaderSqlite.getUser().getAccessToken(), shipmentDetail.getId());
                }
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShipmentDetailActivity.class);
                intent.putExtra("shipment_detail", shipmentDetail);
                context.startActivity(intent);
                listShipmentDetailActivity.reloadAdapter();
            }
        });
        if (listShipmentDetailActivity.isChanged==true) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences preferences = context.getSharedPreferences("shipment_detail", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("shipment_id", shipmentDetail.getId());
                    editor.commit(); // xác nhận lưu
                    ((ListShipmentDetailActivity) context).finish();
                }
            });
        }
    }

    private void callApiRemoveShipment(String token, String id) {
        ApiService.apiService.removeShipment(token, id).enqueue(new Callback<UserResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful()) {
                    User user = response.body().getUser();
                    dialogRemove.dismiss();
                    setToast(context, response.body().getMessage());
                    listShipmentDetailActivity.reloadAdapter();
                }else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        dialogRemove.dismiss();
                        setToast(context,apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                setToast(context,"Lỗi server !");
            }
        });
    }

    @Override
    public int getItemCount() {
        if (shipmentDetails != null) {
            return shipmentDetails.size();
        }
        return 0;
    }
    private void setToast(Context context, String msg) {
        Toast toast = new Toast(context.getApplicationContext());
        View view = inflater.inflate(R.layout.custom_toast, viewGroup.findViewById(R.id.layout_toast));
        TextView message = view.findViewById(R.id.message_toast);
        message.setText(msg);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, isDefault, edit, del, phone, address;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_name_user_personal);
            isDefault = itemView.findViewById(R.id.is_default);
            edit = itemView.findViewById(R.id.txt_edit_address);
            del = itemView.findViewById(R.id.txt_remove_address);
            phone = itemView.findViewById(R.id.txt_number_phone_personal);
            address = itemView.findViewById(R.id.txt_address_personal);
        }
    }
}
