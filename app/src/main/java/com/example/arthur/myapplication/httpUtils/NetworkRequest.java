package com.example.arthur.myapplication.httpUtils;

import com.example.arthur.myapplication.constant.MyUrl;
import com.example.arthur.myapplication.modle.Region;
import com.example.arthur.myapplication.modle.WeatherResponse;

import java.util.List;

import rx.Observable;

/**
 * Created by Administrator on 2016/7/2.
 */
public class NetworkRequest {

    public static Observable<WeatherResponse> getWeatherWithName(final String cityName){
        return ServiceFactory
                .getWeatherService()
                .getWeatherData(cityName, MyUrl.HE_WEATHER_KEY);
    }

    public static Observable<List<Region>> getRegionWithCode(final String superCode){
        return ServiceFactory
                .getRegionService(superCode)
                .getRegion(superCode);
    }

}
