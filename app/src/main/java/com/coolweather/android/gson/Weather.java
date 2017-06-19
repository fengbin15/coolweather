package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Drug on 2017/6/16.
 * 天气类，包括状态，城市基础信息，污染状况AQI，当前天气状况，提示建议和日常预报
 */

public class Weather {
    public String status;   //状态

    public Basic basic;   //基础信息城市

    public AQI aqi;      //城市污染信息

    public Now now;     //当前天气信息

    public Suggestion suggestion;  //建议提示信息
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;    //预报信息
}
