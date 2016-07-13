package com.example.arthur.myapplication.httpUtils;

import com.example.arthur.myapplication.modle.Region;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Administrator on 2016/7/3.
 */
public interface RegionService {
    /*
    通过中国天气网获取全国城市列表的服务接口
     */
    @GET("data/list3/city{city_code}.xml")
    Observable<List<Region>> getRegion(@Path("city_code")String cityCode);
}
