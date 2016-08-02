package com.example.arthur.myapplication.utils;

import com.example.arthur.myapplication.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/8/2.
 */
public class ImageCodeConverter {
    static Class<com.example.arthur.myapplication.R.drawable> myDrawableClass = R.drawable.class;

    static public int getMainBackgroundResource(String imageCode) {

        int iCode = Integer.parseInt(imageCode);
        String imageName;
        if (iCode == 100) {
            imageName = "qing_b";
        } else if (iCode > 100 && iCode < 300) {
            imageName = "yun_b";
        } else if (iCode >= 300 && iCode < 400) {
            imageName = "yu_b";
        } else if (iCode >= 400 && iCode < 500) {
            imageName = "xue_b";
        } else if (iCode >= 500 && iCode < 900) {
            imageName = "wu_b";
        } else {
            imageName = "moren_b";
        }
        Integer value;
        try {
            value = myDrawableClass.getDeclaredField(imageName).getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
            value = R.drawable.moren;//待处理
        }
        return value;
    }

    static public int getBriefBackgroundResource(String imageCode) {

        int iCode = Integer.parseInt(imageCode);
        String imageName;
        if (iCode == 100) {
            imageName = "qing";
        } else if (iCode > 100 && iCode < 300) {
            imageName = "yun";
        } else if (iCode >= 300 && iCode < 400) {
            imageName = "yu";
        } else if (iCode >= 400 && iCode < 500) {
            imageName = "xue";
        } else if (iCode >= 500 && iCode < 900) {
            imageName = "wu";
        } else {
            imageName = "moren";
        }
        Integer value;
        try {
            value = myDrawableClass.getDeclaredField(imageName).getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
            value = R.drawable.moren;//待处理
        }
        return value;
    }

    static public int getWeatherIconResource(String imageCode) {

        int iCode = Integer.parseInt(imageCode);
        String imageName;
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (iCode == 100) {
            if (hour >= 6 && hour < 18)
                imageName = "i_qing_d";
            else
                imageName = "i_qing_n";
        } else if (iCode > 100 && iCode < 200) {
            imageName = "i_yun";
        } else if (iCode >= 200 && iCode < 300) {
            imageName = "i_feng";
        } else if (iCode >= 300 && iCode <= 304) {
            imageName = "i_zhen_yu";
        } else if (iCode >= 305 && iCode < 400) {
            imageName = "i_yu";
        } else if (iCode >= 400 && iCode < 500) {
            imageName = "i_xue";
        } else if (iCode >= 500 && iCode <= 502) {
            imageName = "i_wu";
        } else {
            imageName = "i_sha_chen";
        }
        Integer value;
        try {
            value = myDrawableClass.getDeclaredField(imageName).getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
            value = R.drawable.i_qing_d;//待处理
        }
        return value;
    }
}
