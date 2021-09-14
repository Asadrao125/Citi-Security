package com.appsxone.citisecurity.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsxone.citisecurity.R;

public class DialogClass {
    public static void showEmergencyDialog(final Activity activity, String text) {
        final Dialog builder = new Dialog(activity);
        final View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_premises, null);
        final TextView tvInfo = dialogView.findViewById(R.id.tvInfo);
        ImageView imgClose = dialogView.findViewById(R.id.imgClose);
        Button btnClose = dialogView.findViewById(R.id.btnClose);
        tvInfo.setText(text);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setContentView(dialogView);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(builder.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        builder.getWindow().setAttributes(lp);
        builder.show();
    }
}