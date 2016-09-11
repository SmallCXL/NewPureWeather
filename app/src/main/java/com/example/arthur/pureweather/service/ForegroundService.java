package com.example.arthur.pureweather.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.arthur.pureweather.activity.WeatherActivity;
import com.example.arthur.pureweather.constant.Constants;
import com.example.arthur.pureweather.db.PureWeatherDB;
import com.example.arthur.pureweather.httpUtils.NetworkRequest;
import com.example.arthur.pureweather.modle.Weather;
import com.example.arthur.pureweather.utils.ImageCodeConverter;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2016/8/7.
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
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //在服务的生命周期内每次启动服务，都会调用这个方法，为了保证系统只启动一个通知，需要进行同步监测处理
        //本类服务需要一直在后台执行定期更新数据的任务，IntentService不合适

        int interval = mSharedPreferences.getInt(Constants.UPDATE_INTERVAL, 1);
        Observable
                .interval(interval * Constants.ONE_HOUR, TimeUnit.SECONDS)
//                        .interval(6, TimeUnit.SECONDS)
                .takeWhile(aLong -> (interval > 0))
                .subscribeOn(Schedulers.newThread())
                .subscribe(aLong -> getWeatherByNetwork());

        return START_STICKY;
        //此出也可以返回START_STICKY,因为在传入的intent中没有携带数据
    }

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
                        Toast.makeText(ForegroundService.this, "自动更新失败，请检查网络", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Weather weather) {
                        if (weather != null) {
                            pureWeatherDB.saveWeather(weather);
                            Intent intent = new Intent(Constants.ON_UPDATE_WIDGET_ALL);
                            //发送更新桌面部件的广播
                            sendBroadcast(intent);
                            if (mSharedPreferences.getBoolean(Constants.SHOW_NOTIFICATION, true)) {
                                showNotification(weather);
                            } else {
                                closeNotification();
                            }
                        }
                    }
                });
    }

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

    public void closeNotification() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
    }

}
