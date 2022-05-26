package com.chuthuong.lthstore.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.detailActivities.ShowAllActivity;
import com.chuthuong.lthstore.api.ApiService;
import com.chuthuong.lthstore.model.Category;
import com.chuthuong.lthstore.response.ListCategoryResponse;
import com.chuthuong.lthstore.response.ListProductResponse;
import com.chuthuong.lthstore.utils.ApiResponse;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Context context;
    private ListCategoryResponse list;
    private ListProductResponse listProductResponse;

    public CategoryAdapter(Context context, ListCategoryResponse list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        Category category = list.getCategories().get(position);
        Glide.with(context).load(category.getImageCate()).into(holder.catImage);
        holder.catName.setText(category.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callApiProductByCategory(category);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.getCategories().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView catImage;
        TextView catName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catImage = itemView.findViewById(R.id.cat_img);
            catName = itemView.findViewById(R.id.cat_name);

        }
    }

    private void callApiProductByCategory(Category category) {
        ApiService.apiService.getAllProductByCategory(category.getId()).enqueue(new Callback<ListProductResponse>() {
            @Override
            public void onResponse(Call<ListProductResponse> call, Response<ListProductResponse> response) {
                if (response.isSuccessful()) {
                    listProductResponse = response.body();
                    Intent intent = new Intent(context, ShowAllActivity.class);
                    intent.putExtra("list_see_all", listProductResponse);
                    intent.putExtra("title_see_all", category.getName());
                    context.startActivity(intent);
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
            public void onFailure(Call<ListProductResponse> call, Throwable t) {
                Log.e("Lỗi server ", t.toString());
                Toast.makeText(context, "lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
