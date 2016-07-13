package com.example.arthur.myapplication.modle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.arthur.myapplication.db.PureWeatherOpenHelper;

import org.json.JSONArray;
import org.json.JSONObject;

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

    /*
     * 从数据库中读取全国所有的省份信息
     */
    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));//取名称为"id"这一列的数据
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            }while(cursor.moveToNext());
        }
        if(cursor != null){
            cursor.close();
        }
        return list;
    }
    /*
     * 将Province实例存储到数据库
     */
    public void saveProvince(Province province){
        if (province!=null){
            ContentValues value = new ContentValues();
            value.put("province_name", province.getProvinceName());
            value.put("province_code", province.getProvinceCode());
            db.insert("Province", null, value);
        }
    }
    /*
     * 从数据库中读取对应省份的城市信息
     */
    public List<City> loadCities(int provinceId){
        List<City> list = new ArrayList<City>();
        Cursor cursor =
                db.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                City city = new City();
                city.setProvinceId(provinceId);
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                list.add(city);
            }while(cursor.moveToNext());
        }
        if(cursor!=null){
            cursor.close();
        }
        return list;
    }
    /*
     * 将City实例存储到数据库中
     */
    public void saveCity(City city){
        if(city != null){
            ContentValues value = new ContentValues();
            value.put("city_name", city.getCityName());
            value.put("city_code", city.getCityCode());
            value.put("province_id", city.getProvinceId());
            db.insert("City", null, value);
        }
    }
    /*
     * 从数据库中读取对应城市的县信息
     */
    public List<County> loadCounties(int cityId){
        List<County> list = new ArrayList<County>();
        Cursor cursor =
                db.query("County", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                County county = new County();
                county.setCityId(cityId);
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                list.add(county);
            }while(cursor.moveToNext());
        }
        if(cursor != null){
            cursor.close();
        }
        return list;
    }
    /*
     * 将County实例存储到数据库中
     */
    public void saveCounty(County county){
        if(county != null){
            ContentValues value = new ContentValues();
            value.put("city_id", county.getCityId());
            value.put("county_name",county.getCountyName());
            value.put("county_code", county.getCountyCode());
            db.insert("County", null, value);
        }
    }

    /*
     * 将 SharedPreferenceWeather 的天气信息存入数据库
     */
    public boolean saveWeather(String response){

        ContentValues value = new ContentValues();

        boolean isLegal = false;

        //首先处理返回状态  status 字段
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray HeWeatherInfo = jsonObject.getJSONArray("HeWeather data service 3.0");
            String status = ((JSONObject)HeWeatherInfo.get(0)).getString("status");
            isLegal = status.equals("ok");

        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }

        if(isLegal){
            try{
                JSONObject jsonObject = new JSONObject(response);
                JSONArray HeWeatherInfo = jsonObject.getJSONArray("HeWeather data service 3.0");
                //处理aqi字段，因为部分城市没有这个数值，直接解析会导致出错，故需要分开解析
                String aqiValue;
                String pm25Value;
                try{

                    JSONObject aqiInfo = ((JSONObject) HeWeatherInfo.get(0)).getJSONObject("aqi");
                    JSONObject cityAqiInfo = aqiInfo.getJSONObject("city");
                    aqiValue = cityAqiInfo.getString("aqi");
                    pm25Value = cityAqiInfo.getString("pm25");
                }
                catch(Exception e){
                    e.printStackTrace();
                    aqiValue = "无信息";
                    pm25Value = "无信息";
                }

                // 处理basic字段
                JSONObject basicInfo = ((JSONObject) HeWeatherInfo.get(0)).getJSONObject("basic");
                String cityName = basicInfo.getString("city");
                String cityId = basicInfo.getString("id");
                JSONObject updateInfo = (JSONObject)basicInfo.getJSONObject("update");
                String updateTime = updateInfo.getString("loc");


                //处理now字段
                JSONObject nowInfo = ((JSONObject) HeWeatherInfo.get(0)).getJSONObject("now");
                JSONObject nowCondInfo = nowInfo.getJSONObject("cond");
                String nowCond = nowCondInfo.getString("txt");
                String imageCode = nowCondInfo.getString("code");
                String nowTemp =  nowInfo.getString("tmp");
                String humiValue = nowInfo.getString("hum");

                //处理daily_forecast字段
                JSONArray dailyForecastInfo = ((JSONObject)HeWeatherInfo.get(0)).getJSONArray("daily_forecast");
                JSONObject todayInfo = (JSONObject)dailyForecastInfo.get(0);
                JSONObject astroInfo = todayInfo.getJSONObject("astro");
                String sunsetTime = astroInfo.getString("ss");
                String sunriseTime = astroInfo.getString("sr");
                String forecastDate = todayInfo.getString("date");
                String rainyPos = todayInfo.getString("pop");
                JSONObject tempInfo = todayInfo.getJSONObject("tmp");
                String maxTemp = tempInfo.getString("max");
                String minTemp = tempInfo.getString("min");
                //处理status
                String status = ((JSONObject)HeWeatherInfo.get(0)).getString("status");
                //处理suggestion字段
                JSONObject suggestionInfo = ((JSONObject)HeWeatherInfo.get(0)).getJSONObject("suggestion");
                JSONObject sportInfo = suggestionInfo.getJSONObject("sport");
                String sportSuggestion = sportInfo.getString("txt");
                String sportSuggestionBrf = sportInfo.getString("brf");

                JSONObject travelInfo = suggestionInfo.getJSONObject("trav");
                String travelSuggestion = travelInfo.getString("txt");
                String travelSuggestionBrf = travelInfo.getString("brf");

                JSONObject carWashInfo = suggestionInfo.getJSONObject("cw");
                String carWashSuggestion = carWashInfo.getString("txt");
                String carWashSuggestionBrf = carWashInfo.getString("brf");
                //SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
                //
                value.put("city_name", cityName);
                value.put("city_id", cityId);
                value.put("update_time", updateTime);
                value.put("aqi_value", aqiValue);
                value.put("pm25_value", pm25Value);
                value.put("now_cond", nowCond);
                value.put("now_temp", nowTemp);
                value.put("sunset_time", sunsetTime);
                value.put("sunrise_time", sunriseTime);
                value.put("forecast_date", forecastDate);
                value.put("rainy_pos", rainyPos);
                value.put("max_temp", maxTemp);
                value.put("min_temp", minTemp);
                value.put("status", status);
                value.put("image_code", imageCode);
                value.put("sport_suggestion", sportSuggestion);
                value.put("sport_suggestion_brf", sportSuggestionBrf);
                value.put("travel_suggestion", travelSuggestion);
                value.put("travel_suggestion_brf", travelSuggestionBrf);
                value.put("car_wash_suggestion", carWashSuggestion);
                value.put("car_wash_suggestion_brf", carWashSuggestionBrf);
                value.put("humi_value", humiValue);

                db.replace("Weather", null, value);

                return true;
            }
            catch(Exception e){
                Toast.makeText(mContext, "数据解析出错！", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean saveWeather(WeatherInfo weatherInfo) {
        ContentValues value = new ContentValues();
        if (weatherInfo != null){
            //basic
            value.put("city_name", weatherInfo.getBasic().getCity());
            value.put("city_id", weatherInfo.getBasic().getId());
            value.put("update_time", weatherInfo.getBasic().getUpdate().getLoc());
//            //aqi
//            value.put("aqi_value", weatherInfo.getAqi().getCity().getAqi());
//            value.put("pm25_value", weatherInfo.getAqi().getCity().getPm25());
            //now
            value.put("now_cond", weatherInfo.getNow().getCond().getTxt());
            value.put("now_temp", weatherInfo.getNow().getTmp());
            value.put("image_code", weatherInfo.getNow().getCond().getCode());
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
     * 从数据库中读取已保存的天气信息
     */
    public List<Weather> loadWeatherInfo(){
        List<Weather> infoList = null;
        Cursor cursor ;
        cursor = db.query("Weather", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            infoList = new ArrayList<>();
            do{
                Weather info = new Weather();

                info.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));

                StringBuilder condText = new StringBuilder().append(cursor.getString(cursor.getColumnIndex("now_cond")));
                info.setCondText(condText.toString());

                StringBuilder tempRange = new StringBuilder().append(cursor.getString(cursor.getColumnIndex("max_temp")))
                        .append("°C ~ ").append(cursor.getString(cursor.getColumnIndex("min_temp"))).append("°C");
                info.setTempRange(tempRange.toString());

                StringBuilder nowTemp = new StringBuilder().append(cursor.getString(cursor.getColumnIndex("now_temp"))).append("°");

                info.setNowTemp(nowTemp.toString());
//
//                info.setImageCode(cursor.getString(cursor.getColumnIndex("image_code")));

                infoList.add(info);
            }while(cursor.moveToNext());
        }
        if(cursor != null){
            cursor.close();
        }

        return infoList;
    }

    public Cursor loadWeatherInfo(String cityName){
        Cursor cursor = null;
        cursor = db.query("Weather", null, "city_name = ?", new String[]{cityName}, null, null, null);
        return cursor;

    }

    public int deleteWeatherInfo(String cityName){

        return db.delete("Weather", "city_name = ?", new String[]{cityName});

    }

}
