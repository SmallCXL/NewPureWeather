package com.example.arthur.myapplication.modle;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by Administrator on 2016/6/21.
 */
public class MyImageLoader {

    public static void load(Context context, @DrawableRes int imageRes, ImageView view) {
        Glide.with(context).load(imageRes).crossFade().centerCrop().into(view);
    }



    public static void clear(Context context) {
        Glide.get(context).clearMemory();
    }
}
