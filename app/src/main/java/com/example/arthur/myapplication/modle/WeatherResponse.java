package com.example.arthur.myapplication.modle;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/2.
 */
public class WeatherResponse implements Serializable{
    @SerializedName("HeWeather data service 3.0")
    private List<WeatherInfo> weatherInfos = new ArrayList<>();

    public List<WeatherInfo> getWeatherInfos() {
        return weatherInfos;
    }
}
