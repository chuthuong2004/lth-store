package com.chuthuong.lthstore.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.detailActivities.OrderDetailActivity;
import com.chuthuong.lthstore.adapter.ShowAllAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.Product;
import com.chuthuong.lthstore.response.ListProductResponse;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.widget.CustomProgressDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int LIMIT = 10;
    private List<Product> products;
    private ShowAllAdapter showAllAdapter;
    private RecyclerView recyclerView;
    private SpinKitView progressBar;
    private boolean isLoading = true;
    private ImageView back;
    private TextView searchView;
    private String query;

    private int page = 1;
    private NestedScrollView scrollView;
    private TextView quantityResult;
    private ImageView sort, filter;
    private String conditionSort = "";
    private int filterPrice = 0;
    private TextView noProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.chuthuong.lthstore.R.layout.activity_search_result);
        query = getIntent().getStringExtra("query");
        addControls();
        addEvents();
        searchView.setText(query);
        products = new ArrayList<>();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        callApiSearchProducts(1, query);
    }

    private void addEvents() {
        back.setOnClickListener(this);
        searchView.setOnClickListener(this);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() && isLoading) {
                    page++;
                    callApiSearchProducts(page, query);
                }
            }
        });
        sort.setOnClickListener(this);
        filter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back_search:
            case R.id.search_view:
                onBackPressed();
                finish();
                break;
            case R.id.filter:
                openDialogFilter();
                break;
            case R.id.sort:
                openDialogSort();
                break;
        }
    }

    private void openDialogFilter() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_filter_product);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;
        window.setAttributes(windowAttributes);
        if (Gravity.BOTTOM == Gravity.BOTTOM) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }
        TextView txtProgress = dialog.findViewById(R.id.text_view_progress);
        SeekBar seekBar = dialog.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtProgress.setText(progress+"k");
                filterPrice = progress * 1000;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        TextView btnConfirmFilter = dialog.findViewById(R.id.btn_confirm_filter);
        btnConfirmFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    products.clear();
                    page=1;
                    isLoading =true;
                    progressBar.setVisibility(View.VISIBLE);
                    callApiSearchProducts(page,query);
                    dialog.cancel();
                    filter.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.pink)));
            }
        });

        dialog.show();
    }

    private void openDialogSort() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_sort_product);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;
        window.setAttributes(windowAttributes);
        if (Gravity.BOTTOM == Gravity.BOTTOM) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }
        AppCompatRadioButton radNew, radFavorite, radPriceDesc, radPriceAsc, radDiscount, radBestSelling;
        radNew = dialog.findViewById(R.id.rad_new);
        radFavorite = dialog.findViewById(R.id.rad_favorite);
        radPriceDesc = dialog.findViewById(R.id.rad_price_desc);
        radPriceAsc = dialog.findViewById(R.id.rad_price_asc);
        radDiscount = dialog.findViewById(R.id.rad_discount);
        radBestSelling = dialog.findViewById(R.id.rad_best_selling);
        switch (conditionSort) {
            case "-createdAt":
                radNew.setChecked(true);
                radNew.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0);
                break;
            case "-likeCount":
                radFavorite.setChecked(true);
                radFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0);
                break;
            case "-price":
                radPriceDesc.setChecked(true);
                radPriceDesc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0);
                break;
            case "price":
                radPriceAsc.setChecked(true);
                radPriceAsc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0);
                break;
            case "-discount":
                radDiscount.setChecked(true);
                radDiscount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0);
                break;
            case "-quantitySold":
                radBestSelling.setChecked(true);
                radBestSelling.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0);
                break;
        }
        radNew.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    radNew.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        });
        radFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    radFavorite.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        });
        radPriceDesc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    radPriceDesc.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        });
        radPriceAsc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    radPriceAsc.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        });
        radDiscount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    radDiscount.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        });
        radBestSelling.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    radBestSelling.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        });
        RadioGroup radioGroup = dialog.findViewById(R.id.rad_sort_group);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rad_new:
                        radNew.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0);
                        conditionSort = "-createdAt";
                        break;
                    case R.id.rad_favorite:
                        radFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0);
                        conditionSort = "-likeCount";
                        break;
                    case R.id.rad_price_desc:
                        radPriceDesc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0);
                        conditionSort = "-price";
                        break;
                    case R.id.rad_price_asc:
                        radPriceAsc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0);
                        conditionSort = "price";
                        break;
                    case R.id.rad_discount:
                        radDiscount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0);
                        conditionSort = "-discount";
                        break;
                    case R.id.rad_best_selling:
                        radBestSelling.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0);
                        conditionSort = "-quantitySold";
                        break;
                }
            }
        });
        TextView btnConfirm = dialog.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                products.clear();
                page = 1;
                isLoading = true;
                progressBar.setVisibility(View.VISIBLE);
                callApiSearchProducts(page, query);
                dialog.cancel();
                sort.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.pink)));
            }
        });

        dialog.show();
    }

    private void addControls() {
        recyclerView = findViewById(R.id.search_result_rec);
        progressBar = findViewById(R.id.progress_bar_loading_all_search);
        back = findViewById(R.id.img_back_search);
        searchView = findViewById(R.id.search_view);
        scrollView = findViewById(R.id.nestedScrollView_search);
        quantityResult = findViewById(R.id.quantity_result);
        sort = findViewById(R.id.sort);
        filter = findViewById(R.id.filter);
        noProducts = findViewById(R.id.no_products);

    }

    private void callApiSearchProducts(int page, String query) {
        String sort = "";
        if (conditionSort.equals("")) {
            sort = "-createdAt";
        } else {
            sort = conditionSort;
        }
        ApiService.apiService.getAllProducts(LIMIT, page, sort, query, 0,filterPrice).enqueue(new Callback<ListProductResponse>() {
            @Override
            public void onResponse(Call<ListProductResponse> call, Response<ListProductResponse> response) {
                if (response.isSuccessful()) {
                    if(response.body().getDocument()== 0) {
                        noProducts.setVisibility(View.VISIBLE);
                    }
                    products.addAll(response.body().getProducts());
                    showAllAdapter = new ShowAllAdapter(SearchResultActivity.this, products);
                    recyclerView.setAdapter(showAllAdapter);
                    showAllAdapter.notifyDataSetChanged();
                    quantityResult.setText(response.body().getDocument() + "");
                    if (response.body().getCountDocument() < LIMIT) {
                        progressBar.setVisibility(View.GONE);
                        isLoading = false;
                    }
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Toast.makeText(SearchResultActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ListProductResponse> call, Throwable t) {
                Toast.makeText(SearchResultActivity.this, "Lỗi server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}