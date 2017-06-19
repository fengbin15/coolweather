package com.coolweather.android.gson;

/**
 * Created by Drug on 2017/6/16.
 *
 * 建立AQI类，包括城市的aqi和pm2.5
 */

public class AQI {
    public AQICity city;     //城市污染信息

    public class AQICity{

        public String aqi;     //城市aqi

        public String pm25;     //城市pm2.5
    }
}
