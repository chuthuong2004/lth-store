package com.chuthuong.lthstore.activities.authActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.MainActivity;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.ShowHidePassword;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText edtCurrentPassword, edtNewPassword, edtConfirmPassword;

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
        ShowHidePassword.showPassword(edtCurrentPassword);
        ShowHidePassword.showPassword(edtNewPassword);
        ShowHidePassword.showPassword(edtConfirmPassword);
    }

    public void changePassword(View view) {
        String currentPassword = edtCurrentPassword.getText().toString();
        String newPassword = edtNewPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu hiện tại !", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu mới !", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Vui lòng xác nhận mật khẩu !", Toast.LENGTH_SHORT).show();
            return;
        }
        callApiChangePassword(currentPassword, newPassword, confirmPassword);
    }

    private void callApiChangePassword(String currentPassword, String newPassword, String confirmPassword) {
        Intent intentReceiveFromUser = getIntent();
        User user = (User) intentReceiveFromUser.getSerializableExtra("user");
        Log.e("accessToken", user.getAccessToken());
        String token = user.getAccessToken();
        String accessToken = "Bearer "+token;
        String access = "application/json;versions=1";
        ApiService.apiService.resetPassword(access,accessToken,currentPassword,newPassword,confirmPassword).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Log.e("Message", response.body().getMessage()+"");
                    Toast.makeText(ChangePasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                    intent.putExtra("user", user);
                    startActivity(new Intent(intent));
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError =  gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Log.e("Message", apiError.getMessage());
                        Toast.makeText(ChangePasswordActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("Lỗi server ", t.toString());
                Toast.makeText(ChangePasswordActivity.this, "lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}