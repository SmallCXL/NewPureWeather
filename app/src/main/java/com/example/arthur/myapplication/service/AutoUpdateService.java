package com.example.arthur.myapplication.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.arthur.myapplication.modle.PureWeatherDB;
import com.example.arthur.myapplication.receiver.AutoUpdateReceiver;
import com.example.arthur.myapplication.notUsed.HttpCallbackListener;
import com.example.arthur.myapplication.notUsed.HttpUtils;

/**
 * Created by Administrator on 2016/6/9.
 */
public class AutoUpdateService extends Service{


    private PureWeatherDB pureWeatherDB;
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        new Thread((new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                updateWeather();
            }

        })).start();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour = 1000 * 60 * 60;
        long trigerTime = SystemClock.elapsedRealtime() + pref.getInt("sync_interval",4) * anHour;
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, trigerTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    public void updateWeather() {
        // TODO Auto-generated method stub
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String lastCity = pref.getString("last_city", "");
        String address = new StringBuilder().append("https://api.heweather.com/x3/weather?city=")
                .append(lastCity).append("&key=37fa5d4ad1ea4d5da9f37e75732fb2e7").toString();
        pureWeatherDB = PureWeatherDB.getInstance(this);
        HttpUtils.sendHttpRequest(address, new HttpCallbackListener() {

            @Override
            public void onFinish(String response) {
                // TODO Auto-generated method stub
//                pureWeatherDB.saveWeather(response);
            }

            @Override
            public void onError(Exception e) {
                // TODO Auto-generated method stub
                e.printStackTrace();
            }

        });

    }

}
