package com.chuthuong.lthstore.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.activities.detailActivities.ProductDetailActivity;
import com.chuthuong.lthstore.model.Product;

public class DescriptionProductFragment extends Fragment {
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
        RelativeLayout.LayoutParams layoutParamsReadMore = new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsReadMore.addRule(RelativeLayout.ALIGN_PARENT_START);
        layoutParamsReadMore.addRule(RelativeLayout.ALIGN_PARENT_END);
        layoutParamsReadMore.setMarginStart(46);
        layoutParamsReadMore.setMarginEnd(44);
        layoutParamsReadMore.setMargins(0,-26,0,0);

        RelativeLayout.LayoutParams layoutParamsCollapse = new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, 200);

        layoutParamsCollapse.addRule(RelativeLayout.ALIGN_PARENT_START);
        layoutParamsCollapse.addRule(RelativeLayout.ALIGN_PARENT_END);
        layoutParamsCollapse.setMarginStart(46);
        layoutParamsCollapse.setMarginEnd(44);
        layoutParamsCollapse.setMargins(0,-26,0,0);

        TextView desProduct = root.findViewById(R.id.txt_description_product_detail);
        TextView readMoreDesc =root.findViewById(R.id.read_more_desc);
        readMoreDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(readMoreDesc.getText().toString().equals("Xem thêm")) {
                    readMoreDesc.setText("Thu gọn");
                    readMoreDesc.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_keyboard_arrow_up_24, 0);

//                    android:layout_below="@+id/text_view1"
//                    android:layout_alignParentStart="true"
//                    android:layout_alignParentEnd="true"
//                    android:layout_marginStart="46px"
//                    android:layout_marginTop="-26px"
//                    android:layout_marginEnd="44px"

                    layoutParamsReadMore.addRule(RelativeLayout.BELOW, R.id.text_view1);
                    desProduct.setLayoutParams(layoutParamsReadMore);
                }
                else {
                    readMoreDesc.setText("Xem thêm");
                    readMoreDesc.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_keyboard_arrow_down_24, 0);
                    layoutParamsCollapse.addRule(RelativeLayout.BELOW, R.id.text_view1);
                    desProduct.setLayoutParams(layoutParamsCollapse);
                }
            }
        });

        TextView policy = root.findViewById(R.id.policy);
        TextView readMorePolicy = root.findViewById(R.id.read_more_policy);
        readMorePolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(readMorePolicy.getText().toString().equals("Xem thêm")) {
                    readMorePolicy.setText("Thu gọn");
                    readMorePolicy.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_keyboard_arrow_up_24, 0);
                    layoutParamsReadMore.addRule(RelativeLayout.BELOW, R.id.text_view2);
                    policy.setLayoutParams(layoutParamsReadMore);
                }
                else {
                    readMorePolicy.setText("Xem thêm");
                    readMorePolicy.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_keyboard_arrow_down_24, 0);
                    layoutParamsCollapse.addRule(RelativeLayout.BELOW, R.id.text_view2);
                    policy.setLayoutParams(layoutParamsCollapse);
                }
            }
        });

        desProduct.setText(product.getDesProduct());
        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }
}