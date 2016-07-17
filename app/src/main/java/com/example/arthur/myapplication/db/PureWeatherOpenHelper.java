package com.example.arthur.myapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/6/9.
 */
public class PureWeatherOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_REGION = "create table Region (" +
            "id integer primary key autoincrement, " +
            "region_name text, " +
            "region_code text, " +
            "super_region_name text," +
            "super_region_code text)";

    /*
     * 天气信息的建表语句
     */
    public static final String CREATE_WEATHER = "create table Weather (" +
            "city_name, " +
            "city_id text primary key, " +
            "update_time text, " +
            "aqi_value text, " +
            "pm25_value text, " +
            "now_cond text, " +
            "now_temp text, " +
            "sunset_time text, " +
            "sunrise_time text, " +
            "forecast_date text, " +
            "rainy_pos text, " +
            "max_temp text, " +
            "min_temp text, " +
            "status text, " +
            "image_code text, " +
            "sport_suggestion text," +
            "sport_suggestion_brf text," +
            "travel_suggestion text," +
            "travel_suggestion_brf text," +
            "car_wash_suggestion text," +
            "car_wash_suggestion_brf text," +
            "humi_value text)";

    public PureWeatherOpenHelper(Context context, String name,
                                 SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(CREATE_WEATHER);
        db.execSQL(CREATE_REGION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }


}
