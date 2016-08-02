package com.example.arthur.myapplication.modle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.arthur.myapplication.R;
import com.example.arthur.myapplication.db.PureWeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/9.
 */
public class PureWeatherDB {

/*
将参数名重新定义
 */
    private Context mContext;

    /*
     * 数据库名
     */
    public static final String DB_NAME = "PureWeather.db";
    public static final String TABLE_WEATHER = "Weather";
    public static final String TABLE_REGION = "Region";
    /*
     * 数据库版本号
     */
    public static final int VERSION = 1;
    /*
     * 私有变量
     */
    volatile private static PureWeatherDB pureWeatherDB;
    private SQLiteDatabase db;

    final private static String STATUS = "status";
    final private static String CITY_NAME = "city_name";
    final private static String CITY_ID = "city_id";
    final private static String UPDATE_TIME = "update_time";
    final private static String NOW_CONDITION = "now_condition";
    final private static String NOW_TEMPERATURE = "now_temperature";
    final private static String IMAGE_CODE = "image_code";
    final private static String HUMIDITY = "humidity";
    final private static String RAINY_PROBABILITY = "rainy_probability";
    final private static String MAX_TEMPERATURE = "max_temperature";
    final private static String MIN_TEMPERATURE = "min_temperature";
    final private static String SPORT_SUGGESTION = "sport_suggestion";
    final private static String SPORT_SUGGESTION_BRIEF = "sport_suggestion_brief";
    final private static String TRAVEL_SUGGESTION = "travel_suggestion";
    final private static String TRAVEL_SUGGESTION_BRIEF = "travel_suggestion_brief";
    final private static String CAR_WASH_SUGGESTION = "car_wash_suggestion";
    final private static String CAR_WASH_SUGGESTION_BRIEF = "car_wash_suggestion_brief";

    final private static String[] FORECAST_DATE = {
            "forecast_1_date","forecast_2_date","forecast_3_date","forecast_4_date",
            "forecast_5_date","forecast_6_date","forecast_7_date"};
    final private static String[] FORECAST_HUMIDITY = {
            "forecast_1_humidity","forecast_2_humidity","forecast_3_humidity","forecast_4_humidity",
            "forecast_5_humidity","forecast_6_humidity","forecast_7_humidity"};
    final private static String[] FORECAST_RAINY_PRO = {
            "forecast_1_rainy_pro","forecast_2_rainy_pro","forecast_3_rainy_pro","forecast_4_rainy_pro",
            "forecast_5_rainy_pro","forecast_6_rainy_pro","forecast_7_rainy_pro"};
    final private static String[] FORECAST_MAX_TEMP = {
            "forecast_1_max_temp","forecast_2_max_temp","forecast_3_max_temp","forecast_4_max_temp",
            "forecast_5_max_temp","forecast_6_max_temp","forecast_7_max_temp"};
    final private static String[] FORECAST_MIN_TEMP = {
            "forecast_1_min_temp","forecast_2_min_temp","forecast_3_min_temp","forecast_4_min_temp",
            "forecast_5_min_temp","forecast_6_min_temp","forecast_7_min_temp"};
    final private static String[] FORECAST_CONDITION = {
            "forecast_1_condition","forecast_2_condition","forecast_3_condition","forecast_4_condition",
            "forecast_5_condition","forecast_6_condition","forecast_7_condition"};
    final private static String[] FORECAST_IMAGE_CODE = {
            "forecast_1_image_code","forecast_2_image_code","forecast_3_image_code","forecast_4_image_code",
            "forecast_5_image_code","forecast_6_image_code","forecast_7_image_code"};

    final private static String REGION_NAME = "region_name";
    final private static String REGION_CODE = "region_code";
    final private static String SUPER_REGION_CODE = "super_region_code";

    /*
     * 构造方法私有化，确保只有一个CoolWeatherDB的实例
     */
    private PureWeatherDB(Context context){
        PureWeatherOpenHelper dbHelper = new PureWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
        mContext = context;
    }

    /*
     * 获取CoolWeatherDB的实例
     * 此处的synchronized关键词修饰此方法，为了保证这个方法是线程安全的，从而保证整个系统中，只有一个该类的实例。
     */
    public static PureWeatherDB getInstance(Context context){
        PureWeatherDB tempDB = pureWeatherDB;
        if (tempDB == null){
            synchronized (PureWeatherDB.class){
                tempDB = pureWeatherDB;
                if (tempDB == null){
                    tempDB = new PureWeatherDB(context);
                    pureWeatherDB = tempDB;
                }
            }
        }
        return tempDB;
    }

