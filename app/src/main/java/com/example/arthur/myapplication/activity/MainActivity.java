package com.example.arthur.myapplication.activity;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arthur.myapplication.R;
import com.example.arthur.myapplication.httpUtils.NetworkRequest;
import com.example.arthur.myapplication.modle.ButtonClickedEvent;
import com.example.arthur.myapplication.modle.CharSetEvent;
import com.example.arthur.myapplication.modle.CityAdapter;
import com.example.arthur.myapplication.modle.PureWeatherDB;
import com.example.arthur.myapplication.modle.Region;
import com.example.arthur.myapplication.modle.TempWeatherAdapter;
import com.example.arthur.myapplication.modle.WeatherInfo;
import com.example.arthur.myapplication.utils.RxBus;

import java.util.ArrayList;
import java.util.List;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    public final static String[] months = new String[]{"Sunrise","Sunset","Humidity","Aqi","RainPos"};

    public final static String[] days = new String[]{"Mon", "Tue", "Wen", "Thu", "Fri", "Sat", "Sun",};

    private LineChartView chartTop;
    private ColumnChartView chartBottom;

    private LineChartData lineData;
    private ColumnChartData columnData;
    private Button testButton;
    private PureWeatherDB pureWeatherDB;
    private CollapsingToolbarLayout collapsingToolbar;
    private ListView searchResult;
    private List<String> dataList;
    private EditText searchInput;
    private RecyclerView mRecyclerView;
    private TempWeatherAdapter mAdapter;
    private CityAdapter cityAdapter;
    private CompositeSubscription allSubscription = new CompositeSubscription();
    private WeatherInfo weatherInfo;
    private List<Line> lineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pureWeatherDB = PureWeatherDB.getInstance(this);
        initRecycleView();
       // initCharView();
        allSubscription.add(RxBus.getInstance()
                .toObserverable(ButtonClickedEvent.class)
                .subscribe(this::onDataButtonClick));
    }

    private void initRecycleView() {
        List<WeatherInfo> weatherInfos = new ArrayList<>();

        weatherInfo = pureWeatherDB.mloadWeatherInfo("阳江");
        weatherInfos.add(weatherInfo);
        mAdapter = new TempWeatherAdapter(MainActivity.this,weatherInfos);
        mRecyclerView = ((RecyclerView) findViewById(R.id.main_activity_recycle_view));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }
    public void initCharView(){
        lineList = new ArrayList<>();
        List<PointValue> pointValues = new ArrayList<>();
        for (int i = 0; i < 7; ++i) {
            pointValues.add(new PointValue(i, 0));
        }//设置x轴的标签
        Line line = new Line(pointValues);
        line.setCubic(true);//设置线条圆滑过渡

        lineList.add(line);

        lineData = new LineChartData(lineList);
        chartTop = ((LineChartView) findViewById(R.id.data_char_view));
        chartTop.setLineChartData(lineData);
        // For build-up animation you have to disable viewport recalculation.
        chartTop.setViewportCalculationEnabled(false);
        chartTop.setZoomType(null);
    }
    private void onDataButtonClick(ButtonClickedEvent event){
        int numValues = event.getDataSize();
        List<AxisValue> axisValues_x = new ArrayList<>();
        List<AxisValue> axisValues_y = new ArrayList<>();

        for (int i = 0; i < numValues; ++i) {
            axisValues_x.add(new AxisValue(i).setLabel(event.getxLabel()[i]));
        }//设置x轴的标签
        int rowNum;
        int yMax;
        int yMin;
        if(event.getDataType().equals("MAX_TEMP") || event.getDataType().equals("MIN_TEMP")){
            rowNum = event.getMax() - event.getMin();
            yMax = event.getMax() + 1;
            yMin = event.getMin() - 1;
            for (int i = -1; i <= rowNum ; ++i) {
                axisValues_y.add(
                        new AxisValue(event.getMin() + i).setLabel(String.valueOf(event.getMin() + i)));
            }//设置y轴的标签
        }
        else {
            rowNum = 10;
            yMax = 101;
            yMin = 0;
            for (int i = 0; i < rowNum; ++i) {
                axisValues_y.add(
                        new AxisValue(i * 10).setLabel(String.valueOf(i * 10)));
            }//设置y轴的标签
        }

        lineData.setAxisXBottom(new Axis(axisValues_x).setHasLines(true));
        lineData.setAxisYLeft(new Axis(axisValues_y).setHasLines(true).setMaxLabelChars(3));

        // And set initial max viewport and current viewport- remember to set viewports after data.

        Viewport v = new Viewport(0, yMax, 6,yMin);//(x最小值，y最大值，x最大值，y最小值)
        chartTop.setMaximumViewport(v);
        chartTop.setCurrentViewport(v);

        chartTop.cancelDataAnimation();
        // Modify data targets
        lineList.get(0).setColor(event.getColor());
        for (int i=0; i<numValues; i++) {
            PointValue value = lineList.get(0).getValues().get(i);
            // Change target only for Y value.
            value.setTarget(value.getX(), event.getData().get(i));
        }
        // Start new data animation with 300ms duration;
        chartTop.startDataAnimation(300);
    }



    private Observable<List<Region>> getRegion(String superCode){
        return Observable
                .concat(getRegionByDB(superCode),getRegionByNetwork(superCode))
                .first(regions -> regions.size() > 0);
    }

    private Observable<List<Region>> getRegionByNetwork(String superCode){
        return NetworkRequest
                .getRegionWithCode(superCode)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(regions -> pureWeatherDB.saveRegions(regions));
    }

    private Observable<List<Region>> getRegionByDB(String superCode){
        return Observable.just(pureWeatherDB.loadRegions(superCode));
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


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }
}
