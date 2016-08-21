package com.example.arthur.pureweather.activity;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.arthur.pureweather.R;
import com.example.arthur.pureweather.constant.Constants;
import com.example.arthur.pureweather.httpUtils.NetworkRequest;
import com.example.arthur.pureweather.modle.Weather;
import com.example.arthur.pureweather.service.ForegroundService;
import com.example.arthur.pureweather.utils.CheckVersion;
import com.example.arthur.pureweather.utils.MyImageLoader;
import com.example.arthur.pureweather.db.PureWeatherDB;
import com.example.arthur.pureweather.adapter.WeatherAdapter;
import com.example.arthur.pureweather.utils.ImageCodeConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/6/9.
 */
public class WeatherActivity extends AppCompatActivity {
    private static Boolean isExit = false;

    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbar;

    @Bind(R.id.image_view)
    ImageView toolbarBackground;
    @Bind(R.id.weather_toolbar)
    Toolbar toolbar;
    @Bind(R.id.weather_drawer_layout)
    DrawerLayout mDrawerLayout;

    ActionBarDrawerToggle mDrawerToggle;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.weather_recycler_view)
    RecyclerView mRecyclerView;

    private PureWeatherDB pureWeatherDB;
    private SharedPreferences pref;
    private List<Weather> weathers;
    private String lastCity = null;
    private WeatherAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        SysApplication.getInstance().addActivity(this);
        initUtils();

        if (TextUtils.isEmpty(lastCity)) {
            //没有lastCity，直接去搜索页面
            Toast.makeText(WeatherActivity.this, "先选择一个城市吧~", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            finish();
        }
        ButterKnife.bind(this);
        init();
        CheckVersion.autoCheck(WeatherActivity.this);

    }

    @Override
    public void onResume() {
        super.onResume();
        showWeather();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void initUtils() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        lastCity = pref.getString(Constants.LAST_CITY, "");
        pureWeatherDB = PureWeatherDB.getInstance(this);
    }

    private void init() {
        initRecyclerView();
        initToolbar();
        initNavigationView();
        initSwipeRefreshLayout();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.drawer_open,
                R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void initRecyclerView() {
        Weather weather = pureWeatherDB.loadWeatherInfo(lastCity);
        weathers = new ArrayList<>();
        weathers.add(weather);
        mAdapter = new WeatherAdapter(WeatherActivity.this, weathers);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initNavigationView() {
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_layout);
        final LinearLayout headerBackground = (LinearLayout) headerLayout.findViewById(R.id.nav_header_linear_layout);

        navigationView.setItemTextColor(ColorStateList.valueOf(ContextCompat.getColor(WeatherActivity.this, R.color.textColor)));

        MyImageLoader.load(WeatherActivity.this, R.drawable.background, headerBackground);

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case (R.id.nav_my_list):
                    mDrawerLayout.closeDrawer(navigationView);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(WeatherActivity.this, CityManagerActivity.class);
                            startActivity(intent);
                        }
                    }, 180);
                    break;
                case (R.id.nav_add_city):

                    mDrawerLayout.closeDrawer(navigationView);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(WeatherActivity.this, SearchActivity.class);
                            startActivity(intent);
                        }
                    }, 180);
                    break;
                case R.id.nav_settings:
                    mDrawerLayout.closeDrawer(navigationView);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(WeatherActivity.this, SettingActivity.class);
                            startActivity(intent);
                        }
                    }, 180);
                    break;
                case R.id.nav_about_app:
                    mDrawerLayout.closeDrawer(navigationView);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(WeatherActivity.this, AboutAppActivity.class);
                            startActivity(intent);
                        }
                    }, 180);
                    break;
            }
            return false;
        });
    }

    private void initSwipeRefreshLayout() {
        //下拉刷新 .这里的Subscriber必须用匿名的，如果用事先定义好的观察者只能响应一次，待研究
        swipeRefreshLayout.setOnRefreshListener(() -> {
            lastCity = pref.getString(Constants.LAST_CITY, "");
            NetworkRequest
                    .getWeatherWithName(lastCity)
                    .subscribeOn(Schedulers.newThread())
                    .flatMap(weatherResponse -> Observable.from(weatherResponse.getWeathers()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnTerminate(() -> swipeRefreshLayout.setRefreshing(false))
                    .subscribe(new Subscriber<Weather>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(WeatherActivity.this, Constants.OUT_OF_NETWORK, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(Weather weather) {
                            pureWeatherDB.saveWeather(weather);
                            showWeather();
                            Toast.makeText(WeatherActivity.this, "刷新完成~~", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.indigoPrimary, R.color.greenPrimary, R.color.redPrimary);
    }

    private void showWeather() {
        lastCity = pref.getString(Constants.LAST_CITY, "");
        Weather weather = pureWeatherDB.loadWeatherInfo(lastCity);
/************************************** 处理通知显示 *************/
        boolean allowToShowNotice = pref.getBoolean(Constants.SHOW_NOTIFICATION,false);
        if (allowToShowNotice){
            showNotification(weather);
        }else {
            closeNotification();
        }
/************************************** 处理自动更新显示 *************/
        boolean allowToAutoUpdate = (pref.getInt(Constants.UPDATE_INTERVAL,0) > 0);
        Intent intent = new Intent(WeatherActivity.this, ForegroundService.class);
        if (allowToAutoUpdate && !isServiceRunning(Constants.SERVICE_NAME)){
            startService(intent);//原来停止服务，现在允许启动，开启服务
        }else if (!allowToAutoUpdate && isServiceRunning(Constants.SERVICE_NAME)){
            stopService(intent);//原来启动服务，现在禁止启动，停止服务
        }//其他情况不操作

        weathers.clear();
        weathers.add(weather);
        mAdapter.notifyDataSetChanged();
        if (weather != null) {
            MyImageLoader.load(WeatherActivity.this,
                    ImageCodeConverter.getMainBackgroundResource(weather.now.cond.code), toolbarBackground);
            collapsingToolbar.setTitle(weather.basic.city);
        }
        mRecyclerView.smoothScrollToPosition(0);
    }

    public void showNotification(Weather weather){
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(WeatherActivity.this, WeatherActivity.class);
        PendingIntent pi = PendingIntent
                .getActivity(WeatherActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(WeatherActivity.this);
        Notification notification = builder
                .setContentIntent(pi)
                .setContentTitle(weather.basic.city)
                .setContentText(String.format("%s，温度: %s°C",weather.now.cond.txt,weather.now.tmp))
                .setSmallIcon(ImageCodeConverter.getWeatherIconResource(weather.now.cond.code,"notice"))
                .setOngoing(true)
                .build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
        notification.flags |= Notification.FLAG_NO_CLEAR;
        manager.notify(1, notification);
    }
    public void closeNotification(){
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
    }


    public boolean isServiceRunning(String serviceClassName){
        final ActivityManager activityManager = (ActivityManager) WeatherActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }
        return false;
    }
    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDrawerLayout.isDrawerOpen(navigationView)) {
                mDrawerLayout.closeDrawer(navigationView);
            } else {
                exitBy2Click(); //调用双击退出函数
            }
        }
        return false;
    }

    /**
     * 双击退出函数
     */
    private void exitBy2Click() {
        Timer tExit;
        if (!isExit) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器，取消掉刚才执行的任务
        } else {
            SysApplication.getInstance().exit();
        }
    }
}