    public void saveRegions(List<Region> regions){
        if (regions.size() > 0){
            for (Region region : regions){
                ContentValues value = new ContentValues();
                value.put(REGION_NAME, region.getName());
                value.put(REGION_CODE, region.getCode());
                value.put(SUPER_REGION_CODE, region.getSuperCode());
                db.insert(TABLE_REGION, null, value);
            }
        }
    }

    public List<Region> loadRegions(String superCode){
        List<Region> regions = new ArrayList<>();
        Cursor cursor = db.query(TABLE_REGION,null,SUPER_REGION_CODE + " = ?",new String[]{superCode},null,null,null);
        if (cursor.moveToFirst()){
            do {
                Region region = new Region();
                region.setName(cursor.getString(cursor.getColumnIndex(REGION_NAME)));
                region.setCode(cursor.getString(cursor.getColumnIndex(REGION_CODE)));
                region.setSuperCode(superCode);
                regions.add(region);
            }while (cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }
        return regions;
    }

    public boolean saveWeather(WeatherInfo weatherInfo) {
        ContentValues value = new ContentValues();
        if (weatherInfo != null){
            //basic
            value.put(CITY_NAME, weatherInfo.basic.city);
            value.put(CITY_ID, weatherInfo.basic.id);

           // StringBuilder updateTime = new StringBuilder().append(weatherInfo.basic.update.loc);
            value.put(UPDATE_TIME, weatherInfo.basic.update.loc);
//            //aqi
//            value.put("aqi_value", weatherInfo.getAqi().getCity().getAqi());
//            value.put("pm25_value", weatherInfo.getAqi().getCity().getPm25());
            //now
            value.put(NOW_CONDITION, weatherInfo.now.cond.txt);
            value.put(NOW_TEMPERATURE, weatherInfo.now.tmp);

            //处理背景图片
            value.put(IMAGE_CODE, weatherInfo.now.cond.code);

            value.put(HUMIDITY, weatherInfo.now.hum);
/*----------------------------------------- daily forecast -------------------------------------------*/
            value.put(RAINY_PROBABILITY, weatherInfo.dailyForecast.get(0).pop);
            value.put(MAX_TEMPERATURE, weatherInfo.dailyForecast.get(0).tmp.max);
            value.put(MIN_TEMPERATURE, weatherInfo.dailyForecast.get(0).tmp.min);
            for (int i=0; i<7; i++){
                value.put(PureWeatherDB.FORECAST_DATE[i],weatherInfo.dailyForecast.get(i).date);
                value.put(PureWeatherDB.FORECAST_CONDITION[i],weatherInfo.dailyForecast.get(i).cond.txtD);
                value.put(PureWeatherDB.FORECAST_IMAGE_CODE[i], weatherInfo.dailyForecast.get(i).cond.codeD);
                value.put(PureWeatherDB.FORECAST_HUMIDITY[i], weatherInfo.dailyForecast.get(i).hum);
                value.put(PureWeatherDB.FORECAST_MAX_TEMP[i], weatherInfo.dailyForecast.get(i).tmp.max);
                value.put(PureWeatherDB.FORECAST_MIN_TEMP[i], weatherInfo.dailyForecast.get(i).tmp.min);
                value.put(PureWeatherDB.FORECAST_RAINY_PRO[i], weatherInfo.dailyForecast.get(i).pop);
            }
            //status
            value.put(STATUS, weatherInfo.status);
            //suggestion
            value.put(SPORT_SUGGESTION, weatherInfo.suggestion.sport.txt);
            value.put(SPORT_SUGGESTION_BRIEF, weatherInfo.suggestion.sport.brf);
            value.put(TRAVEL_SUGGESTION, weatherInfo.suggestion.trav.txt);
            value.put(TRAVEL_SUGGESTION_BRIEF, weatherInfo.suggestion.trav.brf);
            value.put(CAR_WASH_SUGGESTION, weatherInfo.suggestion.cw.txt);
            value.put(CAR_WASH_SUGGESTION_BRIEF, weatherInfo.suggestion.cw.brf);

            db.replace(TABLE_WEATHER, null, value);
            return true;
        }
        return false;
    }

    /*
     * 加载简要的天气信息
     */
    public List<BriefWeatherInfo> loadBriefWeatherInfo(){
        List<BriefWeatherInfo> infoList = null;
        Cursor cursor ;

        cursor = db.query(TABLE_WEATHER, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            infoList = new ArrayList<>();
            do{
                BriefWeatherInfo info = new BriefWeatherInfo();

                info.setCityName(cursor.getString(cursor.getColumnIndex(CITY_NAME)));

                StringBuilder condText = new StringBuilder().append(cursor.getString(cursor.getColumnIndex(NOW_CONDITION)));
                info.setCondText(condText.toString());

                StringBuilder tempRange = new StringBuilder().append(cursor.getString(cursor.getColumnIndex(MAX_TEMPERATURE)))
                        .append("°C ~ ").append(cursor.getString(cursor.getColumnIndex(MIN_TEMPERATURE))).append("°C");
                info.setTempRange(tempRange.toString());

                StringBuilder nowTemp = new StringBuilder().append(cursor.getString(cursor.getColumnIndex(NOW_TEMPERATURE))).append("°");
                info.setNowTemp(nowTemp.toString());

                info.setUpdateTime(cursor.getString(cursor.getColumnIndex(UPDATE_TIME)));

                String imageCode = cursor.getString(cursor.getColumnIndex(IMAGE_CODE));
                info.setImageCode(imageCode);

                infoList.add(info);
            }while(cursor.moveToNext());
        }
        if(cursor != null){
            cursor.close();
        }

        return infoList;
    }

    /*
    加载完整的天气信息
     */
    public Cursor loadWeatherInfo(String cityName){
        Cursor cursor ;
        cursor = db.query(TABLE_WEATHER, null, CITY_NAME + " = ?", new String[]{cityName}, null, null, null);
        return cursor;
    }

    public WeatherInfo mloadWeatherInfo(String cityName){
        Cursor cursor ;
        WeatherInfo weatherInfo = new WeatherInfo();

        cursor = db.query(TABLE_WEATHER, null, CITY_NAME + " = ?", new String[]{cityName}, null, null, null);
        if (cursor.moveToNext()){
            weatherInfo.status = cursor.getString(cursor.getColumnIndex(STATUS));
            weatherInfo.now.cond.code = cursor.getString(cursor.getColumnIndex(IMAGE_CODE));
            weatherInfo.now.cond.txt = cursor.getString(cursor.getColumnIndex(NOW_CONDITION));
            weatherInfo.now.hum = cursor.getString(cursor.getColumnIndex(HUMIDITY));
            weatherInfo.now.tmp = cursor.getString(cursor.getColumnIndex(NOW_TEMPERATURE));
            weatherInfo.basic.city = cursor.getString(cursor.getColumnIndex(CITY_NAME));
            weatherInfo.basic.id = cursor.getString(cursor.getColumnIndex(CITY_ID));
            weatherInfo.basic.update.loc = cursor.getString(cursor.getColumnIndex(UPDATE_TIME));
            weatherInfo.suggestion.sport.txt = cursor.getString(cursor.getColumnIndex(SPORT_SUGGESTION));
            weatherInfo.suggestion.sport.brf = cursor.getString(cursor.getColumnIndex(SPORT_SUGGESTION_BRIEF));
            weatherInfo.suggestion.trav.txt = cursor.getString(cursor.getColumnIndex(TRAVEL_SUGGESTION));
            weatherInfo.suggestion.trav.brf = cursor.getString(cursor.getColumnIndex(TRAVEL_SUGGESTION_BRIEF));
            weatherInfo.suggestion.cw.txt = cursor.getString(cursor.getColumnIndex(CAR_WASH_SUGGESTION));
            weatherInfo.suggestion.cw.brf = cursor.getString(cursor.getColumnIndex(CAR_WASH_SUGGESTION_BRIEF));

            for (int i=0; i<7; i++){
                weatherInfo.dailyForecast.get(i).date = cursor.getString(cursor.getColumnIndex(PureWeatherDB.FORECAST_DATE[i]));
                weatherInfo.dailyForecast.get(i).hum = cursor.getString(cursor.getColumnIndex(PureWeatherDB.FORECAST_HUMIDITY[i]));
                weatherInfo.dailyForecast.get(i).pop = cursor.getString(cursor.getColumnIndex(PureWeatherDB.FORECAST_RAINY_PRO[i]));
                weatherInfo.dailyForecast.get(i).tmp.max = cursor.getString(cursor.getColumnIndex(PureWeatherDB.FORECAST_MAX_TEMP[i]));
                weatherInfo.dailyForecast.get(i).tmp.min = cursor.getString(cursor.getColumnIndex(PureWeatherDB.FORECAST_MIN_TEMP[i]));
                weatherInfo.dailyForecast.get(i).cond.codeD = cursor.getString(cursor.getColumnIndex(PureWeatherDB.FORECAST_IMAGE_CODE[i]));
                weatherInfo.dailyForecast.get(i).cond.txtD = cursor.getString(cursor.getColumnIndex(PureWeatherDB.FORECAST_CONDITION[i]));
            }
        }
        if (cursor != null){
            cursor.close();
        }
        return weatherInfo;
    }

    public int deleteWeatherInfo(String cityName){
        return db.delete(TABLE_WEATHER, CITY_NAME + " = ?", new String[]{cityName});
    }

}
