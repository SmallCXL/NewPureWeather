package com.example.arthur.pureweather.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.arthur.pureweather.R;

/**
 * Created by Administrator on 2016/8/8.
 */
public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        SysApplication.getInstance().addActivity(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_activity_toolbar);
        toolbar.setTitle("设置");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().
                replace(R.id.setting_content, new SettingFragment()).commit();
//        setTheme(R.style.GreenTheme);
//        toolbar.setBackground(getDrawable(R.color.greenPrimary));
//        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.greenPrimary));

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, WeatherActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(SettingActivity.this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
