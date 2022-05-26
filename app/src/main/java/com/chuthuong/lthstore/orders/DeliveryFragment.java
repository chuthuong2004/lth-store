package com.chuthuong.lthstore.orders;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.adapter.OrderAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.response.ListOrderResponse;
import com.chuthuong.lthstore.response.ListProductResponse;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.utils.Util;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryFragment extends Fragment {
    RecyclerView recOrder;
    OrderAdapter orderAdapter;
    private User user= null;
    private ListOrderResponse listOrder;
    private UserReaderSqlite userReaderSqlite;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Toast.makeText(getActivity(),"Vô rồi á",Toast.LENGTH_SHORT);
        View view =inflater.inflate(R.layout.fragment_delivery, container, false);
        userReaderSqlite= new UserReaderSqlite(getActivity(),"user.db",null,1);
        Util.refreshToken(getActivity());
        user = userReaderSqlite.getUser();

        addControls(view);

        loadOrders();


        return view;
    }

    private void loadOrders() {
        Log.e("User order ",user.toString());
        if(user!=null){
            Log.e("User order ",user.toString());
            callApiGetMyOrder("Bearer " + user.getAccessToken());

        }
    }

    private void addControls(View view) {
        recOrder = view.findViewById(R.id.rec_order_delivery);
    }

    public void callApiGetMyOrder(String accessToken){
        String accept = "application/json;versions=1";
        ApiService.apiService.getMyOrder(accept, accessToken,"Delivery").enqueue(new Callback<ListOrderResponse>() {
            @Override
            public void onResponse(Call<ListOrderResponse> call, Response<ListOrderResponse> response) {
                if(response.isSuccessful()){
                    listOrder = response.body();
                    recOrder.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                    orderAdapter= new OrderAdapter(getActivity(),listOrder);
                    recOrder.setAdapter(orderAdapter);
                    orderAdapter.notifyDataSetChanged();
                }else {
                    Log.e("order","Không có");
                    Gson gson = new Gson();
                    try {
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ListOrderResponse> call, Throwable t) {
                Log.e("Lỗi server ",t.toString());
                Toast.makeText(getActivity(),"Lỗi server !",Toast.LENGTH_SHORT);
            }
        });

    }

}