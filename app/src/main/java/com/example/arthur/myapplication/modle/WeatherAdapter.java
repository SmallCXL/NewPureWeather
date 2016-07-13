package com.example.arthur.myapplication.modle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arthur.myapplication.R;

import java.util.List;

/**
 * Created by Administrator on 2016/6/9.
 */
public class WeatherAdapter extends ArrayAdapter<Weather> {
    private int resourceID;
    private Context context;
    public WeatherAdapter(Context context, int resource, List<Weather> objects) {
        super(context, resource, objects);
        this.context = context;
        resourceID = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view ;
        Weather info = getItem(position);
        ViewHolder VH;
        if (convertView == null){

            view = LayoutInflater.from(getContext()).inflate(resourceID, null);
            VH = new ViewHolder();
            VH.nowTemp = (TextView) view.findViewById(R.id.city_manager_card_view__now_temp);
            VH.cityName = (TextView) view.findViewById(R.id.city_manager_card_view_title);
            VH.condText = (TextView) view.findViewById(R.id.city_manager_card_view_condition);
            VH.tempRange = (TextView) view.findViewById(R.id.city_manager_card_image_temp_range);
//            VH.background = (ImageView) view.findViewById(R.id.city_manager_card_view_background);
            view.setTag(VH);
        }
        else{
            view = convertView;
            VH = (ViewHolder) view.getTag();

        }
        //动态设置图片的方法！！

//        Class<com.example.arthur.myapplication.R.drawable> cls = R.drawable.class;
//        try {
//            Integer value = cls.getDeclaredField("p"+info.getImageCode()).getInt(null);
//            VH.condImage.setImageResource(value);
//            //Log.v("value",value.toString());
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            VH.condImage.setImageResource(R.drawable.p999);
////        }
//        MyImageLoader.load(context,R.drawable.beijing,VH.background);
//        VH.background.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context,"hello!",Toast.LENGTH_SHORT).show();
//            }
//        });
        VH.cityName.setText(info.getCityName());
        VH.condText.setText(info.getCondText());
        VH.tempRange.setText(info.getTempRange());
        VH.nowTemp.setText(info.getNowTemp());
        return view;


    }
    class ViewHolder{
        TextView nowTemp;
//        ImageView background;
        TextView cityName;
        TextView condText;
        TextView tempRange;
    }



}
