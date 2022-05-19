package com.chuthuong.lthstore.activities.authActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.chuthuong.lthstore.model.Account;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.utils.Util;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword;
    String nameSharePreference = "account";
    private UserReaderSqlite userReaderSqlite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        addControls();
        Account account = getAccount();
        edtUsername.setText(account.getUsername());
        edtPassword.setText(account.getPassword());
    }

    private void addControls() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        // show password
        Util.showPassword(edtPassword);
    }

    public void signIn(View view) {
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();
        callApiLogin(username, password);
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
    private void callApiLogin(String username, String password) {
        ApiService.apiService.loginUser(username, password).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    userReaderSqlite = new UserReaderSqlite(LoginActivity.this, "user.db", null, 1);
                    userReaderSqlite.insertUser(user);
                    saveAccount(username, password);
                    setToast(LoginActivity.this,"Đăng nhập thành công !");
                    finish();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));

//                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_animation);
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(LoginActivity.this, apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                setToast(LoginActivity.this,"Lỗi server !");
            }
        });
    }

    public void saveAccount(String username, String password) {
        SharedPreferences preferences = getSharedPreferences(nameSharePreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit(); // xác nhận lưu
    }

    public Account getAccount() {
        Account acc = new Account();
        SharedPreferences preferences = getSharedPreferences(nameSharePreference, MODE_PRIVATE);
        String username = preferences.getString("username", "");
        String password = preferences.getString("password", "");
        acc.setUsername(username);
        acc.setPassword(password);
        return acc;
    }

    public void signup(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    public void forgotPassword(View view) {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }
}