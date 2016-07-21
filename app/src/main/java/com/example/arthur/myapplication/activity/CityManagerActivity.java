package com.example.arthur.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.arthur.myapplication.R;
import com.example.arthur.myapplication.modle.BriefWeatherAdapter;
import com.example.arthur.myapplication.modle.PureWeatherDB;
import com.example.arthur.myapplication.modle.BriefWeatherInfo;
import com.example.arthur.myapplication.modle.WeatherAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/9.
 */
public class CityManagerActivity extends AppCompatActivity {

    private List<BriefWeatherInfo> briefWeatherInfos;
    private List<BriefWeatherInfo> dataList = new ArrayList<>();
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


//        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
//                                    long arg3) {
//                // TODO Auto-generated method stub
//                lastCity = briefWeatherInfos.get(index).getCityName();
//                editor.putString("last_city", lastCity);
//                editor.commit();
//
//                Intent intent = new Intent(CityManagerActivity.this, WeatherActivity.class);
//                startActivity(intent);
//                finish();
//            }
//
//        });
//        ItemOnLongClick1();
    }

    private void init() {
        initData();
        initToolbarLayout();
        initRecyclerView();
    }
    private void initData(){
        pureWeatherDB = PureWeatherDB.getInstance(this);
        editor = PreferenceManager.getDefaultSharedPreferences(CityManagerActivity.this).edit();
        pref = PreferenceManager.getDefaultSharedPreferences(CityManagerActivity.this);
        lastCity = pref.getString("last_city", "");
        selectedIndex = -1;
    }
    private void initToolbarLayout(){
        toolbar = (Toolbar) findViewById(R.id.city_manager_toolbar);
        toolbar.setTitle("收藏列表");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        collapsingToolbar.setExpandedTitleColor(Color.parseColor("#00FFFFFF"));
//        Glide.with(this)
//                .load(R.drawable.beijing_2)
//                .fitCenter()
//                .crossFade()
//                .into(new SimpleTarget<GlideDrawable>() {
//                    @Override
//                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
//                        collapsingToolbar.setBackground(resource);
//                    }
//                });
    }
    private void initRecyclerView() {
        mRecyclerView = ((RecyclerView) findViewById(R.id.city_manager_recycler_view));
        briefWeatherAdapter = new BriefWeatherAdapter(CityManagerActivity.this, dataList);
        briefWeatherAdapter.setOnItemClickListener(new BriefWeatherAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                lastCity = briefWeatherInfos.get(position).getCityName();
                editor.putString("last_city", lastCity);
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
        lastCity = pref.getString("last_city", "");
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
        if (briefWeatherInfos.size() > 1) {
            if (lastCity.equals(cityName)) {
                //正在删除当前显示的城市
                if (lastCity.equals(briefWeatherInfos.get(0).getCityName())) {
                    //当前显示的城市位于列表顶端
                    lastCity = briefWeatherInfos.get(1).getCityName();
                } else {
                    lastCity = briefWeatherInfos.get(0).getCityName();
                }
            }
            //else lastCity不用变动

        }
        else {
            // 正在删除最后一个元素
            lastCity = "";
        }
        editor.putString("last_city", lastCity);
        editor.commit();

        pureWeatherDB.deleteWeatherInfo(cityName);
        //判断数据库是否有数据，如果有，则读取到cityList当中，并显示在ListView当中
        refreshRecyclerView(DELETE);
    }

    private void refreshRecyclerView(int action) {

        //判断数据库是否有数据，如果有，则读取到cityList当中，并显示在ListView当中
        briefWeatherInfos = pureWeatherDB.loadWeatherInfo();
        if (briefWeatherInfos.size() > 0) {
            dataList.clear();
            for (BriefWeatherInfo w : briefWeatherInfos) {
                dataList.add(w);
            }
            switch (action){
                case REFRESH:
                    briefWeatherAdapter.notifyDataSetChanged();
                    break;
                case DELETE:
//                    briefWeatherAdapter.notifyItemRemoved(selectedIndex);
//                    selectedIndex = -1;
                    briefWeatherAdapter.notifyDataSetChanged();
                    break;
            }
        }
        else {
            Intent intent = new Intent(CityManagerActivity.this, SearchActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // 长按菜单响应函数
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                deleteCity(briefWeatherInfos.get(selectedIndex).getCityName());
                // 添加操作
                // Toast.makeText(ListOnLongClickActivity.this, "添加", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
}
