package com.chuthuong.lthstore.activities.authActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.MainActivity;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.Account;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.ShowHidePassword;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword;
    String nameSharePreference = "account";
    public static User user = null;

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
        ShowHidePassword.showPassword(edtPassword);
    }

    public void signIn(View view) {
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();
        callApiLogin(username, password);
    }

    private void callApiLogin(String username, String password) {
        ApiService.apiService.loginUser(username, password).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User newUser = response.body();
                    user = newUser;
                    saveAccount(username, password, newUser.getRefreshToken(), newUser.getAccessToken());

                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công !", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("user", user);
                    Log.e("USER", user.toString());
                    startActivity(intent);

//                    startActivity(Intent.getIntent());
                    finish();
//                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_animation);
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Log.e("Message", apiError.getMessage());
                        Toast.makeText(LoginActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("Lỗi", t.toString());
                Toast.makeText(LoginActivity.this, "lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveAccount(String username, String password, String refreshToken, String accessToken) {
        SharedPreferences preferences = getSharedPreferences(nameSharePreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("refreshToken", refreshToken);
        editor.putString("accessToken", accessToken);
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