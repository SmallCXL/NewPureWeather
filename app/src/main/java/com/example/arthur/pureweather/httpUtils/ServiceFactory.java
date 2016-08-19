package com.example.arthur.pureweather.httpUtils;

import com.example.arthur.pureweather.constant.Constants;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2016/7/3.
 */
public class ServiceFactory {

    private static volatile WeatherService weatherService;
    private static volatile RegionService regionService;
    private static volatile VersionService versionService;

    //将构造函数私有化，同时本类内部也不创建实例，保证整个类没有实例，只能通过类名调用暴露的getXXXService接口来获取服务的实例
    //这是一个特殊的类，与普通单件模式区别在于它的功能唯一，不需要做其他的事情，它不需要任何实例，只是一个生产服务实例的工厂
    //与RxBus对比，RxBus还需要一些post、toObservable的方法，完成其他的事情，因此RxBus类内部需要创建一个实例来操作这些方法
    private ServiceFactory() {}

    //获取WeatherService实例  单件模式，确保线程安全,减少生成服务的开支
    public static WeatherService getWeatherService(){
        WeatherService tempService = weatherService;
        if (tempService == null){
            synchronized (ServiceFactory.class){
                tempService = weatherService;
                if (tempService == null){
                    tempService = createWeatherService();
                    weatherService = tempService;
                }
            }
        }
        return tempService;
    }
    /*
    创建和风天气API的服务实例，仅类的内部使用
     */
    private static WeatherService createWeatherService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.HE_WEATHER_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 添加Rx适配器，使Retrofit支持RxJava的Observable回调类型
                .addConverterFactory(GsonConverterFactory.create()) // 添加Gson转换器
                .build();
        return retrofit.create(WeatherService.class);
    }


    /*
    获取RegionService实例 - 单件模式，确保线程安全
     */
    public static RegionService getRegionService(String superCode){
        RegionService tempService = regionService;
        if (tempService == null){
            synchronized (ServiceFactory.class){
                tempService = regionService;
                if (tempService == null){
                    tempService = createRegionService(superCode);
                    regionService = tempService;
                }
            }
        }
        return tempService;
    }
    /*
    创建中国天气网API服务实例
     */
    private static RegionService createRegionService(String superCode) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.CHINA_WEATHER_BASE_BRL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 添加Rx适配器，使Retrofit支持RxJava的Observable回调类型
                .addConverterFactory(RegionConverterFactory.create(superCode)) // 添加自定义的Province数据转换器
                .build();
        return retrofit.create(RegionService.class);
    }

    /*
    获取IM FIR 的服务实例
     */
    public static VersionService getVersionService(){
        VersionService tempService = versionService;
        if (tempService == null){
            synchronized (ServiceFactory.class){
                tempService = versionService;
                if (tempService == null){
                    tempService = createVersionService();
                    versionService = tempService;
                }
            }
        }
        return tempService;
    }
    /*
    创建IM FIR 的服务实例
     */
    private static VersionService createVersionService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.CHECK_VERSION_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(VersionService.class);
    }
}
