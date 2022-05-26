package com.chuthuong.lthstore.orders;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.chuthuong.lthstore.fragments.DescriptionProductFragment;
import com.chuthuong.lthstore.fragments.HomeFragment;
import com.chuthuong.lthstore.fragments.OrderFragment;
import com.chuthuong.lthstore.fragments.ProductFragment;
import com.chuthuong.lthstore.fragments.ProfileFragment;
import com.chuthuong.lthstore.fragments.ReviewProductFragment;
import com.chuthuong.lthstore.fragments.SuggestionProductFragment;

public class OrderViewPagerAdapter extends FragmentStateAdapter {


    public OrderViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new OrderedFragment();
            case 2:
                return new ShippingFragment();
            case 3:
                return new DeliveryFragment();
            case 4:
                return new DeliveredFragment();
            case 5:
                return new CancelFragment();
            case 0:
            default:
                return new AllOrderFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}
