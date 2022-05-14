package com.chuthuong.lthstore.fragments;

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
import com.chuthuong.lthstore.adapter.FlashSaleProductAdapter;
import com.chuthuong.lthstore.adapter.ReviewProductAdapter;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.ListProduct;
import com.chuthuong.lthstore.model.ListReview;
import com.chuthuong.lthstore.model.Product;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewProductFragment extends Fragment {
        RecyclerView recyclerViewReview;
        ReviewProductAdapter reviewProductAdapter ;
        ListReview listReview;
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
            View view =inflater.inflate(R.layout.fragment_review_product, container, false);
            ratingBar = view.findViewById(R.id.detail_product_review_rating_bar);
            ratingBar.setRating(product.getRate());
            rating = view.findViewById(R.id.rating_product_review);
            rating.setText(product.getRate()+"/5");
            recyclerViewReview = view.findViewById(R.id.rec_review);
            recyclerViewReview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));;
            callApiGetAllReviewByProduct(product.getId());
            quantityReviewProduct = view.findViewById(R.id.quantity_review_product);
            reviewNull =view.findViewById(R.id.review_null);
            layoutReview = view.findViewById(R.id.layout_review);
            return view;
        }

    private void callApiGetAllReviewByProduct(String productID) {
        ApiService.apiService.getAllReviewByProduct(productID).enqueue(new Callback<ListReview>() {
            @Override
            public void onResponse(Call<ListReview> call, Response<ListReview> response) {
                if (response.isSuccessful()) {
                    listReview = response.body();
                    if(listReview.getReviews().size()==0) {
                        reviewNull.setVisibility(View.VISIBLE);
                        layoutReview.setVisibility(View.GONE);
                    }else {
                        quantityReviewProduct.setText("("+listReview.getReviews().size()+" đánh giá)");
                        reviewProductAdapter = new ReviewProductAdapter(getContext(), listReview);
                        recyclerViewReview.setAdapter(reviewProductAdapter);
                        reviewProductAdapter.notifyDataSetChanged();
//                    Log.e("Reviews", listReview.getReviews().get(0).getContent() + "");
                    }
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
            public void onFailure(Call<ListReview> call, Throwable t) {
                Log.e("Lỗi server ", t.toString());
                Toast.makeText(getActivity(), "lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}