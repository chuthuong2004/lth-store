package com.chuthuong.lthstore.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.detailActivities.ProductDetailActivity;
import com.chuthuong.lthstore.activities.detailActivities.ShowAllActivity;
import com.chuthuong.lthstore.adapter.PopularProductAdapter;
import com.chuthuong.lthstore.adapter.SameProductDetailAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.response.ListProductResponse;
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
    ListProductResponse listSameProduct;
    ProductDetailActivity productDetailActivity;
    TextView sameProductShowAll, showAllProduct;

    // Popular product RecycleView
    PopularProductAdapter popularProductAdapter;
    ListProductResponse popularProductList;
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
        ApiService.apiService.getAllProducts("0", "1", "-likeCount", "0").enqueue(new Callback<ListProductResponse>() {
            @Override
            public void onResponse(Call<ListProductResponse> call, Response<ListProductResponse> response) {
                if (response.isSuccessful()) {
                    ListProductResponse products = response.body();
                    popularProductList = products;
                    popularProductAdapter = new PopularProductAdapter(getContext(), popularProductList);
                    recyclerViewAllProduct.setAdapter(popularProductAdapter);
                    popularProductAdapter.notifyDataSetChanged();
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(getActivity(), apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ListProductResponse> call, Throwable t) {
                setToast(getActivity(),"Lỗi server !");
            }
        });
    }
    private void callApiProductByCategory(String category) {
        ApiService.apiService.getAllProductByCategory(category).enqueue(new Callback<ListProductResponse>() {
            @Override
            public void onResponse(Call<ListProductResponse> call, Response<ListProductResponse> response) {
                if (response.isSuccessful()) {
                    listSameProduct = response.body();
                    sameProductDetailAdapter = new SameProductDetailAdapter(getContext(), listSameProduct);
                    recyclerViewSameProductDetail.setAdapter(sameProductDetailAdapter);
                    sameProductDetailAdapter.notifyDataSetChanged();
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(getActivity(), apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ListProductResponse> call, Throwable t) {
                setToast(getActivity(),"Lỗi server !");
            }
        });
    }
    private void setToast(Activity activity, String msg) {
        Toast toast = new Toast(activity);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_toast, (ViewGroup) activity.findViewById(R.id.layout_toast));
        TextView message = view.findViewById(R.id.message_toast);
        message.setText(msg);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}