package com.chuthuong.lthstore.activities.detailActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.adapter.ViewPagerDetailProductAdapter;
import com.chuthuong.lthstore.model.Product;
import com.chuthuong.lthstore.model.ProductDetail;
import com.chuthuong.lthstore.model.ProductImage;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.tabs.TabLayout;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {
    TextView rating, name, description,currentPrice, price, discount, quantitySold, likeCount;
    Button btnAddToCart, btnBuyNow;
    ImageView imgAddToCart, imgRemoveItemCart;
    RatingBar ratingBar;

    LinearLayout linearLayoutSize;
    // Product
    Product product = null;
    private int count = 0;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerDetailProductAdapter viewPagerDetailProductAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        addControls();
        final Product obj = (Product) getIntent().getSerializableExtra("product_detail");
        if (obj instanceof Product) {
            product = (Product) obj;

        }
        Log.e("obj","Ok");
        if(product!=null){
            addData();
            viewPagerDetailProductAdapter = new ViewPagerDetailProductAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            viewPager.setAdapter(viewPagerDetailProductAdapter);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private void addData() {
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
        viewPager = findViewById(R.id.view_pager_detail);

    }
}