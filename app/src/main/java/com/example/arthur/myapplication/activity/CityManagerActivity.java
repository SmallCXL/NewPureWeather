package com.example.arthur.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.arthur.myapplication.R;
import com.example.arthur.myapplication.modle.PureWeatherDB;
import com.example.arthur.myapplication.modle.Weather;
import com.example.arthur.myapplication.modle.WeatherAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/9.
 */
public class CityManagerActivity extends AppCompatActivity{

    private ListView cityListView;
    private WeatherAdapter adapter;
    private List<Weather> cityList;;
    private List<Weather> dataList = new ArrayList<>();

    private String lastCity;
    private int seletedCity;
    private Toolbar toolbar;

    private PureWeatherDB pureWeatherDB;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_manager_layout);

        init();
        refreshListView();

        if(TextUtils.isEmpty(lastCity)){
            Intent intent = new Intent(CityManagerActivity.this,SearchActivity.class);
            startActivity(intent);
            finish();
        }

        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long arg3) {
                // TODO Auto-generated method stub
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(CityManagerActivity.this).edit();

                lastCity = cityList.get(index).getCityName();
                editor.putString("last_city", lastCity);
                editor.commit();

                Intent intent = new Intent(CityManagerActivity.this,WeatherActivity.class);
                startActivity(intent);
                finish();
            }

        });
        ItemOnLongClick1();
    }

    private void init(){
        cityListView = (ListView) findViewById(R.id.main_city_list_view);
        pureWeatherDB = PureWeatherDB.getInstance(this);

        adapter = new WeatherAdapter(CityManagerActivity.this, R.layout.new_city_manager_activity_listview_item, dataList);
        cityListView.setAdapter(adapter);

        editor = PreferenceManager.getDefaultSharedPreferences(CityManagerActivity.this).edit();
        pref = PreferenceManager.getDefaultSharedPreferences(CityManagerActivity.this);
        lastCity = pref.getString("last_city", "");

        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setTitle("管理列表");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onResume(){
        super.onResume();
        lastCity = pref.getString("last_city", "");
        refreshListView();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            Intent intent = new Intent(CityManagerActivity.this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this,WeatherActivity.class);
        startActivity(intent);
        finish();
    }

    public void deleteCity(String cityName){
        if(cityList.size() > 1){
            if(lastCity.equals(cityName)){
                //正在删除当前显示的城市
                if(lastCity.equals(cityList.get(0).getCityName())){
                    //当前显示的城市位于列表顶端
                    lastCity = cityList.get(1).getCityName();
                }
                else{
                    lastCity = cityList.get(0).getCityName();
                }
            }
        }
        else{
            // 正在删除最后一个元素
            lastCity = "";
        }
        editor.putString("last_city", lastCity);
        editor.commit();

        pureWeatherDB.deleteWeatherInfo(cityName);
        //判断数据库是否有数据，如果有，则读取到cityList当中，并显示在ListView当中
        refreshListView();
    }

    private void refreshListView(){

        //判断数据库是否有数据，如果有，则读取到cityList当中，并显示在ListView当中
        cityList = pureWeatherDB.loadWeatherInfo();
        if(cityList.size() > 0){
            dataList.clear();
            for(Weather w : cityList){
                dataList.add(w);
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void ItemOnLongClick1() {
        //注：setOnCreateContextMenuListener是与下面onContextItemSelected配套使用的
        cityListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, "删除");
                // menu.add(0, 1, 0, "收藏");
                // menu.add(0, 2, 0, "对比");
            }
        });
    }

    // 长按菜单响应函数
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        seletedCity = (int) info.id;// 这里的info.id对应的就是数据库中_id的值

        switch (item.getItemId()) {
            case 0:
                deleteCity(cityList.get(seletedCity).getCityName());
                // 添加操作
                // Toast.makeText(ListOnLongClickActivity.this, "添加", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
}
