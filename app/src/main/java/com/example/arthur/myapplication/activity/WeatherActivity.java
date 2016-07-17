package com.example.arthur.myapplication.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.arthur.myapplication.R;
import com.example.arthur.myapplication.httpUtils.NetworkRequest;
import com.example.arthur.myapplication.modle.CharSetEvent;
import com.example.arthur.myapplication.modle.PureWeatherDB;
import com.example.arthur.myapplication.modle.WeatherInfo;
import com.example.arthur.myapplication.service.AutoUpdateService;
import com.example.arthur.myapplication.utils.RxBus;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2016/6/9.
 */
public class WeatherActivity extends AppCompatActivity {

    public final static String[] days = new String[]{"Mon", "Tue", "Wen", "Thu", "Fri", "Sat", "Sun",};
    private CollapsingToolbarLayout collapsingToolbar;
    private static Boolean isExit = false;

    private CompositeSubscription allSubscription = new CompositeSubscription();

    private TextView nowTemp;
    private TextView nowCond;
    private TextView sportSuggestion;
    private TextView sportSuggestionBrf;
    private TextView travelSuggestion;
    private TextView travelSuggestionBrf;
    private TextView carWashSuggestion;
    private TextView carWashSuggestionBrf;
    private TextView rainyPos;
    private TextView tempRange;
    private TextView humiValue;
    private ImageView toolbarBackground;
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private LineChartView chartTop;
    private SwipeRefreshLayout swipeRefreshLayout;

