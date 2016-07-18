package com.example.arthur.myapplication.modle;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.arthur.myapplication.R;

import java.util.List;

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
                .inflate(R.layout.new_city_manager_activity_item,parent,false));
    }

    @Override
    public void onBindViewHolder(BriefWeatherViewHolder holder, int position) {
        holder.nowTemp.setText(weatherInfos.get(position).getNowTemp());
        holder.cityName.setText(weatherInfos.get(position).getCityName());
        holder.nowCondition.setText(weatherInfos.get(position).getCondText());
        holder.tempRange.setText(weatherInfos.get(position).getTempRange());
        holder.updateTime.setText(weatherInfos.get(position).getUpdateTime());
        Glide.with(context)
                .load(R.drawable.xiayu2)
                .fitCenter()
                .crossFade()
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        holder.nowTemp.setBackground(resource);
                    }
                });
        if(mOnItemClickListener != null){
            holder.cardView.setOnClickListener(v -> mOnItemClickListener.onItemClick(v, position));
            holder.cardView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    mOnItemClickListener.onItemLongClick(menu, v, menuInfo, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return weatherInfos.size();
    }
    class BriefWeatherViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView nowTemp;
        TextView cityName;
        TextView nowCondition;
        TextView tempRange;
        TextView updateTime;
        public BriefWeatherViewHolder(View itemView) {
            super(itemView);
            cardView = ((CardView) itemView.findViewById(R.id.city_manager_card_view));
            nowTemp = ((TextView) itemView.findViewById(R.id.city_manager_card_view_now_temp));
            cityName = ((TextView) itemView.findViewById(R.id.city_manager_card_view_title));
            nowCondition = ((TextView) itemView.findViewById(R.id.city_manager_card_view_condition));
            tempRange = ((TextView) itemView.findViewById(R.id.city_manager_card_view_temp_range));
            updateTime = ((TextView) itemView.findViewById(R.id.city_manager_card_view_update_time));
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
