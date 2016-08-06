package com.example.arthur.pureweather.modle;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/2.
 */
public class WeatherResponse implements Serializable{
    @SerializedName("HeWeather data service 3.0")
    private List<Weather> weathers = new ArrayList<>();

    public List<Weather> getWeathers() {
        return weathers;
    }
}
