package com.example.arthur.pureweather.activity;

import android.app.Activity;
import android.app.Application;

import com.example.arthur.pureweather.db.PureWeatherDB;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/9.
 */
public class SysApplication extends Application {
    private List<Activity> mList = new ArrayList<>();
    private static volatile SysApplication instance;
    private PureWeatherDB pureWeatherDB;

    private SysApplication() {

    }

    public static SysApplication getInstance() {
        SysApplication tempInstance = instance;
        if (null == tempInstance) {
            synchronized (SysApplication.class){
                tempInstance = instance;
                if (tempInstance == null){
                    tempInstance = new SysApplication();
                    instance = tempInstance;
                }
            }
        }
        return tempInstance;
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        pureWeatherDB = PureWeatherDB.getInstance(SysApplication.this);
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pureWeatherDB.closeDB();
            System.exit(0);
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

}
