package com.example.arthur.pureweather.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.arthur.pureweather.R;
import com.example.arthur.pureweather.constant.Constants;
import com.example.arthur.pureweather.httpUtils.NetworkRequest;
import com.example.arthur.pureweather.adapter.CityAdapter;
import com.example.arthur.pureweather.db.PureWeatherDB;
import com.example.arthur.pureweather.modle.Region;
import com.example.arthur.pureweather.modle.Weather;
import com.example.arthur.pureweather.utils.GaoDeService;
import com.example.arthur.pureweather.utils.MyImageLoader;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int PROVINCE = 0;
    public static final int CITY = 1;
    public static final int COUNTY = 2;

    private final String ChinaCode = "";

    private ProgressDialog progressDialog;
    private TextView searchResult;

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
    private RecyclerView mRecyclerView;

    private Weather tempInfo;
    private CityAdapter cityAdapter;

    private CardView locateCurrent;
    private CollapsingToolbarLayout collapsingToolbar;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private AMapLocationListener aMapLocationListener;
    private ImageView locateCurrentIcon;
    private final int PERMISSION_REQUEST_CODE = 0;
    private GaoDeService gaoDeService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);
        SysApplication.getInstance().addActivity(this);
        init();
        boolean need = pref.getBoolean(Constants.IS_NEED_TO_CHECK, true);
        if (need) {
            checkPermission();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        lastCity = pref.getString(Constants.LAST_CITY, "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gaoDeService != null){
            gaoDeService.onDestroy();
        }
        mRecyclerView.destroyDrawingCache();
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
    }

    private void init() {
        initData();
        initView();
        initGaoDeLocate();
        initRecyclerView();
    }

    private void initData() {
        selectedRegion = null;
        currentLevel = PROVINCE;
        pureWeatherDB = PureWeatherDB.getInstance(this);
        editor = PreferenceManager.getDefaultSharedPreferences(SearchActivity.this).edit();
        pref = PreferenceManager.getDefaultSharedPreferences(SearchActivity.this);
        lastCity = pref.getString(Constants.LAST_CITY, "");
        getRegion(ChinaCode);
    }

    private void initView() {
        inputName = (EditText) findViewById(R.id.search_input);
        searchCity = (Button) findViewById(R.id.search_city_btn);
        searchCity.setOnClickListener(this);
        clearData = ((Button) findViewById(R.id.clear_text_btn));
        clearData.setOnClickListener(this);
        searchResult = (TextView) findViewById(R.id.search_result);
        searchResult.setOnClickListener(this);

        locateCurrent = ((CardView) findViewById(R.id.search_city_my_location));
        locateCurrent.setOnClickListener(this);
        locateCurrentIcon = ((ImageView) findViewById(R.id.search_city_locate_image));
        MyImageLoader.load(this, R.drawable.my_location, locateCurrentIcon);
        /******************************** toolbar ********************************************/
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbar = ((Toolbar) findViewById(R.id.weather_toolbar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar.setTitle("选择省份");
        collapsingToolbar.setExpandedTitleColor(Color.parseColor("#00000000"));
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
                        collapsingToolbar.setTitle("选择地级市");
                        break;
                    case 4:
                        getRegion(selectedRegion.getCode());
                        currentLevel = COUNTY;
                        collapsingToolbar.setTitle("选择城市");
                        break;
                    case 6:
                        getWeatherByNetwork(selectedRegion.getName())
                                .doOnTerminate(() -> closeProgressDialog())
                                .subscribe(new Subscriber<Weather>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Toast.makeText(SearchActivity.this, Constants.OUT_OF_NETWORK, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onNext(Weather weather) {
                                        if (weather != null) {
                                            switch (weather.status) {
                                                case "ok":
                                                    pureWeatherDB.saveWeather(weather);
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
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(0);
    }

    private void initGaoDeLocate() {
        gaoDeService = GaoDeService.getInstance(SearchActivity.this);
        aMapLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                closeProgressDialog();
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        if (!TextUtils.isEmpty(amapLocation.getCity())){
                            lastCity = amapLocation.getCity().replaceAll("(?:省|市|自治区|特别行政区|地区|盟)", "");
                            getWeatherByNetwork(lastCity)
                                    .subscribe(new Subscriber<Weather>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Toast.makeText(SearchActivity.this, "定位失败，请稍候重试", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onNext(Weather weather) {
                                            if (weather.status.equals("ok")){
                                                pureWeatherDB.saveWeather(weather);
                                                editor.putString(Constants.MY_LOCATION, lastCity).commit();
                                                editor.putBoolean(Constants.IS_NEED_TO_CHECK, false).commit();
                                                goToWeatherActivity();
                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                            }
                                        }
                                    });
                        }
                    } else if (amapLocation.getErrorCode() == 12) {
                        Toast.makeText(SearchActivity.this, "缺少定位权限，无法使用定位功能", Toast.LENGTH_SHORT).show();
                        checkPermission();
                    } else {
                        Toast.makeText(SearchActivity.this, "自动定位失败，请手动选择城市", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }

    private void goToWeatherActivity() {
        editor.putString(Constants.LAST_CITY, lastCity).commit();
        closeProgressDialog();
        mRecyclerView.destroyDrawingCache();
        startActivity(new Intent(SearchActivity.this, WeatherActivity.class));
        finish();
    }

    private void getRegion(final String superCode) {
        Observable
                .concat(getRegionByDB(superCode), getRegionByNetwork(superCode))
                .observeOn(AndroidSchedulers.mainThread())
                .first(regions -> regions.size() > 0)
                .flatMap(regions -> {
                    if (regions != null) {
                        dataList.clear();
                        regionList.clear();
                        regionList = regions;
                        return Observable.from(regions);
                    }
                    return Observable.error(new Throwable(Constants.PARSE_FAILURE));
                })
                .map(region -> {
                    if (region != null)
                        return region.getName();
                    else
                        return Observable.error(new Throwable(Constants.PARSE_FAILURE));
                })
                .doOnTerminate(() -> closeProgressDialog())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(SearchActivity.this, Constants.OUT_OF_NETWORK, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Object o) {
                        dataList.add((String) o);
                        cityAdapter.notifyDataSetChanged();
                        mRecyclerView.smoothScrollToPosition(0);
                    }
                });
    }

    private Observable<List<Region>> getRegionByNetwork(final String superCode) {
        return NetworkRequest
                .getRegionWithCode(superCode)
                .subscribeOn(Schedulers.newThread())//事件产生线程为新建的线程
                .doOnSubscribe(() -> showProgressDialog("正在加载城市列表..."))
                .subscribeOn(AndroidSchedulers.mainThread())//显示对话框的线程为主线程
                .doOnNext(regions -> {
                    if (regions != null) {
                        pureWeatherDB.saveRegions(regions);
                    }
                });
    }

    private Observable<List<Region>> getRegionByDB(final String superCode) {
        return Observable.just(pureWeatherDB.loadRegions(superCode));
    }

    private Observable<Weather> getWeatherByNetwork(String cityName) {
        return NetworkRequest.getWeatherWithName(cityName)
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(() -> showProgressDialog("正在加载天气数据..."))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(weatherResponse -> weatherResponse.getWeathers())
                .flatMap(weathers -> {
                    if (weathers != null) {
                        return Observable.from(weathers);
                    } else
                        return Observable.error(new Exception(Constants.OUT_OF_NETWORK));
                });
    }

    /*
     * 显示下载进度对话框
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
                SysApplication.getInstance().exit();
            }
            goToWeatherActivity();
        } else {
            switch (currentLevel) {
                case COUNTY:
                    getRegion(selectedProvince.getCode());
                    currentLevel = CITY;
                    collapsingToolbar.setTitle("选择地级市");
                    break;
                case CITY:
                    getRegion(ChinaCode);
                    currentLevel = PROVINCE;
                    collapsingToolbar.setTitle("选择省份");
                    break;
                case PROVINCE:
                    goToWeatherActivity();
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.search_city_btn):
                String input = inputName.getText().toString();
                if (!TextUtils.isEmpty(input)) {
                    getWeatherByNetwork(input)
                            .doOnTerminate(() -> closeProgressDialog())
                            .subscribe(new Subscriber<Weather>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(SearchActivity.this, Constants.OUT_OF_NETWORK, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNext(Weather weather) {
                                    String item;
                                    if (weather.status.equals("ok")) {
                                        item = weather.basic.city;
                                        tempInfo = weather;
                                    } else {
                                        item = Constants.SEARCH_FAIL;
                                    }
                                    selectedResult = item;
                                    searchResult.setText(item);
                                }
                            });
                }
                break;
            case (R.id.clear_text_btn):
                inputName.setText("");
                resultList.clear();
                searchResult.setText("");
                tempInfo = null;
                break;
            case (R.id.search_result):
                if ((selectedResult != null) && !selectedResult.equals(Constants.SEARCH_FAIL) && (tempInfo != null)) {
                    pureWeatherDB.saveWeather(tempInfo);
                    lastCity = selectedResult;
                    goToWeatherActivity();
                }
                break;
            case (R.id.search_city_my_location):
                showProgressDialog("正在定位中...");
                gaoDeService
                        .setOnLocationChangeListener(aMapLocationListener)
                        .startLocation();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            if (TextUtils.isEmpty(lastCity)) {
                SysApplication.getInstance().exit();
            }
            goToWeatherActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mRecyclerView.destroyDrawingCache();
    }

    /******************************
     * checkPermission my location
     *****************************/
    public void checkMyLocation() {
        String myLocation = pref.getString(Constants.MY_LOCATION, "");
        if (TextUtils.isEmpty(myLocation)) {
            gaoDeService
                    .setOnLocationChangeListener(aMapLocationListener)
                    .startLocation();
            showProgressDialog("正在定位中...");
        }
    }

    /******************************
     * checkPermission permission
     *****************************/
    public void checkPermission() {
        List<String> needRequestPermissionList = findDeniedPermissions(Constants.NEEDED_PERMISSION);
        if (null != needRequestPermissionList
                && needRequestPermissionList.size() > 0) {
            ActivityCompat.requestPermissions(SearchActivity.this,
                    needRequestPermissionList.toArray(
                            new String[needRequestPermissionList.size()]),
                    PERMISSION_REQUEST_CODE);
        }
    }

    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissionList = new ArrayList<>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(SearchActivity.this,
                    perm) != PackageManager.PERMISSION_GRANTED) {
                needRequestPermissionList.add(perm);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        SearchActivity.this, perm)) {
                    needRequestPermissionList.add(perm);
                }
            }
        }
        return needRequestPermissionList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                //缺少权限，弹出提示框
                showMissingPermissionDialog();
            } else {
                checkMyLocation();
            }
        }
    }

    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;//缺少所需权限，返回false
            }
        }
        return true;
    }

    private void showMissingPermissionDialog() {
        new AlertDialog.Builder(SearchActivity.this)
                .setTitle(R.string.notifyTitle)
                .setMessage(R.string.notifyMsg)
                .setNegativeButton(R.string.cancel,
                        (dialog, which) -> {
                            editor.putBoolean(Constants.IS_NEED_TO_CHECK, true).commit();
                            SysApplication.getInstance().exit();
                        })
                .setPositiveButton(R.string.setting,
                        (dialog, which) -> {
                            editor.putBoolean(Constants.IS_NEED_TO_CHECK, true).commit();
                            startAppSettings();
                        })
                .setCancelable(false)
                .show();
    }

    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
        SysApplication.getInstance().exit();
    }
}
