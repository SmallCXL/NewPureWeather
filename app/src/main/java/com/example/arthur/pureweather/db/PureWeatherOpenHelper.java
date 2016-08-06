package com.example.arthur.pureweather.db;

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
            "city_name text, " +
            "city_id text primary key, " +
            "update_time text, " +
            "now_condition text, " +
            "now_temperature text, " +
            "image_code text, " +
            "humidity text," +
            "rainy_probability text, " +
            "max_temperature text, " +
            "min_temperature text, " +
            "status text, " +
            "sport_suggestion text," +
            "sport_suggestion_brief text," +
            "travel_suggestion text," +
            "travel_suggestion_brief text," +
            "car_wash_suggestion text," +
            "car_wash_suggestion_brief text," +

            "forecast_1_date text," +
            "forecast_1_humidity text," +
            "forecast_1_rainy_pro text," +
            "forecast_1_max_temp text," +
            "forecast_1_min_temp text," +
            "forecast_1_condition text," +
            "forecast_1_image_code text," +

            "forecast_2_date text," +
            "forecast_2_humidity text," +
            "forecast_2_rainy_pro text," +
            "forecast_2_max_temp text," +
            "forecast_2_min_temp text," +
            "forecast_2_condition text," +
            "forecast_2_image_code text," +

            "forecast_3_date text," +
            "forecast_3_humidity text," +
            "forecast_3_rainy_pro text," +
            "forecast_3_max_temp text," +
            "forecast_3_min_temp text," +
            "forecast_3_condition text," +
            "forecast_3_image_code text," +

            "forecast_4_date text," +
            "forecast_4_humidity text," +
            "forecast_4_rainy_pro text," +
            "forecast_4_max_temp text," +
            "forecast_4_min_temp text," +
            "forecast_4_condition text," +
            "forecast_4_image_code text," +

            "forecast_5_date text," +
            "forecast_5_humidity text," +
            "forecast_5_rainy_pro text," +
            "forecast_5_max_temp text," +
            "forecast_5_min_temp text," +
            "forecast_5_condition text," +
            "forecast_5_image_code text," +

            "forecast_6_date text," +
            "forecast_6_humidity text," +
            "forecast_6_rainy_pro text," +
            "forecast_6_max_temp text," +
            "forecast_6_min_temp text," +
            "forecast_6_condition text," +
            "forecast_6_image_code text," +

            "forecast_7_date text," +
            "forecast_7_humidity text," +
            "forecast_7_rainy_pro text," +
            "forecast_7_max_temp text," +
            "forecast_7_min_temp text," +
            "forecast_7_condition text," +
            "forecast_7_image_code text)";

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
