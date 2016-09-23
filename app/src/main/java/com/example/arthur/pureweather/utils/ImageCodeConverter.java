package com.example.arthur.pureweather.utils;

import com.example.arthur.pureweather.R;

import java.util.Calendar;
import java.util.Date;

/**
 * ImageCodeConverter：自定义图片代码转换工具，根据和风天气API提供的天气状况信息，完成以下功能：
 * 1、获取主界面中天气背景图片的资源代码（int）
 * 2、获取收藏界面中天气简图的资源代码（int）
 * 3、获取天气图标的资源代码（int）
 */
public class ImageCodeConverter {
    static Class<com.example.arthur.pureweather.R.drawable> myDrawableClass = R.drawable.class;

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

    /**
     * 获取天气图标的资源代码（int）
     * @param imageCode 和风提供的天气状况码
     * @param requestType 请求类型，若为七天预测中的请求，则一律返回白天的天气图标，其余请求则根据当前时间（白天或夜晚）返回不同的图标
     * @return 天气图标的资源代码（int）
     */
    static public int getWeatherIconResource(String imageCode, String requestType) {

        int iCode = Integer.parseInt(imageCode);
        String imageName;
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (iCode == 100) {
            if ((hour >= 6 && hour < 18)|| requestType.equals("daily_forecast"))
                imageName = "i_qing_d";
            else
                imageName = "i_qing_n";
        } else if (iCode > 100 && iCode <=103) {
            if ((hour >= 6 && hour < 18)|| requestType.equals("daily_forecast"))
                imageName = "i_yun_d";
            else
                imageName = "i_yun_n";
        } else if (iCode == 104){
            imageName = "i_yin";
        } else if (iCode >= 200 && iCode < 300) {
            imageName = "i_feng";
        } else if (iCode >= 300 && iCode <= 304) {
            if ((hour >= 6 && hour < 18)|| requestType.equals("daily_forecast"))
                imageName = "i_zhen_yu_d";
            else
                imageName = "i_zhen_yu_n";
        } else if (iCode >= 305 && iCode <=309) {
            imageName = "i_yu";
        } else if (iCode >= 310 && iCode < 400){
            imageName = "i_bao_yu";
        }
        else if (iCode >= 400 && iCode < 500) {
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
