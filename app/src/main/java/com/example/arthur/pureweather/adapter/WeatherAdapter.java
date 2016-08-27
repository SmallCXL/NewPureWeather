package com.example.arthur.pureweather.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.arthur.pureweather.R;
import com.example.arthur.pureweather.constant.Constants;
import com.example.arthur.pureweather.modle.Weather;
import com.example.arthur.pureweather.utils.ImageCodeConverter;
import com.example.arthur.pureweather.utils.MyImageLoader;
import com.example.arthur.pureweather.utils.RxBus;
import com.example.arthur.pureweather.utils.Utils;
import com.jakewharton.rxbinding.view.RxView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import rx.android.schedulers.AndroidSchedulers;

public class WeatherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Weather> weathers;
    private RxBus mRxBus;
    private final int NOW_CONDITION = 0;
    private final int DAILY_FORECAST = 1;
    private final int SUGGESTION = 2;
    private final int HOURLY_FORECAST = 3;


    public WeatherAdapter(Context context, List<Weather> weathers) {
        this.context = context;
        this.weathers = weathers;
        this.mRxBus = RxBus.getInstance();
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case NOW_CONDITION:
                return NOW_CONDITION;
            case DAILY_FORECAST:
                return DAILY_FORECAST;
            case HOURLY_FORECAST:
                return HOURLY_FORECAST;
            case SUGGESTION:
                return SUGGESTION;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //注意，这个方法只会执行一次，每次notifyDataChange之后，并不会执行这个方法
        View view;
        switch (viewType) {
            case NOW_CONDITION:
                view = LayoutInflater.from(context).inflate(R.layout.item_now_condition, parent, false);
                return new NowConditionViewHolder(view);
            case DAILY_FORECAST:
                view = LayoutInflater.from(context).inflate(R.layout.item_daily_forecast, parent, false);
                return new DailyForecastViewHolder(view);
            case SUGGESTION:
                view = LayoutInflater.from(context).inflate(R.layout.item_suggestion, parent, false);
                return new SuggestionViewHolder(view);
            case HOURLY_FORECAST:
                view = LayoutInflater.from(context).inflate(R.layout.item_hourly_forecast, parent, false);
                return new HourlyForecastViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType) {
        //这个方法在每次notifyDataChange之后，都会执行重新绑定的行为，一些需要变更的操作一般放在这个方法中
        switch (viewType) {
            case NOW_CONDITION:
                ((NowConditionViewHolder) holder).bind(weathers.get(0));
                break;
            case DAILY_FORECAST:
                ((DailyForecastViewHolder) holder).bind(weathers.get(0));
                break;
            case HOURLY_FORECAST:
                ((HourlyForecastViewHolder) holder).bind(weathers.get(0));
                break;
            case SUGGESTION:
                ((SuggestionViewHolder) holder).bind(weathers.get(0));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (weathers.get(0).basic.cnty.equals("中国")){
            if (weathers.get(0).hourlyForecast.size() > 0){
                return 4;
            }
            else return 3;
        }
        else {
            return 2;
        }
    }

    class NowConditionViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.now_temp)
        TextView nowTemp;
        @Bind(R.id.now_image)
        ImageView nowImage;
        @Bind(R.id.now_cond)
        TextView nowCondition;
        @Bind(R.id.humidity)
        TextView humidity;
        @Bind(R.id.rainy_pos)
        TextView rainyPos;
        @Bind(R.id.temp_range)
        TextView tempRange;
        @Bind(R.id.update_time)
        TextView updateTime;

        public NowConditionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Weather weather) {
            nowTemp.setText(weather.now.tmp + "°");
            nowCondition.setText(weather.now.cond.txt);
            humidity.setText(weather.dailyForecast.get(0).hum + "%");
            rainyPos.setText(weather.dailyForecast.get(0).pop + "%");
            String range = new StringBuilder().append(weather.dailyForecast.get(0).tmp.max)
                    .append("°C~")
                    .append(weather.dailyForecast.get(0).tmp.min)
                    .append("°C").toString();
            tempRange.setText(range);

            StringBuilder syncTime = new StringBuilder().append(weather.basic.update.loc).append(" 发布");
            updateTime.setText(syncTime.toString().substring(5));

            MyImageLoader.load(context, ImageCodeConverter.getWeatherIconResource(weather.now.cond.code, "normal"), nowImage);
        }
    }

    class DailyForecastViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.data_char_view)
        LineChartView lineChartView;
        @Bind(R.id.max_temp_btn)
        Button maxTempBtn;
        @Bind(R.id.min_temp_btn)
        Button minTempBtn;
        @Bind(R.id.humidity_btn)
        Button humidityBtn;
        @Bind(R.id.rainy_pos_btn)
        Button rainyPosBtn;

        int forecastTextViewId[] ={
                R.id.forecast_1_condition,R.id.forecast_2_condition,R.id.forecast_3_condition,
                R.id.forecast_4_condition,R.id.forecast_5_condition,R.id.forecast_6_condition,
                R.id.forecast_7_condition};
        private TextView[] forecastTextView = new TextView[7];
        int forecastImageViewId[] ={
                R.id.forecast_1_image,R.id.forecast_2_image,R.id.forecast_3_image,
                R.id.forecast_4_image,R.id.forecast_5_image,R.id.forecast_6_image,
                R.id.forecast_7_image};
        private ImageView[] forecastImageView = new ImageView[7];

        private List<Integer> maxTemp ;
        private List<Integer> minTemp ;
        private List<Integer> humidity;
        private List<Integer> rainyPos;
        private List<String> xLabel;

        public DailyForecastViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            for (int i=0; i<7; i++){
                forecastTextView[i] = ((TextView) itemView.findViewById(forecastTextViewId[i]));
                forecastImageView[i] = ((ImageView) itemView.findViewById(forecastImageViewId[i]));
            }
            maxTemp = new ArrayList<>();
            minTemp = new ArrayList<>();
            humidity = new ArrayList<>();
            rainyPos = new ArrayList<>();
            xLabel = new ArrayList<>();
            xLabel.add("今天");
            xLabel.add("明天");
            for (int i=2; i<7; i++){
                String date = Utils.getDateOfWeek(weathers.get(0).dailyForecast.get(i).date);
                xLabel.add(date);
            }
            //负责将数据按钮类型和天气数据装载到点击事件中，然后发射事件
            RxView.clicks(maxTempBtn)
                    .throttleFirst(3000, TimeUnit.MICROSECONDS)
                    .subscribe(aVoid -> {
                        onButtonClick(ContextCompat.getColor(context,R.color.redPrimary),"MAX_TEMP");
                    });
            RxView.clicks(minTempBtn)
                    .throttleFirst(3000, TimeUnit.MICROSECONDS)
                    .subscribe(aVoid -> {
                        onButtonClick(ContextCompat.getColor(context,R.color.orangePrimary),"MIN_TEMP");
                    });
            RxView.clicks(humidityBtn)
                    .throttleFirst(3000, TimeUnit.MICROSECONDS)
                    .subscribe(aVoid -> {
                        onButtonClick(ContextCompat.getColor(context, R.color.greenPrimary), "HUMIDITY");
                    });
            RxView.clicks(rainyPosBtn)
                    .throttleFirst(3000, TimeUnit.MICROSECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aVoid -> {
                        onButtonClick(ContextCompat.getColor(context, R.color.indigoPrimary), "RAINY_PRO");
                    });
        }

        public void bind(Weather weather) {
            initData(weather);//装数据、装图标和天气描述
            initCharView();
        }

        private void initData(Weather weather){
            minTemp.clear();
            maxTemp.clear();
            humidity.clear();
            rainyPos.clear();
            for (int i=0; i<7; i++){
                maxTemp.add(Integer.parseInt(weather.dailyForecast.get(i).tmp.max));
                minTemp.add(Integer.parseInt(weather.dailyForecast.get(i).tmp.min));
                humidity.add(Integer.parseInt(weather.dailyForecast.get(i).hum));
                rainyPos.add(Integer.parseInt(weather.dailyForecast.get(i).pop));
            }
            for (int i=0; i<7; i++){
                forecastTextView[i].setText(weather.dailyForecast.get(i).cond.txtD.replace("晴间", ""));//晴间多云  改为 多云，去掉多余信息保证页面整洁
                MyImageLoader.load(context,
                        ImageCodeConverter.getWeatherIconResource(weather.dailyForecast.get(i).cond.codeD, "daily_forecast"), forecastImageView[i]);
            }
        }

        private void initCharView(){
            List<PointValue> pointValues = new ArrayList<>();
            List<AxisValue> axisValues_x = new ArrayList<>();
            List<AxisValue> axisValues_y = new ArrayList<>();
            List<Line> lineList = new ArrayList<>();

            int rowNum = Collections.max(maxTemp) - Collections.min(maxTemp);
            int yAxisMax = Collections.max(maxTemp)+1;
            int yAxisMin = Collections.min(maxTemp)-1;
            for (int i = -1; i <= rowNum+1 ; i++) {
                axisValues_y
                        .add(new AxisValue(Collections.min(maxTemp) + i).setLabel(String.valueOf(Collections.min(maxTemp) + i)+"°"));
            }
            for (int i = 0; i < 7; ++i) {
                pointValues.add(new PointValue(i, maxTemp.get(i)));
                axisValues_x.add(new AxisValue(i).setLabel(xLabel.get(i)));
            }//生成7个点以及x轴的标签
            Line line = new Line(pointValues);
            line.setColor(ContextCompat.getColor(context, R.color.redPrimary));
            line.setCubic(true);//设置线条圆滑过渡
            boolean hasLabel = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.HAS_LABEL,false);
            if (hasLabel){
                line.setHasLabels(true);
            }else{
                line.setHasLabels(false);
            }

            lineList.add(line);

            LineChartData lineChartData = new LineChartData(lineList);
            lineChartData.setAxisXBottom(new Axis(axisValues_x).setHasLines(true));
            lineChartData.setAxisYLeft(new Axis(axisValues_y).setHasLines(true).setMaxLabelChars(3));
            lineChartData.getAxisYLeft().setMaxLabelChars(4).setTextColor(ContextCompat.getColor(context,R.color.textColorLight));
            lineChartData.getAxisXBottom().setMaxLabelChars(2).setTextColor(ContextCompat.getColor(context,R.color.textColorLight));

            lineChartView.setLineChartData(lineChartData);
            lineChartView.setViewportCalculationEnabled(false);//设置false的情况下，坐标轴直接突变到目标值，而不是慢慢演变
            lineChartView.setZoomType(null);
            Viewport viewport = new Viewport(0, yAxisMax, 6.2f, yAxisMin);//(x最小值，y最大值，x最大值，y最小值)，其实是表格的四边起始点
            lineChartView.setMaximumViewport(viewport);
            lineChartView.setCurrentViewport(viewport);
        }
        private void onButtonClick(int color, String dataType){
            lineChartView.getLineChartData().getAxisYLeft().setTextColor(color);
            lineChartView.cancelDataAnimation();

            Line line = lineChartView.getLineChartData().getLines().get(0);
            line.setColor(color);
            List<AxisValue> yAxis = new ArrayList<>();
            int rowNum;
            float yAxisMax;
            float yAxisMin;

            switch (dataType){
                case "MAX_TEMP":
                    for (int i=0; i<7; i++) {
                        PointValue value = line.getValues().get(i);
                        value.setTarget(value.getX(), maxTemp.get(i));
                    }
                    rowNum = Collections.max(maxTemp) - Collections.min(maxTemp);
                    yAxisMax = Collections.max(maxTemp)+1;
                    yAxisMin = Collections.min(maxTemp)-1;
                    for (int i = -1; i <= rowNum+1 ; i++) {
                        yAxis.add(new AxisValue(Collections.min(maxTemp) + i).setLabel(String.valueOf(Collections.min(maxTemp) + i)+"°"));
                    }
                    break;
                case "MIN_TEMP":
                    for (int i=0; i<7; i++) {
                        PointValue value = line.getValues().get(i);
                        value.setTarget(value.getX(), minTemp.get(i));
                    }
                    rowNum = Collections.max(minTemp) - Collections.min(minTemp);
                    yAxisMax = Collections.max(minTemp)+1;
                    yAxisMin = Collections.min(minTemp)-1;
                    for (int i = -1; i <= rowNum +1; i++) {
                        yAxis.add(new AxisValue(Collections.min(minTemp) + i).setLabel(String.valueOf(Collections.min(minTemp) + i)+"°"));
                    }
                    break;
                case "HUMIDITY":
                    for (int i=0; i<7; i++) {
                        PointValue value = line.getValues().get(i);
                        value.setTarget(value.getX(), humidity.get(i));
                    }
                    yAxisMax = 100;
                    yAxisMin = 0;
                    for (int i = 0; i < 10 ; i++) {
                        yAxis.add(new AxisValue(i * 10).setLabel(String.valueOf(i * 10)+"%"));
                    }
                    yAxis.add(new AxisValue(100).setLabel(String.valueOf(99)+"%"));
                    break;
                case "RAINY_PRO":
                    for (int i=0; i<7; i++) {
                        PointValue value = line.getValues().get(i);
                        value.setTarget(value.getX(), rainyPos.get(i));
                    }
                    yAxisMax = 100;
                    yAxisMin = 0;
                    for (int i = 0; i < 10 ; i++) {
                        yAxis.add(new AxisValue(i * 10).setLabel(String.valueOf(i * 10)+"%"));
                    }
                    yAxis.add(new AxisValue(100).setLabel(String.valueOf(99)+"%"));
                    break;
                default:
                    yAxisMax = 0;
                    yAxisMin = 0;
                    break;
            }
            lineChartView.getLineChartData().getAxisYLeft().setValues(yAxis);
            lineChartView.startDataAnimation(300);
            Viewport viewport = new Viewport(0, yAxisMax, 6.2f, yAxisMin);//(x最小值，y最大值，x最大值，y最小值)，其实是表格的四边起始点
            lineChartView.setMaximumViewport(viewport);
            lineChartView.setCurrentViewport(viewport);
        }
    }

    class HourlyForecastViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout hourlyForecastLayout;
        private List<TextView> time;
        private List<TextView> temp;
        private List<TextView> humidity;
        private List<TextView> rainyPro;
        public HourlyForecastViewHolder(View itemView) {
            super(itemView);
            hourlyForecastLayout = (LinearLayout) itemView.findViewById(R.id.hourly_forecast_layout);
            time = new ArrayList<>();
            temp = new ArrayList<>();
            humidity = new ArrayList<>();
            rainyPro = new ArrayList<>();
        }
        public void bind(Weather weather){
            time.clear();
            temp.clear();
            humidity.clear();
            rainyPro.clear();
            hourlyForecastLayout.removeAllViews();
            int size = weathers.get(0).hourlyForecast.size();
            for (int i=0; i<size; i++){
                View item = View.inflate(context,R.layout.item_hour_forecast_detail,null);
                time.add((TextView) item.findViewById(R.id.hourly_forecast_time));
                temp.add((TextView) item.findViewById(R.id.hourly_forecast_temp));
                humidity.add((TextView) item.findViewById(R.id.hourly_forecast_humidity));
                rainyPro.add((TextView) item.findViewById(R.id.hourly_forecast_rainy_pro));
                hourlyForecastLayout.addView(item);
            }
            for (int i = 0; i<size;i++){
                time.get(i).setText(weather.hourlyForecast.get(i).date);
                temp.get(i).setText(weather.hourlyForecast.get(i).tmp + "°C");
                humidity.get(i).setText(weather.hourlyForecast.get(i).hum + "%");
                rainyPro.get(i).setText(weather.hourlyForecast.get(i).pop + "%");
            }
        }
    }

    class SuggestionViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.sport_suggestion_brf)
        TextView sportSuggestionBrf;
        @Bind(R.id.sport_suggestion)
        TextView sportSuggestion;
        @Bind(R.id.travel_suggestion_brf)
        TextView travelSuggestionBrf;
        @Bind(R.id.travel_suggestion)
        TextView travelSuggestion;
        @Bind(R.id.car_wash_suggestion_brf)
        TextView carWashSuggestionBrf;
        @Bind(R.id.car_wash_suggestion)
        TextView carWashSuggestion;

        public SuggestionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Weather weather) {
            sportSuggestionBrf.setText(weather.suggestion.sport.brf);
            sportSuggestion.setText(weather.suggestion.sport.txt);
            travelSuggestionBrf.setText(weather.suggestion.trav.brf);
            travelSuggestion.setText(weather.suggestion.trav.txt);
            carWashSuggestionBrf.setText(weather.suggestion.cw.brf);
            carWashSuggestion.setText(weather.suggestion.cw.txt);
        }
    }
}
