package com.example.arthur.pureweather.httpUtils;

import com.example.arthur.pureweather.constant.MyString;
import com.example.arthur.pureweather.modle.VersionInfo;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2016/8/6.
 */
public interface VersionService {
    @GET(MyString.PURE_WEATHER_ID)
    Observable<VersionInfo> getVersionInfo(@Query("api_token")String apiToken);
}
