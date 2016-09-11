package com.example.arthur.pureweather.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.arthur.pureweather.constant.Constants;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/9/11.
 */
public class BackupTimeService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Observable.concat(Observable.just(1), Observable.interval(1000, TimeUnit.MILLISECONDS))
                .subscribeOn(Schedulers.newThread())
                .subscribe(number -> {
                    sendBroadcast(new Intent(Constants.ON_UPDATE_WIDGET_TIME));
                });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(this,TimeService.class);
        startService(intent);
        super.onDestroy();
    }
}
