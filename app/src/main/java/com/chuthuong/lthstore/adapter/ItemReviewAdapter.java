package com.chuthuong.lthstore.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.ReviewProductActivity;
import com.chuthuong.lthstore.model.Order;
import com.chuthuong.lthstore.model.OrderItem;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemReviewAdapter extends RecyclerView.Adapter<ItemReviewAdapter.ViewHolder> {
    private Context context;
    private Order order;
    private ReviewProductActivity reviewProductActivity;
    public List<Float> rates;
    public  List<String> reviews;
    public ItemReviewAdapter(Context context, Order order) {
        this.context = context;
        this.order = order;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemReviewAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_create_review_product, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        reviews = new ArrayList<String>();
        rates = new ArrayList<Float>();
        for (int i = 0; i < order.getOrderItems().size(); i++) {
            reviews.add(i, "");
            rates.add(i, 5.0F);
        }
        reviewProductActivity = (ReviewProductActivity) context;
        OrderItem orderItem = order.getOrderItems().get(position);
        Glide.with(context).load(orderItem.getImage()).into(holder.imgProduct);
        holder.nameProduct.setText(orderItem.getName());
        holder.types.setText(orderItem.getSize()+", "+orderItem.getColor());
        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                switch ((int) rating) {
                    case 1:
                        holder.edtReviews.setHint("Hãy chia sẻ vì sao sản phẩm này không tốt nhé");
                        break;
                    case 2:
                        holder.edtReviews.setHint("Hãy chia sẻ vì sao bạn không thích sản phẩm này nhé");
                        break;
                    case 3:
                        holder.edtReviews.setHint("Hãy chia sẻ vì sao bạn chưa thật sự thích sản phẩm này nhé");
                        break;
                    case 4:
                        holder.edtReviews.setHint("Hãy chia sẻ vì sao bạn thích sản phẩm này nhé");
                        break;
                    case 5:
                    default:
                        holder.edtReviews.setHint("Hãy chia sẻ những điều bạn thích về sản phẩm này nhé");
                        break;
                }
                rates.set(position, holder.ratingBar.getRating());
            }
        });
        holder.edtReviews.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reviews.set(position, holder.edtReviews.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void callApiCreateReviews(){

    }
    @Override
    public int getItemCount() {
        if (order.getOrderItems() != null) {
            return  order.getOrderItems().size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgProduct;
        TextView nameProduct, types;
        RatingBar ratingBar;
        EditText edtReviews;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product_review);
            nameProduct = itemView.findViewById(R.id.txt_name_product_review);
            types = itemView.findViewById(R.id.txt_type_product);
            ratingBar =itemView.findViewById(R.id.rating_bar_review_product);
            edtReviews = itemView.findViewById(R.id.edit_text_reviews);
        }
    }
}
