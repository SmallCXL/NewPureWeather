package com.example.arthur.pureweather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.arthur.pureweather.R;
import com.example.arthur.pureweather.service.ForegroundService;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2016/8/12.
 */
public class GuideActivity extends AppCompatActivity {
    private CompositeSubscription allSubscription = new CompositeSubscription();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Subscription launch = Observable.timer(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    Intent intent = new Intent(GuideActivity.this,WeatherActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);//要放在Finish后面
//                    System.gc();
                });
        allSubscription.add(launch);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        allSubscription.unsubscribe();
    }
//    private SwitchHandler mHandler = new SwitchHandler(Looper.getMainLooper(), this);
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mHandler.sendEmptyMessageDelayed(1, 1200);
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
//            GuideActivity.this.finish();
//            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//        }
//    }
}
