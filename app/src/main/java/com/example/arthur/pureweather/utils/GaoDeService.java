package com.example.arthur.pureweather.utils;

import android.content.Context;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
/**
 * Created by Administrator on 2016/9/15.
 */
public class GaoDeService {
    private Context context;
    private static GaoDeService instance;
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;

    private AMapLocationListener aMapLocationListener;

    private GaoDeService(Context context){
        this.context = context;
        initService();
    }
    private void initService(){
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取最近3s内精度最高的一次定位结果：
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setNeedAddress(true);
    }
    public static GaoDeService getInstance(Context context){
        instance = new GaoDeService(context);
        return instance;
    }
    public GaoDeService setOnLocationChangeListener(AMapLocationListener aMapLocationListener){
        this.aMapLocationListener = aMapLocationListener;
        return instance;
    }
    public GaoDeService startLocation(){
        mLocationClient = new AMapLocationClient(context);
        mLocationClient.setLocationListener(aMapLocationListener);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
        return instance;
    }
    public void onDestroy(){
        if (mLocationClient != null){
            mLocationClient.onDestroy();
        }
    }
}
