package com.example.arthur.pureweather.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/8/19.
 */
public class Utils {
    public static void copyToClipboard(String info, Context context) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("msg", info);
        manager.setPrimaryClip(clipData);
        Toast.makeText(context,"已经复制到剪切板~",Toast.LENGTH_SHORT).show();
    }
}
