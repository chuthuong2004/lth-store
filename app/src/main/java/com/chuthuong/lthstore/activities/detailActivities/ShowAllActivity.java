package com.chuthuong.lthstore.activities.detailActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.adapter.PopularProductAdapter;
import com.chuthuong.lthstore.adapter.ShowAllAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.ListCategory;
import com.chuthuong.lthstore.model.ListProduct;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowAllActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ShowAllAdapter showAllAdapter;
    ListProduct listProduct;
    ListCategory listCategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        recyclerView = findViewById(R.id.show_all_rec);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        final  Object obj = getIntent().getSerializableExtra("list_see_all");
        if(obj instanceof ListProduct) {
            listProduct = new ListProduct();
            listProduct= (ListProduct) obj;
            showAllAdapter = new ShowAllAdapter(ShowAllActivity.this, listProduct);
            recyclerView.setAdapter(showAllAdapter);
            showAllAdapter.notifyDataSetChanged();
        }

    }
}