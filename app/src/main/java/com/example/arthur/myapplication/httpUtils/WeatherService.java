package com.example.arthur.myapplication.httpUtils;
import com.example.arthur.myapplication.modle.WeatherResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2016/7/3.
 */
public interface WeatherService {
   /*
   通过和风天气网获取天气信息的服务接口
    */
   @GET("weather")
   Observable<WeatherResponse> getWeatherData(@Query("city")String cityName, @Query("key")String KEY);
}
