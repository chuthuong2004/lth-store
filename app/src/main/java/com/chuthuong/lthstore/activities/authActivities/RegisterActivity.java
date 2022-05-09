package com.chuthuong.lthstore.activities.authActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.OnBoardingActivity;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.ShowHidePassword;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    EditText edtUsername, edtEmail, edtPassword;
//    private FirebaseAuth auth;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        auth = FirebaseAuth.getInstance();
//        if (auth.getCurrentUser() != null) {
//            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
//            finish();
//        }
        addControls();

    }

    private void addControls() {
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        sharedPreferences = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("firstTime", true);
        if (isFirstTime) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();

            Intent intent = new Intent(RegisterActivity.this, OnBoardingActivity.class);
            startActivity(intent);
            finish();
        }
        ShowHidePassword.showPassword(edtPassword);
    }

    public void signUpRegisterClicked(View view) {
        String username = edtUsername.getText().toString();
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Vui lòng nhập tên đăng nhập !", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ email !", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu !", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu quá ngắn, hãy nhập tối thiểu 6 ký tự!", Toast.LENGTH_SHORT).show();
            return;
        }
        callApiRegister(username, email, password);

    }

    private void callApiRegister(String username, String email, String password) {

        ApiService.apiService.createUser(username, email, password).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Log.e("Message", response.body().getMessage());
                    Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                } else {
//                    switch (response.code()){
//                        case 400:
//                            Log.e("Message", "Email đã tồn tại !");
//                            Toast.makeText(RegisterActivity.this, "Email đã tồn tại !", Toast.LENGTH_SHORT).show();
//                            break;
//                        case 500:
//                            Log.e("Message", "Lỗi server !");
//                            Toast.makeText(RegisterActivity.this, "Lỗi server !", Toast.LENGTH_SHORT).show();
//                            break;
//                    }
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError =  gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Log.e("Message", apiError.getMessage());
                        Toast.makeText(RegisterActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("Lỗi server !", t.toString());
                Toast.makeText(RegisterActivity.this, "lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void signInRegisterClicked(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

}