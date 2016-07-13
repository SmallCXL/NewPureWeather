package com.example.arthur.myapplication.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arthur.myapplication.R;
import com.example.arthur.myapplication.httpUtils.NetworkRequest;
import com.example.arthur.myapplication.modle.City;
import com.example.arthur.myapplication.modle.County;
import com.example.arthur.myapplication.modle.Province;
import com.example.arthur.myapplication.modle.PureWeatherDB;
import com.example.arthur.myapplication.modle.Region;
import com.example.arthur.myapplication.modle.WeatherInfo;
import com.example.arthur.myapplication.utils.HttpCallbackListener;
import com.example.arthur.myapplication.utils.HttpUtils;
import com.example.arthur.myapplication.utils.ResponseHandleUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/6/10.
 */
public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int PROVINCE = 0;
    public static final int CITY = 1;
    public static final int COUNTY = 2;
    public static final int SEARCH = 3;

    private ProgressDialog progressDialog;
    private TextView resultText;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private PureWeatherDB pureWeatherDB;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    /*
     * 数据列表
     */
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private List<Region> regionList = new ArrayList<>();
    /*
     * 选中的内容
     */
    private Province seletedProvince;
    private City seletedCity;
    private Region seletedRegion;
    private String seletedResult;
    private String lastCity;
    private int currentLevel = PROVINCE;

    private EditText inputName;
    private Button searchCity;
    private Toolbar toolbar;

    private static final String SEARCH_FAIL = "找不到该城市，请重新搜索...";

    private WeatherInfo tempInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_city_activity);

        init();
        final String provinceSuperId = "";//Province为最顶层区域，不需要superId

        getRegion(provinceSuperId);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
                currentLevel = getNowLevel();
                if (currentLevel == CITY || currentLevel == PROVINCE) {
//                    seletedProvince = provinceList.get(index);
//                    queryCities();
                    seletedRegion = regionList.get(index);
                    getRegion(seletedRegion.getCode());
                } else if (currentLevel == COUNTY) {
                    lastCity = regionList.get(index).getName();
                    getWeatherByNetwork(lastCity)
                            .subscribe(new Subscriber<WeatherInfo>() {
                                @Override
                                public void onCompleted() {
                                    goToWeatherActivity();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(SearchActivity.this, "下载天气数据失败...请重试！", Toast.LENGTH_SHORT).show();
                                    closeProgressDialog();
                                }

                                @Override
                                public void onNext(WeatherInfo weatherInfo) {
                                    pureWeatherDB.saveWeather(weatherInfo);
                                }
                            });
                } else if (currentLevel == SEARCH) {
                    seletedResult = dataList.get(index);
                    if (!seletedResult.equals(SEARCH_FAIL)) {
                        pureWeatherDB.saveWeather(tempInfo);
                        editor.putString("last_city", seletedResult);
                        editor.commit();
                        Intent intent = new Intent(SearchActivity.this, WeatherActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
//                else if(currentLevel == CITY){
////                    seletedCity = cityList.get(index);
////                    queryCounties();
//                    seletedRegion = regionList.get(index);
//                    getRegion(seletedRegion.getCode());
//                }
//                else if(currentLevel == COUNTY){
//
//                    lastCity = dataList.get(index);
//                    queryFromServer(lastCity, "last_city");
//                }
//                else if (currentLevel == SEARCH){
//                    seletedResult = dataList.get(index);
//                    if((!seletedResult.equals(SEARCH_FAIL) && (!TextUtils.isEmpty(seletedResult)))){
//                        //同时将该天气信息保存到数据库中，以供main界面显示
//                        pureWeatherDB.saveWeather( httpResponse);
//                        //该方法返回一个boolean，用于确认是否保存成功，目前未使用
//                        editor.putString("last_city", seletedResult);
//                        editor.commit();
//
//                        Intent intent = new Intent(SearchActivity.this,WeatherActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                }
            }
        });
    }

    private void init() {
        seletedProvince = null;
        seletedCity = null;

        listView = (ListView) findViewById(R.id.search_result_list_view);
        resultText = (TextView) findViewById(R.id.search_result);

        inputName = (EditText) findViewById(R.id.input_city_name);

        searchCity = (Button) findViewById(R.id.search_city);
        searchCity.setOnClickListener(this);

        adapter = new ArrayAdapter<>(this, R.layout.search_city_listview, dataList);
        listView.setAdapter(adapter);

        pureWeatherDB = PureWeatherDB.getInstance(this);
        editor = PreferenceManager.getDefaultSharedPreferences(SearchActivity.this).edit();
        pref = PreferenceManager.getDefaultSharedPreferences(SearchActivity.this);
        lastCity = pref.getString("last_city", "");

        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setTitle("搜索城市");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void goToWeatherActivity() {
        editor.putString("last_city", lastCity);
        editor.commit();
        closeProgressDialog();
        Intent intent = new Intent(SearchActivity.this, WeatherActivity.class);
        startActivity(intent);
        finish();
    }

    private int getNowLevel() {
        if (seletedRegion == null)
            return PROVINCE;
        switch (seletedRegion.getCode().length()) {
            case 2:
                return CITY;
            case 4:
                return COUNTY;
            default:
                return -1;
        }


    }

    private void getRegion(final String superCode) {
        Observable
                .concat(getRegionByDB(superCode), getRegionByNetwork(superCode))
                .first(regions -> regions.size() > 0)
                .subscribe(regions -> {
                    dataList.clear();
                    for (Region r : regions) {
                        dataList.add(r.getName());
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private Observable<List<Region>> getRegionByNetwork(final String superCode) {
        return NetworkRequest
                .getRegionWithCode(superCode)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(regions -> {
                    pureWeatherDB.saveRegions(regions);
                    regionList.clear();
                    regionList = regions;
                });
    }

    private Observable<List<Region>> getRegionByDB(final String superCode) {
        return Observable
                .just(pureWeatherDB.loadRegions(superCode))
                .doOnNext(regions -> {
                    regionList.clear();
                    regionList = regions;
                });
    }

    private Observable<WeatherInfo> getWeatherByNetwork(String cityName) {
        return NetworkRequest.getWeatherWithName(cityName)
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(this::showProgressDialog)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(weatherResponse -> Observable.from(weatherResponse.getWeatherInfos()));
    }

    private void queryProvinces() {
        // TODO Auto-generated method stub
        provinceList = pureWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province p : provinceList) {
                dataList.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            StringBuilder result = new StringBuilder().append("请选择省份：");
            resultText.setText(result.toString());
            currentLevel = PROVINCE;
        }//end if
        else {
            queryFromServer(null, "province");
        }
    }

    private void queryCities() {
        // TODO Auto-generated method stub
        cityList = pureWeatherDB.loadCities(seletedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City c : cityList) {
                dataList.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            StringBuilder result = new StringBuilder().append("请选择城市： ").append(seletedProvince.getProvinceName());
            resultText.setText(result.toString());
            currentLevel = CITY;
        } else {
            queryFromServer(seletedProvince.getProvinceCode(), "city");
        }
    }

    private void queryCounties() {
        // TODO Auto-generated method stub
        countyList = pureWeatherDB.loadCounties(seletedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County c : countyList) {
                dataList.add(c.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            StringBuilder title = new StringBuilder().append("请选择县城： ").append(seletedProvince.getProvinceName())
                    .append(" - ").append(seletedCity.getCityName());
            resultText.setText(title.toString());
            currentLevel = COUNTY;
        } else {
            queryFromServer(seletedCity.getCityCode(), "county");
        }
    }

    /*
     *  在数据库中找不到相关数据或者第一次加载，则调用queryFromServer方法从网络上下载城市数据
     */
    private void queryFromServer(final String code, final String type) {
        // TODO Auto-generated method stub
        String address;
        switch (type) {
            case "city":
            case "county":
                address = new StringBuilder().append("http://www.weather.com.cn/data/list3/city").
                        append(code).append(".xml").toString();
                break;
            case "province":
                address = new StringBuilder().append("http://www.weather.com.cn/data/list3/city.xml").toString();
                break;
            case "last_city":
            case "search":
                address = new StringBuilder().append("https://api.heweather.com/x3/weather?city=")
                        .append(code).append("&key=37fa5d4ad1ea4d5da9f37e75732fb2e7").toString();
                break;
            default:
                return;
        }

        showProgressDialog();
        HttpUtils.sendHttpRequest(address, new HttpCallbackListener() {

            @Override
            public void onFinish(final String response) {
                // TODO Auto-generated method stub
                boolean result = false;
                switch (type) {
                    case "province":
                        result = ResponseHandleUtils.handleProvincesResponse(pureWeatherDB, response);
                        break;
                    case "city":
                        result = ResponseHandleUtils.handleCitiesResponse(pureWeatherDB, response, seletedProvince.getId());
                        break;
                    case "county":
                        result = ResponseHandleUtils.handleCountiesResponse(pureWeatherDB, response, seletedCity.getId());
                        break;
                    case "last_city":
                        result = pureWeatherDB.saveWeather(response);
                        break;
                    case "search":
                        result = ResponseHandleUtils.handleWeatherResponse(SearchActivity.this, response);
                        break;
                }

                //以上已经下载完毕，以下重新调用查询数据库的方法
                if (result) {
                    //queryProvince一类的方法中涉及到UI操作，而onFinish方法是在子线程中执行的。
                    //因此，需要在调用queryProvince之前，将代码放入UI线程中执行
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            switch (type) {
                                case "province":
                                    queryProvinces();
                                    break;
                                case "city":
                                    queryCities();
                                    break;
                                case "county":
                                    queryCounties();
                                    break;
                                case "last_city":
                                    editor.putString("last_city", code);
                                    editor.commit();
//                                    Toast.makeText(SearchActivity.this, "数据解析成功！", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SearchActivity.this, WeatherActivity.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                case "search":
//                                    httpResponse = response;
                                    dataList.clear();
                                    dataList.add(pref.getString("city_name", ""));
                                    adapter.notifyDataSetChanged();

                                    break;
                            }
                            closeProgressDialog();//下载完成，关闭提示框
                        }//end run
                    });//end runOnUiThread
                }//end if(result)
                else if (result == false && type.equals("search")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            //查询的结果不合法
                            dataList.clear();
                            dataList.add(SEARCH_FAIL);
                            adapter.notifyDataSetChanged();
                            closeProgressDialog();
                        }
                    });

                }
                //closeProgressDialog();
            }//end onFinish

            @Override
            public void onError(Exception e) {
                // TODO Auto-generated method stub
                //进入子线程中进行UI操作
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        closeProgressDialog();
                        Toast.makeText(SearchActivity.this, "加载网络数据失败!请重试...", Toast.LENGTH_SHORT).show();
                    }//end run
                });//end runOnUiThread
            }//end onError

        });//end HttpUtil.sendHttpRequest

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
        switch (currentLevel) {
            case COUNTY:
                queryCities();
                listView.setSelection(dataList.indexOf(seletedCity.getCityName()));
                break;
            case CITY:
                queryProvinces();
                listView.setSelection(dataList.indexOf(seletedProvince.getProvinceName()));
                break;
            case PROVINCE:
                if (TextUtils.isEmpty(lastCity)) {
                    finish();
                    System.exit(0);
                }
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
                finish();
                break;
            case SEARCH:
                resultText.setText("请选择省份：");
                queryProvinces();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.search_city):
                String input = inputName.getText().toString();
                if (!TextUtils.isEmpty(input)) {
                    getWeatherByNetwork(input)
                            .doOnTerminate(() -> {
                                inputName.setText("");
                                resultText.setText("搜索结果：");
                                adapter.notifyDataSetChanged();
                                currentLevel = SEARCH;
                                closeProgressDialog();
                            })
                            .subscribe(new Subscriber<WeatherInfo>() {
                                @Override
                                public void onCompleted() {
//                                    dataList.clear();
//                                    dataList.add(SEARCH_FAIL);
//                                    Toast.makeText(SearchActivity.this, "加载完成...", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(WeatherInfo weatherInfo) {
                                    String item;
                                    if (weatherInfo.getStatus().equals("ok")) {
                                        item = weatherInfo.getBasic().getCity();
                                        tempInfo = weatherInfo;
                                    } else {
                                        item = SEARCH_FAIL;
                                    }
                                    dataList.clear();
                                    dataList.add(item);
                                }
                            });
//                    queryFromServer(input, "search");
                }
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
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences pref = PreferenceManager.
                getDefaultSharedPreferences(this);
        lastCity = pref.getString("last_city", "");

    }

}
