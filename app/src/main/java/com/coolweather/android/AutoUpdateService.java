package com.coolweather.android;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Drug on 2017/6/19.
 *
 * 实现天气和必应图片更新
 */

public class AutoUpdateService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //更新天气和图片
        updateWeather();
        updateBingPic();
        //定义机制管理器
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int betweenTime = 8 * 60 * 60 * 1000;   //更新时间间隔
        long triggerAtTime = SystemClock.elapsedRealtime() + betweenTime;  //触发时间点
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */
    private void updateWeather(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = pref.getString("weather", null);
        if (weatherString != null){
            //如果缓冲不为空，则直接解析数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            //获取天气ID
            String weatherId = weather.basic.weatherId;
            //定义更新获取地址
            String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" +
                    weatherId + "&key=001be178c1a54af7971a895bebff259e";
            //网络请求获取数据
            HttpUtil.sendOkHttpRequest(weatherId, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //从response中获取数据
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)){
                        SharedPreferences.Editor editor = PreferenceManager.
                                getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

    /**
     * 更新必应每日图片
     */
    private void updateBingPic(){
        //图片地址
        String bingPicUrl = "http://guolin.tech/api/bing_pic";
        //从网络获取图片
        HttpUtil.sendOkHttpRequest(bingPicUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取返回数据
                String responseText = response.body().string();
                //将数据存入缓冲
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic", responseText);
                editor.apply();
            }
        });
    }
}
