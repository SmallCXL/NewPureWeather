package com.example.arthur.myapplication.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.arthur.myapplication.R;
import com.example.arthur.myapplication.httpUtils.NetworkRequest;
import com.example.arthur.myapplication.modle.CityAdapter;
import com.example.arthur.myapplication.modle.PureWeatherDB;
import com.example.arthur.myapplication.modle.Region;
import com.example.arthur.myapplication.modle.WeatherInfo;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int PROVINCE = 0;
    public static final int CITY = 1;
    public static final int COUNTY = 2;

    private final String ChinaCode = "";
    private static final String SEARCH_FAIL = "找不到该城市，请重新搜索...";

    private ProgressDialog progressDialog;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private PureWeatherDB pureWeatherDB;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    /*
     * 数据列表
     */
    private List<String> dataList = new ArrayList<>();
    private List<String> resultList = new ArrayList<>();

    private List<Region> regionList = new ArrayList<>();

    /*
     * 选中的内容
     */
    private Region selectedRegion;
    private String selectedResult;
    private String lastCity;
    private Region selectedProvince;
    private int currentLevel = PROVINCE;

    private EditText inputName;
    private Button searchCity;
    private Button clearData;
    private Toolbar toolbar;

    private WeatherInfo tempInfo;

    private RecyclerView mRecyclerView;
    private CityAdapter cityAdapter;

    private CollapsingToolbarLayout collapsingToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_search_city_activity_layout);
        init();
        getRegion(ChinaCode);
    }

    private void init() {
        initData();

        initToolbarLayout();
        initView();
        initRecyclerView();
    }

    private void initData() {
        selectedRegion = null;
        currentLevel = PROVINCE;
        pureWeatherDB = PureWeatherDB.getInstance(this);
        editor = PreferenceManager.getDefaultSharedPreferences(SearchActivity.this).edit();
        pref = PreferenceManager.getDefaultSharedPreferences(SearchActivity.this);
        lastCity = pref.getString("last_city", "");
    }

    private void initView() {
        inputName = (EditText) findViewById(R.id.search_input);
        searchCity = (Button) findViewById(R.id.search_city_btn);
        searchCity.setOnClickListener(this);
        clearData = ((Button) findViewById(R.id.clear_text_btn));
        clearData.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.search_result);
        adapter = new ArrayAdapter<>(this, R.layout.search_city_listview, resultList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {

                selectedResult = resultList.get(index);
                if (!selectedResult.equals(SEARCH_FAIL) && tempInfo != null) {
                    pureWeatherDB.saveWeather(tempInfo);
                    editor.putString("last_city", selectedResult);
                    editor.commit();
                    Intent intent = new Intent(SearchActivity.this, WeatherActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void initToolbarLayout() {
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbar = ((Toolbar) findViewById(R.id.weather_toolbar));

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar.setTitle("选择城市");
        collapsingToolbar.setExpandedTitleColor(Color.parseColor("#003F51B5"));
    }

    private void initRecyclerView() {
        mRecyclerView = ((RecyclerView) findViewById(R.id.search_city_activity_recycle_view));
        cityAdapter = new CityAdapter(SearchActivity.this, dataList);
        cityAdapter.setOnItemClickListener(new CityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectedRegion = regionList.get(position);
                switch (selectedRegion.getCode().length()) {
                    case 2:
                        getRegion(selectedRegion.getCode());
                        currentLevel = CITY;
                        selectedProvince = selectedRegion;
                        break;
                    case 4:
                        getRegion(selectedRegion.getCode());
                        currentLevel = COUNTY;
                        break;
                    case 6:
                        getWeatherByNetwork(selectedRegion.getName())
                                .doOnError(throwable ->
                                        Toast.makeText(SearchActivity.this, "网络不给力，请稍后重试...", Toast.LENGTH_SHORT).show())
                                .doOnTerminate(() -> closeProgressDialog())
                                .subscribe(new Subscriber<WeatherInfo>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Toast.makeText(SearchActivity.this, "网络不给力，请稍后重试...", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onNext(WeatherInfo weatherInfo) {
                                        if (weatherInfo != null) {
                                            switch ( weatherInfo.status) {
                                                case "ok":
                                                    pureWeatherDB.saveWeather(weatherInfo);
                                                    lastCity = selectedRegion.getName();
                                                    goToWeatherActivity();
                                                    break;
                                                case "unknown city":
                                                    Toast.makeText(SearchActivity.this, "暂时没有这个城市的信息...", Toast.LENGTH_SHORT).show();
                                                    break;
                                                case "no more requests":
                                                    Toast.makeText(SearchActivity.this, "免费的访问次数用完了...", Toast.LENGTH_SHORT).show();
                                                    break;
                                                case "anr":
                                                    Toast.makeText(SearchActivity.this, "服务器无响应，请稍候重试...", Toast.LENGTH_SHORT).show();
                                                    break;
                                                default:
                                                    Toast.makeText(SearchActivity.this, "网络不给力，请稍后重试...", Toast.LENGTH_SHORT).show();
                                                    break;
                                            }
                                        }
                                    }
                                });
                        break;
            }
        }
    });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(cityAdapter);
    }

    private void goToWeatherActivity() {
        editor.putString("last_city", lastCity);
        editor.commit();
        closeProgressDialog();
        Intent intent = new Intent(SearchActivity.this, WeatherActivity.class);
        startActivity(intent);
        finish();
    }

    private void getRegion(final String superCode) {
        Observable
                .concat(getRegionByDB(superCode), getRegionByNetwork(superCode))
                .first(regions -> regions.size() > 0)
                .flatMap(regions -> {
                    if (regions != null) {
                        dataList.clear();
                        regionList.clear();
                        regionList = regions;
                        return Observable.from(regions);
                    }
                    return Observable.error(new Throwable("解析数据失败"));
                })
                .map(region -> {
                    if (region != null)
                        return region.getName();
                    else
                        return Observable.error(new Throwable("解析数据失败"));
                })
                .doOnError(throwable ->
                                Toast.makeText(SearchActivity.this, throwable.getMessage().toString(), Toast.LENGTH_SHORT).show()
                )
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        cityAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
//                        Toast.makeText(SearchActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Object o) {
                        dataList.add((String)o);
                    }
                });
    }



    private Observable<List<Region>> getRegionByNetwork(final String superCode) {
        return NetworkRequest
                .getRegionWithCode(superCode)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(regions -> pureWeatherDB.saveRegions(regions));
    }

    private Observable<List<Region>> getRegionByDB(final String superCode) {
        return Observable.just(pureWeatherDB.loadRegions(superCode));
    }

    private Observable<WeatherInfo> getWeatherByNetwork(String cityName) {
        return NetworkRequest.getWeatherWithName(cityName)
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(this::showProgressDialog)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(weatherResponse -> {
                    if (weatherResponse != null){
                        return Observable.from(weatherResponse.getWeatherInfos());
                    }
                    else
                        return Observable.error(new Exception("网络连接失败"));
                });
    }

    /*
     * 显示下载进度对话框
     */
    private void showProgressDialog() {
        // TODO Auto-generated method stub
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在下载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /*
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        // TODO Auto-generated method stub
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /*
     * 重载返回事件响应，判断此时返回的界面
     */
    @Override
    public void onBackPressed() {
        if (selectedRegion == null) {
            if (TextUtils.isEmpty(lastCity)) {
                finish();
                System.exit(0);
            }
            goToWeatherActivity();
        } else {
            switch (currentLevel) {
                case COUNTY:
                    getRegion(selectedProvince.getCode());
                    currentLevel = CITY;
                    break;
                case CITY:
                    getRegion(ChinaCode);
                    currentLevel = PROVINCE;
                    break;
                case PROVINCE:
                    goToWeatherActivity();
                    break;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.search_city_btn):
                String input = inputName.getText().toString();
                if (!TextUtils.isEmpty(input)) {
                    getWeatherByNetwork(input)
                            .doOnTerminate(() -> {
                                adapter.notifyDataSetChanged();
                                closeProgressDialog();
                            })
                            .subscribe(new Subscriber<WeatherInfo>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(SearchActivity.this, "网络不给力，请稍候重试...", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNext(WeatherInfo weatherInfo) {
                                    String item;
                                    if (weatherInfo.status.equals("ok")) {
                                        item = weatherInfo.basic.city;
                                        tempInfo = weatherInfo;
                                    } else {
                                        item = SEARCH_FAIL;
                                    }
                                    resultList.clear();
                                    resultList.add(item);
                                }
                            });
                }
                break;
            case (R.id.clear_text_btn):
                inputName.setText("");
                resultList.clear();
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            if (TextUtils.isEmpty(lastCity)) {
                finish();
                System.exit(0);
            }
            goToWeatherActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        lastCity = pref.getString("last_city", "");
    }
}
