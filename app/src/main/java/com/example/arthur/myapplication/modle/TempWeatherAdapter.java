package com.example.arthur.myapplication.modle;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.arthur.myapplication.R;
import com.example.arthur.myapplication.utils.RxBus;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.view.LineChartView;
import rx.subscriptions.CompositeSubscription;

public class TempWeatherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private WeatherInfo weatherInfo;
    private RxBus mRxBus;
    private final int NOW_CONDITION = 0;
    private final int DAILY_FORECAST = 1;
    private final int SUGGESTION = 2;

    public TempWeatherAdapter(Context context, WeatherInfo weatherInfo) {
        this.context = context;
        this.weatherInfo = weatherInfo;
        this.mRxBus = RxBus.getInstance();
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case NOW_CONDITION:
                return NOW_CONDITION;
            case DAILY_FORECAST:
                return DAILY_FORECAST;
            case SUGGESTION:
                return SUGGESTION;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case NOW_CONDITION:
                view = LayoutInflater.from(context).inflate(R.layout.item_now_condition, parent, false);
                return new NowConditionViewHolder(view);
            case DAILY_FORECAST:
                view = LayoutInflater.from(context).inflate(R.layout.item_daily_forecast, parent, false);
                return new NowConditionViewHolder(view);
            case SUGGESTION:
                view = LayoutInflater.from(context).inflate(R.layout.item_suggestion, parent, false);
                return new NowConditionViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType) {
        switch (viewType) {
            case NOW_CONDITION:
                ((NowConditionViewHolder) holder).bind(context, weatherInfo);
                break;
            case DAILY_FORECAST:
                ((DailyForecastViewHolder) holder).bind(context, weatherInfo);
                break;
            case SUGGESTION:
                ((SuggestionViewHolder) holder).bind(context, weatherInfo);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return weatherInfo.status.equals("ok") ? 3 : 0;
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

        public void bind(Context context, WeatherInfo weatherInfo) {
            nowTemp.setText(weatherInfo.now.tmp);
            nowCondition.setText(weatherInfo.now.cond.txt);
            humidity.setText(weatherInfo.now.hum);
            rainyPos.setText(weatherInfo.dailyForecast.get(0).hum);
            String range = new StringBuilder().append(weatherInfo.dailyForecast.get(0).tmp.max)
                    .append("°C ~ ")
                    .append(weatherInfo.dailyForecast.get(0).tmp.min)
                    .append("°C").toString();
            tempRange.setText(range);

            StringBuilder syncTime = new StringBuilder().append(weatherInfo.basic.update.utc).append("更新");
            updateTime.setText(syncTime.toString().substring(5));
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

        private List<String> maxTemp;
        private List<String> minTemp;
        private List<String> humidity;
        private List<String> rainyPos;

        public DailyForecastViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @TargetApi(Build.VERSION_CODES.M)
        public void bind(Context context, WeatherInfo weatherInfo) {
            //负责初始化数据并装载到点击事件中，然后发射事件
            initData(weatherInfo);
            RxView.clicks(maxTempBtn)
                    .throttleFirst(1000, TimeUnit.MICROSECONDS)
                    .subscribe(aVoid -> {
                        RxBus.getInstance().post(new ButtonClickedEvent(maxTemp, context.getColor(R.color.redPrimary)));
                    });
            RxView.clicks(minTempBtn)
                    .throttleFirst(1000, TimeUnit.MICROSECONDS)
                    .subscribe(aVoid -> {
                        RxBus.getInstance().post(new ButtonClickedEvent(minTemp, context.getColor(R.color.orangePrimary)));
                    });
            RxView.clicks(humidityBtn)
                    .throttleFirst(1000, TimeUnit.MICROSECONDS)
                    .subscribe(aVoid -> {
                        RxBus.getInstance().post(new ButtonClickedEvent(humidity, context.getColor(R.color.greenPrimary)));
                    });
            RxView.clicks(rainyPosBtn)
                    .throttleFirst(1000, TimeUnit.MICROSECONDS)
                    .subscribe(aVoid -> {
                        RxBus.getInstance().post(new ButtonClickedEvent(rainyPos, context.getColor(R.color.indigoPrimary)));
                    });
        }
        public void initData(WeatherInfo weatherInfo){
            int size = weatherInfo.dailyForecast.size();
            maxTemp = new ArrayList<>();
            minTemp = new ArrayList<>();
            humidity = new ArrayList<>();
            rainyPos = new ArrayList<>();

            for (int i=0; i<size; i++){
                maxTemp.add(weatherInfo.dailyForecast.get(i).tmp.max);
                minTemp.add(weatherInfo.dailyForecast.get(i).tmp.min);
                humidity.add(weatherInfo.dailyForecast.get(i).hum);
                rainyPos.add(weatherInfo.dailyForecast.get(i).pop);
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

        public void bind(Context context, WeatherInfo weatherInfo) {
            sportSuggestionBrf.setText(weatherInfo.suggestion.sport.brf);
            sportSuggestion.setText(weatherInfo.suggestion.sport.txt);
            travelSuggestionBrf.setText(weatherInfo.suggestion.trav.brf);
            travelSuggestion.setText(weatherInfo.suggestion.trav.txt);
            carWashSuggestionBrf.setText(weatherInfo.suggestion.cw.brf);
            carWashSuggestion.setText(weatherInfo.suggestion.cw.txt);
        }
    }
//    public interface OnButtonClickListener
//    {
//        void onMaxTempButtonClick();
//        void onMinTempButtonClick(View view, int position);
//        void onHumidityButtonClick(View view, int position);
//        void onRainyPosButtonClick(View view, int position);
//    }
//    private OnButtonClickListener mOnButtonClickListener;
//
//    public void setOnItemClickListener(OnButtonClickListener mOnButtonClickListener){
//        this.mOnButtonClickListener = mOnButtonClickListener;
//    }
}
