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
import android.util.Log;
import android.widget.Toast;

import com.example.arthur.pureweather.activity.WeatherActivity;
import com.example.arthur.pureweather.constant.Constants;
import com.example.arthur.pureweather.db.PureWeatherDB;
import com.example.arthur.pureweather.httpUtils.NetworkRequest;
import com.example.arthur.pureweather.modle.Weather;
import com.example.arthur.pureweather.utils.ImageCodeConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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
        Intent intent = new Intent(this,BackupService.class);
        startService(intent);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int interval = mSharedPreferences.getInt(Constants.UPDATE_INTERVAL, 1);
        Observable
                .concat(Observable.just(1),Observable.interval(interval * Constants.ONE_HOUR, TimeUnit.MILLISECONDS))
                .delay( getDelay(), TimeUnit.SECONDS )
                .takeWhile(aLong -> (interval > 0))
                .subscribeOn(Schedulers.newThread())
                .subscribe(aLong -> getWeatherByNetwork());

        Observable
                .concat(Observable.just(1), Observable.interval(1000, TimeUnit.MILLISECONDS))
                .subscribeOn(Schedulers.newThread())
                .subscribe(number -> sendBroadcast(new Intent(Constants.ON_UPDATE_WIDGET_TIME)));
        return START_STICKY;
    }

    public int getDelay(){
        Date today = new Date();
        String nowMinute = new SimpleDateFormat("mm").format(today);
        //和风API会在每小时的52-53分钟发布新的天气信息，在54分钟的时候进行更新
        int delay = (54 - Integer.parseInt(nowMinute)) * 60;
        return (delay < 0) ? 0 : delay;
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
                        if (weather.status.equals("ok")) {
//                            Log.d("Small","data coming");
                            pureWeatherDB.saveWeather(weather);
                            Intent intent = new Intent(Constants.ON_UPDATE_WIDGET_ALL);
                            //延时5秒后发送更新桌面部件的广播，确保数据已经保存完毕
                            //此处有坑，原来使用的是Timer定时任务，计时5秒很不准确，导致收到数据后还没来的及保存，就发送了广播，桌面部件也就不能按时更新
                            //RxJava的interval函数具有高精度定时任务功能
                            Observable
                                    .just(1)
                                    .delay(5,TimeUnit.SECONDS)
                                    .subscribe(integer -> sendBroadcast(intent));
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
