package com.example.arthur.pureweather.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.example.arthur.pureweather.R;
import com.example.arthur.pureweather.constant.Constants;
import com.example.arthur.pureweather.httpUtils.NetworkRequest;
import com.example.arthur.pureweather.modle.Weather;
import com.example.arthur.pureweather.service.ForegroundService;
import com.example.arthur.pureweather.utils.CheckVersion;
import com.example.arthur.pureweather.utils.GaoDeService;
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
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * WeatherActivity ：
 * 1、检测当前数据库是否有天气信息，若没有，直接跳转搜索界面，若有，则执行以下功能
 * 2、启动后自动更新天气数据，并保存到数据库
 * 3、启动后自动检查当前软件的版本与服务器最新版本是否一致，不一致则弹出提示框提示下载
 * 4、若智能定位功能处于开启状态，则检测当前显示与当前位置是否一致，不一致则自动定位，下载天气数据，并弹出提示框提示切换城市
 */
public class WeatherActivity extends AppCompatActivity {
    private static Boolean isExit = false;
    private ProgressDialog progressDialog;
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

    PureWeatherDB pureWeatherDB;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    List<Weather> weathers;
    String lastCity = null;
    String myLocation = null;
    WeatherAdapter mAdapter;
    GaoDeService gaoDeService;
    private AMapLocationListener aMapLocationListener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        SysApplication.getInstance().addActivity(this);
        initUtils();

        if (TextUtils.isEmpty(lastCity)) {
            //没有lastCity，直接去搜索页面
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            finish();
        }else {
            ButterKnife.bind(this);
            init();
            showProgressDialog("更新中...");
            Action0 closeDialog = () -> closeProgressDialog();
            getWeatherByNetwork(lastCity, closeDialog);
            CheckVersion.autoCheck(WeatherActivity.this);
            /************************************** 处理自动更新显示 *************/
            boolean allowToAutoUpdate = (pref.getInt(Constants.UPDATE_INTERVAL, 1) > 0);
            Intent intent = new Intent(WeatherActivity.this, ForegroundService.class);
            if (allowToAutoUpdate) {
                initGaoDeLocate();
                startService(intent);//原来停止服务，现在允许启动，开启服务
            }
        }
        //智能切换，当前城市于所在位置不一致时，弹出提示框
        if (pref.getBoolean(Constants.SMART_LOCATION,true)){
            if (!myLocation.equals(lastCity)){//当前城市与所在地不同，提示是否更换
                gaoDeService
                        .setOnLocationChangeListener(aMapLocationListener)
                        .startLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showWeather();
    }

    @Override
    protected void onDestroy() {
        if (gaoDeService != null){
            gaoDeService.onDestroy();
        }
        super.onDestroy();
        ButterKnife.unbind(this);
    }
    /**
     * 进入WeatherActivity前的简单初始化
     */
    private void initUtils() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        lastCity = pref.getString(Constants.LAST_CITY, "");
        myLocation = pref.getString(Constants.MY_LOCATION,"");
        pureWeatherDB = PureWeatherDB.getInstance(this);
    }

    /**
     * 进入WeatherActivity后的整体初始化
     */
    private void init() {
        initRecyclerView();
        initToolbar();
        initNavigationView();
        initSwipeRefreshLayout();
    }

