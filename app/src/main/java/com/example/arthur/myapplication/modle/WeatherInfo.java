package com.example.arthur.myapplication.modle;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/7/2.
 */
public class WeatherInfo implements Serializable{
/*
所有字段信息
 */
//    @SerializedName("aqi")             private AqiInfo aqi;
    @SerializedName("basic")           private BasicInfo basic;
    @SerializedName("now")             private NowInfo now;
    @SerializedName("status")          private String status;
    @SerializedName("suggestion")      private SuggestionInfo suggestion;
    @SerializedName("daily_forecast")  private List<DailyForecastInfo> dailyForecast;
    @SerializedName("hourly_forecast") private List<HourlyForecastInfo> hourlyForecast;

//    public AqiInfo getAqi() {
//        return aqi;
//    }

    public BasicInfo getBasic() {
        return basic;
    }

    public NowInfo getNow() {
        return now;
    }

    public String getStatus() {
        return status;
    }

    public SuggestionInfo getSuggestion() {
        return suggestion;
    }

    public List<DailyForecastInfo> getDailyForecast() {
        return dailyForecast;
    }

    public List<HourlyForecastInfo> getHourlyForecast() {
        return hourlyForecast;
    }

    /*
    基本信息
     */
    public static class BasicInfo implements Serializable {
        @SerializedName("city")   private String city;
        @SerializedName("cnty")   private String cnty;
        @SerializedName("id")     private String id;
        @SerializedName("lat")    private String lat;
        @SerializedName("lon")    private String lon;
        @SerializedName("update") private UpdateInfo update;

        public String getCity() {
            return city;
        }

        public String getCnty() {
            return cnty;
        }

        public String getId() {
            return id;
        }

        public String getLat() {
            return lat;
        }

        public String getLon() {
            return lon;
        }

        public UpdateInfo getUpdate() {
            return update;
        }

        public static class UpdateInfo implements Serializable {
            @SerializedName("loc") private String loc;
            @SerializedName("utc") private String utc;

            public String getLoc() {
                return loc;
            }

            public String getUtc() {
                return utc;
            }
        }
    }
/*
空气质量信息
 */
//    public static class AqiInfo implements Serializable {
//
//        @SerializedName("city") private CityInfo city;
//
//        public CityInfo getCity() {
//            return city;
//        }
//
//        public static class CityInfo implements Serializable {
//            @SerializedName("aqi")  private String aqi;
//            @SerializedName("co")   private String co;
//            @SerializedName("no2")  private String no2;
//            @SerializedName("o3")   private String o3;
//            @SerializedName("pm10") private String pm10;
//            @SerializedName("pm25") private String pm25;
//            @SerializedName("qlty") private String qlty;
//            @SerializedName("so2")  private String so2;
//
//            public String getAqi() {
//                return aqi;
//            }
//
//            public String getCo() {
//                return co;
//            }
//
//            public String getNo2() {
//                return no2;
//            }
//
//            public String getO3() {
//                return o3;
//            }
//
//            public String getPm10() {
//                return pm10;
//            }
//
//            public String getPm25() {
//                return pm25;
//            }
//
//            public String getQlty() {
//                return qlty;
//            }
//
//            public String getSo2() {
//                return so2;
//            }
//        }
//    }
/*
现在天气信息
 */
    public static class NowInfo implements Serializable {
        @SerializedName("cond") private CondInfo cond;
        @SerializedName("fl")   private String fl;
        @SerializedName("hum")  private String hum;
        @SerializedName("pcpn") private String pcpn;
        @SerializedName("pres") private String pres;
        @SerializedName("tmp")  private String tmp;
        @SerializedName("vis")  private String vis;
        @SerializedName("wind") private WindInfo wind;

        public CondInfo getCond() {
            return cond;
        }

        public String getFl() {
            return fl;
        }

        public String getHum() {
            return hum;
        }

        public String getPcpn() {
            return pcpn;
        }

        public String getPres() {
            return pres;
        }

        public String getTmp() {
            return tmp;
        }

        public String getVis() {
            return vis;
        }

        public WindInfo getWind() {
            return wind;
        }

    public static class CondInfo implements Serializable {
        @SerializedName("code") private String code;
        @SerializedName("txt")  private String txt;

        public String getCode() {
            return code;
        }

        public String getTxt() {
            return txt;
        }
    }

        public static class WindInfo implements Serializable {
            @SerializedName("deg") private String deg;
            @SerializedName("dir") private String dir;
            @SerializedName("sc")  private String sc;
            @SerializedName("spd") private String spd;

            public String getDeg() {
                return deg;
            }

            public String getDir() {
                return dir;
            }

            public String getSc() {
                return sc;
            }

            public String getSpd() {
                return spd;
            }
        }
    }
    /*
   每日预报信息
    */
    public static class DailyForecastInfo implements Serializable {
        @SerializedName("astro") private AstroInfo astro;
        @SerializedName("cond")  private CondInfo cond;
        @SerializedName("date")  private String date;
        @SerializedName("hum")   private String hum;
        @SerializedName("pcpn")  private String pcpn;
        @SerializedName("pop")   private String pop;
        @SerializedName("pres")  private String pres;
        @SerializedName("tmp")   private TmpInfo tmp;
        @SerializedName("vis")   private String vis;
        @SerializedName("wind")  private WindInfo wind;

        public AstroInfo getAstro() {
            return astro;
        }

