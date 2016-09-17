package com.example.arthur.pureweather.constant;

import android.Manifest;

public class Constants {
    /*
    Comment key
     */
    public static final String AUTO_CHECK = "auto_check";
    public static final String MANUAL_CHECK = "manual_check";
    public static final String IGNORE_UPDATE = "ignore_update";
    public static final String LAST_CITY = "last_city";
    public static final String MY_LOCATION = "my_location";
    public static final int ONE_HOUR = 60 * 60 * 1000;// 增加更新频率
    public static final String GIT_HUB_ADDRESS = "https://github.com/SmallCXL/NewPureWeather";
    public static final String EMAIL_ADDRESS = "415318545@qq.com";
    public static final String DONATION_ADDRESS = "18023891508";
    public static final String IS_NEED_TO_CHECK = "is_need_to_check";
    /*
    APP Key
    */
    public static final String HE_WEATHER_KEY = "37fa5d4ad1ea4d5da9f37e75732fb2e7";
//    public static final String HE_WEATHER_KEY = "511077d0f15f4d82bf772dd18ce894b7";
    public static final String API_TOKEN = "dae845dc49739f113a00784ac777b94f";
    public static final String PURE_WEATHER_ID = "57a5ba23548b7a60b1000733";
    /*
    Base Url
    */
    public static final String HE_WEATHER_BASE_URL = "https://api.heweather.com/x3/";
    public static final String CHINA_WEATHER_BASE_BRL = "http://www.weather.com.cn/";
    public static final String CHECK_VERSION_BASE_URL = "http://api.fir.im/apps/latest/";

    /*
    preference key
     */
    public static final String UPDATE_MODE = "preference_update_mode";
    public static final String UPDATE_INTERVAL = "preference_update_interval";
    public static final String THEME_COLOR = "preference_system_color";
    public static final String APP_INFORMATION = "preference_app_information";
    public static final String CHECK_FOR_UPDATE = "preference_check_for_update";
    public static final String SHOW_NOTIFICATION = "preference_notification";
    public static final String HAS_LABEL = "preference_has_label";
    public static final String SMART_LOCATION = "preference_smart_location";
    /*
    Statement
     */
    public static final String OUT_OF_NETWORK = "链接失败，请检查网络";
    public static final String PARSE_FAILURE = "解析数据失败，请重试";
    public static final String REFRESH_COMPLETE = "刷新完成~";
    public static final String SEARCH_FAIL = "无该城市信息，请重新搜索";
    /*
    class name
     */
    public static final String SERVICE_NAME = "com.example.arthur.pureweather.service.ForegroundService";
    /*
    permissions to checkPermission
     */
    public static final String[] NEEDED_PERMISSION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    public static final String ON_UPDATE_WIDGET_TIME = "com.example.arthur.pureweather.widget.UPDATE_TIME";
    public static final String ON_UPDATE_WIDGET_ALL = "com.example.arthur.pureweather.widget.UPDATE_ALL";



}
