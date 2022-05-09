package com.chuthuong.lthstore.utils;

import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.chuthuong.lthstore.R;

public class ShowHidePassword {

    public static void showPassword(EditText edtPassword) {
        // hiển thị mật khẩu
        edtPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= (edtPassword.getRight() - (edtPassword.getLeft() / 2) - edtPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (edtPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                            edtPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_vpn_key_24, 0, R.drawable.ic_baseline_remove_red_eye_24, 0);
                        } else {
                            edtPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_vpn_key_24, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }

                        return true;
                    }
                }
                return false;
            }
        });
    }
}
