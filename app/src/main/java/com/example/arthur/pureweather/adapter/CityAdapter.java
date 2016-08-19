package com.example.arthur.pureweather.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arthur.pureweather.R;
import com.example.arthur.pureweather.utils.MyImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/14.
 */
public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {

    private List<String> cityList;
    private Context context;
    public CityAdapter(Context context, List<String> cityList){
        this.context = context;
        this.cityList = cityList;
    }
    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CityViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_city_list,parent,false));
    }

    @Override
    public void onBindViewHolder(CityViewHolder holder, int position) {
        holder.bind(cityList.get(position));
        if(mOnItemClickListener != null){
            holder.cardView.setOnClickListener(v -> mOnItemClickListener.onItemClick(v, position));
        }
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    class CityViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.search_city_recycle_view_city_title)
        TextView textView;
        @Bind(R.id.search_city_recycle_view_city_image)
        ImageView imageView;
        @Bind(R.id.search_city_recycle_view_card)
        CardView cardView;


        public CityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        public void bind(String cityName){
            textView.setText(cityName);
            MyImageLoader.load(context,R.drawable.location,imageView);
        }
    }

    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

}
