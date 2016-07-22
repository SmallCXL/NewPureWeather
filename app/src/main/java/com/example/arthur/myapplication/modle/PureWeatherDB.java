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
    /*
     * 数据库版本号
     */
    public static final int VERSION = 1;
    /*
     * 私有变量
     */
    volatile private static PureWeatherDB pureWeatherDB;
    private SQLiteDatabase db;

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
                value.put("region_name", region.getName());
                value.put("region_code", region.getCode());
                value.put("super_region_code", region.getSuperCode());
                db.insert("Region", null, value);
            }
        }
    }

    public List<Region> loadRegions(String superCode){
        List<Region> regions = new ArrayList<>();
        Cursor cursor = db.query("Region",null,"super_region_code = ?",new String[]{superCode},null,null,null);
        if (cursor.moveToFirst()){
            do {
                Region region = new Region();
                region.setName(cursor.getString(cursor.getColumnIndex("region_name")));
                region.setCode(cursor.getString(cursor.getColumnIndex("region_code")));
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
            value.put("city_name", weatherInfo.getBasic().getCity());
            value.put("city_id", weatherInfo.getBasic().getId());

            StringBuilder updateTime = new StringBuilder().append(weatherInfo.getBasic().getUpdate().getLoc()).append("更新");
            value.put("update_time", updateTime.toString().substring(5));
//            //aqi
//            value.put("aqi_value", weatherInfo.getAqi().getCity().getAqi());
//            value.put("pm25_value", weatherInfo.getAqi().getCity().getPm25());
            //now
            value.put("now_cond", weatherInfo.getNow().getCond().getTxt());
            value.put("now_temp", weatherInfo.getNow().getTmp());

            //处理背景图片
            String imageCode = weatherInfo.getNow().getCond().getCode();
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
            value.put("image_code", imageName);


            value.put("humi_value", weatherInfo.getNow().getHum());
            //forecast
            value.put("sunset_time", weatherInfo.getDailyForecast().get(0).getAstro().getSs());
            value.put("sunrise_time", weatherInfo.getDailyForecast().get(0).getAstro().getSr());
            value.put("forecast_date", weatherInfo.getDailyForecast().get(0).getDate());
            value.put("rainy_pos", weatherInfo.getDailyForecast().get(0).getPop());
            value.put("max_temp", weatherInfo.getDailyForecast().get(0).getTmp().getMax());
            value.put("min_temp", weatherInfo.getDailyForecast().get(0).getTmp().getMin());
            //status
            value.put("status", weatherInfo.getStatus());
            //suggestion
            value.put("sport_suggestion", weatherInfo.getSuggestion().getSport().getTxt());
            value.put("sport_suggestion_brf", weatherInfo.getSuggestion().getSport().getBrf());
            value.put("travel_suggestion", weatherInfo.getSuggestion().getTrav().getTxt());
            value.put("travel_suggestion_brf", weatherInfo.getSuggestion().getTrav().getBrf());
            value.put("car_wash_suggestion", weatherInfo.getSuggestion().getCw().getTxt());
            value.put("car_wash_suggestion_brf", weatherInfo.getSuggestion().getCw().getBrf());

            db.replace("Weather", null, value);
            return true;
        }
        return false;
    }


    /*
     * 加载简要的天气信息
     */
    public List<BriefWeatherInfo> loadWeatherInfo(){
        List<BriefWeatherInfo> infoList = null;
        Cursor cursor ;

        cursor = db.query("Weather", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            infoList = new ArrayList<>();
            do{
                BriefWeatherInfo info = new BriefWeatherInfo();

                info.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));

                StringBuilder condText = new StringBuilder().append(cursor.getString(cursor.getColumnIndex("now_cond")));
                info.setCondText(condText.toString());

                StringBuilder tempRange = new StringBuilder().append(cursor.getString(cursor.getColumnIndex("max_temp")))
                        .append("°C ~ ").append(cursor.getString(cursor.getColumnIndex("min_temp"))).append("°C");
                info.setTempRange(tempRange.toString());

                StringBuilder nowTemp = new StringBuilder().append(cursor.getString(cursor.getColumnIndex("now_temp"))).append("°");
                info.setNowTemp(nowTemp.toString());

                //StringBuilder updateTime = new StringBuilder().append(cursor.getString(cursor.getColumnIndex("update_time"))).append("更新");
                info.setUpdateTime(cursor.getString(cursor.getColumnIndex("update_time")));

                String imageCode = cursor.getString(cursor.getColumnIndex("image_code"));
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
        cursor = db.query("Weather", null, "city_name = ?", new String[]{cityName}, null, null, null);
        return cursor;
    }

    public int deleteWeatherInfo(String cityName){

        return db.delete("Weather", "city_name = ?", new String[]{cityName});

    }

}
