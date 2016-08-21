package com.example.arthur.pureweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.arthur.pureweather.modle.BriefWeather;
import com.example.arthur.pureweather.modle.Region;
import com.example.arthur.pureweather.modle.Weather;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/9.
 */
public class PureWeatherDB {

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
    final private static String COUNTRY = "country";
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

    final private static String HOURLY_FORECAST_SIZE = "hourly_forecast_size";
    final private static String[] HOURLY_FORECAST_TIME = {
            "hourly_forecast_1_time","hourly_forecast_2_time","hourly_forecast_3_time",
            "hourly_forecast_4_time","hourly_forecast_5_time","hourly_forecast_6_time",
            "hourly_forecast_7_time","hourly_forecast_8_time"};

    final private static String[] HOURLY_FORECAST_TEMP = {
            "hourly_forecast_1_temp","hourly_forecast_2_temp","hourly_forecast_3_temp",
            "hourly_forecast_4_temp","hourly_forecast_5_temp","hourly_forecast_6_temp",
            "hourly_forecast_7_temp","hourly_forecast_8_temp"};

    final private static String[] HOURLY_FORECAST_HUMIDITY = {
            "hourly_forecast_1_humidity","hourly_forecast_2_humidity","hourly_forecast_3_humidity",
            "hourly_forecast_4_humidity","hourly_forecast_5_humidity","hourly_forecast_6_humidity",
            "hourly_forecast_7_humidity","hourly_forecast_8_humidity"};

    final private static String[] HOURLY_FORECAST_RAINY_PRO = {
            "hourly_forecast_1_rainy_pro","hourly_forecast_2_rainy_pro","hourly_forecast_3_rainy_pro",
            "hourly_forecast_4_rainy_pro","hourly_forecast_5_rainy_pro","hourly_forecast_6_rainy_pro",
            "hourly_forecast_7_rainy_pro","hourly_forecast_8_rainy_pro"};

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

