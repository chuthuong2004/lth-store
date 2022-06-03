package com.chuthuong.lthstore.activities.detailActivities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.MainActivity;
import com.chuthuong.lthstore.activities.MyCartActivity;
import com.chuthuong.lthstore.activities.PaymentActivity;
import com.chuthuong.lthstore.activities.authActivities.LoginActivity;
import com.chuthuong.lthstore.adapter.ViewPagerDetailProductAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.response.CartResponse;
import com.chuthuong.lthstore.model.Product;
import com.chuthuong.lthstore.model.ProductDetail;
import com.chuthuong.lthstore.model.ProductDetailColor;
import com.chuthuong.lthstore.model.ProductImage;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.response.ProductResponse;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.utils.Util;
import com.chuthuong.lthstore.widget.CustomProgressDialog;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    TextView rating, name, currentPrice, price, discount, quantitySold, likeCount;
    ImageView imgFavoriteProduct;
    RatingBar ratingBar;

    User user;
    ImageView imgCart;
    TextView quantityCart;


    TextView chatWithShop, addToCart, buyNow;
    TextView txtSize;
    LinearLayout linearLayoutSize;
    // Product
    Product product = null;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPagerDetailProductAdapter viewPagerDetailProductAdapter;
    private CartResponse cartResponse = null;
    CustomProgressDialog dialogAddItem, dialogMyCart;
    String productID;
    private UserReaderSqlite userReaderSqlite;
    private String strSize, strColor;
    private List<ProductDetail> details;
    private RadioGroup radioGroupSize, radioGroupColor;
    private CustomProgressDialog dialogFavorite;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        userReaderSqlite = new UserReaderSqlite(this, "user.db", null, 1);
        Util.refreshToken(this);
        user = userReaderSqlite.getUser();
        addControls();
        productID = getIntent().getStringExtra("product_id");
        callProduct(productID);
        addEvents();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadProduct(Product product) {
        Util.refreshToken(ProductDetailActivity.this);
        user = userReaderSqlite.getUser();
        if (product != null) {
            loadData(product);
            viewPagerDetailProductAdapter = new ViewPagerDetailProductAdapter(this);
            viewPager2.setAdapter(viewPagerDetailProductAdapter);
            new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
                switch (position) {
                    case 1:
                        tab.setText(R.string.strTitleReviewTablayout);
                        break;
                    case 2:
                        tab.setText(R.string.strTitleSuggesForYou);
                        break;
                    case 0:
                    default:
                        tab.setText(R.string.strTitleProductTablayout);
                        break;
                }
            }).attach();
            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }

                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    viewPagerDetailProductAdapter.notifyItemChanged(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    super.onPageScrollStateChanged(state);
                }
            });
            handleToolbar();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addEvents() {
        imgFavoriteProduct.setOnClickListener(v -> {
            Util.refreshToken(ProductDetailActivity.this);
            user = userReaderSqlite.getUser();
            if (user != null) {
                if(product.getFavorites().contains(user.getId())) {
                    callApiRemoveFavorite(user.getAccessToken(), productID);
                }else {
                    callApiAddFavorite(user.getAccessToken(), productID);
                }
            } else {
                openDialogRequestLogin();
            }
        });
        chatWithShop.setOnClickListener(v -> {
            Util.refreshToken(ProductDetailActivity.this);
            user = userReaderSqlite.getUser();
            if (user != null) {
                setToast(ProductDetailActivity.this, "Chức năng chat với shop đang cập nhật !");
            } else {
                openDialogRequestLogin();
            }
        });
        addToCart.setOnClickListener(v -> {
            Util.refreshToken(ProductDetailActivity.this);
            user = userReaderSqlite.getUser();
            if (user != null) {
                if (strSize != "" && strColor != "") {
                    Util.refreshToken(ProductDetailActivity.this);
                    user = userReaderSqlite.getUser();
                    dialogAddItem = new CustomProgressDialog(ProductDetailActivity.this);
                    dialogAddItem.show();
                    callApiAddItemToCart("Bearer " + user.getAccessToken(), product.getId(),
                            strSize, strColor, 1);
                } else {
                    setToast(ProductDetailActivity.this, "Vui lòng chọn size và color");
                }
            } else {
                openDialogRequestLogin();
            }
        });
        buyNow.setOnClickListener(v -> {
            Util.refreshToken(ProductDetailActivity.this);
            user = userReaderSqlite.getUser();
            if (user != null) {
                if (strSize != "" && strColor != "") {
                    Util.refreshToken(ProductDetailActivity.this);
                    user = userReaderSqlite.getUser();
                    dialogAddItem = new CustomProgressDialog(ProductDetailActivity.this);
                    dialogAddItem.show();
                    callApiAddItemToCart("Bearer " + user.getAccessToken(), product.getId(),
                            strSize, strColor, 1);
                    startActivity(new Intent(ProductDetailActivity.this, PaymentActivity.class));
                } else {
                    setToast(ProductDetailActivity.this, "Vui lòng chọn size và color");
                }
            } else {
                openDialogRequestLogin();
            }
        });
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.refreshToken(ProductDetailActivity.this);
                user = userReaderSqlite.getUser();
                if (user == null) {
                    openDialogRequestLogin();
                }
                // xử lý render cart
                else {
                    Intent intent = new Intent(ProductDetailActivity.this, MyCartActivity.class);
                    intent.putExtra("title_my_cart", getResources().getString(R.string.strTitleMyCart));
                    startActivity(intent);
                }
            }
        });
    }

    private void openDialogRequestLogin() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_login);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        if (Gravity.CENTER == Gravity.CENTER) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }
        TextView cancel = dialog.findViewById(R.id.dialog_cancel);
        TextView login = dialog.findViewById(R.id.dialog_login);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    private void handleToolbar() {
        ImageView imgBack, imgSearch, imgHome;
        imgBack = findViewById(R.id.img_back_detail_toolbar);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Util.refreshToken(ProductDetailActivity.this);
                user = userReaderSqlite.getUser();
                onBackPressed();
            }
        });
        imgSearch = findViewById(R.id.search_img_toolbar);
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Util.refreshToken(ProductDetailActivity.this);
                setToast(ProductDetailActivity.this, "Chức năng tìm kiếm đang cập nhật !");
            }
        });

        imgHome = findViewById(R.id.home_img_toolbar);
        imgHome.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Util.refreshToken(ProductDetailActivity.this);
                startActivity(new Intent(ProductDetailActivity.this, MainActivity.class));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadData(Product product) {
        Util.refreshToken(ProductDetailActivity.this);
        user = userReaderSqlite.getUser();
        loadCart();
        List<ProductImage> productImages = product.getImages();
        // image slider
        ImageSlider imageSlider = findViewById(R.id.image_slider_detail);
        List<SlideModel> slideModelList = new ArrayList<>();
        for (int i = 0; i < productImages.size(); i++) {
            slideModelList.add(new SlideModel(productImages.get(i).getImg(), ScaleTypes.CENTER_CROP));
        }
        imageSlider.setImageList(slideModelList);
        name.setText(product.getName());
        float rate = product.getRate();
        rating.setText((float) Math.round(rate * 10) / 10 + "");
        discount.setText("-" + product.getDiscount() + "%");
        ratingBar.setRating(product.getRate());
        NumberFormat formatter = new DecimalFormat("#,###");
        String formatterPriceProduct = formatter.format(product.getPrice() - (product.getPrice() * product.getDiscount() / 100));
        price.setText(formatterPriceProduct + "đ");
        quantitySold.setText(product.getQuantitySold() + "");
        if (product.getDiscount() != 0) {
            String formatterCurrentPriceProduct = formatter.format(product.getPrice());
            currentPrice.setText(formatterCurrentPriceProduct + "đ");
        } else {
            currentPrice.setVisibility(View.GONE);
        }
        loadFavorite(product);
        details = product.getDetail();
        renderSizeColor(details);
    }

    private void loadFavorite(Product product) {
        likeCount.setText(product.getLikeCount() + "");
        if(user!=null){
            if(product.getFavorites().contains(user.getId())) {
                imgFavoriteProduct.setImageResource(R.drawable.ic_baseline_favorite_24);
            }else {
                imgFavoriteProduct.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    private void renderSizeColor(List<ProductDetail> productDetails) {
        radioGroupSize = findViewById(R.id.rad_group_size);
        radioGroupSize.removeAllViews();
        radioGroupColor = findViewById(R.id.rad_group_color);
        radioGroupColor.setVisibility(View.GONE);
        strColor = "";
        strSize = "";
        for (int i = 0; i < productDetails.size(); i++) {
            Log.e("i", i + "");
            RadioButton radSize = new RadioButton(ProductDetailActivity.this);
            // run máy ảo thì set 120, máy thật set 60
            RadioGroup.LayoutParams layoutParamsSize = new RadioGroup.LayoutParams(120, 120);
            // run máy ảo thì set 25, máy thật set 10
            layoutParamsSize.setMarginStart(25);
            radSize.setLayoutParams(layoutParamsSize);
            radSize.setBackground(getResources().getDrawable(R.drawable.radio_button_size_selector));
            radSize.setButtonDrawable(getResources().getDrawable(R.color.transparent));
            radSize.setGravity(Gravity.CENTER);
            radSize.setText(productDetails.get(i).getSize());
            radSize.setTypeface(Typeface.DEFAULT_BOLD);
            radSize.setTextColor(getResources().getColor(R.drawable.radio_button_text_size));
            radioGroupSize.addView(radSize);
        }
        radioGroupSize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                strColor = "";
                radioGroupColor.setVisibility(View.VISIBLE);
                RadioButton radChecked = findViewById(checkedId);
                if (radChecked != null) {
                    strSize = radChecked.getText().toString();
                    for (int i = 0; i < productDetails.size(); i++) {
                        if (productDetails.get(i).getSize().equals(radChecked.getText().toString())) {
                            radioGroupColor.removeAllViews();
                            List<ProductDetailColor> productDetailColors = productDetails.get(i).getDetailColor();
                            for (int j = 0; j < productDetailColors.size(); j++) {
                                RadioButton radColor = new RadioButton(ProductDetailActivity.this);
                                RadioGroup.LayoutParams layoutParamsColor = new RadioGroup.LayoutParams(120, 120);
                                layoutParamsColor.setMarginStart(25);
                                radColor.setLayoutParams(layoutParamsColor);
                                radColor.setBackground(getResources().getDrawable(R.drawable.radio_button_color_selector));
                                radColor.setButtonDrawable(getResources().getDrawable(R.color.transparent));
                                radColor.setGravity(Gravity.CENTER);
                                radColor.setText(productDetailColors.get(j).getColor());
                                radColor.setTextSize(1);
                                if (productDetailColors.get(j).getColor().toLowerCase().equals("orange")) {
                                    radColor.setTextColor(Color.parseColor("#FFA500"));
                                    radColor.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFA500")));
                                }
                                else if (productDetailColors.get(j).getColor().toLowerCase().equals("dark red")) {
                                    radColor.setTextColor(Color.parseColor("#7f0000"));
                                    radColor.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7f0000")));
                                } else {
                                    radColor.setTextColor(Color.parseColor(productDetailColors.get(j).getColor()));
                                    radColor.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(productDetailColors.get(j).getColor())));
                                }

                                if (radColor.isChecked() == false) {
                                    radColor.setForeground(null);
                                }
                                radColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (!isChecked) {
                                            radColor.setForeground(null);
                                        }
                                    }
                                });
                                radioGroupColor.addView(radColor);
                            }
                        }
                    }
                }
            }
        });
        radioGroupColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radCheckedColor = findViewById(checkedId);
                if (radCheckedColor != null) {
                    strColor = radCheckedColor.getText().toString();
                    radCheckedColor.setForeground(getResources().getDrawable(R.drawable.ic_baseline_check_24));
                    radCheckedColor.setForegroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.pink)));
                }

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadCart() {
        if (user != null) {
            Util.refreshToken(ProductDetailActivity.this);
            user = userReaderSqlite.getUser();
            dialogMyCart = new CustomProgressDialog(ProductDetailActivity.this);
            callApiGetMyCart("Bearer " + user.getAccessToken());
        }
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

    private void callApiAddItemToCart(String token, String productID, String size, String color, int quantity) {
        String accept = "application/json;versions=1";
        ApiService.apiService.addItemToCart(accept, token, productID, size, color, quantity).enqueue(new Callback<CartResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    cartResponse = response.body();
                    dialogAddItem.dismiss();
                    if (cartResponse.getCart().getCartItems() != null) {
                        int sizeCart = cartResponse.getCart().getCartItems().size();
                        if (sizeCart > 0) {
                            quantityCart.setText(sizeCart + "");
                            quantityCart.setVisibility(View.VISIBLE);
                        }
                        strSize = "";
                        strColor = "";
                        radioGroupSize.clearCheck();
                        radioGroupColor.setVisibility(View.GONE);
                        setToast(ProductDetailActivity.this, cartResponse.getMessage());
                    }
                } else {
                    dialogAddItem.dismiss();
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(ProductDetailActivity.this, apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                dialogAddItem.dismiss();
                setToast(ProductDetailActivity.this, "Lỗi server !");
            }
        });
    }

    private void callApiGetMyCart(String accessToken) {
        String accept = "application/json;versions=1";
        ApiService.apiService.getMyCart(accept, accessToken).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    cartResponse = response.body();
                    dialogMyCart.dismiss();
                    if (cartResponse.getCart().getCartItems() != null) {
                        int size = cartResponse.getCart().getCartItems().size();
                        if (size > 0) {
                            quantityCart.setText(size + "");
                            quantityCart.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        quantityCart.setVisibility(View.GONE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                setToast(ProductDetailActivity.this, "Lỗi server !");
            }
        });
    }

    private void callApiGetProduct(String id) {
        ApiService.apiService.getProduct(id).enqueue(new Callback<ProductResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful()) {
                    ProductResponse productResponse = response.body();
                    product = productResponse.getProduct();
                    Log.e("product", product.toString());
                    loadProduct(product);
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(ProductDetailActivity.this, apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                setToast(ProductDetailActivity.this, "Lỗi server !");
            }
        });
    }
    private  void callApiAddFavorite(String accessToken, String productID) {
        String token = "Bearer "+ accessToken;
        ApiService.apiService.addFavorite(token, productID).enqueue(new Callback<ProductResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if(response.isSuccessful()) {
                    product = response.body().getProduct();
                    loadFavorite(product);
                }else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(ProductDetailActivity.this, apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                setToast(ProductDetailActivity.this, "Lỗi server");
            }
        });
    }
    private  void callApiRemoveFavorite(String accessToken, String productID) {
        String token = "Bearer "+ accessToken;
        ApiService.apiService.removeFavorite(token, productID).enqueue(new Callback<ProductResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if(response.isSuccessful()) {
                    product = response.body().getProduct();
                    loadFavorite(product);
                }else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(ProductDetailActivity.this, apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                setToast(ProductDetailActivity.this, "Lỗi server");
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void callProduct(String id) {
        Util.refreshToken(ProductDetailActivity.this);
        user = userReaderSqlite.getUser();
        if (id != null) {
            callApiGetProduct(id);
        }
    }

    private void addControls() {
        rating = findViewById(R.id.detail_product_rating);
        name = findViewById(R.id.detail_product_name);
        price = findViewById(R.id.detail_product_price);
        currentPrice = findViewById(R.id.detail_product_current_price);
        quantitySold = findViewById(R.id.detail_product_sold);
        likeCount = findViewById(R.id.detail_product_favorite);
        discount = findViewById(R.id.detail_product_discount);
        ratingBar = findViewById(R.id.detail_product_rating_bar);
        linearLayoutSize = findViewById(R.id.linear_layout_size);
        tabLayout = findViewById(R.id.tab_layout_detail);
        viewPager2 = findViewById(R.id.view_pager_detail);
        imgFavoriteProduct = findViewById(R.id.img_favorite_product);
        chatWithShop = findViewById(R.id.txt_chat_message_navigation_detail);
        addToCart = findViewById(R.id.txt_add_to_cart_navigation_detail);
        buyNow = findViewById(R.id.txt_buy_now_navigation_detail);
        imgCart = findViewById(R.id.cart_img_toolbar);
        quantityCart = findViewById(R.id.quantity_cart_toolbar);
        userReaderSqlite = new UserReaderSqlite(this, "user.db", null, 1);
        user = userReaderSqlite.getUser();
    }

    public Product getProduct() {
        return product;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        Util.refreshToken(this);
        user = userReaderSqlite.getUser();
        callProduct(productID);
        loadCart();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        Util.refreshToken(this);
        user = userReaderSqlite.getUser();
    }
}