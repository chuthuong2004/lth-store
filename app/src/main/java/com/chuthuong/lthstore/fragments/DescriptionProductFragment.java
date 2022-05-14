package com.chuthuong.lthstore.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.detailActivities.ProductDetailActivity;
import com.chuthuong.lthstore.adapter.PopularProductAdapter;
import com.chuthuong.lthstore.adapter.SameProductDetailAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.ListCategory;
import com.chuthuong.lthstore.model.ListProduct;
import com.chuthuong.lthstore.model.Product;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DescriptionProductFragment extends Fragment {
    RecyclerView recyclerViewSameProductDetail;
    SameProductDetailAdapter sameProductDetailAdapter;
    ListProduct listProduct;
    ProductDetailActivity productDetailActivity;
    private Product product;

    public DescriptionProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        productDetailActivity = (ProductDetailActivity) getActivity();
        product = productDetailActivity.getProduct();
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_description_product, container, false);
        TextView desProduct = root.findViewById(R.id.txt_description_product_detail);
        desProduct.setText(product.getDesProduct());
        // new Products
//        recyclerViewSameProductDetail= root.findViewById(R.id.rec_product_same_detail);
//        recyclerViewSameProductDetail.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
//        callApiProductByCategory(product.getCategory());

//        SearchView searchView= root.findViewById(R.id.search_view_home);
//        CardView cardView = root.findViewById(R.id.card_view_search_home);
//        TextView textView=  root.findViewById(R.id.text_view_hint_search);
//        cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                searchView.setIconified(false);
//                textView.setVisibility(View.GONE);
////                getActivity().onSearchRequested();
//            }
//        });
//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                textView.setVisibility(View.VISIBLE);
//                return false;
//            }
//        });
        return root;
    }

    private void callApiProductByCategory(String category) {
//        ApiService.apiService.getAllProductByCategory(category).enqueue(new Callback<ListProduct>() {
//            @Override
//            public void onResponse(Call<ListProduct> call, Response<ListProduct> response) {
//                if (response.isSuccessful()) {
//                    listProduct = response.body();
//                    sameProductDetailAdapter = new SameProductDetailAdapter(getContext(), listProduct);
//                    recyclerViewSameProductDetail.setAdapter(sameProductDetailAdapter);
//                    sameProductDetailAdapter.notifyDataSetChanged();
//                    Log.e("Products", listProduct.getProducts().get(0).getName() + "");
//                } else {
//                    try {
//                        Gson gson = new Gson();
//                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
//                        Log.e("Message", apiError.getMessage());
////                        Toast.makeText(HomeFragment.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ListProduct> call, Throwable t) {
//                Log.e("Lỗi server ", t.toString());
//                Toast.makeText(getActivity(), "lỗi", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}