package com.example.arthur.pureweather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Administrator on 2016/8/8.
 */
public class PreferenceUtils {
    private Context context;
    private static PreferenceUtils INSTANCE;
    private SharedPreferences mSharedPreferences;

    private PreferenceUtils(Context context){
        this.context = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public static PreferenceUtils getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = new PreferenceUtils(context);
        }
        return INSTANCE;
    }
    public String getString(String key, String defaultValue){
        return mSharedPreferences.getString(key,defaultValue);
    }
    public boolean getBoolean(String key, Boolean defaultValue){
        return mSharedPreferences.getBoolean(key, defaultValue);
    }
    public int getInt(String key, int defaultValue){
        return mSharedPreferences.getInt(key,defaultValue);
    }
    public void putInt(String key, int value){
        mSharedPreferences.edit().putInt(key, value).commit();
    }
    public String getIntervalText(String key, int defaultInterval){
        int interval = mSharedPreferences.getInt(key,0);
        String text;
        if (interval == 0){
            text = new String("取消自动更新");
        }else {
            text = new StringBuilder().append(interval).append("小时").toString();
        }
        return text;
    }
}
