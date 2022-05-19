package com.chuthuong.lthstore.activities.authActivities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.MainActivity;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.utils.Util;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText edtCurrentPassword, edtNewPassword, edtConfirmPassword;
    private UserReaderSqlite userReaderSqlite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        addControls();
    }

    private void addControls() {
        edtCurrentPassword = findViewById(R.id.edtCurrentPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        Util.showPassword(edtCurrentPassword);
        Util.showPassword(edtNewPassword);
        Util.showPassword(edtConfirmPassword);
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
    public void changePassword(View view) {
        String currentPassword = edtCurrentPassword.getText().toString();
        String newPassword = edtNewPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();
//
//        if (TextUtils.isEmpty(currentPassword)) {
//            Toast.makeText(this, "Vui lòng nhập mật khẩu hiện tại !", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (TextUtils.isEmpty(newPassword)) {
//            Toast.makeText(this, "Vui lòng nhập mật khẩu mới !", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (TextUtils.isEmpty(confirmPassword)) {
//            Toast.makeText(this, "Vui lòng xác nhận mật khẩu !", Toast.LENGTH_SHORT).show();
//            return;
//        }

        userReaderSqlite = new UserReaderSqlite(this, "user.db", null, 1);
        Util.refreshToken(this);
        User user = userReaderSqlite.getUser();
        callApiChangePassword(user.getAccessToken(),currentPassword, newPassword, confirmPassword);
    }

    private void callApiChangePassword(String accessToken,String currentPassword, String newPassword, String confirmPassword) {
        String token = "Bearer "+accessToken;
        String accept = "application/json;versions=1";
        ApiService.apiService.resetPassword(accept,token,currentPassword,newPassword,confirmPassword).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    setToast(ChangePasswordActivity.this, response.body().getMessage());
                    Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                    startActivity(new Intent(intent));
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError =  gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(ChangePasswordActivity.this,apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                setToast(ChangePasswordActivity.this, "Lỗi server !");
            }
        });
    }
}