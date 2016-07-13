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
import com.example.arthur.myapplication.modle.PureWeatherDB;
import com.example.arthur.myapplication.modle.Region;

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
    private MyRecycleViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        collapsingToolbar =(CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbar = ((Toolbar) findViewById(R.id.weather_toolbar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar.setTitle("选择城市");
        collapsingToolbar.setExpandedTitleColor(Color.parseColor("#003F51B5"));

        dataList = new ArrayList<>();
        searchResult = ((ListView) findViewById(R.id.search_result));
        ArrayAdapter<String> adapter = new ArrayAdapter(this,R.layout.search_city_listview,dataList);
        searchResult.setAdapter(adapter);
        searchInput = ((EditText) findViewById(R.id.search_input));

        pureWeatherDB = PureWeatherDB.getInstance(this);
        testButton = ((Button) findViewById(R.id.test_btn));
        final String superId = "1904";

//        testButton.setOnClickListener(v -> getRegion(searchInput.getText().toString())
//                .subscribe(regions -> {
//                        dataList.clear();
//                        dataList.add(regions.get(0).getName());
//                        adapter.notifyDataSetChanged();
//                }));

        initRecycleView();

    }

    private void initRecycleView() {
        mAdapter = new MyRecycleViewAdapter();
        mRecyclerView = ((RecyclerView) findViewById(R.id.search_city_activity_recycle_view));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }





    class MyRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private final int TYPE_ONE = 0;
        private final int TYPE_TWO = 1;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
//            viewType = getItemViewType(viewType);
            switch (viewType){
                case TYPE_ONE:
                    return new MyViewHolderOne(LayoutInflater.from(
                            MainActivity.this).inflate(R.layout.recycle_view_item, parent,false));
                case 1:
                case 2:
                case 3:
                case 4:
                    return new MyViewHolderTwo(LayoutInflater.from(
                            MainActivity.this).inflate(R.layout.recycle_view_item_imag,parent,false));

            }
            return  null;

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType) {

            switch (viewType){
                case TYPE_ONE:
                    ((MyViewHolderOne)holder).textView.setText("Hello Small!");
                    break;
                case TYPE_TWO:
                    Glide.with(MainActivity.this)
                            .load(R.drawable.beijing)
                            .fitCenter()
                            .crossFade()
                            .into(((MyViewHolderTwo) holder).imageView);
//                    ((MyViewHolderTwo)holder).imageView.setImageResource(R.drawable.beijing);
                    break;
                case 2:
                    Glide.with(MainActivity.this)
                            .load(R.drawable.beijing_2)
                            .fitCenter()
                            .crossFade()
                            .into(((MyViewHolderTwo)holder).imageView);
                    break;
                case 3:
                    Glide.with(MainActivity.this)
                            .load(R.drawable.yanxi)
                            .fitCenter()
                            .crossFade()
                            .into(((MyViewHolderTwo)holder).imageView);
                    break;
                case 4:
                    Glide.with(MainActivity.this)
                            .load(R.drawable.yangjiang)
                            .fitCenter()
                            .crossFade()
                            .into(((MyViewHolderTwo)holder).imageView);
                    break;
            }


        }

        @Override
        public int getItemCount() {
            return 5;
        }

        @Override
        public int getItemViewType(int position) {

            return position;
        }

        class MyViewHolderOne extends RecyclerView.ViewHolder{
            TextView textView;
            public MyViewHolderOne(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.recycle_view_text);

            }
        }
        class MyViewHolderTwo extends RecyclerView.ViewHolder{
            ImageView imageView;
            public MyViewHolderTwo(View itemView) {
                super(itemView);
                imageView = ((ImageView) itemView.findViewById(R.id.recycle_view_image));
            }
        }
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
        if (id == R.id.action_list) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
