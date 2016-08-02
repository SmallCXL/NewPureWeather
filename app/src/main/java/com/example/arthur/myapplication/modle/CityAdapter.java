package com.example.arthur.myapplication.modle;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.arthur.myapplication.R;

import java.util.List;

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
        holder.textView.setText(cityList.get(position));
        if(mOnItemClickListener != null){
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    class CityViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        CardView cardView;
        public CityViewHolder(View itemView) {
            super(itemView);
            cardView = ((CardView) itemView.findViewById(R.id.city_list_card_view));
            textView = (TextView) itemView.findViewById(R.id.search_city_recycle_view_city_title);
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
