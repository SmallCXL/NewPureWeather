package com.example.arthur.pureweather.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 通用工具类
 * 1、复制信息到剪切板
 * 2、判断指定日期是星期几
 */
public class Utils {
    public static void copyToClipboard(String info, Context context) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("msg", info);
        manager.setPrimaryClip(clipData);
        Toast.makeText(context,"已经复制到剪切板~",Toast.LENGTH_SHORT).show();
    }
    public static String getDateOfWeek(String date){
        String[] dates = {"周日","周一","周二","周三","周四","周五","周六"};
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date tempDate = sdf.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tempDate);
            int dateOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            return dates[dateOfWeek - 1];
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "未知";
    }

}
