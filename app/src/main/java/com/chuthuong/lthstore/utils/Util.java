package com.chuthuong.lthstore.utils;

import android.content.Context;
import android.os.Build;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Base64;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Util {
    private static UserReaderSqlite userReaderSqlite;
    public static String URI_API = "http://web-api-chuthuong.herokuapp.com/api/v2/";
    public static String URI_API_PROVINCES = "https://provinces.open-api.vn/api/";
    private static User user;

    public static void showPassword(EditText edtPassword) {
        // hiển thị mật khẩu
        edtPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= (edtPassword.getRight() - (edtPassword.getLeft() / 2) - edtPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (edtPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                            edtPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_vpn_key_24, 0, R.drawable.ic_baseline_remove_red_eye_24, 0);
                        } else {
                            edtPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_vpn_key_24, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }

                        return true;
                    }
                }
                return false;
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void refreshToken(Context context){
        userReaderSqlite = new UserReaderSqlite(context, "user.db", null, 1);
        user = userReaderSqlite.getUser();
        if (user == null) {
            Log.e("K có", "không có");
        } else {
            long currentSecond = System.currentTimeMillis() / 1000;
            String accessToken = user.getAccessToken();
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String[] verifyTokens = accessToken.split("\\.");
            String header = new String(decoder.decode(verifyTokens[0]));
            String payload = new String(decoder.decode(verifyTokens[1]));
            String p = payload.split(",")[3];
            String newPayload = p.substring(0, p.length() - 1);
            long exp = Long.parseLong(newPayload.split(":")[1]);
            if (exp < currentSecond) {
                callApiRefreshToken(user.getRefreshToken());
            }
        }
    }

    private static void callApiRefreshToken(String refreshToken) {
        ApiService.apiService.refreshToken(refreshToken).enqueue(new Callback<ApiToken>() {
            @Override
            public void onResponse(Call<ApiToken> call, Response<ApiToken> response) {
                if(response.isSuccessful()) {
                    ApiToken apiToken= response.body();
                    user.setAccessToken(apiToken.getAccessToken());
                    user.setRefreshToken(apiToken.getRefreshToken());
                    userReaderSqlite.updateUser(user);
                }
                else     {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Log.e("Refresh api", apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiToken> call, Throwable t) {
                Log.e("Lỗi refresh", t.toString());
            }
        });
    }
}
