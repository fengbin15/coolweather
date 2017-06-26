package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.LogUtil;
import com.coolweather.android.util.Utility;

/**
 * Created by Drug on 2017/6/23.
 */

public class AutoShowWeatherFragment extends Fragment {
    //打印日志标签
    String TAG = "AutoShowWeatherFragment";
    /**
     * @param savedInstanceState
     * 该方法中实现布局的创建实例化
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * @param savedInstanceState
     * 相应的逻辑活动在该方法中完成
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.d(TAG, "onActivityCreate");
        //开始处理自动显示天气信息的逻辑。
        //判断缓冲中是否存在数据
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String weatherString = prefs.getString("weather", null);
        //解析天气数据
        Weather weather = Utility.handleWeatherResponse(weatherString);
        //获取天气id
        String weatherId = weather.basic.weatherId;
        //开启天气活动
        Intent intent = new Intent(getActivity(), WeatherActivity.class);
        intent.putExtra("weather_id", weatherId);
        startActivity(intent);
        getActivity().finish();
    }
}

