package com.example.arthur.pureweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.arthur.pureweather.R;
import com.example.arthur.pureweather.adapter.BriefWeatherAdapter;
import com.example.arthur.pureweather.constant.MyString;
import com.example.arthur.pureweather.db.PureWeatherDB;
import com.example.arthur.pureweather.modle.BriefWeather;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/9.
 */
public class CityManagerActivity extends AppCompatActivity {

    private List<BriefWeather> briefWeathers;
    private List<BriefWeather> dataList = new LinkedList<>();
    final static private int DELETE = -1;
    final static private int REFRESH = 0;


    private String lastCity;
    private int selectedIndex;
    private Toolbar toolbar;

    private PureWeatherDB pureWeatherDB;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private RecyclerView mRecyclerView;
    private BriefWeatherAdapter briefWeatherAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_city_manager_layout);

        init();
        refreshRecyclerView(REFRESH);

        if (TextUtils.isEmpty(lastCity)) {
            Intent intent = new Intent(CityManagerActivity.this, SearchActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void init() {
        initData();
        initToolbarLayout();
        initRecyclerView();
    }

    private void initData() {
        pureWeatherDB = PureWeatherDB.getInstance(this);
        editor = PreferenceManager.getDefaultSharedPreferences(CityManagerActivity.this).edit();
        pref = PreferenceManager.getDefaultSharedPreferences(CityManagerActivity.this);
        lastCity = pref.getString(MyString.LAST_CITY, "");
        selectedIndex = -1;
    }

    private void initToolbarLayout() {
        toolbar = (Toolbar) findViewById(R.id.city_manager_toolbar);
        toolbar.setTitle("收藏列表");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initRecyclerView() {
        mRecyclerView = ((RecyclerView) findViewById(R.id.city_manager_recycler_view));
        briefWeatherAdapter = new BriefWeatherAdapter(CityManagerActivity.this, dataList);
        briefWeatherAdapter.setOnItemClickListener(new BriefWeatherAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                lastCity = briefWeathers.get(position).getCityName();
                editor.putString(MyString.LAST_CITY, lastCity);
                editor.commit();

                Intent intent = new Intent(CityManagerActivity.this, WeatherActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onItemLongClick(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, int position) {
                selectedIndex = position;
                menu.setHeaderTitle("选择操作");
                menu.add(0, 0, 0, "删除");
            }

        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(briefWeatherAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onResume() {
        super.onResume();
        lastCity = pref.getString(MyString.LAST_CITY, "");
        refreshRecyclerView(REFRESH);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(CityManagerActivity.this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, WeatherActivity.class);
        startActivity(intent);
        finish();
    }

    public void deleteCity(String cityName) {
        if (briefWeathers.size() > 1) {
            if (lastCity.equals(cityName)) {
                //正在删除当前显示的城市
                if (lastCity.equals(briefWeathers.get(0).getCityName())) {
                    //当前显示的城市位于列表顶端
                    lastCity = briefWeathers.get(1).getCityName();
                } else {
                    lastCity = briefWeathers.get(0).getCityName();
                }
            }
            //else lastCity不用变动
        } else {
            // 正在删除最后一个元素
            lastCity = "";
        }
        editor.putString(MyString.LAST_CITY, lastCity);
        editor.commit();

        pureWeatherDB.deleteWeatherInfo(cityName);
        //判断数据库是否有数据，如果有，则读取到cityList当中，并显示在ListView当中
        refreshRecyclerView(DELETE);
    }

    private void refreshRecyclerView(int action) {

        //判断数据库是否有数据，如果有，则读取到cityList当中，并显示在ListView当中
        briefWeathers = pureWeatherDB.loadBriefWeatherInfo();
        if (briefWeathers.size() > 0) {
            dataList.clear();
            for (BriefWeather w : briefWeathers) {
                dataList.add(w);
            }
            briefWeatherAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(CityManagerActivity.this,"没有收藏城市咯...选一个吧~",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CityManagerActivity.this, SearchActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // 长按菜单响应函数
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                deleteCity(briefWeathers.get(selectedIndex).getCityName());
                // 添加操作
                // Toast.makeText(ListOnLongClickActivity.this, "添加", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
}