    /**
     * 初始化Toolbar
     */
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
    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Weather weather = pureWeatherDB.loadWeatherInfo(lastCity);
        weathers = new ArrayList<>();
        weathers.add(weather);
        mAdapter = new WeatherAdapter(WeatherActivity.this, weathers);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }
    /**
     * 初始化NavigationView
     */
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
    /**
     * 初始化SwipeRefreshLayout 添加下拉刷新逻辑
     */
    private void initSwipeRefreshLayout() {
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(() -> {
            lastCity = pref.getString(Constants.LAST_CITY, "");
            Action0 stopAnim = () -> swipeRefreshLayout.setRefreshing(false);
            getWeatherByNetwork(lastCity, stopAnim);
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.indigoPrimary, R.color.greenPrimary, R.color.redPrimary);
    }
    /**
     * 初始化GaoDeLocate 添加定位成功后的回调逻辑
     */
    private void initGaoDeLocate() {
        gaoDeService = GaoDeService.getInstance(WeatherActivity.this);
        aMapLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                closeProgressDialog();
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        myLocation = amapLocation.getCity().replaceAll("(?:省|市|自治区|特别行政区|地区|盟)", "");
                        if (!myLocation.equals(lastCity) && !TextUtils.isEmpty(myLocation)){
                            switchToMyLocation(WeatherActivity.this,myLocation);
                        }
                    } else if (amapLocation.getErrorCode() == 12) {
                        Toast.makeText(WeatherActivity.this, "缺少定位权限，无法使用定位功能", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WeatherActivity.this, "无法定位当前的位置", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }
    /**
     * 从网络获取天气数据
     * @param cityName 请求的城市名称
     * @param aheadAction 获取天气数据之前执行的事件
     */
    private void getWeatherByNetwork(String cityName,Action0 aheadAction) {
        NetworkRequest.getWeatherWithName(cityName)
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(aheadAction)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(weatherResponse -> weatherResponse.getWeathers())
                .flatMap(weathers -> {
                    if (weathers != null) {
                        return Observable.from(weathers);
                    } else
                        return Observable.error(new Exception(Constants.OUT_OF_NETWORK));
                })
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(WeatherActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Weather weather) {
                        if (weather.status.equals("ok")) {
                            pureWeatherDB.saveWeather(weather);
                            showWeather();
                        }
                    }
                });
    }
    /**
     * 弹出切换城市信息的提示框
     * @param context 显示环境
     */
    private void switchToMyLocation(Context context,String myLocation){
        String title = "智能定位";
        String body = new StringBuilder().append("是否切换到您当前所在地：").append(myLocation).append("？").toString();
        String positiveText = "切换";
        String negativeText = "不，谢谢";
        new AlertDialog.Builder(context).setTitle(title)
                .setMessage(body)
                .setPositiveButton(positiveText, (dialog, which) -> {
                    showProgressDialog("切换中...");
                    editor.putString(Constants.LAST_CITY,myLocation).commit();
                    lastCity = myLocation;
                    getWeatherByNetwork(myLocation, () -> closeProgressDialog());
                })
                .setNegativeButton(negativeText, null)
                .setCancelable(false)
                .show();
    }

    /**
     * 更新 WeatherActivity 界面，同时发送更新widget的广播，处理通知栏是否显示，是否关闭自动更新服务
     */
    private void showWeather() {
        sendBroadcast(new Intent(Constants.ON_UPDATE_WIDGET_ALL));
        lastCity = pref.getString(Constants.LAST_CITY, "");
        Weather weather = pureWeatherDB.loadWeatherInfo(lastCity);
        /************************************** 处理通知显示 *************/
        boolean allowToShowNotice = pref.getBoolean(Constants.SHOW_NOTIFICATION, true);
        if (allowToShowNotice) {
            showNotification(weather);
        } else {
            closeNotification();
        }
        /************************************** 处理服务显示 *************/
        boolean allowToAutoUpdate = (pref.getInt(Constants.UPDATE_INTERVAL, 1) > 0);
        Intent intent = new Intent(WeatherActivity.this, ForegroundService.class);
        if (!allowToAutoUpdate){
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

    /**
     * 更新通知栏显示
     * @param weather 通知栏显示的天气数据
     */
    public void showNotification(Weather weather) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(WeatherActivity.this, WeatherActivity.class);
        PendingIntent pi = PendingIntent
                .getActivity(WeatherActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(WeatherActivity.this);
        Notification notification = builder
                .setContentIntent(pi)
                .setContentTitle(weather.basic.city)
                .setContentText(String.format("%s°C，%s",  weather.now.tmp,weather.now.cond.txt))
                .setSmallIcon(ImageCodeConverter.getWeatherIconResource(weather.now.cond.code, "notice"))
                .setOngoing(true)
                .build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
        notification.flags |= Notification.FLAG_NO_CLEAR;
        manager.notify(1, notification);
    }

    /**
     * 关闭通知栏显示
     */
    public void closeNotification() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
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

    /**
     * 显示对话框
     * @param message 对话框内容
     */
    private void showProgressDialog(String message) {
        // TODO Auto-generated method stub
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        // TODO Auto-generated method stub
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
