package com.chuthuong.lthstore.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.MainActivity;
import com.chuthuong.lthstore.activities.MyCartActivity;
import com.chuthuong.lthstore.activities.authActivities.LoginActivity;
import com.chuthuong.lthstore.activities.detailActivities.ShowAllActivity;
import com.chuthuong.lthstore.adapter.CategoryAdapter;
import com.chuthuong.lthstore.adapter.FlashSaleProductAdapter;
import com.chuthuong.lthstore.adapter.NewProductsAdapter;
import com.chuthuong.lthstore.adapter.PopularProductAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.Cart;
import com.chuthuong.lthstore.model.CartResponse;
import com.chuthuong.lthstore.model.ListCategory;
import com.chuthuong.lthstore.model.ListProduct;
import com.chuthuong.lthstore.model.User;
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

    ConstraintLayout homeLayout;
    ProgressDialog progressDialog;

    RecyclerView catRecyclerView, newProductRecycleView, flashSaleProductRecycleView, popularRecycleView;

    TextView categoryShowAll, newProductShowALl, flashSaleProductShowAll, popularProductShowAll;
    ImageView imgCart;
    TextView quantityCart;

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
    User user;
    CartResponse cart;

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
        Log.e("Thuong", "Reload FragmentHome");
    }

    public void addControls(View view) {
        catRecyclerView = view.findViewById(R.id.rec_category);
        newProductRecycleView = view.findViewById(R.id.new_product_rec);
        popularRecycleView = view.findViewById(R.id.popular_rec);
        flashSaleProductRecycleView = view.findViewById(R.id.sale_product_rec);
        categoryShowAll = view.findViewById(R.id.category_see_all);
        newProductShowALl = view.findViewById(R.id.new_product_see_all);
        flashSaleProductShowAll = view.findViewById(R.id.flash_sale_product_see_all);
        popularProductShowAll = view.findViewById(R.id.popular_see_all);
        homeLayout = view.findViewById(R.id.home_layout);
        imgCart = view.findViewById(R.id.cart_img_toolbar);
        quantityCart = view.findViewById(R.id.quantity_cart_toolbar);
    }

    ;

    private void addEvents() {
        categoryShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), ShowAllActivity.class);
