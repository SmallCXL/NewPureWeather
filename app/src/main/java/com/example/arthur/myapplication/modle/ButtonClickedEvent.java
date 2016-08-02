package com.example.arthur.myapplication.modle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/7/24.
 */
public class ButtonClickedEvent {
    private int color;
    private String dataType;
    private WeatherInfo weatherInfo;
    private ArrayList<Integer> dataList;
    private int dataSize;
    private String[] xLabel;

    public ButtonClickedEvent(WeatherInfo weatherInfo, String dataType, int color) {
        this.weatherInfo = weatherInfo;
        this.dataType = dataType;
        this.color = color;
        initData();
    }

    public void initData() {
        dataSize = weatherInfo.dailyForecast.size();
        dataList = new ArrayList<>();
        switch (dataType) {
            case "MAX_TEMP":
                for (int i=0; i<dataSize; i++)
                    dataList.add(Integer.parseInt(weatherInfo.dailyForecast.get(i).tmp.max));
                break;
            case "MIN_TEMP":
                for (int i=0; i<dataSize; i++)
                    dataList.add(Integer.parseInt(weatherInfo.dailyForecast.get(i).tmp.min));
                break;
            case "HUMIDITY":
                for (int i=0; i<dataSize; i++)
                    dataList.add(Integer.parseInt(weatherInfo.dailyForecast.get(i).hum));
                break;
            case "RAINY_PRO":
                for (int i=0; i<dataSize; i++)
                    dataList.add(Integer.parseInt(weatherInfo.dailyForecast.get(i).pop));
                break;
        }
        xLabel = new String[dataSize];
        for (int i=0; i<dataSize; i++){
            xLabel[i] = weatherInfo.dailyForecast.get(i).date.replace("-",".").substring(8) + "号";
        }

    }

    public int getColor() {
        return color;
    }

    public int getDataSize() {
        return dataSize;
    }
    public int getMax(){
        return Collections.max(dataList);
    }
    public int getMin(){
        return Collections.min(dataList);
    }
    public ArrayList<Integer> getData(){
        return dataList;
    }
    public String[] getxLabel(){
        return xLabel;
    }
    public String getDataType(){
        return dataType;
    }
}
