package com.chuthuong.lthstore.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.detailActivities.ProductDetailActivity;
import com.chuthuong.lthstore.activities.detailActivities.ShowAllActivity;
import com.chuthuong.lthstore.adapter.PopularProductAdapter;
import com.chuthuong.lthstore.adapter.ReviewProductAdapter;
import com.chuthuong.lthstore.adapter.SameProductDetailAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.ListProduct;
import com.chuthuong.lthstore.model.ListReview;
import com.chuthuong.lthstore.model.Product;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuggestionProductFragment extends Fragment {
    RecyclerView recyclerViewSameProductDetail, recyclerViewAllProduct;
    SameProductDetailAdapter sameProductDetailAdapter;
    ListProduct listSameProduct;
    ProductDetailActivity productDetailActivity;
    TextView sameProductShowAll, showAllProduct;

    // Popular product RecycleView
    PopularProductAdapter popularProductAdapter;
    ListProduct popularProductList;
    private Product product;
    public SuggestionProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        productDetailActivity = (ProductDetailActivity) getActivity();
        product = productDetailActivity.getProduct();
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_suggestion_product, container, false);

        // same product
        recyclerViewSameProductDetail= root.findViewById(R.id.rec_product_same_detail);
        recyclerViewSameProductDetail.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        callApiProductByCategory(product.getCategory());
        sameProductShowAll = root.findViewById(R.id.suggestion_product_see_all);
        sameProductShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShowAllActivity.class);
                intent.putExtra("list_see_all", listSameProduct);
                intent.putExtra("title_see_all",getResources().getString(R.string.strTitleSameProductDetail));
                startActivity(intent);
            }
        });


        // all product
        recyclerViewAllProduct= root.findViewById(R.id.rec_product_all);
        recyclerViewAllProduct.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        callApiGetAllPopularProducts();
        showAllProduct = root.findViewById(R.id.product_of_shop_see_all);
        showAllProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShowAllActivity.class);
                intent.putExtra("list_see_all", popularProductList);
                intent.putExtra("title_see_all",getResources().getString(R.string.strTitleProductOfShop));
                startActivity(intent);
            }
        });

        return root;
    }
    private void callApiGetAllPopularProducts() {
        ApiService.apiService.getAllProducts("0", "1", "-likeCount", "0").enqueue(new Callback<ListProduct>() {
            @Override
            public void onResponse(Call<ListProduct> call, Response<ListProduct> response) {
                if (response.isSuccessful()) {
                    ListProduct products = response.body();
                    popularProductList = products;
                    popularProductAdapter = new PopularProductAdapter(getContext(), popularProductList);
                    recyclerViewAllProduct.setAdapter(popularProductAdapter);
                    popularProductAdapter.notifyDataSetChanged();
                    Log.e("Products", products.getProducts().get(0).getName() + "");
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Log.e("Message", apiError.getMessage());
//                        Toast.makeText(HomeFragment.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ListProduct> call, Throwable t) {
                Log.e("Lỗi server ", t.toString());
                Toast.makeText(getActivity(), "lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void callApiProductByCategory(String category) {
        ApiService.apiService.getAllProductByCategory(category).enqueue(new Callback<ListProduct>() {
            @Override
            public void onResponse(Call<ListProduct> call, Response<ListProduct> response) {
                if (response.isSuccessful()) {
                    listSameProduct = response.body();
                    sameProductDetailAdapter = new SameProductDetailAdapter(getContext(), listSameProduct);
                    recyclerViewSameProductDetail.setAdapter(sameProductDetailAdapter);
                    sameProductDetailAdapter.notifyDataSetChanged();
                    Log.e("Products", listSameProduct.getProducts().get(0).getName() + "");
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Log.e("Message", apiError.getMessage());
//                        Toast.makeText(HomeFragment.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ListProduct> call, Throwable t) {
                Log.e("Lỗi server ", t.toString());
                Toast.makeText(getActivity(), "lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Toast.makeText(productDetailActivity, "Suggest 1", Toast.LENGTH_SHORT).show();

        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            Toast.makeText(productDetailActivity, "Suggest", Toast.LENGTH_SHORT).show();
        }
    }
}