//                intent.putExtra("list_see_all", categoryList);
//                startActivity(intent);
                Toast.makeText(getContext(), "Đang bảo trì !", Toast.LENGTH_SHORT).show();
            }
        });
        newProductShowALl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShowAllActivity.class);
                intent.putExtra("list_see_all", newProductList);
                intent.putExtra("title_see_all", getResources().getString(R.string.strTitleNewProducts));
                startActivity(intent);
            }
        });
        flashSaleProductShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShowAllActivity.class);
                intent.putExtra("list_see_all", flashSaleProductList);
                intent.putExtra("title_see_all", getResources().getString(R.string.strTitleFlashSaleProducts));
                startActivity(intent);
            }
        });
        popularProductShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShowAllActivity.class);
                intent.putExtra("list_see_all", popularProductList);
                intent.putExtra("title_see_all", getResources().getString(R.string.strTitlePopularProduct));
                startActivity(intent);
            }
        });
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user == null) {
                    openDialogRequestLogin();
                }
                // xử lý render cart
                else {
                    // start vô cart
                    Intent intent = new Intent(getActivity(), MyCartActivity.class);
                    intent.putExtra("my_cart", cart.getCart());
                    intent.putExtra("title_my_cart", getResources().getString(R.string.strTitleMyCart));
                    startActivity(intent);
                }
            }
        });
    }

    private void openDialogRequestLogin() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_login);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        if (Gravity.CENTER == Gravity.CENTER) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }
        TextView cancel = dialog.findViewById(R.id.dialog_cancel);
        TextView login = dialog.findViewById(R.id.dialog_login);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        user = LoginActivity.user;
        if (user == null) {
            Toast.makeText(getActivity(), "Không có user", Toast.LENGTH_SHORT).show();
        } else {
            callApiGetMyCart("Bearer " + user.getAccessToken());

        }
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        progressDialog = new ProgressDialog(getActivity());

        addControls(root);
        addEvents();
        homeLayout.setVisibility(View.GONE);

        // image slider
        ImageSlider imageSlider = root.findViewById(R.id.image_slider);
        List<SlideModel> slideModelList = new ArrayList<>();
        slideModelList.add(new SlideModel(R.drawable.banner1, "Discount On Shoes Items", ScaleTypes.CENTER_CROP));
        slideModelList.add(new SlideModel(R.drawable.banner2, "Discount On Perfume", ScaleTypes.CENTER_CROP));
        slideModelList.add(new SlideModel(R.drawable.banner3, "70% OFF", ScaleTypes.CENTER_CROP));
        imageSlider.setImageList(slideModelList);

        progressDialog.setTitle("Welcome to LTH Store");
        progressDialog.setMessage("Please wait....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // Category
        catRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        categoryList = new ListCategory();
//        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
//        catRecyclerView.setAdapter(categoryAdapter)
        callApiGetAllCategories();

        // new Products
        newProductRecycleView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        callApiGetAllNewProducts();

        // Flash Sale Products
        flashSaleProductRecycleView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        callApiGetAllFlashSaleProducts();

        // popular products
        popularRecycleView.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        popularProductList = new ListProduct();
//        popularProductAdapter = new PopularProductAdapter(getContext(), popularProductList);
//        popularRecycleView.setAdapter(popularProductAdapter);
        callApiGetAllPopularProducts();

        SearchView searchView = root.findViewById(R.id.search_view_home);
        CardView cardView = root.findViewById(R.id.card_view_search_home);
        TextView textView = root.findViewById(R.id.text_view_hint_search);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchView.setIconified(false);
                textView.setVisibility(View.GONE);
//                getActivity().onSearchRequested();
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                textView.setVisibility(View.VISIBLE);
                return false;
            }
        });
        return root;
    }

    private void callApiGetMyCart(String accessToken) {
        Toast.makeText(getActivity(), accessToken, Toast.LENGTH_SHORT).show();
        Log.e("ACCESS", accessToken);
        String accept = "application/json;versions=1";
        ApiService.apiService.getMyCart(accept, accessToken).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    cart = response.body();
                    int size = cart.getCart().getCartItems().size();
                    quantityCart.setText(size+"");
                    quantityCart.setVisibility(View.VISIBLE);
                    Log.e("cart", cart.getCart().toString());
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Log.e("Message", apiError.getMessage());
                        Toast.makeText(getActivity(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Log.e("Lỗi server ", t.toString());
                Toast.makeText(getActivity(), "lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void callApiGetAllPopularProducts() {
        ApiService.apiService.getAllProducts("0", "1", "-likeCount", "0").enqueue(new Callback<ListProduct>() {
            @Override
            public void onResponse(Call<ListProduct> call, Response<ListProduct> response) {
                if (response.isSuccessful()) {
                    ListProduct products = response.body();
                    popularProductList = products;
                    popularProductAdapter = new PopularProductAdapter(getContext(), popularProductList);
                    popularRecycleView.setAdapter(popularProductAdapter);
                    popularProductAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
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

    private void callApiGetAllFlashSaleProducts() {
        ApiService.apiService.getAllProducts("0", "1", "-discount", "20").enqueue(new Callback<ListProduct>() {
            @Override
            public void onResponse(Call<ListProduct> call, Response<ListProduct> response) {
                if (response.isSuccessful()) {
                    ListProduct products = response.body();
                    flashSaleProductList = new ListProduct();
                    flashSaleProductList = products;
                    flashSaleProductAdapter = new FlashSaleProductAdapter(getContext(), flashSaleProductList);
                    flashSaleProductRecycleView.setAdapter(flashSaleProductAdapter);
                    flashSaleProductAdapter.notifyDataSetChanged();
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

    private void callApiGetAllNewProducts() {
        ApiService.apiService.getAllProducts("0", "1", "-createdAt", "0").enqueue(new Callback<ListProduct>() {
            @Override
            public void onResponse(Call<ListProduct> call, Response<ListProduct> response) {
                if (response.isSuccessful()) {
                    ListProduct products = response.body();
                    newProductList = new ListProduct();
                    newProductList = products;
                    newProductsAdapter = new NewProductsAdapter(getContext(), newProductList);
                    newProductRecycleView.setAdapter(newProductsAdapter);
                    newProductsAdapter.notifyDataSetChanged();
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

    public void callApiGetAllCategories() {
        ApiService.apiService.getAllCategories().enqueue(new Callback<ListCategory>() {
            @Override
            public void onResponse(Call<ListCategory> call, Response<ListCategory> response) {
                if (response.isSuccessful()) {
                    ListCategory categories = response.body();
                    categoryList = categories;
                    categoryAdapter = new CategoryAdapter(getContext(), categoryList);
                    catRecyclerView.setAdapter(categoryAdapter);
                    categoryAdapter.notifyDataSetChanged();
                    homeLayout.setVisibility(View.VISIBLE);
                    Log.e("Categories", categoryList.getCategories().get(0).getId() + "");
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
    }

    public void reloadData() {
        Toast.makeText(getActivity(), "Reload FragmentHome", Toast.LENGTH_SHORT).show();
    }
}