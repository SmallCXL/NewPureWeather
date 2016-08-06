package com.example.arthur.pureweather.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
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
import com.example.arthur.pureweather.constant.MyString;
import com.example.arthur.pureweather.httpUtils.NetworkRequest;
import com.example.arthur.pureweather.modle.Weather;
import com.example.arthur.pureweather.utils.CheckVersion;
import com.example.arthur.pureweather.utils.MyImageLoader;
import com.example.arthur.pureweather.db.PureWeatherDB;
import com.example.arthur.pureweather.adapter.WeatherAdapter;
import com.example.arthur.pureweather.utils.ImageCodeConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/6/9.
 */
public class WeatherActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private static Boolean isExit = false;

    private ImageView toolbarBackground;
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NavigationView navigationView;

    private PureWeatherDB pureWeatherDB;
    private SharedPreferences pref;

    private List<Weather> weathers;

    private String lastCity = null;
    private RecyclerView mRecyclerView;
    private WeatherAdapter mAdapter;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_weather_activity_layout);

        initUtils();

        if (TextUtils.isEmpty(lastCity)) {
            //没有lastCity，直接去搜索页面
            Toast.makeText(WeatherActivity.this, "还没选择城市，快选择一个吧~", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            finish();
        }
        init();
        showWeather();

        CheckVersion.autoCheck(WeatherActivity.this);
    }

    private void initUtils() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        lastCity = pref.getString(MyString.LAST_CITY, "");
        pureWeatherDB = PureWeatherDB.getInstance(this);
    }

    private void init() {
        initRecyclerView();
        initToolbar();
        initNavigationView();
        initSwipeRefreshLayout();
    }

    private void initToolbar() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.weather_drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.weather_toolbar);
        toolbarBackground = (ImageView) findViewById(R.id.image_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
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
        Weather weather = pureWeatherDB.mloadWeatherInfo(lastCity);
        weathers = new ArrayList<>();
        weathers.add(weather);
        mAdapter = new WeatherAdapter(WeatherActivity.this, weathers);
        mRecyclerView = ((RecyclerView) findViewById(R.id.weather_recycler_view));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initNavigationView() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_layout);
        final LinearLayout headerBackground = (LinearLayout) headerLayout.findViewById(R.id.nav_header_linear_layout);
        navigationView.setItemTextColor(ColorStateList.valueOf(getColor(R.color.textColor)));

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
                case R.id.nav_about_me:
                    CheckVersion.manualCheck(WeatherActivity.this);
                    break;
            }
            return false;
        });
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        //下拉刷新 .这里的Subscriber必须用匿名的，如果用事先定义好的观察者只能响应一次，待研究
        swipeRefreshLayout.setOnRefreshListener(() -> {
            lastCity = pref.getString(MyString.LAST_CITY, "");
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
                            Toast.makeText(WeatherActivity.this, "链接超时，请检查网络", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onResume() {
        super.onResume();
        lastCity = pref.getString(MyString.LAST_CITY, "");
        showWeather();
    }

    private void showWeather() {
        Weather weather = pureWeatherDB.mloadWeatherInfo(lastCity);
        weathers.clear();
        weathers.add(weather);
        mAdapter.notifyDataSetChanged();
        if (weather != null) {
            MyImageLoader.load(WeatherActivity.this,
                    ImageCodeConverter.getMainBackgroundResource(weather.now.cond.code), toolbarBackground);
            collapsingToolbar.setTitle(weather.basic.city);
        }

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
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次返回键将退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器，取消掉刚才执行的任务
        } else {
            finish();
            System.exit(0);
        }
    }
}
