package com.example.arthur.pureweather.httpUtils;

import com.example.arthur.pureweather.constant.Constants;
import com.example.arthur.pureweather.modle.Region;
import com.example.arthur.pureweather.modle.VersionInfo;
import com.example.arthur.pureweather.modle.WeatherResponse;

import java.util.List;

import rx.Observable;

/**
 * NetworkRequest：
 * 1、提供三个请求接口，分别对应天气数据请求、城市列表请求、版本信息请求
 */
public class NetworkRequest {

    public static Observable<WeatherResponse> getWeatherWithName(final String cityName){
        return ServiceFactory
                .getWeatherService()
                .getWeatherData(cityName, Constants.HE_WEATHER_KEY);
    }

    public static Observable<List<Region>> getRegionWithCode(final String superCode){
        return ServiceFactory
                .getRegionService()
                .getRegion(superCode);
    }

    public static Observable<VersionInfo> getNewVersion(){
        return ServiceFactory
                .getVersionService()
                .getVersionInfo(Constants.API_TOKEN);
    }

}
