package com.chuthuong.lthstore.activities.detailActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.MainActivity;
import com.chuthuong.lthstore.activities.MyCartActivity;
import com.chuthuong.lthstore.activities.authActivities.LoginActivity;
import com.chuthuong.lthstore.adapter.ViewPagerDetailProductAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.fragments.DescriptionProductFragment;
import com.chuthuong.lthstore.fragments.HomeFragment;
import com.chuthuong.lthstore.fragments.ReviewProductFragment;
import com.chuthuong.lthstore.fragments.SearchFragment;
import com.chuthuong.lthstore.fragments.SuggestionProductFragment;
import com.chuthuong.lthstore.model.Account;
import com.chuthuong.lthstore.model.CartResponse;
import com.chuthuong.lthstore.model.Product;
import com.chuthuong.lthstore.model.ProductDetail;
import com.chuthuong.lthstore.model.ProductImage;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.utils.ApiResponse;
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
    TextView rating, name, description,currentPrice, price, discount, quantitySold, likeCount;
    ImageView imgFavoriteProduct;
    RatingBar ratingBar;

    User user;
    ImageView imgCart;
    TextView quantityCart;


    TextView chatWithShop, addToCart, buyNow;

    LinearLayout linearLayoutSize;
    // Product
    Product product = null;
    private int count = 0;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPagerDetailProductAdapter viewPagerDetailProductAdapter;
    private CartResponse cart;
    Bundle save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        save = savedInstanceState;
        setContentView(R.layout.activity_product_detail);
        Toast.makeText(this, "[ProductActivity] onCreate", Toast.LENGTH_SHORT).show();
        addControls();
        addEvents();
        final Product obj = (Product) getIntent().getSerializableExtra("product_detail");
        if (obj instanceof Product) {
            product = (Product) obj;

        }
        Log.e("obj","Ok");
        if(product!=null){
            loadData();
//            ReviewProductFragment reviewProductFragment = new ReviewProductFragment();
//            loadFragment(reviewProductFragment);
//            DescriptionProductFragment descriptionProductFragment = new DescriptionProductFragment();
//            loadFragment(descriptionProductFragment);
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

    private void addEvents() {
        imgFavoriteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProductDetailActivity.this, "Chức năng yêu thích sản phẩm đang cập nhật !", Toast.LENGTH_SHORT).show();
            }
        });
        chatWithShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProductDetailActivity.this, "Chức năng chat với shop đang cập nhật !", Toast.LENGTH_SHORT).show();
            }
        });
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProductDetailActivity.this, "Thêm vào giỏ hàng không thành công ! Chức năng thêm đang cập nhật !", Toast.LENGTH_SHORT).show();
            }
        });
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProductDetailActivity.this, "Chức năng mua ngay đang cập nhật !", Toast.LENGTH_SHORT).show();
            }
        });
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user == null) {
                    openDialogRequestLogin();
                }
                // xử lý render cart
                else {
                    // start vô cart
                    Toast.makeText(ProductDetailActivity.this, "vô cart", Toast.LENGTH_SHORT).show();

                    Log.e("GỬi Cart", cart.getCart().toString() );
                    Intent intent = new Intent(ProductDetailActivity.this, MyCartActivity.class);
                    intent.putExtra("my_cart", cart.getCart());
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
                intent.putExtra("intent", "ProductActivity");
                startActivity(intent);
            }
        });
        dialog.show();
    }

    private void handleToolbar() {
        ImageView imgBack, imgSearch, imgHome;
        imgBack = findViewById(R.id.img_back_detail_toolbar);
        imgBack.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });
        imgSearch = findViewById(R.id.search_img_toolbar);
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                Fragment searchFragment = new SearchFragment();
//                fragmentTransaction.add(R.id.search_container,searchFragment );
////                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_out_right,
////                        R.anim.slide_out_right);
//                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,0);
//                fragmentTransaction.show(searchFragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
                Toast.makeText(ProductDetailActivity.this, "Chức năng tìm kiếm đang cập nhật !", Toast.LENGTH_SHORT).show();
            }
        });

        imgHome = findViewById(R.id.home_img_toolbar);
        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductDetailActivity.this,MainActivity.class));
            }
        });
    }

    private void loadData() {
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
//            description.setText(product.getDesProduct());
        rating.setText(product.getRate()+"");
        discount.setText("-"+product.getDiscount()+"%");

        ratingBar.setRating(product.getRate());
        NumberFormat formatter = new DecimalFormat("#,###");
        String formatterPriceProduct = formatter.format(product.getPrice() - (product.getPrice() * product.getDiscount() / 100));
        price.setText(formatterPriceProduct + "đ");
        likeCount.setText(product.getLikeCount() + "");
        quantitySold.setText(product.getQuantitySold() + "");
        if (product.getDiscount() != 0) {
            String formatterCurrentPriceProduct = formatter.format(product.getPrice());
            currentPrice.setText(formatterCurrentPriceProduct + "đ");
        } else {
            currentPrice.setText("");
        }
        List<ProductDetail> details = product.getDetail();
        Log.e("size",details.toString());
        for (int i = 0; i < details.size(); i++) {
            TextView textView = new TextView(ProductDetailActivity.this);
            textView.setText(details.get(i).getSize()+"");
            textView.setBackgroundResource(R.drawable.image_view_bg_circle);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (100, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 20, 0);
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER);
            linearLayoutSize.addView(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @Override

                public void onClick(View v) {
                    count++;
                    if(count>1) {
                        textView.setTextColor(getResources().getColor(R.color.pink));
                        textView.setBackgroundResource(R.drawable.image_view_bg_circle);
                        count = 0;
                    } else {
                        textView.setTextColor(getResources().getColor(R.color.white));
                        textView.setBackgroundResource(R.drawable.image_view_bg_circle_pink);

                    }
                }
            });
        }
        Log.e("size", product.getDetail().size()+"");
    }

    private void loadCart() {
        if (user == null) {
            Toast.makeText(ProductDetailActivity.this, "Không có user", Toast.LENGTH_SHORT).show();
        } else {
            callApiGetMyCart("Bearer " + user.getAccessToken());

        }
    }
    private void callApiGetMyCart(String accessToken) {
        Toast.makeText(this, accessToken, Toast.LENGTH_SHORT).show();
        Log.e("ACCESS", accessToken);
        String accept = "application/json;versions=1";
        ApiService.apiService.getMyCart(accept, accessToken).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    cart = response.body();
                    int size = cart.getCart().getCartItems().size();
                    quantityCart.setText(size+"");
                    quantityCart.setVisibility(View.VISIBLE);
                    Log.e("cart", cart.getCart().toString());
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Log.e("Message", apiError.getMessage());
                        Toast.makeText(ProductDetailActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Log.e("Lỗi server ", t.toString());
                Toast.makeText(ProductDetailActivity.this, "lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addControls() {
        rating = findViewById(R.id.detail_product_rating);
        name = findViewById(R.id.detail_product_name);
//        description = findViewById(R.id.detail_description_product);
        price = findViewById(R.id.detail_product_price);
        currentPrice = findViewById(R.id.detail_product_current_price);
        quantitySold = findViewById(R.id.detail_product_sold);
        likeCount = findViewById(R.id.detail_product_favorite);
        discount = findViewById(R.id.detail_product_discount);
        ratingBar =findViewById(R.id.detail_product_rating_bar);
        linearLayoutSize = findViewById(R.id.linear_layout_size);

        tabLayout = findViewById(R.id.tab_layout_detail);
        viewPager2 = findViewById(R.id.view_pager_detail);
        imgFavoriteProduct = findViewById(R.id.img_favorite_product);

        chatWithShop = findViewById(R.id.txt_chat_message_navigation_detail);
        addToCart = findViewById(R.id.txt_add_to_cart_navigation_detail);
        buyNow = findViewById(R.id.txt_buy_now_navigation_detail);
        imgCart = findViewById(R.id.cart_img_toolbar);
        quantityCart = findViewById(R.id.quantity_cart_toolbar);
        user = LoginActivity.user;

    }
    public Product getProduct() {
        return product;
    }
}