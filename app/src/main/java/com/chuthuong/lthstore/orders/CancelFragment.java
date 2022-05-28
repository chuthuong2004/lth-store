package com.chuthuong.lthstore.orders;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.adapter.OrderAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.response.ListOrderResponse;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.utils.Util;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CancelFragment extends Fragment {

    RecyclerView recOrder;
    OrderAdapter orderAdapter;
    private User user= null;
    private ListOrderResponse listOrder;
    private UserReaderSqlite userReaderSqlite;
    private TextView txtNoOrder;
    private ImageView imgNoOrder;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_cancel, container, false);
        userReaderSqlite= new UserReaderSqlite(getActivity(),"user.db",null,1);
        Util.refreshToken(getActivity());
        user = userReaderSqlite.getUser();
        addControls(view);
        loadOrders();
        return view;
    }

    private void loadOrders() {
        if(user!=null){
            callApiGetMyOrder("Bearer " + user.getAccessToken());

        }
    }
    @Override
    public void onStart() {
        super.onStart();
        loadOrders();
    }
    private void addControls(View view) {
        recOrder = view.findViewById(R.id.rec_order_cancel);
        txtNoOrder = view.findViewById(R.id.no_order);
        imgNoOrder = view.findViewById(R.id.img_no_order);
    }

    public void callApiGetMyOrder(String accessToken){
        String accept = "application/json;versions=1";
        ApiService.apiService.getMyOrder(accept, accessToken, "-canceledAt","Canceled").enqueue(new Callback<ListOrderResponse>() {
            @Override
            public void onResponse(Call<ListOrderResponse> call, Response<ListOrderResponse> response) {
                if(response.isSuccessful()){
                    listOrder = response.body();
                    if(listOrder.getCountDocuments()==0) {
                        txtNoOrder.setVisibility(View.VISIBLE);
                        imgNoOrder.setVisibility(View.VISIBLE);
                    }
                    recOrder.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                    orderAdapter= new OrderAdapter(getActivity(),listOrder);
                    recOrder.setAdapter(orderAdapter);
                    orderAdapter.notifyDataSetChanged();
                }else {
                    Gson gson = new Gson();
                    try {
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ListOrderResponse> call, Throwable t) {
                Toast.makeText(getActivity(),"Lỗi server !",Toast.LENGTH_SHORT);
            }
        });

    }
}