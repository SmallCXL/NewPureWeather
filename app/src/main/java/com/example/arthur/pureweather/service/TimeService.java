package com.example.arthur.pureweather.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.arthur.pureweather.constant.Constants;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/9/11.
 */
public class TimeService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        AlarmManager manager = ((AlarmManager) getSystemService(ALARM_SERVICE));
//        int interval = 1000 ;//1秒
//        long triggerTime = SystemClock.elapsedRealtime() + interval;
//        Intent i = new Intent(Constants.ON_UPDATE_WIDGET_TIME);
//        PendingIntent pi = PendingIntent.getBroadcast(this,0,i,0);
//        manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pi);
        Observable
                .concat(Observable.just(1), Observable.interval(1000, TimeUnit.MILLISECONDS))
                .subscribeOn(Schedulers.newThread())
                .subscribe(number -> {
                    sendBroadcast(new Intent(Constants.ON_UPDATE_WIDGET_TIME));
                });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(this,BackupTimeService.class);
        startService(intent);
        super.onDestroy();
    }
}
