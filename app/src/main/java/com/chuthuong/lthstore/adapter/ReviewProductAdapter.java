package com.chuthuong.lthstore.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.response.ListReviewResponse;
import com.chuthuong.lthstore.model.Review;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewProductAdapter extends RecyclerView.Adapter<ReviewProductAdapter.ViewHolder> {

    private Context context;
    private ListReviewResponse listReviewResponse;

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @NonNull
    @Override
    public ReviewProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReviewProductAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_product, parent, false));
    }

    public ReviewProductAdapter(Context context, ListReviewResponse listReviewResponse) {
        this.context = context;
        this.listReviewResponse = listReviewResponse;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewProductAdapter.ViewHolder holder, int position) {
        Review review = listReviewResponse.getReviews().get(position);
        Glide.with(context).load(review.getUser().getAvatar()).into(holder.imageViewUser);
        holder.txtNameUser.setText(review.getUser().getUsername());

        Date date = review.getCreatedAt();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dateFormat = formatter.format(date);
        holder.txtDateReview.setText(dateFormat);


        holder.ratingBar.setRating(review.getStar());
        if (review.getReviewInfoProductOrdered() != null) {
            holder.txtProductInfo.setText(review.getReviewInfoProductOrdered().getSize() + ", " + review.getReviewInfoProductOrdered().getColor());
        } else {
            holder.txtProductInfo.setText("");
        }
        holder.txtContentReview.setText(review.getContent());
//        holder.itemView.findViewById(R.id.txt_update_review).setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                inflater.inflate(R.menu.menu_main, menu);
//            }
//        });
//        holder.itemView.findViewById(R.id.txt_update_review).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "Chức năng update đang cập nhật !", Toast.LENGTH_SHORT).show();
//            }
//        });

        holder.txtUpdateReview.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add("Sửa").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(context, "update", Toast.LENGTH_SHORT).show();
                        //do what u want
                        return true;
                    }
                });
                menu.add("Xóa").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(context, "xóa", Toast.LENGTH_SHORT).show();

                        return true;
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        if (listReviewResponse != null) {
            return listReviewResponse.getReviews().size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageViewUser;
        TextView txtNameUser, txtDateReview, txtProductInfo, txtContentReview, txtUpdateReview;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            imageViewUser = itemView.findViewById(com.chuthuong.lthstore.R.id.image_user_review);
            txtNameUser = itemView.findViewById(R.id.name_user_review);
            txtDateReview = itemView.findViewById(R.id.date_user_review);
            ratingBar = itemView.findViewById(R.id.review_item_rating_bar);
            txtProductInfo = itemView.findViewById(R.id.product_info);
            txtContentReview = itemView.findViewById(R.id.review_content);
            txtUpdateReview = itemView.findViewById(R.id.txt_update_review);
        }
    }
}
