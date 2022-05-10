package com.chuthuong.lthstore.orders;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.chuthuong.lthstore.fragments.HomeFragment;
import com.chuthuong.lthstore.fragments.OrderFragment;
import com.chuthuong.lthstore.fragments.ProductFragment;
import com.chuthuong.lthstore.fragments.ProfileFragment;

public class OrderViewPagerAdapter extends FragmentStatePagerAdapter {

    public OrderViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new Tab2Fragment();
            case 2:
                return new Tab3Fragment();
            case 0:
            default:
                return new Tab1Fragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 1:
                return "Đã giao";
            case 2:
                return "Tất cả đơn hàng";
            case 0:
            default:
                return "Đã đặt";
        }
    }
}
