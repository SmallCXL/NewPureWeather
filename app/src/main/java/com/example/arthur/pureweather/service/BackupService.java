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
 * BackupService 完成以下任务
 * 1、启动后，开启ForegroundService服务，并自动停止。
 */
public class BackupService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent i = new Intent(this,ForegroundService.class);
        startService(i);
        stopSelf();
        return START_STICKY;
    }

}
