package com.chuthuong.lthstore.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chuthuong.lthstore.R;
import com.chuthuong.lthstore.orders.OrderViewPagerAdapter;
import com.chuthuong.lthstore.widget.CustomViewPager;
import com.google.android.material.tabs.TabLayout;

public class OrderFragment extends Fragment {
    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private View view;
    public OrderFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e("OrderFragment", "Fragment 3");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.e("Thuong","Reload FragmentOrder");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_order, container, false);
        tabLayout = view.findViewById(R.id.tab_layout_order);
        viewPager = view.findViewById(R.id.view_pager_order);
        OrderViewPagerAdapter orderViewPagerAdapter= new OrderViewPagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(orderViewPagerAdapter);
        viewPager.setPagingEnable(false);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }
}