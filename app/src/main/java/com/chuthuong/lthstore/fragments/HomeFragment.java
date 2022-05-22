package com.chuthuong.lthstore.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.chuthuong.lthstore.response.CartResponse;
import com.chuthuong.lthstore.response.ListCategoryResponse;
import com.chuthuong.lthstore.response.ListProductResponse;
import com.chuthuong.lthstore.model.User;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.chuthuong.lthstore.utils.UserReaderSqlite;
import com.chuthuong.lthstore.utils.Util;
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
    ListCategoryResponse categoryList;

    // New product RecycleView
    NewProductsAdapter newProductsAdapter;
    ListProductResponse newProductList;

    // Flash Sale product RecycleView
    FlashSaleProductAdapter flashSaleProductAdapter;
    ListProductResponse flashSaleProductList;

    // Popular product RecycleView
    PopularProductAdapter popularProductAdapter;
    ListProductResponse popularProductList;
    User user;
    CartResponse cartResponse =null;
    UserReaderSqlite userReaderSqlite;
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                Util.refreshToken(getActivity());
//                Intent intent = new Intent(getContext(), ShowAllActivity.class);
//                intent.putExtra("list_see_all", categoryList);
//                startActivity(intent);
                setToast(getActivity(),"Đang bảo trì !");
            }
        });
        newProductShowALl.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Util.refreshToken(getActivity());
                Intent intent = new Intent(getContext(), ShowAllActivity.class);
                intent.putExtra("list_see_all", newProductList);
                intent.putExtra("title_see_all", getResources().getString(R.string.strTitleNewProducts));
                startActivity(intent);
            }
        });
        flashSaleProductShowAll.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Util.refreshToken(getActivity());
                Intent intent = new Intent(getContext(), ShowAllActivity.class);
                intent.putExtra("list_see_all", flashSaleProductList);
                intent.putExtra("title_see_all", getResources().getString(R.string.strTitleFlashSaleProducts));
                startActivity(intent);
            }
        });
        popularProductShowAll.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Util.refreshToken(getActivity());
                Intent intent = new Intent(getContext(), ShowAllActivity.class);
                intent.putExtra("list_see_all", popularProductList);
                intent.putExtra("title_see_all", getResources().getString(R.string.strTitlePopularProduct));
                startActivity(intent);
            }
        });
        imgCart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                Util.refreshToken(getActivity());
                if (user == null) {
                    openDialogRequestLogin();
                }
                // xử lý render cart
                else {
                    // start vô cart
                    Intent intent = new Intent(getActivity(), MyCartActivity.class);
                    intent.putExtra("my_cart", cartResponse);
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                Util.refreshToken(getActivity());

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userReaderSqlite = new UserReaderSqlite(getActivity(), "user.db", null, 1);

        Util.refreshToken(getActivity());
        user = userReaderSqlite.getUser();
        loadCart();
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
        categoryList = new ListCategoryResponse();
//        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
//        catRecyclerView.setAdapter(categoryAdapter)

        Util.refreshToken(getActivity());
        callApiGetAllCategories();

        // new Products
        newProductRecycleView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        Util.refreshToken(getActivity());
        callApiGetAllNewProducts();

        // Flash Sale Products
        flashSaleProductRecycleView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        Util.refreshToken(getActivity());
        callApiGetAllFlashSaleProducts();

        // popular products
        popularRecycleView.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        popularProductList = new ListProductResponse();
//        popularProductAdapter = new PopularProductAdapter(getContext(), popularProductList);
//        popularRecycleView.setAdapter(popularProductAdapter);
        Util.refreshToken(getActivity());
        callApiGetAllPopularProducts();

        SearchView searchView = root.findViewById(R.id.search_view_home);
        CardView cardView = root.findViewById(R.id.card_view_search_home);
        TextView textView = root.findViewById(R.id.text_view_hint_search);
        Util.refreshToken(getActivity());
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Util.refreshToken(getActivity());
                searchView.setIconified(false);
                textView.setVisibility(View.GONE);
//                getActivity().onSearchRequested();
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Util.refreshToken(getActivity());
                textView.setVisibility(View.VISIBLE);
                return false;
            }
        });
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadCart() {
        Util.refreshToken(getActivity());
        if (user != null) {
            callApiGetMyCart("Bearer " + user.getAccessToken());
        }
    }

    public void callApiGetMyCart(String accessToken) {
        String accept = "application/json;versions=1";
        ApiService.apiService.getMyCart(accept, accessToken).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    cartResponse = response.body();
                    if (cartResponse.getCart().getCartItems() != null) {
                        int size = cartResponse.getCart().getCartItems().size();
                        if (size > 0) {
                            quantityCart.setText(size + "");
                            quantityCart.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        quantityCart.setVisibility(View.GONE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
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
    private void callApiGetAllPopularProducts() {
        ApiService.apiService.getAllProducts("0", "1", "-likeCount", "0").enqueue(new Callback<ListProductResponse>() {
            @Override
            public void onResponse(Call<ListProductResponse> call, Response<ListProductResponse> response) {
                if (response.isSuccessful()) {
                    ListProductResponse products = response.body();
                    popularProductList = products;
                    popularProductAdapter = new PopularProductAdapter(getContext(), popularProductList);
                    popularRecycleView.setAdapter(popularProductAdapter);
                    popularProductAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
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
                setToast(getActivity(), "Lỗi server !");
            }
        });
    }

    private void callApiGetAllFlashSaleProducts() {
        ApiService.apiService.getAllProducts("0", "1", "-discount", "20").enqueue(new Callback<ListProductResponse>() {
            @Override
            public void onResponse(Call<ListProductResponse> call, Response<ListProductResponse> response) {
                if (response.isSuccessful()) {
                    ListProductResponse products = response.body();
                    flashSaleProductList = new ListProductResponse();
                    flashSaleProductList = products;
                    flashSaleProductAdapter = new FlashSaleProductAdapter(getContext(), flashSaleProductList);
                    flashSaleProductRecycleView.setAdapter(flashSaleProductAdapter);
                    flashSaleProductAdapter.notifyDataSetChanged();
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(getActivity(),apiError.getMessage());
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

    private void callApiGetAllNewProducts() {
        ApiService.apiService.getAllProducts("0", "1", "-createdAt", "0").enqueue(new Callback<ListProductResponse>() {
            @Override
            public void onResponse(Call<ListProductResponse> call, Response<ListProductResponse> response) {
                if (response.isSuccessful()) {
                    ListProductResponse products = response.body();
                    newProductList = new ListProductResponse();
                    newProductList = products;
                    newProductsAdapter = new NewProductsAdapter(getContext(), newProductList);
                    newProductRecycleView.setAdapter(newProductsAdapter);
                    newProductsAdapter.notifyDataSetChanged();
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(getActivity(),apiError.getMessage());
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

    public void callApiGetAllCategories() {
        ApiService.apiService.getAllCategories().enqueue(new Callback<ListCategoryResponse>() {
            @Override
            public void onResponse(Call<ListCategoryResponse> call, Response<ListCategoryResponse> response) {
                if (response.isSuccessful()) {
                    ListCategoryResponse categories = response.body();
                    categoryList = categories;
                    categoryAdapter = new CategoryAdapter(getContext(), categoryList);
                    catRecyclerView.setAdapter(categoryAdapter);
                    categoryAdapter.notifyDataSetChanged();
                    homeLayout.setVisibility(View.VISIBLE);
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        setToast(getActivity(),apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ListCategoryResponse> call, Throwable t) {
                setToast(getActivity(),"Lỗi server !");
            }
        });
    }

    public void reloadData() {
        setToast(getActivity(),"Reload Fragment Homt");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart() {
        super.onStart();
        loadCart();
    }
}