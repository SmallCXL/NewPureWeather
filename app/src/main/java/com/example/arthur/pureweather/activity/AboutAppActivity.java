package com.example.arthur.pureweather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.arthur.pureweather.R;
import com.example.arthur.pureweather.adapter.AboutAppAdapter;

/**
 * Created by Administrator on 2016/8/15.
 */
public class AboutAppActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private AboutAppAdapter aboutAppAdapter;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        recyclerView = ((RecyclerView) findViewById(R.id.about_recycler_view));
        aboutAppAdapter = new AboutAppAdapter(this);
        recyclerView.setAdapter(aboutAppAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(0);

        toolbar = ((Toolbar) findViewById(R.id.about_app_toolbar));
        toolbar.setTitle("关于软件");
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.colorWhite));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(AboutAppActivity.this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
