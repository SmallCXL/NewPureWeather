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

    final private static String FORECAST_1_DATE = "forecast_1_date";
    final private static String FORECAST_1_HUMIDITY = "forecast_1_humidity";
    final private static String FORECAST_1_RAINY_PRO = "forecast_1_rainy_pro";
    final private static String FORECAST_1_MAX_TEMP = "forecast_1_max_temp";
    final private static String FORECAST_1_MIN_TEMP = "forecast_1_min_temp";
    final private static String FORECAST_1_CONDITION = "forecast_1_condition";
    final private static String FORECAST_1_IMAGE_CODE = "forecast_1_image_code";

    final private static String FORECAST_2_DATE = "forecast_2_date";
    final private static String FORECAST_2_HUMIDITY = "forecast_2_humidity";
    final private static String FORECAST_2_RAINY_PRO = "forecast_2_rainy_pro";
    final private static String FORECAST_2_MAX_TEMP = "forecast_2_max_temp";
    final private static String FORECAST_2_MIN_TEMP = "forecast_2_min_temp";
    final private static String FORECAST_2_CONDITION = "forecast_2_condition";
    final private static String FORECAST_2_IMAGE_CODE = "forecast_2_image_code";

    final private static String FORECAST_3_DATE = "forecast_3_date";
    final private static String FORECAST_3_HUMIDITY = "forecast_3_humidity";
    final private static String FORECAST_3_RAINY_PRO = "forecast_3_rainy_pro";
    final private static String FORECAST_3_MAX_TEMP = "forecast_3_max_temp";
    final private static String FORECAST_3_MIN_TEMP = "forecast_3_min_temp";
    final private static String FORECAST_3_CONDITION = "forecast_3_condition";
    final private static String FORECAST_3_IMAGE_CODE = "forecast_3_image_code";

    final private static String FORECAST_4_DATE = "forecast_4_date";
    final private static String FORECAST_4_HUMIDITY = "forecast_4_humidity";
    final private static String FORECAST_4_RAINY_PRO = "forecast_4_rainy_pro";
    final private static String FORECAST_4_MAX_TEMP = "forecast_4_max_temp";
    final private static String FORECAST_4_MIN_TEMP = "forecast_4_min_temp";
    final private static String FORECAST_4_CONDITION = "forecast_4_condition";
    final private static String FORECAST_4_IMAGE_CODE = "forecast_4_image_code";

    final private static String FORECAST_5_DATE = "forecast_5_date";
    final private static String FORECAST_5_HUMIDITY = "forecast_5_humidity";
    final private static String FORECAST_5_RAINY_PRO = "forecast_5_rainy_pro";
    final private static String FORECAST_5_MAX_TEMP = "forecast_5_max_temp";
    final private static String FORECAST_5_MIN_TEMP = "forecast_5_min_temp";
    final private static String FORECAST_5_CONDITION = "forecast_5_condition";
    final private static String FORECAST_5_IMAGE_CODE = "forecast_5_image_code";

    final private static String FORECAST_6_DATE = "forecast_6_date";
    final private static String FORECAST_6_HUMIDITY = "forecast_6_humidity";
    final private static String FORECAST_6_RAINY_PRO = "forecast_6_rainy_pro";
    final private static String FORECAST_6_MAX_TEMP = "forecast_6_max_temp";
    final private static String FORECAST_6_MIN_TEMP = "forecast_6_min_temp";
    final private static String FORECAST_6_CONDITION = "forecast_6_condition";
    final private static String FORECAST_6_IMAGE_CODE = "forecast_6_image_code";

    final private static String FORECAST_7_DATE = "forecast_7_date";
    final private static String FORECAST_7_HUMIDITY = "forecast_7_humidity";
    final private static String FORECAST_7_RAINY_PRO = "forecast_7_rainy_pro";
    final private static String FORECAST_7_MAX_TEMP = "forecast_7_max_temp";
    final private static String FORECAST_7_MIN_TEMP = "forecast_7_min_temp";
    final private static String FORECAST_7_CONDITION = "forecast_7_condition";
    final private static String FORECAST_7_IMAGE_CODE = "forecast_7_image_code";

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

            StringBuilder updateTime = new StringBuilder().append(weatherInfo.basic.update.loc).append(" 发布");
            value.put(UPDATE_TIME, updateTime.toString().substring(5));
