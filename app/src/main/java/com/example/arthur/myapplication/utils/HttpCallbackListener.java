package com.example.arthur.myapplication.utils;

/**
 * Created by Administrator on 2016/6/9.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
