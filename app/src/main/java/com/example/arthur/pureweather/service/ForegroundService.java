package com.example.arthur.pureweather.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.example.arthur.pureweather.activity.WeatherActivity;
import com.example.arthur.pureweather.constant.Constants;
import com.example.arthur.pureweather.db.PureWeatherDB;
import com.example.arthur.pureweather.httpUtils.NetworkRequest;
import com.example.arthur.pureweather.modle.Weather;
import com.example.arthur.pureweather.utils.ImageCodeConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * ForegroundService 完成以下事情：
 * 1、启动服务后，每秒钟发送一个更新widget时间数据的广播
 * 2、每小时的10:00整，进行一次网络请求，更新并保存天气数据，并发送更新widget天气数据的广播
 * 3、本服务被kill之后，启动备用服务 BackupService
 */
public class ForegroundService extends Service {
    public SharedPreferences mSharedPreferences;
    private String lastCity;
    private PureWeatherDB pureWeatherDB;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        pureWeatherDB = PureWeatherDB.getInstance(this);
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(this,BackupService.class);
        startService(intent);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        int interval = mSharedPreferences.getInt(Constants.UPDATE_INTERVAL, 1);
// 为了较好的用户体验，暂时没有按照设定的时间定时更新
        Observable
                .concat(Observable.just(1), Observable.interval(1000, TimeUnit.MILLISECONDS))
                .subscribeOn(Schedulers.newThread())
                .subscribe(number -> {
                    sendBroadcast(new Intent(Constants.ON_UPDATE_WIDGET_TIME));
                    Date today = new Date();
                    String nowMinute = new SimpleDateFormat("mm:ss").format(today);
                    if (nowMinute.equals("10:00")) {
                        getWeatherByNetwork();
                    }
                });
        return START_STICKY;
    }
    /**
     * 从网络获取天气信息，获取成功后，延时10秒发送更新widget的广播（确保数据已经存入数据库）
     * 同时处理通知栏的显示
     *
     * @since 1.0.0
     *
     */
    public void getWeatherByNetwork() {
        lastCity = mSharedPreferences.getString(Constants.LAST_CITY, "");
        NetworkRequest.getWeatherWithName(lastCity)
                .observeOn(AndroidSchedulers.mainThread())
                .map(weatherResponse -> weatherResponse.getWeathers())
                .flatMap(weathers -> {
                    if (weathers != null) {
                        return Observable.from(weathers);
                    } else
                        return Observable.error(new Exception("网络连接失败"));
                })
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Weather weather) {
                        if (weather.status.equals("ok")) {
                            pureWeatherDB.saveWeather(weather);
                            //延时5秒后发送更新桌面部件的广播，确保数据已经保存完毕
                            //此处有坑，原来使用的是Timer定时任务，计时5秒很不准确，导致收到数据后还没来的及保存，就发送了广播，桌面部件也就不能按时更新
                            //RxJava的interval函数具有高精度定时任务功能
                            Observable
                                    .just(1)
                                    .delay(10, TimeUnit.SECONDS)
                                    .subscribe(integer -> {
                                        Intent intent = new Intent(Constants.ON_UPDATE_WIDGET_ALL);
                                        sendBroadcast(intent);
                                    });
                            if (mSharedPreferences.getBoolean(Constants.SHOW_NOTIFICATION, true)) {
                                showNotification(weather);
                            } else {
                                closeNotification();
                            }
                        }
                    }
                });
    }
    /**
     * 将获取的天气信息显示在通知栏
     *
     * @param weather 需要显示的天气信息
     * @since 1.0.0
     *
     */
    public void showNotification(Weather weather) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(ForegroundService.this, WeatherActivity.class);
        PendingIntent pi = PendingIntent
                .getActivity(ForegroundService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(ForegroundService.this);
        Notification notification = builder
                .setContentIntent(pi)
                .setContentTitle(weather.basic.city)
                .setContentText(String.format("%s°C，%s",  weather.now.tmp,weather.now.cond.txt))
                .setSmallIcon(ImageCodeConverter.getWeatherIconResource(weather.now.cond.code, "notice"))
                .setOngoing(true)
                .build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
        notification.flags |= Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(1, notification);
    }

    /**
     * 关闭通知栏显示
     */
    public void closeNotification() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
    }

}
