package com.chuthuong.lthstore.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class CustomViewPager2 extends ViewPager {
    private boolean enable;

    public CustomViewPager2(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.enable = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(this.enable) {
            return super.onTouchEvent(ev);
        }
        return false;
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        if(this.enable) {
            return super.onInterceptHoverEvent(event);
        }
        return false;
    }
    public void setPagingEnable(boolean enabled){
        this.enable = enabled;
    }
}
