package com.chuthuong.lthstore.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.adapter.CategoryAdapter;
import com.chuthuong.lthstore.adapter.FlashSaleProductAdapter;
import com.chuthuong.lthstore.adapter.NewProductsAdapter;
import com.chuthuong.lthstore.adapter.PopularProductAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.ListCategory;
import com.chuthuong.lthstore.model.ListProduct;
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

public class HomeFragment extends Fragment {

    RecyclerView catRecyclerView, newProductRecycleView, flashSaleProductRecycleView, popularRecycleView;

    // Category RecycleView
    CategoryAdapter categoryAdapter;
    ListCategory categoryList;

    // New product RecycleView
    NewProductsAdapter newProductsAdapter;
    ListProduct newProductList;

    // Flash Sale product RecycleView
    FlashSaleProductAdapter flashSaleProductAdapter;
    ListProduct flashSaleProductList;

    // Popular product RecycleView
    PopularProductAdapter popularProductAdapter;
    ListProduct popularProductList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e("HomeFragment", "Fragment 1");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("Thuong","Reload FragmentHome");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_home, container, false);

        catRecyclerView = root.findViewById(R.id.rec_category);
        newProductRecycleView= root.findViewById(R.id.new_product_rec);
        popularRecycleView= root.findViewById(R.id.popular_rec);
        flashSaleProductRecycleView= root.findViewById(R.id.sale_product_rec);


        // image slider
        ImageSlider imageSlider = root.findViewById(R.id.image_slider);
        List<SlideModel> slideModelList = new ArrayList<>();
        slideModelList.add(new SlideModel(R.drawable.banner1,"Discount On Shoes Items", ScaleTypes.CENTER_CROP));
        slideModelList.add(new SlideModel(R.drawable.banner2,"Discount On Perfume", ScaleTypes.CENTER_CROP));
        slideModelList.add(new SlideModel(R.drawable.banner3,"70% OFF", ScaleTypes.CENTER_CROP));
        imageSlider.setImageList(slideModelList);


        // Category
        catRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL, false));
        categoryList = new ListCategory();
//        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
//        catRecyclerView.setAdapter(categoryAdapter)
        ApiService.apiService.getAllCategories().enqueue(new Callback<ListCategory>() {
            @Override
            public void onResponse(Call<ListCategory> call, Response<ListCategory> response) {
                if (response.isSuccessful()) {
                    ListCategory categories= response.body();
                    categoryList = categories;
                    categoryAdapter = new CategoryAdapter(getContext(), categoryList);
                    catRecyclerView.setAdapter(categoryAdapter);
                    categoryAdapter.notifyDataSetChanged();
                    Log.e("Categories", categoryList.getCategories().get(0).getId()+ "");
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
            public void onFailure(Call<ListCategory> call, Throwable t) {
                Log.e("Lỗi server ", t.toString());
                Toast.makeText(getActivity(), "lỗi", Toast.LENGTH_SHORT).show();
            }
        });


        // new Products
        newProductRecycleView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
        ApiService.apiService.getAllProducts("20","1","-createdAt","0").enqueue(new Callback<ListProduct>() {
            @Override
            public void onResponse(Call<ListProduct> call, Response<ListProduct> response) {
                if (response.isSuccessful()) {
                    ListProduct products= response.body();
                    newProductList = new ListProduct();
                    newProductList = products;
                    newProductsAdapter = new NewProductsAdapter(getContext(),newProductList);
                    newProductRecycleView.setAdapter(newProductsAdapter);
                    newProductsAdapter.notifyDataSetChanged();
                    Log.e("Products", products.getProducts().get(0).getName()+ "");
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

        // Flash Sale Products
        flashSaleProductRecycleView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
        ApiService.apiService.getAllProducts("0","1","-discount","20").enqueue(new Callback<ListProduct>() {
            @Override
            public void onResponse(Call<ListProduct> call, Response<ListProduct> response) {
                if (response.isSuccessful()) {
                    ListProduct products= response.body();
                    flashSaleProductList = new ListProduct();
                    flashSaleProductList = products;
                    flashSaleProductAdapter = new FlashSaleProductAdapter(getContext(),flashSaleProductList);
                    flashSaleProductRecycleView.setAdapter(flashSaleProductAdapter);
                    flashSaleProductAdapter.notifyDataSetChanged();
                    Log.e("Products", products.getProducts().get(0).getName()+ "");
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

        // popular products
        popularRecycleView.setLayoutManager(new GridLayoutManager(getContext(),2));
//        popularProductList = new ListProduct();
//        popularProductAdapter = new PopularProductAdapter(getContext(), popularProductList);
//        popularRecycleView.setAdapter(popularProductAdapter);

        ApiService.apiService.getAllProducts("0","1","-likeCount","0").enqueue(new Callback<ListProduct>() {
            @Override
            public void onResponse(Call<ListProduct> call, Response<ListProduct> response) {
                if (response.isSuccessful()) {
                    ListProduct products= response.body();
                    popularProductList = new ListProduct();
                    popularProductList = products;
                    popularProductAdapter = new PopularProductAdapter(getContext(), popularProductList);
                    popularRecycleView.setAdapter(popularProductAdapter);
                    popularProductAdapter.notifyDataSetChanged();
                    Log.e("Products", products.getProducts().get(0).getName()+ "");
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

        return  root;
    }

    public void reloadData(){
        Toast.makeText(getActivity(), "Reload FragmentHome", Toast.LENGTH_SHORT).show();
    }
}