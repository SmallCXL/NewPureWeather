package com.example.arthur.pureweather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.example.arthur.pureweather.R;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
/**
 * Created by Administrator on 2016/8/12.
 */
public class GuideActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Observable.timer(1200, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    Intent intent = new Intent(GuideActivity.this,WeatherActivity.class);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    startActivity(intent);
                    finish();
                });
    }
//    private static final String TAG = FirstActivity.class.getSimpleName();
//    private SwitchHandler mHandler = new SwitchHandler(Looper.getMainLooper(), this);
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mHandler.sendEmptyMessageDelayed(1, 1000);
//    }
//
//    class SwitchHandler extends Handler {
//        private WeakReference<GuideActivity> mWeakReference;
//
//        public SwitchHandler(Looper mLooper, GuideActivity activity) {
//            super(mLooper);
//            mWeakReference = new WeakReference<>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//            Intent i = new Intent(GuideActivity.this, WeatherActivity.class);
//            GuideActivity.this.startActivity(i);
//            //activity切换的淡入淡出效果
//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//            GuideActivity.this.finish();
//        }
//    }
}