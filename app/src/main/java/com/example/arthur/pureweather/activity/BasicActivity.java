package com.example.arthur.pureweather.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.arthur.pureweather.R;
import com.example.arthur.pureweather.utils.PreferenceUtils;

/**
 * Created by Administrator on 2016/8/21.
 */
public class BasicActivity extends AppCompatActivity {
    protected PreferenceUtils preferenceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferenceUtils = PreferenceUtils.getInstance(this);
        initTheme();
        super.onCreate(savedInstanceState);
    }

    private void initTheme() {
//        MyThemeUtils.Theme theme = MyThemeUtils.getCurrentTheme(this);
//        MyThemeUtils.changTheme(this, theme);
//        this.setTheme(R.style.AppTheme_1);
    }
}
