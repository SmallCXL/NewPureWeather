package com.example.arthur.pureweather.utils;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * Created by Administrator on 2016/6/21.
 */
public class MyImageLoader {

    public static void load(Context context, @DrawableRes int imageRes, ImageView view) {
        Glide.with(context).load(imageRes).crossFade().centerCrop().into(view);
    }

    public static void load(Context context, @DrawableRes int imageRes, View view){
        Glide.with(context)
                .load(imageRes)
                .fitCenter()
                .crossFade()
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        view.setBackground(resource);
                    }
                });
    }
    public static void clear(Context context) {
        Glide.get(context).clearMemory();
    }
}
