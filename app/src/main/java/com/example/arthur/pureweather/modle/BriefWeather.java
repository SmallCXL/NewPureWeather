package com.example.arthur.pureweather.modle;

public class BriefWeather {
    private String nowTemp;
    private String cityName;
    private String condText;
    private String tempRange;
    private String updateTime;
    private String imageCode;

    public String getNowTemp() {
        return nowTemp;
    }

    public void setNowTemp(String imageCode) {
        this.nowTemp = imageCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCondText() {
        return condText;
    }

    public void setCondText(String condText) {
        this.condText = condText;
    }

    public String getTempRange() {
        return tempRange;
    }

    public void setTempRange(String tempRange) {
        this.tempRange = tempRange;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getImageCode() {
        return imageCode;
    }

    public void setImageCode(String imageCode) {
        this.imageCode = imageCode;
    }
}
