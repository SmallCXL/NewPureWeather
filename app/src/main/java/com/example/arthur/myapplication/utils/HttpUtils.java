package com.example.arthur.myapplication.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/6/9.
 */
public class HttpUtils {
    /*
     * 发送一个http请求
     */
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
        new Thread(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(4000);
                    connection.setReadTimeout(4000);
                    //connection.setRequestProperty("apikey",  "3cc16767d699197caf22d11f13c5729b");
                    //connection.connect();
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    //response 为返回的初始数据 待后续处理
                    if(listener != null){
                        listener.onFinish(response.toString());//回调监听器的onFinish方法处理结果
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    listener.onError(e);
                    e.printStackTrace();
                }finally{
                    if(connection != null){
                        connection.disconnect();
                    }
                }

            }


        }).start();
    }


}
