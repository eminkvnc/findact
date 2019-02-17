package com.example.emin.findact;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ProgressDialog extends Dialog {

    private ImageView imageView;

    public ProgressDialog(@NonNull Context context) {
        super(context,R.style.TransparentProgressDialog);
        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.gravity= Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(windowManager);
        setTitle(null);
        setCancelable(true);

        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });

        LinearLayout linearLayout=new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        //It can be change size with below line.
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(160,160);
        imageView = new ImageView(context);
        imageView.setBackgroundResource(R.drawable.animation_progress);
        linearLayout.addView(imageView,params);
        addContentView(linearLayout,params);

    }

    @Override
    public void show() {
        super.show();
        AnimationDrawable frameAnimation=(AnimationDrawable)imageView.getBackground();
        frameAnimation.start();

    }


}
