package com.example.arthur.pureweather.httpUtils;

import com.example.arthur.pureweather.constant.MyString;
import com.example.arthur.pureweather.modle.Region;
import com.example.arthur.pureweather.modle.VersionInfo;
import com.example.arthur.pureweather.modle.WeatherResponse;

import java.util.List;

import rx.Observable;

/**
 * Created by Administrator on 2016/7/2.
 */
public class NetworkRequest {

    public static Observable<WeatherResponse> getWeatherWithName(final String cityName){
        return ServiceFactory
                .getWeatherService()
                .getWeatherData(cityName, MyString.HE_WEATHER_KEY);
    }

    public static Observable<List<Region>> getRegionWithCode(final String superCode){
        return ServiceFactory
                .getRegionService(superCode)
                .getRegion(superCode);
    }

    public static Observable<VersionInfo> getNewVersion(){
        return ServiceFactory
                .getVersionService()
                .getVersionInfo(MyString.API_TOKEN);
    }

}
