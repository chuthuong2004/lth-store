package com.chuthuong.lthstore.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.chuthuong.lthstore.R;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.RotatingCircle;
import com.github.ybq.android.spinkit.style.ThreeBounce;

import java.io.Serializable;

public class CustomProgressDialog extends Dialog implements Serializable {

    public CustomProgressDialog(@NonNull Context context) {
        super(context);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        getWindow().setAttributes(params);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.loading_layout, null);
        ProgressBar progressBar = view.findViewById(R.id.progress_bar_loading);
        progressBar.setIndeterminateDrawable(new FadingCircle());
        setContentView(view);
    }
}
