package com.chuthuong.lthstore.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.SearchableInfo;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchView = findViewById(R.id.search_view);
        searchView.setQuery("", false);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if (searchView.getQuery().toString().equals("")) {
                    return true;
                }
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent =new Intent(SearchActivity.this, SearchResultActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
                Toast.makeText(SearchActivity.this, query, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        TextView cancel = findViewById(R.id.txt_cancel_search);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
    }
}