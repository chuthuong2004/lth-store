package com.chuthuong.lthstore.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.chuthuong.lthstore.fragments.DescriptionProductFragment;
import com.chuthuong.lthstore.fragments.ReviewProductFragment;
import com.chuthuong.lthstore.fragments.SuggestionProductFragment;

public class ViewPagerDetailProductAdapter extends FragmentStateAdapter {

    public ViewPagerDetailProductAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new ReviewProductFragment();
            case 2:
                return new SuggestionProductFragment();
            case 0:
            default:
                return new DescriptionProductFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
