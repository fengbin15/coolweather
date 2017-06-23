package com.coolweather.android.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.coolweather.android.MainActivity;
import com.coolweather.android.WeatherActivity;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.LogUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by Drug on 2017/6/20.
 * 实现自动定位查询天气信息
 */

public class AutoLocationService extends Service {
    //日志标签
    String TAG = "AutoLocationService";
    //所在地位置
    String locName = null;
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate");
    }

    //声明AMapLocationClient对象
    public AMapLocationClient mLocationClient = null;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand");
        //实例化AMapLocationClient
        mLocationClient = new AMapLocationClient(AutoLocationService.this);
        //设置回调监听器
        mLocationClient.setLocationListener(new MyLocationListener());
        setLocationClientOption();
        startLocationClient();
        return super.onStartCommand(intent, flags, startId);
    }

    //设置参数
    public void setLocationClientOption(){
        //声明设置参数AMapLocationClientOption
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setInterval(100000);    //刷新时间
        //定位模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //禁止WiFi刷新
        mLocationOption.setWifiScan(false);
        mLocationOption.setHttpTimeOut(2 * 60 * 60 * 1000);   //定位请求超时时限
        mLocationOption.setMockEnable(true);    //允许模拟位置
        mLocationOption.setNeedAddress(true);    //返回位置信息
        mLocationClient.setLocationOption(mLocationOption);
    }

    //开启定位服务
    public void startLocationClient(){
        mLocationClient.startLocation();
    }

    public class MyLocationListener implements AMapLocationListener{
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            //获取所在位置所属的城市
            String address = aMapLocation.getAddress();
            LogUtil.d(TAG, address);
            String cityName = aMapLocation.getCity();
            LogUtil.d(TAG, cityName);
            //获取经度与纬度
            double longitude = aMapLocation.getLongitude();
            double latitude = aMapLocation.getLatitude();
            LogUtil.d(TAG, longitude + "," + latitude);
            //天气信息获取地址
            String cityStr = longitude + "," + latitude;
            getWeatherInfo(cityStr);
        }
    }
    //网络获取天气信息
    private void getWeatherInfo(String city){
        //获取天气信息的网络地址
        String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" +
                city + "&key=001be178c1a54af7971a895bebff259e";
        //获取天气信息
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d(TAG, "响应返回的天气信息");
                //获取返回的数据
                String weatherString = response.body().string();
                LogUtil.d(TAG, weatherString);
                //将返回的数据进行解析
                Weather weather = Utility.handleWeatherResponse(weatherString);
                locName = weather.basic.cityName;
                LogUtil.d(TAG + "----locName", locName);
                //如果天气信息不为空
                if (weather != null && "ok".equals(weather.status)){
                    SharedPreferences.Editor editor = PreferenceManager.
                            getDefaultSharedPreferences(AutoLocationService.this).edit();
                    editor.putString("weather", weatherString);
                    editor.apply();
                    LogUtil.d(TAG,"数据缓存成功");
                }
//                    Intent locIntent = new Intent(AutoLocationService.this, WeatherActivity.class);
//                    locIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(locIntent);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //关闭定位服务
        mLocationClient.stopLocation();
        LogUtil.d("AutoLocationService", "ONDestroy");
    }
}
