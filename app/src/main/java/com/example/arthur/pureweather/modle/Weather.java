package com.example.arthur.pureweather.modle;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/2.
 */
public class Weather implements Serializable {

    public Weather() {
        this.basic = new BasicInfo();
        this.now = new Weather.NowInfo();
        this.now.cond = new Weather.NowInfo.CondInfo();
        this.basic = new Weather.BasicInfo();
        this.basic.update = new Weather.BasicInfo.UpdateInfo();
        this.suggestion = new SuggestionInfo();
        this.suggestion.sport = new SuggestionInfo.SportInfo();
        this.suggestion.trav = new SuggestionInfo.TravInfo();
        this.suggestion.cw = new SuggestionInfo.CwInfo();
        this.dailyForecast = new ArrayList<>();
        for(int i= 0; i<7; i++){
            DailyForecastInfo dailyForecastInfo = new DailyForecastInfo();
            dailyForecastInfo.tmp = new DailyForecastInfo.TmpInfo();
            dailyForecastInfo.cond = new DailyForecastInfo.CondInfo();
            dailyForecast.add(dailyForecastInfo);
        }
        this.hourlyForecast = new ArrayList<>();
    }

    /*
        所有字段信息
        此处为了整体代码风格简洁，没用使用Bean的模式设置所有属性的get和set方法，直接操作该类的成员变量，存在不规范的地方。
         */
//    @SerializedName("aqi")             public AqiInfo aqi;
    @SerializedName("basic")
    public BasicInfo basic;
    @SerializedName("now")
    public NowInfo now;
    @SerializedName("status")
    public String status;
    @SerializedName("suggestion")
    public SuggestionInfo suggestion;
    @SerializedName("daily_forecast")
    public ArrayList<DailyForecastInfo> dailyForecast;
    @SerializedName("hourly_forecast")
    public ArrayList<HourlyForecastInfo> hourlyForecast;

    //    public AqiInfo getAqi() {
//        return aqi;
//    }
    /*
    基本信息
     */
    public static class BasicInfo implements Serializable {
        @SerializedName("city")
        public String city;
        @SerializedName("cnty")
        public String cnty;
        @SerializedName("id")
        public String id;
        @SerializedName("lat")
        public String lat;
        @SerializedName("lon")
        public String lon;
        @SerializedName("update")
        public UpdateInfo update;

        public static class UpdateInfo implements Serializable {
            @SerializedName("loc")
            public String loc;
            @SerializedName("utc")
            public String utc;
        }
    }

    /*
    空气质量信息
     */
//    public static class AqiInfo implements Serializable {
//
//        @SerializedName("city") public CityInfo city;
//
//        public static class CityInfo implements Serializable {
//            @SerializedName("aqi")  public String aqi;
//            @SerializedName("co")   public String co;
//            @SerializedName("no2")  public String no2;
//            @SerializedName("o3")   public String o3;
//            @SerializedName("pm10") public String pm10;
//            @SerializedName("pm25") public String pm25;
//            @SerializedName("qlty") public String qlty;
//            @SerializedName("so2")  public String so2;
//        }
//    }
/*
现在天气信息
 */
    public static class NowInfo implements Serializable {
        @SerializedName("cond")
        public CondInfo cond;
        @SerializedName("fl")
        public String fl;
        @SerializedName("hum")
        public String hum;
        @SerializedName("pcpn")
        public String pcpn;
        @SerializedName("pres")
        public String pres;
        @SerializedName("tmp")
        public String tmp;
        @SerializedName("vis")
        public String vis;
        @SerializedName("wind")
        public WindInfo wind;

        public static class CondInfo implements Serializable {
            @SerializedName("code")
            public String code;
            @SerializedName("txt")
            public String txt;
        }

        public static class WindInfo implements Serializable {
            @SerializedName("deg")
            public String deg;
            @SerializedName("dir")
            public String dir;
            @SerializedName("sc")
            public String sc;
            @SerializedName("spd")
            public String spd;
        }
    }

    /*
   每日预报信息
    */
    public static class DailyForecastInfo implements Serializable {
        @SerializedName("astro")
        public AstroInfo astro;
        @SerializedName("cond")
        public CondInfo cond;
        @SerializedName("date")
        public String date;
        @SerializedName("hum")
        public String hum;
        @SerializedName("pcpn")
        public String pcpn;
        @SerializedName("pop")
        public String pop;
        @SerializedName("pres")
        public String pres;
        @SerializedName("tmp")
        public TmpInfo tmp;
        @SerializedName("vis")
        public String vis;
        @SerializedName("wind")
        public WindInfo wind;

        public static class AstroInfo implements Serializable {
            @SerializedName("sr")
            public String sr;
            @SerializedName("ss")
            public String ss;
        }

        public static class CondInfo implements Serializable {
            @SerializedName("code_d")
            public String codeD;
            @SerializedName("code_n")
            public String codeN;
            @SerializedName("txt_d")
            public String txtD;
            @SerializedName("txt_n")
            public String txtN;
        }

        public static class TmpInfo implements Serializable {
            @SerializedName("max")
            public String max;
            @SerializedName("min")
            public String min;
        }

        public static class WindInfo implements Serializable {
            @SerializedName("deg")
            public String deg;
            @SerializedName("dir")
            public String dir;
            @SerializedName("sc")
            public String sc;
            @SerializedName("spd")
            public String spd;
        }
    }

    /*
    每小时预报信息
     */
    public static class HourlyForecastInfo implements Serializable {
        @SerializedName("date")
        public String date;
        @SerializedName("hum")
        public String hum;
        @SerializedName("pop")
        public String pop;
        @SerializedName("pres")
        public String pres;
        @SerializedName("tmp")
        public String tmp;
        @SerializedName("wind")
        public WindInfo wind;

        public static class WindInfo implements Serializable {
            @SerializedName("deg")
            public String deg;
            @SerializedName("dir")
            public String dir;
            @SerializedName("sc")
            public String sc;
            @SerializedName("spd")
            public String spd;
        }
    }

    /*
    生活建议信息
     */
    public static class SuggestionInfo implements Serializable {

        @SerializedName("comf")
        public ComfInfo comf;
        @SerializedName("cw")
        public CwInfo cw;
        @SerializedName("drsg")
        public DrsgInfo drsg;
        @SerializedName("flu")
        public FluInfo flu;
        @SerializedName("sport")
        public SportInfo sport;
        @SerializedName("trav")
        public TravInfo trav;
        @SerializedName("uv")
        public UvInfo uv;


        public static class ComfInfo implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }

        public static class CwInfo implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }

        public static class DrsgInfo implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }

        public static class FluInfo implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }

        public static class SportInfo implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }

        public static class TravInfo implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }

        public static class UvInfo implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }
    }
}
