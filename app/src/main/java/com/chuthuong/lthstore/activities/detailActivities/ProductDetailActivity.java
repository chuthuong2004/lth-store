package com.chuthuong.lthstore.activities.detailActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.model.Product;

public class ProductDetailActivity extends AppCompatActivity {
    ImageView imgDetailProduct;
    TextView rating, name, description, price;
    Button btnAddToCart, btnBuyNow;
    ImageView imgAddToCart, imgRemoveItemCart;

    // Product
    Product product = null;

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
            Glide.with(getApplicationContext()).load(product.getImages().get(0).getImg()).into(imgDetailProduct);
            name.setText(product.getName());
            description.setText(product.getDesProduct());
            price.setText(product.getPrice()+"");
            rating.setText(product.getRate()+"");
        }
    }

    private void addControls() {
        imgDetailProduct = findViewById(R.id.detail_product_img);
        rating = findViewById(R.id.detail_product_rating);
        name = findViewById(R.id.detail_product_name);
        description = findViewById(R.id.detail_description_product);
        price = findViewById(R.id.detail_product_price);
        btnAddToCart = findViewById(R.id.add_to_cart);
        btnBuyNow = findViewById(R.id.buy_now);
        imgAddToCart = findViewById(R.id.add_quantity_detail);
        imgRemoveItemCart = findViewById(R.id.remove_quantity_detail);
    }
}