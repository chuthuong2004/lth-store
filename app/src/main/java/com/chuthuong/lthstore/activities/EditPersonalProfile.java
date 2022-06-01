package com.chuthuong.lthstore.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.ShipmentDetail;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.response.UserResponse;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.RealPathUtil;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.utils.Util;
import com.chuthuong.lthstore.widget.CustomProgressDialog;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPersonalProfile extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 24;
    ImageView imgViewPersonal;
    ImageView back, backToHome;
    CardView cardView;
    EditText edtNamePersonal, edtAgePersonal, edtPhonePersonal, edtEmailPersonal ;
    CircleImageView circleImageViewAvatar;
    TextView txtSave, edtAddressPersonal;
    User user = null;
    private UserReaderSqlite userReaderSqlite;
    private CustomProgressDialog customProgressDialog;
    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if(data==null) {
                            return;
                        }
                        Uri uri = data.getData();
                        Util.refreshToken(EditPersonalProfile.this);
                        customProgressDialog = new CustomProgressDialog(EditPersonalProfile.this);
                        if (userReaderSqlite.getUser() != null) {
                            callApiUpdateAvatar("Bearer "+userReaderSqlite.getUser().getAccessToken() ,uri);
                        }

                    }
                }
            });

    private void callApiUpdateAvatar(String token,Uri uri) {
        customProgressDialog.show();
        String strRealPath = RealPathUtil.getRealPath(this,uri);
        Log.e("str", strRealPath);
        File file = new File(strRealPath);
        RequestBody requestBody  =RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part mulPartBody = MultipartBody.Part.createFormData("avatar", file.getName(), requestBody);
        ApiService.apiService.updateAvatar(token,mulPartBody).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()){
                    user = response.body().getUser();
                    Log.e("avt", user.getAvatar());
                    render();
                    customProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                    setToast(EditPersonalProfile.this, "Lỗi server !");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_profile);
        addControls();
        addEvent();
        userReaderSqlite = new UserReaderSqlite(this, "user.db", null, 1);
        Util.refreshToken(this);
        if (userReaderSqlite.getUser() != null) {
            callApiMyAccount(userReaderSqlite.getUser().getAccessToken());
        }
    }

    private void addEvent() {
        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToast(EditPersonalProfile.this, "Đợi tui cập nhật API");
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckRequestPermission();
            }
        });
        circleImageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckRequestPermission();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditPersonalProfile.this, MainActivity.class));
            }
        });
    }

    private void onCheckRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission, MY_REQUEST_CODE);
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent,"Chọn ảnh"));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==MY_REQUEST_CODE) {
            if(grantResults.length> 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    private void render() {
        Glide.with(this).load(user.getAvatar()).into(imgViewPersonal);
        edtEmailPersonal.setText(user.getEmail());
        ShipmentDetail shipmentDetail = findDefaultShipmentDetail(user);
        if (shipmentDetail != null) {
            edtNamePersonal.setText(shipmentDetail.getFullName());
            edtAddressPersonal.setText(shipmentDetail.getAddress() + ", " + shipmentDetail.getWard() + ", " + shipmentDetail.getDistrict() + ", " + shipmentDetail.getProvince());
            if (shipmentDetail.getPhone() != null) {
                edtPhonePersonal.setText(shipmentDetail.getPhone());
            }

        }
    }


    private void addControls() {
        imgViewPersonal = findViewById(R.id.image_personal_information);
        edtNamePersonal = findViewById(R.id.edit_text_name);
        edtAgePersonal = findViewById(R.id.edit_text_age);
        edtPhonePersonal = findViewById(R.id.edit_text_number_phone);
        edtEmailPersonal = findViewById(R.id.edit_text_email);
        edtAddressPersonal = findViewById(R.id.edit_text_address);
        txtSave = findViewById(R.id.txt_save);
        cardView = findViewById(R.id.image_camera);
        circleImageViewAvatar =findViewById(R.id.image_personal_information);
        back = findViewById(R.id.img_back_detail);
        backToHome = findViewById(R.id.back_to_home);
    }

    private ShipmentDetail findDefaultShipmentDetail(User user) {
        List<ShipmentDetail> shipmentDetails = user.getShipmentDetails();
        if (shipmentDetails == null) return null;
        if (shipmentDetails.size() == 0) return shipmentDetails.get(0);
        for (int i = 0; i < shipmentDetails.size(); i++) {
            if (shipmentDetails.get(i).isDefault() == true) {
                return shipmentDetails.get(i);
            }
        }
        return shipmentDetails.get(0);
    }

    private void callApiMyAccount(String token) {
        String tokenUser = "Bearer " + token;
        String accept = "application/json;versions=1";
        ApiService.apiService.getMyAccount(accept, tokenUser).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    UserResponse userResponse = response.body();
                    user = userResponse.getUser();
                    render();
                    Log.e("user", user.toString());
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(EditPersonalProfile.this, apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                setToast(EditPersonalProfile.this, "Lỗi server !");
            }
        });
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
}