//            //aqi
//            value.put("aqi_value", weatherInfo.getAqi().getCity().getAqi());
//            value.put("pm25_value", weatherInfo.getAqi().getCity().getPm25());
            //now
            value.put(NOW_CONDITION, weatherInfo.now.cond.txt);
            value.put(NOW_TEMPERATURE, weatherInfo.now.tmp);

            //处理背景图片
            String imageCode = weatherInfo.now.cond.code;
            int iCode = Integer.parseInt(imageCode);
            String imageName;
            if (iCode == 100){
                imageName = "qing";
            }
            else if (iCode >100 && iCode < 300){
                imageName = "yun";
            }
            else if (iCode >= 300 && iCode < 400){
                imageName = "yu";
            }
            else if (iCode >= 400 && iCode < 500){
                imageName = "xue";
            }
            else if (iCode >= 500 && iCode < 900)
            {
                imageName = "wu";
            }
            else {
                imageName = "moren";
            }
            value.put(IMAGE_CODE, imageName);


            value.put(HUMIDITY, weatherInfo.now.hum);
/*----------------------------------------- daily forecast -------------------------------------------*/
            value.put(RAINY_PROBABILITY, weatherInfo.dailyForecast.get(0).pop);
            value.put(MAX_TEMPERATURE, weatherInfo.dailyForecast.get(0).tmp.max);
            value.put(MIN_TEMPERATURE, weatherInfo.dailyForecast.get(0).tmp.min);
    /*---------------------------------------- day 1 ------------------------------------------------------*/
            value.put(FORECAST_1_DATE, weatherInfo.dailyForecast.get(0).date);
            value.put(FORECAST_1_CONDITION, weatherInfo.dailyForecast.get(0).cond.txtD);
            value.put(FORECAST_1_IMAGE_CODE, weatherInfo.dailyForecast.get(0).cond.codeD);
            value.put(FORECAST_1_HUMIDITY, weatherInfo.dailyForecast.get(0).hum);
            value.put(FORECAST_1_MAX_TEMP, weatherInfo.dailyForecast.get(0).tmp.max);
            value.put(FORECAST_1_MIN_TEMP, weatherInfo.dailyForecast.get(0).tmp.min);
            value.put(FORECAST_1_RAINY_PRO, weatherInfo.dailyForecast.get(0).pop);
    /*---------------------------------------- day 2 ------------------------------------------------------*/
            value.put(FORECAST_2_DATE, weatherInfo.dailyForecast.get(1).date);
            value.put(FORECAST_2_CONDITION, weatherInfo.dailyForecast.get(1).cond.txtD);
            value.put(FORECAST_2_IMAGE_CODE, weatherInfo.dailyForecast.get(1).cond.codeD);
            value.put(FORECAST_2_HUMIDITY, weatherInfo.dailyForecast.get(1).hum);
            value.put(FORECAST_2_MAX_TEMP, weatherInfo.dailyForecast.get(1).tmp.max);
            value.put(FORECAST_2_MIN_TEMP, weatherInfo.dailyForecast.get(1).tmp.min);
            value.put(FORECAST_2_RAINY_PRO, weatherInfo.dailyForecast.get(1).pop);
    /*---------------------------------------- day 3 ------------------------------------------------------*/
            value.put(FORECAST_3_DATE, weatherInfo.dailyForecast.get(2).date);
            value.put(FORECAST_3_CONDITION, weatherInfo.dailyForecast.get(2).cond.txtD);
            value.put(FORECAST_3_IMAGE_CODE, weatherInfo.dailyForecast.get(2).cond.codeD);
            value.put(FORECAST_3_HUMIDITY, weatherInfo.dailyForecast.get(2).hum);
            value.put(FORECAST_3_MAX_TEMP, weatherInfo.dailyForecast.get(2).tmp.max);
            value.put(FORECAST_3_MIN_TEMP, weatherInfo.dailyForecast.get(2).tmp.min);
            value.put(FORECAST_3_RAINY_PRO, weatherInfo.dailyForecast.get(2).pop);
    /*---------------------------------------- day 4 ------------------------------------------------------*/
            value.put(FORECAST_4_DATE, weatherInfo.dailyForecast.get(3).date);
            value.put(FORECAST_4_CONDITION, weatherInfo.dailyForecast.get(3).cond.txtD);
            value.put(FORECAST_4_IMAGE_CODE, weatherInfo.dailyForecast.get(3).cond.codeD);
            value.put(FORECAST_4_HUMIDITY, weatherInfo.dailyForecast.get(3).hum);
            value.put(FORECAST_4_MAX_TEMP, weatherInfo.dailyForecast.get(3).tmp.max);
            value.put(FORECAST_4_MIN_TEMP, weatherInfo.dailyForecast.get(3).tmp.min);
            value.put(FORECAST_4_RAINY_PRO, weatherInfo.dailyForecast.get(3).pop);
    /*---------------------------------------- day 5 ------------------------------------------------------*/
            value.put(FORECAST_5_DATE, weatherInfo.dailyForecast.get(4).date);
            value.put(FORECAST_5_CONDITION, weatherInfo.dailyForecast.get(4).cond.txtD);
            value.put(FORECAST_5_IMAGE_CODE, weatherInfo.dailyForecast.get(4).cond.codeD);
            value.put(FORECAST_5_HUMIDITY, weatherInfo.dailyForecast.get(4).hum);
            value.put(FORECAST_5_MAX_TEMP, weatherInfo.dailyForecast.get(4).tmp.max);
            value.put(FORECAST_5_MIN_TEMP, weatherInfo.dailyForecast.get(4).tmp.min);
            value.put(FORECAST_5_RAINY_PRO, weatherInfo.dailyForecast.get(4).pop);
    /*---------------------------------------- day 6 ------------------------------------------------------*/
            value.put(FORECAST_6_DATE, weatherInfo.dailyForecast.get(5).date);
            value.put(FORECAST_6_CONDITION, weatherInfo.dailyForecast.get(5).cond.txtD);
            value.put(FORECAST_6_IMAGE_CODE, weatherInfo.dailyForecast.get(5).cond.codeD);
            value.put(FORECAST_6_HUMIDITY, weatherInfo.dailyForecast.get(5).hum);
            value.put(FORECAST_6_MAX_TEMP, weatherInfo.dailyForecast.get(5).tmp.max);
            value.put(FORECAST_6_MIN_TEMP, weatherInfo.dailyForecast.get(5).tmp.min);
            value.put(FORECAST_6_RAINY_PRO, weatherInfo.dailyForecast.get(5).pop);
    /*---------------------------------------- day 7 ------------------------------------------------------*/
            value.put(FORECAST_7_DATE, weatherInfo.dailyForecast.get(6).date);
            value.put(FORECAST_7_CONDITION, weatherInfo.dailyForecast.get(6).cond.txtD);
            value.put(FORECAST_7_IMAGE_CODE, weatherInfo.dailyForecast.get(6).cond.codeD);
            value.put(FORECAST_7_HUMIDITY, weatherInfo.dailyForecast.get(6).hum);
            value.put(FORECAST_7_MAX_TEMP, weatherInfo.dailyForecast.get(6).tmp.max);
            value.put(FORECAST_7_MIN_TEMP, weatherInfo.dailyForecast.get(6).tmp.min);
            value.put(FORECAST_7_RAINY_PRO, weatherInfo.dailyForecast.get(6).pop);
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
            weatherInfo.basic.update.utc = cursor.getString(cursor.getColumnIndex(UPDATE_TIME));
            weatherInfo.suggestion.sport.txt = cursor.getString(cursor.getColumnIndex(SPORT_SUGGESTION));
            weatherInfo.suggestion.sport.brf = cursor.getString(cursor.getColumnIndex(SPORT_SUGGESTION_BRIEF));
            weatherInfo.suggestion.trav.txt = cursor.getString(cursor.getColumnIndex(TRAVEL_SUGGESTION));
            weatherInfo.suggestion.trav.brf = cursor.getString(cursor.getColumnIndex(TRAVEL_SUGGESTION_BRIEF));
            weatherInfo.suggestion.cw.txt = cursor.getString(cursor.getColumnIndex(CAR_WASH_SUGGESTION));
            weatherInfo.suggestion.cw.brf = cursor.getString(cursor.getColumnIndex(CAR_WASH_SUGGESTION_BRIEF));

    /*--------------------------------------- day 1 --------------------------------------------------*/
            weatherInfo.dailyForecast.get(0).date = cursor.getString(cursor.getColumnIndex(FORECAST_1_DATE));
            weatherInfo.dailyForecast.get(0).hum = cursor.getString(cursor.getColumnIndex(FORECAST_1_HUMIDITY));
            weatherInfo.dailyForecast.get(0).pop = cursor.getString(cursor.getColumnIndex(FORECAST_1_RAINY_PRO));
            weatherInfo.dailyForecast.get(0).tmp.max = cursor.getString(cursor.getColumnIndex(FORECAST_1_MAX_TEMP));
            weatherInfo.dailyForecast.get(0).tmp.min = cursor.getString(cursor.getColumnIndex(FORECAST_1_MIN_TEMP));
            weatherInfo.dailyForecast.get(0).cond.codeD = cursor.getString(cursor.getColumnIndex(FORECAST_1_IMAGE_CODE));
            weatherInfo.dailyForecast.get(0).cond.txtD = cursor.getString(cursor.getColumnIndex(FORECAST_1_CONDITION));
    /*--------------------------------------- day 2 --------------------------------------------------*/
            weatherInfo.dailyForecast.get(1).date = cursor.getString(cursor.getColumnIndex(FORECAST_2_DATE));
            weatherInfo.dailyForecast.get(1).hum = cursor.getString(cursor.getColumnIndex(FORECAST_2_HUMIDITY));
            weatherInfo.dailyForecast.get(1).pop = cursor.getString(cursor.getColumnIndex(FORECAST_2_RAINY_PRO));
            weatherInfo.dailyForecast.get(1).tmp.max = cursor.getString(cursor.getColumnIndex(FORECAST_2_MAX_TEMP));
            weatherInfo.dailyForecast.get(1).tmp.min = cursor.getString(cursor.getColumnIndex(FORECAST_2_MIN_TEMP));
            weatherInfo.dailyForecast.get(1).cond.codeD = cursor.getString(cursor.getColumnIndex(FORECAST_2_IMAGE_CODE));
            weatherInfo.dailyForecast.get(1).cond.txtD = cursor.getString(cursor.getColumnIndex(FORECAST_2_CONDITION));
    /*--------------------------------------- day 3 --------------------------------------------------*/
            weatherInfo.dailyForecast.get(2).date = cursor.getString(cursor.getColumnIndex(FORECAST_3_DATE));
            weatherInfo.dailyForecast.get(2).hum = cursor.getString(cursor.getColumnIndex(FORECAST_3_HUMIDITY));
            weatherInfo.dailyForecast.get(2).pop = cursor.getString(cursor.getColumnIndex(FORECAST_3_RAINY_PRO));
            weatherInfo.dailyForecast.get(2).tmp.max = cursor.getString(cursor.getColumnIndex(FORECAST_3_MAX_TEMP));
            weatherInfo.dailyForecast.get(2).tmp.min = cursor.getString(cursor.getColumnIndex(FORECAST_3_MIN_TEMP));
            weatherInfo.dailyForecast.get(2).cond.codeD = cursor.getString(cursor.getColumnIndex(FORECAST_3_IMAGE_CODE));
            weatherInfo.dailyForecast.get(2).cond.txtD = cursor.getString(cursor.getColumnIndex(FORECAST_3_CONDITION));
    /*--------------------------------------- day 4 --------------------------------------------------*/
            weatherInfo.dailyForecast.get(3).date = cursor.getString(cursor.getColumnIndex(FORECAST_4_DATE));
            weatherInfo.dailyForecast.get(3).hum = cursor.getString(cursor.getColumnIndex(FORECAST_4_HUMIDITY));
            weatherInfo.dailyForecast.get(3).pop = cursor.getString(cursor.getColumnIndex(FORECAST_4_RAINY_PRO));
            weatherInfo.dailyForecast.get(3).tmp.max = cursor.getString(cursor.getColumnIndex(FORECAST_4_MAX_TEMP));
            weatherInfo.dailyForecast.get(3).tmp.min = cursor.getString(cursor.getColumnIndex(FORECAST_4_MIN_TEMP));
            weatherInfo.dailyForecast.get(3).cond.codeD = cursor.getString(cursor.getColumnIndex(FORECAST_4_IMAGE_CODE));
            weatherInfo.dailyForecast.get(3).cond.txtD = cursor.getString(cursor.getColumnIndex(FORECAST_4_CONDITION));
    /*--------------------------------------- day 5 --------------------------------------------------*/
            weatherInfo.dailyForecast.get(4).date = cursor.getString(cursor.getColumnIndex(FORECAST_4_DATE));
            weatherInfo.dailyForecast.get(4).hum = cursor.getString(cursor.getColumnIndex(FORECAST_4_HUMIDITY));
            weatherInfo.dailyForecast.get(4).pop = cursor.getString(cursor.getColumnIndex(FORECAST_4_RAINY_PRO));
            weatherInfo.dailyForecast.get(4).tmp.max = cursor.getString(cursor.getColumnIndex(FORECAST_4_MAX_TEMP));
            weatherInfo.dailyForecast.get(4).tmp.min = cursor.getString(cursor.getColumnIndex(FORECAST_4_MIN_TEMP));
            weatherInfo.dailyForecast.get(4).cond.codeD = cursor.getString(cursor.getColumnIndex(FORECAST_4_IMAGE_CODE));
            weatherInfo.dailyForecast.get(4).cond.txtD = cursor.getString(cursor.getColumnIndex(FORECAST_4_CONDITION));
    /*--------------------------------------- day 6 --------------------------------------------------*/
            weatherInfo.dailyForecast.get(5).date = cursor.getString(cursor.getColumnIndex(FORECAST_4_DATE));
            weatherInfo.dailyForecast.get(5).hum = cursor.getString(cursor.getColumnIndex(FORECAST_4_HUMIDITY));
            weatherInfo.dailyForecast.get(5).pop = cursor.getString(cursor.getColumnIndex(FORECAST_4_RAINY_PRO));
            weatherInfo.dailyForecast.get(5).tmp.max = cursor.getString(cursor.getColumnIndex(FORECAST_4_MAX_TEMP));
            weatherInfo.dailyForecast.get(5).tmp.min = cursor.getString(cursor.getColumnIndex(FORECAST_4_MIN_TEMP));
            weatherInfo.dailyForecast.get(5).cond.codeD = cursor.getString(cursor.getColumnIndex(FORECAST_4_IMAGE_CODE));
            weatherInfo.dailyForecast.get(5).cond.txtD = cursor.getString(cursor.getColumnIndex(FORECAST_4_CONDITION));
    /*--------------------------------------- day 7 --------------------------------------------------*/
            weatherInfo.dailyForecast.get(6).date = cursor.getString(cursor.getColumnIndex(FORECAST_4_DATE));
            weatherInfo.dailyForecast.get(6).hum = cursor.getString(cursor.getColumnIndex(FORECAST_4_HUMIDITY));
            weatherInfo.dailyForecast.get(6).pop = cursor.getString(cursor.getColumnIndex(FORECAST_4_RAINY_PRO));
            weatherInfo.dailyForecast.get(6).tmp.max = cursor.getString(cursor.getColumnIndex(FORECAST_4_MAX_TEMP));
            weatherInfo.dailyForecast.get(6).tmp.min = cursor.getString(cursor.getColumnIndex(FORECAST_4_MIN_TEMP));
            weatherInfo.dailyForecast.get(6).cond.codeD = cursor.getString(cursor.getColumnIndex(FORECAST_4_IMAGE_CODE));
            weatherInfo.dailyForecast.get(6).cond.txtD = cursor.getString(cursor.getColumnIndex(FORECAST_4_CONDITION));
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
