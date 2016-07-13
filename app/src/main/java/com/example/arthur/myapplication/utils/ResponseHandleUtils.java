package com.example.arthur.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.arthur.myapplication.modle.City;
import com.example.arthur.myapplication.modle.County;
import com.example.arthur.myapplication.modle.Province;
import com.example.arthur.myapplication.modle.PureWeatherDB;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/6/9.
 */
public class ResponseHandleUtils {
    /*
     *
     */
	/*
	 * 用来解析和处理服务器返回的省级数据，并保存至数据库中
	 */
    public synchronized static boolean handleProvincesResponse(
            PureWeatherDB pureWeatherDB, String response){
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if(allProvinces != null && allProvinces.length > 0){
                for(String p : allProvinces){
                    Province province = new Province();
                    String[] array = p.split("\\|");
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    pureWeatherDB.saveProvince(province);
                }//end for
                return true;
            }//end if(allProvince != null && allProvince.length>0)
        }//end if(!TextUtils.isEmpty(response))
        return false;
    }
    /*
     * 用来解析和处理服务器返回的城市数据，并保存至数据库中
     */
    public static boolean handleCitiesResponse(
            PureWeatherDB pureWeatherDB, String response, int provinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if(allCities != null && allCities.length > 0){
                for(String c : allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);

                    pureWeatherDB.saveCity(city);
                }//end for
                return true;
            }//end if(allCities != null && allCities.length>0)
        }//end if(!TextUtils.isEmpty(response))
        return false;
    }
    /*
     * 用来解析和处理服务器返回的县级数据，并保存至数据库中
     */
    public static boolean handleCountiesResponse(
            PureWeatherDB pureWeatherDB, String response, int cityId){
        if(!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if(allCounties != null && allCounties.length > 0){
                for(String c : allCounties){
                    County county = new County();
                    String[] array = c.split("\\|");
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    pureWeatherDB.saveCounty(county);
                }//end for
                return true;
            }
        }//end if(!TextUtils.isEmpty(response))
        return false;
    }
    /*
     * 解析和处理服务器返回的JSON格式的天气数据，并保存到文件中
     * 这里只取出城市名称，保存至 pref
     */
    public static boolean handleWeatherResponse(Context context, String response){

        boolean isLegal = false;
        SharedPreferences.Editor editor = PreferenceManager.
                getDefaultSharedPreferences(context).edit();
        //首先处理返回状态  status 字段
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray HeWeatherInfo = jsonObject.getJSONArray("HeWeather data service 3.0");
            String status = ((JSONObject)HeWeatherInfo.get(0)).getString("status");
            isLegal = status.equals("ok");

            //editor.putString("status", status);
            //seditor.commit();
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }

        if(isLegal){
            try{
                JSONObject jsonObject = new JSONObject(response);
                JSONArray HeWeatherInfo = jsonObject.getJSONArray("HeWeather data service 3.0");

                // 处理basic字段
                JSONObject basicInfo = ((JSONObject) HeWeatherInfo.get(0)).getJSONObject("basic");
                String cityName = basicInfo.getString("city");

                editor.putString("city_name", cityName);

                editor.commit();
                return true;
            }
            catch(Exception e){
                //Toast.makeText(MyApplication.getContext(), "数据解析出错！", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

}
