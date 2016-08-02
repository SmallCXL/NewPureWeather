package com.example.arthur.myapplication.modle;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.arthur.myapplication.R;
import com.example.arthur.myapplication.utils.ImageCodeConverter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/18.
 */
public class BriefWeatherAdapter extends RecyclerView.Adapter<BriefWeatherAdapter.BriefWeatherViewHolder>{

    private Context context;
    private List<BriefWeatherInfo> weatherInfos;

    public BriefWeatherAdapter(Context context, List<BriefWeatherInfo> weatherInfos){
        this.context = context;
        this.weatherInfos = weatherInfos;
    }

    @Override
    public BriefWeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BriefWeatherViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_collection_list,parent,false));
    }

    Class<com.example.arthur.myapplication.R.drawable> myDrawableClass = R.drawable.class;
    @Override
    public void onBindViewHolder(BriefWeatherViewHolder holder, int position) {
        holder.bind(context, weatherInfos.get(position), position);
    }

    @Override
    public int getItemCount() {
        return weatherInfos.size();
    }

    class BriefWeatherViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.city_manager_card_view)              CardView cardView;
        @Bind(R.id.city_manager_card_view_now_temp)     TextView nowTemp;
        @Bind(R.id.city_manager_card_view_title)        TextView cityName;
        @Bind(R.id.city_manager_card_view_condition)    TextView nowCondition;
        @Bind(R.id.city_manager_card_view_temp_range)   TextView tempRange;
        @Bind(R.id.city_manager_card_view_update_time)  TextView updateTime;
        public BriefWeatherViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        public void bind(Context context, BriefWeatherInfo briefWeatherInfo, int position){
            nowTemp.setText(briefWeatherInfo.getNowTemp());
            cityName.setText(briefWeatherInfo.getCityName());
            nowCondition.setText(briefWeatherInfo.getCondText());
            tempRange.setText(briefWeatherInfo.getTempRange());
            updateTime.setText(briefWeatherInfo.getUpdateTime().substring(5) + " 发布");

            MyImageLoader.load(context, ImageCodeConverter.getBriefBackgroundResource(briefWeatherInfo.getImageCode()) ,nowTemp);

            if(mOnItemClickListener != null){
                cardView.setOnClickListener(v -> mOnItemClickListener.onItemClick(v, position));
                cardView.setOnCreateContextMenuListener((menu, v, menuInfo) ->
                        mOnItemClickListener.onItemLongClick(menu, v, menuInfo, position)
                );
            }
        }
    }

    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
        void onItemLongClick(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, int position);
    }
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
