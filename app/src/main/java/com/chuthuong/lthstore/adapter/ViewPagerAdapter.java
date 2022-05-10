package com.chuthuong.lthstore.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.chuthuong.lthstore.fragments.HomeFragment;
import com.chuthuong.lthstore.fragments.OrderFragment;
import com.chuthuong.lthstore.fragments.ProductFragment;
import com.chuthuong.lthstore.fragments.ProfileFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new ProductFragment();
            case 2:
                return new OrderFragment();
            case 3:
                return new ProfileFragment();
            case 0:
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
