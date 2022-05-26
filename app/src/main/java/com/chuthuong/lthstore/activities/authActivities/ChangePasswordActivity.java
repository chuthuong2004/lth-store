package com.chuthuong.lthstore.activities.authActivities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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
    ImageView back, backToHome;
    private UserReaderSqlite userReaderSqlite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        addControls();
        addEvents();
    }

    private void addEvents() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
            }
        });
    }

    private void addControls() {
        edtCurrentPassword = findViewById(R.id.edit_text_current_password);
        edtNewPassword = findViewById(R.id.edit_text_new_password);
        edtConfirmPassword = findViewById(R.id.edit_text_confirm_password);
        back = findViewById(R.id.img_back_detail);
        backToHome = findViewById(R.id.back_to_home);
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

        userReaderSqlite = new UserReaderSqlite(this, "user.db", null, 1);
        Util.refreshToken(this);
        User user = userReaderSqlite.getUser();
        callApiChangePassword(user.getAccessToken(),currentPassword, newPassword, confirmPassword);
    }

    public void saveAccount() {
        SharedPreferences preferences = getSharedPreferences("account", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", "");
        editor.putString("password", "");
        editor.commit(); // xác nhận lưu
    }
    private void callApiChangePassword(String accessToken, String currentPassword, String newPassword, String confirmPassword) {
        String token = "Bearer "+accessToken;
        String accept = "application/json;versions=1";
        ApiService.apiService.resetPassword(accept,token,currentPassword,newPassword,confirmPassword).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    saveAccount();
                    openDialogLogin();
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
    private void openDialogLogin() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_update_pasword);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(false);
        TextView login = dialog.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accessToken = userReaderSqlite.getUser().getAccessToken();
                userReaderSqlite.deleteUser(userReaderSqlite.getUser());
                callApiLogout(accessToken);

            }
        });
        dialog.show();
    }
    private void callApiLogout(String token) {
        String accessToken = "Bearer " + token;
        ApiService.apiService.logoutUser(accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Log.e("Không",apiError.getMessage());
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