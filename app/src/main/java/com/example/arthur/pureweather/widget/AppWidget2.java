package com.example.arthur.pureweather.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.DigitalClock;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.arthur.pureweather.R;
import com.example.arthur.pureweather.activity.GuideActivity;
import com.example.arthur.pureweather.constant.Constants;
import com.example.arthur.pureweather.db.PureWeatherDB;
import com.example.arthur.pureweather.modle.Weather;
import com.example.arthur.pureweather.utils.ImageCodeConverter;
import com.example.arthur.pureweather.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/9/1.
 */
public class AppWidget2 extends AppWidgetProvider {
    public PureWeatherDB pureWeatherDB;
    public SharedPreferences pref;
    private static List<Integer> mAppWidgetIds = new ArrayList<>();
    @Override
    public void onEnabled(Context context) {
//        Log.d("Small","on enable");
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        Log.d("Small","on update");
        if (appWidgetIds.length > 0 ){
            for (int i=0; i<appWidgetIds.length;i++){
                mAppWidgetIds.add(appWidgetIds[i]);
            }
        }
        showTimeInfo(context);
        int[] appId = AppWidgetManager.getInstance(context).getAppWidgetIds(
                new ComponentName(context.getPackageName(),AppWidget2.class.getName()));
        showWeatherInfo(context, appId);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.d("Small","on receive "+intent.getAction());
        switch (intent.getAction()){
            case Constants.ON_UPDATE_WIDGET_ALL:
                int[] appId = AppWidgetManager.getInstance(context).getAppWidgetIds(
                        new ComponentName(context.getPackageName(),AppWidget2.class.getName()));
                showWeatherInfo(context, appId);
                break;
            case Constants.ON_UPDATE_WIDGET_TIME:
                showTimeInfo(context);
                break;
        }
        super.onReceive(context, intent);
    }

    private void showWeatherInfo(Context context, int[] appWidgetIds){
        pureWeatherDB = PureWeatherDB.getInstance(context);
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        String lastCity = pref.getString(Constants.LAST_CITY, "");
        if (!lastCity.equals("")){
            Weather weather = pureWeatherDB.loadWeatherInfo(lastCity);
            RemoteViews remoteViews=new RemoteViews(context.getPackageName(), R.layout.widget_app_2);
            remoteViews.setImageViewResource(R.id.widget_image, ImageCodeConverter.getWeatherIconResource(weather.now.cond.code, "widget"));
            String nowCondition = new StringBuilder().append(weather.now.tmp).append("°C，").append(weather.now.cond.txt).toString();
//            String nowCondition = new StringBuilder().append(weather.now.tmp).append("°C，").append(weather.basic.update.loc).toString();
            remoteViews.setTextViewText(R.id.widget_now_condition,nowCondition);
            remoteViews.setTextViewText(R.id.widget_city_name, weather.basic.city);
            AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remoteViews);

        }else {
            Toast.makeText(context,"请先选择一个城市",Toast.LENGTH_SHORT).show();
        }
    }
    private void showTimeInfo(Context context){

        Date today = new Date();
        String time = new SimpleDateFormat("HH:mm:ss").format(today);
//        Log.d("Small","time change"+time.substring(6));
        String date = new SimpleDateFormat("yyyy-MM-dd").format(today);
        String dateOfWeek = Utils.getDateOfWeek(date);
        String dateInfo = new StringBuilder().append(new SimpleDateFormat("MM月dd日").format(today)).append(" - ").append(dateOfWeek).toString();
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_app_2);
        remoteViews.setTextViewText(R.id.widget_now_time,time.substring(0,5));
        remoteViews.setTextViewText(R.id.widget_date,dateInfo);

        Intent goToSystemAlarm = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
        PendingIntent pi = PendingIntent.getActivity(context, 0, goToSystemAlarm, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_now_time, pi);

        Intent goToApp = new Intent(context,GuideActivity.class);
        PendingIntent pi2 = PendingIntent.getActivity(context, 0, goToApp, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_weather_info, pi2);

        ComponentName componentName = new ComponentName(context.getPackageName(),AppWidget2.class.getName());
        AppWidgetManager.getInstance(context).updateAppWidget(AppWidgetManager.getInstance(context).getAppWidgetIds(componentName), remoteViews);
    }
}
