package com.chuthuong.lthstore.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import com.chuthuong.lthstore.activities.ListShipmentDetailActivity;
import com.chuthuong.lthstore.activities.MainActivity;
import com.chuthuong.lthstore.activities.PersonalInforActivity;
import com.chuthuong.lthstore.activities.authActivities.LoginActivity;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.response.UserResponse;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {

    TextView txtNameUserProfile, btnLogout,btnLogin,txtHello, txtNumberAddress ;
    ImageView imageUserProfile;
    User user = null ;
    String accessToken ;
    String nameSharePreference = "account";
    private UserReaderSqlite userReaderSqlite;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userReaderSqlite = new UserReaderSqlite(getActivity(), "user.db", null, 1);
        if(userReaderSqlite.getUser()!=null) {
            accessToken = userReaderSqlite.getUser().getAccessToken();
            callApiMyAccount("Bearer " + accessToken);
        }
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        addControls(view);
        addEvents();
        return view;
    }

    private void addEvents() {
        imageUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user!=null){
                    startActivity(new Intent(getActivity(), PersonalInforActivity.class));
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userReaderSqlite.getUser()!=null){
                    userReaderSqlite.deleteUser(userReaderSqlite.getUser());
                    callApiLogout(accessToken);
                }else {
                    Log.e("Không","Có");
                }

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),LoginActivity.class));
            }
        });
        txtNumberAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ListShipmentDetailActivity.class));
            }
        });

    }


    private void addControls(View view) {
        txtNameUserProfile = view.findViewById(R.id.txt_name_user_profile);
        imageUserProfile = view.findViewById(R.id.image_user_profile);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnLogin = view.findViewById(R.id.btn_login);
        txtHello = view.findViewById(R.id.txt_hello);
        txtNumberAddress=view.findViewById(R.id.txt_number_address);
    }

    private void callApiMyAccount(String token){
        String accept ="application/json;versions=1";
        ApiService.apiService.getMyAccount(accept,token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful()){
                    UserResponse userResponse = response.body();
                    user = userResponse.getUser();
                    showHideLoggedIn(user);
                    render();
                    Log.e("user",user.toString());
                }else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Toast.makeText(getActivity(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("Lỗi server ", t.toString());
                Toast.makeText(getActivity(), "Lỗi server", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void callApiLogout(String token) {
        String accessToken = "Bearer " + token;
        ApiService.apiService.logoutUser(accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    userReaderSqlite.deleteUser(user);
                    setToast(getActivity(), response.body().getMessage());
                    startActivity(new Intent(getActivity(), MainActivity.class));
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(getActivity(),apiError.getMessage());
                        Log.e("Không",apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                setToast(getActivity(), "Lỗi server !");
            }
        });
    }

    private void showHideLoggedIn(User user){
        if(user!=null){
            btnLogin.setVisibility(View.GONE);
            txtHello.setVisibility(View.VISIBLE);
            txtNameUserProfile.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);
            imageUserProfile.setMaxWidth(250);
            imageUserProfile.setMaxHeight(250);
        }else {
            btnLogin.setVisibility(View.VISIBLE);
            txtHello.setVisibility(View.GONE);
            txtNameUserProfile.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
            imageUserProfile.setMaxWidth(310);
            imageUserProfile.setMaxHeight(310);
        }
    }
    private void render(){
        txtNameUserProfile.setText(user.getUsername());
        Glide.with(getActivity()).load(user.getAvatar()).into(imageUserProfile);
    }
    public void reloadData(){
        setToast(getActivity(),"Reload Fragment Profile");
    }
    private void setToast(Activity activity, String msg) {
        Toast toast = new Toast(activity);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_toast, (ViewGroup) activity.findViewById(R.id.layout_toast));
        TextView message = view.findViewById(R.id.message_toast);
        message.setText(msg);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

}