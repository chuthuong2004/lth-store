package com.chuthuong.lthstore.activities.detailActivities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.MainActivity;
import com.chuthuong.lthstore.activities.authActivities.ChangePasswordActivity;
import com.chuthuong.lthstore.adapter.PopularProductAdapter;
import com.chuthuong.lthstore.adapter.ShowAllAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.fragments.SearchFragment;
import com.chuthuong.lthstore.model.ListCategory;
import com.chuthuong.lthstore.model.ListProduct;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowAllActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ShowAllAdapter showAllAdapter;
    ListProduct listProduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        recyclerView = findViewById(R.id.show_all_rec);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        final  Object obj = getIntent().getSerializableExtra("list_see_all");
        String title = getIntent().getStringExtra("title_see_all");
        Log.e("title", title);
        if(obj instanceof ListProduct) {
            listProduct = new ListProduct();
            listProduct= (ListProduct) obj;
            showAllAdapter = new ShowAllAdapter(ShowAllActivity.this, listProduct);
            TextView txtTitle = findViewById(R.id.title_see_all);
            txtTitle.setText(title);
            recyclerView.setAdapter(showAllAdapter);
            showAllAdapter.notifyDataSetChanged();
        }
        ImageView imgBack, imgSearch;
        imgBack = findViewById(R.id.img_back_detail);
        imgBack.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgSearch = findViewById(R.id.search_img);
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment searchFragment = new SearchFragment();
                fragmentTransaction.add(R.id.search_container,searchFragment );
//                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_out_right,
//                        R.anim.slide_out_right);
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,0);
                fragmentTransaction.show(searchFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Thoát", Toast.LENGTH_SHORT).show();
    }
}