    public void closeDB (){
        db.close();
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

    public boolean saveWeather(Weather weather) {
        ContentValues value = new ContentValues();
        if (weather != null){
            //basic
            value.put(CITY_NAME, weather.basic.city);
            value.put(CITY_ID, weather.basic.id);
            value.put(COUNTRY, weather.basic.cnty);
            value.put(UPDATE_TIME, weather.basic.update.loc);
//            //aqi
//            value.put("aqi_value", weather.getAqi().getCity().getAqi());
//            value.put("pm25_value", weather.getAqi().getCity().getPm25());
            //now
            value.put(NOW_CONDITION, weather.now.cond.txt);
            value.put(NOW_TEMPERATURE, weather.now.tmp);

            //处理背景图片
            value.put(IMAGE_CODE, weather.now.cond.code);

            value.put(HUMIDITY, weather.now.hum);
/*----------------------------------------- daily forecast -------------------------------------------*/
            value.put(RAINY_PROBABILITY, weather.dailyForecast.get(0).pop);
            value.put(MAX_TEMPERATURE, weather.dailyForecast.get(0).tmp.max);
            value.put(MIN_TEMPERATURE, weather.dailyForecast.get(0).tmp.min);

            int size = weather.hourlyForecast.size();
            if (size > 8){
                size = 8;
            }
            value.put(HOURLY_FORECAST_SIZE,size);
            if (size >0){
                for (int i=0; i<size;i++){
                    value.put(HOURLY_FORECAST_TIME[i],weather.hourlyForecast.get(i).date.substring(11));
                    value.put(HOURLY_FORECAST_TEMP[i],weather.hourlyForecast.get(i).tmp);
                    value.put(HOURLY_FORECAST_HUMIDITY[i],weather.hourlyForecast.get(i).hum);
                    value.put(HOURLY_FORECAST_RAINY_PRO[i],weather.hourlyForecast.get(i).pop);
                }
            }else {
                for (int i=0; i<8;i++){
                    value.put(HOURLY_FORECAST_TIME[i],"");
                    value.put(HOURLY_FORECAST_TEMP[i],"");
                    value.put(HOURLY_FORECAST_HUMIDITY[i],"");
                    value.put(HOURLY_FORECAST_RAINY_PRO[i],"");
                }
            }

            for (int i=0; i<7; i++){
                value.put(FORECAST_DATE[i], weather.dailyForecast.get(i).date);
                value.put(FORECAST_CONDITION[i], weather.dailyForecast.get(i).cond.txtD);
                value.put(FORECAST_IMAGE_CODE[i], weather.dailyForecast.get(i).cond.codeD);
                value.put(FORECAST_HUMIDITY[i], weather.dailyForecast.get(i).hum);
                value.put(FORECAST_MAX_TEMP[i], weather.dailyForecast.get(i).tmp.max);
                value.put(FORECAST_MIN_TEMP[i], weather.dailyForecast.get(i).tmp.min);
                value.put(FORECAST_RAINY_PRO[i], weather.dailyForecast.get(i).pop);
            }
            //status
            value.put(STATUS, weather.status);
            //suggestion
            StringBuilder sb = new StringBuilder();
            value.put(SPORT_SUGGESTION, weather.suggestion.sport.txt);
            value.put(SPORT_SUGGESTION_BRIEF, sb.append("运动：").append(weather.suggestion.sport.brf).toString());
            sb.delete(0, sb.length());
            value.put(TRAVEL_SUGGESTION, weather.suggestion.trav.txt);
            value.put(TRAVEL_SUGGESTION_BRIEF, sb.append("旅游：").append(weather.suggestion.trav.brf).toString());
            sb.delete(0,sb.length());
            value.put(CAR_WASH_SUGGESTION, weather.suggestion.cw.txt);
            value.put(CAR_WASH_SUGGESTION_BRIEF, sb.append("洗车：").append(weather.suggestion.cw.brf).toString());

            db.replace(TABLE_WEATHER, null, value);
            return true;
        }
        return false;
    }

    /*
     * 加载简要的天气信息
     */
    public List<BriefWeather> loadBriefWeatherInfo(){
        List<BriefWeather> infoList = null;
        Cursor cursor ;

        cursor = db.query(TABLE_WEATHER, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            infoList = new ArrayList<>();
            do{
                BriefWeather info = new BriefWeather();

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
    public Weather loadWeatherInfo(String cityName){
        Cursor cursor ;
        Weather weather = new Weather();

        cursor = db.query(TABLE_WEATHER, null, CITY_NAME + " = ?", new String[]{cityName}, null, null, null);
        if (cursor.moveToNext()){
            weather.status = cursor.getString(cursor.getColumnIndex(STATUS));
            weather.now.cond.code = cursor.getString(cursor.getColumnIndex(IMAGE_CODE));
            weather.now.cond.txt = cursor.getString(cursor.getColumnIndex(NOW_CONDITION));
            weather.now.hum = cursor.getString(cursor.getColumnIndex(HUMIDITY));
            weather.now.tmp = cursor.getString(cursor.getColumnIndex(NOW_TEMPERATURE));
            weather.basic.city = cursor.getString(cursor.getColumnIndex(CITY_NAME));
            weather.basic.id = cursor.getString(cursor.getColumnIndex(CITY_ID));
            weather.basic.cnty = cursor.getString(cursor.getColumnIndex(COUNTRY));
            weather.basic.update.loc = cursor.getString(cursor.getColumnIndex(UPDATE_TIME));
            weather.suggestion.sport.txt = cursor.getString(cursor.getColumnIndex(SPORT_SUGGESTION));
            weather.suggestion.sport.brf = cursor.getString(cursor.getColumnIndex(SPORT_SUGGESTION_BRIEF));
            weather.suggestion.trav.txt = cursor.getString(cursor.getColumnIndex(TRAVEL_SUGGESTION));
            weather.suggestion.trav.brf = cursor.getString(cursor.getColumnIndex(TRAVEL_SUGGESTION_BRIEF));
            weather.suggestion.cw.txt = cursor.getString(cursor.getColumnIndex(CAR_WASH_SUGGESTION));
            weather.suggestion.cw.brf = cursor.getString(cursor.getColumnIndex(CAR_WASH_SUGGESTION_BRIEF));

            int size = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HOURLY_FORECAST_SIZE)));
            for (int i=0;i<size;i++){
                Weather.HourlyForecastInfo hourlyForecastInfo = new Weather.HourlyForecastInfo();
                hourlyForecastInfo.date = cursor.getString(cursor.getColumnIndex(HOURLY_FORECAST_TIME[i]));
                hourlyForecastInfo.tmp = cursor.getString(cursor.getColumnIndex(HOURLY_FORECAST_TEMP[i]));
                hourlyForecastInfo.hum = cursor.getString(cursor.getColumnIndex(HOURLY_FORECAST_HUMIDITY[i]));
                hourlyForecastInfo.pop = cursor.getString(cursor.getColumnIndex(HOURLY_FORECAST_RAINY_PRO[i]));
                weather.hourlyForecast.add(hourlyForecastInfo);
            }

            for (int i=0; i<7; i++){
                weather.dailyForecast.get(i).date = cursor.getString(cursor.getColumnIndex(FORECAST_DATE[i]));
                weather.dailyForecast.get(i).hum = cursor.getString(cursor.getColumnIndex(FORECAST_HUMIDITY[i]));
                weather.dailyForecast.get(i).pop = cursor.getString(cursor.getColumnIndex(FORECAST_RAINY_PRO[i]));
                weather.dailyForecast.get(i).tmp.max = cursor.getString(cursor.getColumnIndex(FORECAST_MAX_TEMP[i]));
                weather.dailyForecast.get(i).tmp.min = cursor.getString(cursor.getColumnIndex(FORECAST_MIN_TEMP[i]));
                weather.dailyForecast.get(i).cond.codeD = cursor.getString(cursor.getColumnIndex(FORECAST_IMAGE_CODE[i]));
                weather.dailyForecast.get(i).cond.txtD = cursor.getString(cursor.getColumnIndex(FORECAST_CONDITION[i]));
            }
        }
        if (cursor != null){
            cursor.close();
        }
        return weather;
    }

    public int deleteWeatherInfo(String cityName){
        return db.delete(TABLE_WEATHER, CITY_NAME + " = ?", new String[]{cityName});
    }

}
