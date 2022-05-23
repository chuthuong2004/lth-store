package com.chuthuong.lthstore.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.detailActivities.ProductDetailActivity;
import com.chuthuong.lthstore.adapter.ReviewProductAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.response.ListReviewResponse;
import com.chuthuong.lthstore.model.Product;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewProductFragment extends Fragment {
    RecyclerView recyclerViewReview;
    ReviewProductAdapter reviewProductAdapter;
    ListReviewResponse listReviewResponse;
    Product product;
    RatingBar ratingBar;
    TextView rating, quantityReviewProduct, reviewNull;
    LinearLayout layoutReview;
    ProductDetailActivity productDetailActivity;


    public ReviewProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        productDetailActivity = (ProductDetailActivity) getActivity();
        product = productDetailActivity.getProduct();
        View view = inflater.inflate(R.layout.fragment_review_product, container, false);
        ratingBar = view.findViewById(R.id.detail_product_review_rating_bar);
        ratingBar.setRating(product.getRate());
        rating = view.findViewById(R.id.rating_product_review);
        rating.setText(product.getRate() + "/5");
        recyclerViewReview = view.findViewById(R.id.rec_review);
        recyclerViewReview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        ;
        callApiGetAllReviewByProduct(product.getId());
        quantityReviewProduct = view.findViewById(R.id.quantity_review_product);
        reviewNull = view.findViewById(R.id.review_null);
        layoutReview = view.findViewById(R.id.layout_review);
        registerForContextMenu(recyclerViewReview);
        return view;
    }
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        // inflate menu
//        MenuInflater inflater = getActivity().getMenuInflater();
//        inflater.inflate(R.menu.menu_review_product, menu);
//    }
//
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        return super.onContextItemSelected(item);
//        Recy info = (RecyclerViewContextMenuInfo) item.getMenuInfo();
//        // handle menu item here
//    }
//    @Override
//    public boolean onContextItemSelected(@NonNull MenuItem item) {
//        int position = -1;
//        try {
//            position = ((ReviewProductAdapter) recyclerViewReview.getAdapter()).getPosition();
//        } catch (Exception e) {
//            return super.onContextItemSelected(item);
//        }
//        switch (item.getItemId()) {
//            case R.id.ctx_menu_remove_backup:
//                // do your stuff
//                break;
//            case R.id.ctx_menu_restore_backup:
//                // do your stuff
//                break;
//        }
//        return super.onContextItemSelected(item);
//    }

    private void callApiGetAllReviewByProduct(String productID) {
        ApiService.apiService.getAllReviewByProduct(productID).enqueue(new Callback<ListReviewResponse>() {
            @Override
            public void onResponse(Call<ListReviewResponse> call, Response<ListReviewResponse> response) {
                if (response.isSuccessful()) {
                    listReviewResponse = response.body();
                    if (listReviewResponse.getReviews().size() == 0) {
                        reviewNull.setVisibility(View.VISIBLE);
                        layoutReview.setVisibility(View.GONE);
                    } else {
                        quantityReviewProduct.setText("(" + listReviewResponse.getReviews().size() + " đánh giá)");
                        reviewProductAdapter = new ReviewProductAdapter(getContext(), listReviewResponse);
                        recyclerViewReview.setAdapter(reviewProductAdapter);
                        reviewProductAdapter.notifyDataSetChanged();
                    }
                } else {
                    try {
                        Gson gson = new Gson();
                        ApiResponse apiError = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        Log.e("Message", apiError.getMessage());
                        setToast(getActivity(), apiError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ListReviewResponse> call, Throwable t) {
                setToast(getActivity(), "Lỗi server !");
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