        public CondInfo getCond() {
            return cond;
        }

        public String getDate() {
            return date;
        }

        public String getHum() {
            return hum;
        }

        public String getPcpn() {
            return pcpn;
        }

        public String getPop() {
            return pop;
        }

        public String getPres() {
            return pres;
        }

        public TmpInfo getTmp() {
            return tmp;
        }

        public String getVis() {
            return vis;
        }

        public WindInfo getWind() {
            return wind;
        }

        public static class AstroInfo implements Serializable {
            @SerializedName("sr") private String sr;
            @SerializedName("ss") private String ss;

            public String getSr() {
                return sr;
            }

            public String getSs() {
                return ss;
            }
        }

        public static class CondInfo implements Serializable {
            @SerializedName("code_d") private String codeD;
            @SerializedName("code_n") private String codeN;
            @SerializedName("txt_d")  private String txtD;
            @SerializedName("txt_n")  private String txtN;

            public String getCodeD() {
                return codeD;
            }

            public String getCodeN() {
                return codeN;
            }

            public String getTxtD() {
                return txtD;
            }

            public String getTxtN() {
                return txtN;
            }
        }

        public static class TmpInfo implements Serializable {
            @SerializedName("max") private String max;
            @SerializedName("min") private String min;

            public String getMax() {
                return max;
            }

            public String getMin() {
                return min;
            }
        }

        public static class WindInfo implements Serializable {
            @SerializedName("deg") private String deg;
            @SerializedName("dir") private String dir;
            @SerializedName("sc")  private String sc;
            @SerializedName("spd") private String spd;

            public String getDeg() {
                return deg;
            }

            public String getDir() {
                return dir;
            }

            public String getSc() {
                return sc;
            }

            public String getSpd() {
                return spd;
            }
        }
    }
/*
每小时预报信息
 */
    public static class HourlyForecastInfo implements Serializable {
        @SerializedName("date") private String date;
        @SerializedName("hum")  private String hum;
        @SerializedName("pop")  private String pop;
        @SerializedName("pres") private String pres;
        @SerializedName("tmp")  private String tmp;
        @SerializedName("wind") private WindInfo wind;

        public String getDate() {
            return date;
        }

        public String getHum() {
            return hum;
        }

        public String getPop() {
            return pop;
        }

        public String getPres() {
            return pres;
        }

        public String getTmp() {
            return tmp;
        }

        public WindInfo getWind() {
            return wind;
        }

        public static class WindInfo implements Serializable {
            @SerializedName("deg") private String deg;
            @SerializedName("dir") private String dir;
            @SerializedName("sc")  private String sc;
            @SerializedName("spd") private String spd;

            public String getDeg() {
                return deg;
            }

            public String getDir() {
                return dir;
            }

            public String getSc() {
                return sc;
            }

            public String getSpd() {
                return spd;
            }
        }
    }
/*
生活建议信息
 */
    public static class SuggestionInfo implements Serializable {

        @SerializedName("comf")  private ComfInfo comf;
        @SerializedName("cw")    private CwInfo cw;
        @SerializedName("drsg")  private DrsgInfo drsg;
        @SerializedName("flu")   private FluInfo flu;
        @SerializedName("sport") private SportInfo sport;
        @SerializedName("trav")  private TravInfo trav;
        @SerializedName("uv")    private UvInfo uv;

        public ComfInfo getComf() {
            return comf;
        }

        public CwInfo getCw() {
            return cw;
        }

        public DrsgInfo getDrsg() {
            return drsg;
        }

        public FluInfo getFlu() {
            return flu;
        }

        public SportInfo getSport() {
            return sport;
        }

        public TravInfo getTrav() {
            return trav;
        }

        public UvInfo getUv() {
            return uv;
        }

        public static class ComfInfo implements Serializable {
            @SerializedName("brf") private String brf;
            @SerializedName("txt") private String txt;

            public String getBrf() {
                return brf;
            }

            public String getTxt() {
                return txt;
            }
        }

        public static class CwInfo implements Serializable {
            @SerializedName("brf") private String brf;
            @SerializedName("txt") private String txt;

            public String getBrf() {
                return brf;
            }

            public String getTxt() {
                return txt;
            }
        }

        public static class DrsgInfo implements Serializable {
            @SerializedName("brf") private String brf;
            @SerializedName("txt") private String txt;

            public String getBrf() {
                return brf;
            }

            public String getTxt() {
                return txt;
            }
        }

        public static class FluInfo implements Serializable {
            @SerializedName("brf") private String brf;
            @SerializedName("txt") private String txt;

            public String getBrf() {
                return brf;
            }

            public String getTxt() {
                return txt;
            }
        }

        public static class SportInfo implements Serializable {
            @SerializedName("brf") private String brf;
            @SerializedName("txt") private String txt;

            public String getBrf() {
                return brf;
            }

            public String getTxt() {
                return txt;
            }
        }

        public static class TravInfo implements Serializable {
            @SerializedName("brf") private String brf;
            @SerializedName("txt") private String txt;

            public String getBrf() {
                return brf;
            }

            public String getTxt() {
                return txt;
            }
        }

        public static class UvInfo implements Serializable {
            @SerializedName("brf") private String brf;
            @SerializedName("txt") private String txt;

            public String getBrf() {
                return brf;
            }

            public String getTxt() {
                return txt;
            }
        }
    }
}
