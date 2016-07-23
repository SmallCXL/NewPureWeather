package com.example.arthur.myapplication.modle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.arthur.myapplication.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.view.LineChartView;

public class TempWeatherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private WeatherInfo weatherInfo;
    private final int NOW_CONDITION = 0;
    private final int DAILY_FORECAST = 1;
    private final int SUGGESTION = 2;

    public TempWeatherAdapter(Context context, WeatherInfo weatherInfo) {
        this.context = context;
        this.weatherInfo = weatherInfo;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position){
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
        switch (viewType){
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
        switch (viewType){
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
        return weatherInfo.getStatus().equals("ok")?  3 : 0;
    }

    class NowConditionViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.now_temp)        TextView nowTemp;
        @Bind(R.id.now_image)       ImageView nowImage;
        @Bind(R.id.now_cond)        TextView nowCondition;
        @Bind(R.id.humidity)        TextView humidity;
        @Bind(R.id.rainy_pos)       TextView rainyPos;
        @Bind(R.id.temp_range)      TextView tempRange;
        @Bind(R.id.update_time)     TextView updateTime;
        public NowConditionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        public void bind(Context context, WeatherInfo weatherInfo){
            nowTemp.setText(weatherInfo.getNow().getTmp());
            nowCondition.setText(weatherInfo.getNow().getCond().getTxt());
            humidity.setText(weatherInfo.getNow().getHum());
            rainyPos.setText(weatherInfo.getDailyForecast().get(0).getHum());
            String range = new StringBuilder().append(weatherInfo.getDailyForecast().get(0).getTmp().getMax())
                    .append("°C ~ ")
                    .append(weatherInfo.getDailyForecast().get(0).getTmp().getMin())
                    .append("°C").toString();
            tempRange.setText(range);

            StringBuilder syncTime = new StringBuilder().append(weatherInfo.getBasic().getUpdate().getUtc()).append("更新");
            updateTime.setText(syncTime.toString().substring(5));
        }
    }

    class DailyForecastViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.data_char_view)      LineChartView lineChartView;
        @Bind(R.id.max_temp_btn)        Button maxTemp;
        @Bind(R.id.min_temp_btn)        Button minTemp;
        @Bind(R.id.humidity_btn)        Button humidity;
        @Bind(R.id.rainy_pos_btn)       Button rainyPos;
        public DailyForecastViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        public void bind(Context context, WeatherInfo weatherInfo){

        }
    }

    class SuggestionViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.sport_suggestion_brf)    TextView sportSuggestionBrf;
        @Bind(R.id.sport_suggestion)        TextView sportSuggestion;
        @Bind(R.id.travel_suggestion_brf)   TextView travelSuggestionBrf;
        @Bind(R.id.travel_suggestion)       TextView travelSuggestion;
        @Bind(R.id.car_wash_suggestion_brf) TextView carWashSuggestionBrf;
        @Bind(R.id.car_wash_suggestion)     TextView carWashSuggestion;

        public SuggestionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        public void bind(Context context, WeatherInfo weatherInfo){
            sportSuggestionBrf.setText(weatherInfo.getSuggestion().getSport().getBrf());
            sportSuggestion.setText(weatherInfo.getSuggestion().getSport().getTxt());
            travelSuggestionBrf.setText(weatherInfo.getSuggestion().getTrav().getBrf());
            travelSuggestion.setText(weatherInfo.getSuggestion().getTrav().getTxt());
            carWashSuggestionBrf.setText(weatherInfo.getSuggestion().getCw().getBrf());
            carWashSuggestion.setText(weatherInfo.getSuggestion().getCw().getTxt());
        }
    }

}