    private LineChartData lineData;
    private PureWeatherDB pureWeatherDB;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private Subscriber<List<WeatherInfo>> subscriber;
    private String lastCity = null;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_weather_activity_layout);

        initUtils();

        if(TextUtils.isEmpty(lastCity)){
            //没有lastCity，直接去搜索页面
            Toast.makeText(WeatherActivity.this, "还没选择城市，快选择一个吧~", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,SearchActivity.class);
            startActivity(intent);
            finish();
        }
        startService(new Intent(this, AutoUpdateService.class));
        initView();
        initToolbar();

        initNavigationView();
        onDataButtonClick(getColor(R.color.redPrimary));
        showWeather();
    }

    private void initUtils() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        lastCity = pref.getString("last_city", "");
        pureWeatherDB = PureWeatherDB.getInstance(this);
    }


    private void initToolbar() {
        mDrawerLayout = (DrawerLayout)findViewById(R.id.weather_drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.weather_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar =(CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

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

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        //下拉刷新 .这里的Subscriber必须用匿名的，如果用事先定义好的观察者只能响应一次，待研究
        swipeRefreshLayout.setOnRefreshListener(() -> {
            lastCity = pref.getString("last_city","");
            NetworkRequest
                    .getWeatherWithName(lastCity)
                    .subscribeOn(Schedulers.newThread())
                    .flatMap(weatherResponse -> Observable.just(weatherResponse.getWeatherInfos()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(weatherInfos -> {
                        pureWeatherDB.saveWeather(weatherInfos.get(0));
                        showWeather();
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(WeatherActivity.this, "刷新完成~~", Toast.LENGTH_SHORT).show();
                    });
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.indigoPrimary, R.color.greenPrimary, R.color.redPrimary);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initView(){

        sportSuggestion = (TextView)findViewById(R.id.sport_suggestion);
        sportSuggestionBrf = (TextView) findViewById(R.id.sport_suggestion_brf);
        travelSuggestion = (TextView)findViewById(R.id.travel_suggestion);
        travelSuggestionBrf = (TextView) findViewById(R.id.travel_suggestion_brf);
        carWashSuggestion = (TextView)findViewById(R.id.car_wash_suggestion);
        carWashSuggestionBrf = (TextView) findViewById(R.id.car_wash_suggestion_brf);
        nowTemp = (TextView)findViewById(R.id.now_temp);
        nowCond = (TextView)findViewById(R.id.now_cond);
        humiValue = (TextView)findViewById(R.id.humidity);
        rainyPos = (TextView)findViewById(R.id.rainy_pos);
        tempRange = (TextView)findViewById(R.id.temp_range);


        toolbarBackground = (ImageView) findViewById(R.id.image_view);
        chartTop = (LineChartView) findViewById(R.id.chart_top);
        generateInitialLineData();

        Button btn1 = (Button) findViewById(R.id.max_temp_btn);
        Button btn2 = (Button) findViewById(R.id.min_temp_btn);
        Button btn3 = (Button) findViewById(R.id.humidity_btn);
        Button btn4 = (Button) findViewById(R.id.rainy_pos_btn);

        Glide.with(this)
                .load(R.drawable.beijing)
                .centerCrop()
                .crossFade()
                .into(toolbarBackground);

        RxView.clicks(btn1)
                .throttleFirst(500, TimeUnit.MICROSECONDS)
                .subscribe(aVoid -> {
                    RxBus.getInstance().post(new CharSetEvent(getColor(R.color.redPrimary)));
                });
        RxView.clicks(btn2)
                .throttleFirst(300, TimeUnit.MICROSECONDS)
                .subscribe(aVoid -> {
                    RxBus.getInstance().post(new CharSetEvent(getColor(R.color.orangePrimary)));
                });
        RxView.clicks(btn3)
                .throttleFirst(300, TimeUnit.MICROSECONDS)
                .subscribe(aVoid -> {
                    RxBus.getInstance().post(new CharSetEvent(getColor(R.color.greenPrimary)));
                });
        RxView.clicks(btn4)
                .throttleFirst(500, TimeUnit.MICROSECONDS)
                .subscribe(aVoid -> {
                    RxBus.getInstance().post(new CharSetEvent(getColor(R.color.indigoPrimary)));
                });
        allSubscription.add(RxBus.getInstance()
                .toObserverable(CharSetEvent.class)
                .subscribe(this::onDataButtonClick));
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initNavigationView(){
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_layout);
        final LinearLayout headerBackground = (LinearLayout) headerLayout.findViewById(R.id.nav_header_linear_layout);
        navigationView.setItemTextColor(ColorStateList.valueOf(getColor(R.color.textColor)));
        Glide.with(this)
                .load(R.drawable.beijing_2)
                .centerCrop()
                .crossFade()
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        headerBackground.setBackground(resource);
                    }
                });
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case (R.id.nav_my_list):
                    Intent intent = new Intent(WeatherActivity.this, CityManagerActivity.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawer(navigationView);
                    break;
                case (R.id.nav_add_city):
                    intent = new Intent(WeatherActivity.this, SearchActivity.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawer(navigationView);
                    break;
                case R.id.nav_about_me:
                    RxBus.getInstance().post(new CharSetEvent(getColor(R.color.orangePrimary)));
                    break;
            }
            return false;
        });
    }

    private void onDataButtonClick(CharSetEvent charSetEvent){
        chartTop.cancelDataAnimation();
        // Modify data targets
        Line line = lineData.getLines().get(0);// For this example there is always only one line.
        line.setColor(charSetEvent.getColor());
        for (PointValue value : line.getValues()) {
            // Change target only for Y value.
            value.setTarget(value.getX(), (float) Math.random() * 50);
        }
        // Start new data animation with 300ms duration;
        chartTop.startDataAnimation(300);
    }

    private void onDataButtonClick(int color){
        chartTop.cancelDataAnimation();
        // Modify data targets
        Line line = lineData.getLines().get(0);// For this example there is always only one line.
        line.setColor(color);
        for (PointValue value : line.getValues()) {
            // Change target only for Y value.
            value.setTarget(value.getX(), (float) Math.random() * 50);
        }
        // Start new data animation with 300ms duration;
        chartTop.startDataAnimation(300);
    }

    private void generateInitialLineData() {
        int numValues = 7;

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<AxisValue> axisValues_y = new ArrayList<AxisValue>();

        List<PointValue> values = new ArrayList<PointValue>();
        for (int i = 0; i < numValues; ++i) {
            values.add(new PointValue(i, 0));
            axisValues.add(new AxisValue(i).setLabel(days[i]));
        }
        for (int i = 3; i <9; ++i) {
            axisValues_y.add(new AxisValue(i*5).setLabel(String.valueOf(i*5)));
        }
        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);

        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        lineData = new LineChartData(lines);
        lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        lineData.setAxisYLeft(new Axis(axisValues_y).setHasLines(true).setMaxLabelChars(3));

        chartTop.setLineChartData(lineData);

        // For build-up animation you have to disable viewport recalculation.
        chartTop.setViewportCalculationEnabled(false);

        // And set initial max viewport and current viewport- remember to set viewports after data.
        Viewport v = new Viewport(0, 60, 6, 0);
        chartTop.setMaximumViewport(v);
        chartTop.setCurrentViewport(v);
        chartTop.setZoomType(null);
    }



    private void startService(){
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
    private void stopService(){
        Intent intent = new Intent(this, AutoUpdateService.class);
        stopService(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
        lastCity = pref.getString("last_city", "");
        showWeather();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (allSubscription != null && !allSubscription.isUnsubscribed())
            allSubscription.unsubscribe();
    }

    private void showWeather() {
        // TODO Auto-generated method stub

//        syncButton.setText("上次同步：" + pref.getString("sync_time", ""));

        Cursor cursor = pureWeatherDB.loadWeatherInfo(lastCity);
        if(cursor.moveToNext()){
            collapsingToolbar.setTitle(cursor.getString(cursor.getColumnIndex("city_name")));

            sportSuggestion.setText(cursor.getString(cursor.getColumnIndex("sport_suggestion")));
            sportSuggestionBrf.setText(cursor.getString(cursor.getColumnIndex("sport_suggestion_brf")));
            travelSuggestion.setText(cursor.getString(cursor.getColumnIndex("travel_suggestion")));
            travelSuggestionBrf.setText(cursor.getString(cursor.getColumnIndex("travel_suggestion_brf")));
            carWashSuggestion.setText(cursor.getString(cursor.getColumnIndex("car_wash_suggestion")));
            carWashSuggestionBrf.setText(cursor.getString(cursor.getColumnIndex("car_wash_suggestion_brf")));

            nowTemp.setText(cursor.getString(cursor.getColumnIndex("now_temp"))+"°");

            humiValue.setText(cursor.getString(cursor.getColumnIndex("humi_value"))+"%");

            String sunCond = new StringBuilder().append(cursor.getString(cursor.getColumnIndex("now_cond"))).toString();
            nowCond.setText(sunCond);
            //forecastDate.setText(pref.getString("forecast_date", ""));
            rainyPos.setText(cursor.getString(cursor.getColumnIndex("rainy_pos"))+"%");

            String range = new StringBuilder().append(cursor.getString(cursor.getColumnIndex("max_temp"))).append("°C ~ ")
                    .append(cursor.getString(cursor.getColumnIndex("min_temp"))).append("°C").toString();
            tempRange.setText(range);

        }
        else{
            //读取不到数据库的数据，做处理
        }
        if(cursor != null){
            cursor.close();
        }
        //closeMyDialog();//加载完成，关闭对话框

    }


//    @Override
//    public void onClick(View v) {
//        // TODO Auto-generated method stub
//        switch(v.getId()){
//            case (R.id.sync_button):
////                syncButton.setText("同步中...");
////                createDownloadAddress(lastCity);
////                SimpleDateFormat    formatter    =   new SimpleDateFormat("MM月dd日 - HH:mm");
////                Date    curDate    =   new Date(System.currentTimeMillis());//获取当前时间
////                String    syncTime    =    formatter.format(curDate);
////                editor.putString("sync_time", syncTime);
////                editor.commit();
//                break;
//            case (R.id.left_menu_switch):
////                if(syncSwitch.isChecked()){
////                    editor.putBoolean("sync_switch",true);
////                    editor.commit();
////                }
////                else {
////                    editor.putBoolean("sync_switch",false);
////                    editor.commit();
////                }
////                break;
//            default:
//                break;
//        }
//
//    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode == KeyEvent.KEYCODE_BACK){
            exitBy2Click(); //调用双击退出函数
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
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        }
        else {
            finish();
            System.exit(0);
        }
    }

}
