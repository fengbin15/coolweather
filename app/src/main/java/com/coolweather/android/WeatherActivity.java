package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.LogUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    //定义各种控件
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;

    private LinearLayout forcastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    //必应图片显示区
    private ImageView bingPicImg;

    //获取刷新布局
    public SwipeRefreshLayout swipeRefreshLayout;

    //定义滑动菜单布局
    public DrawerLayout drawerLayout;
    //home按钮
    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        if (Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        //实例化各种控件
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);

        forcastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);
        comfortText = (TextView)findViewById(R.id.comfort_text);
        carWashText = (TextView)findViewById(R.id.car_wash_text);
        sportText = (TextView)findViewById(R.id.sport_text);
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);

        //实例化刷新控件
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_fresh_layout);

        //定义获取天气信息的天气ID
        final String weatherId;

        //实例化滑动菜单布局
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        //按钮Nav_button
        navButton = (Button)findViewById(R.id.nav_button);
        //点击NavButton跳出滑动菜单
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //数据缓存
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather", null);
        //从缓存中获取图片
        String bingPic = preferences.getString("bing_pic", null);
        if (weatherString != null){
            //如果缓冲区数据不为空,就解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else {
            //不存在就从服务器上获取并缓存
            weatherId = getIntent().getStringExtra("weather_id");  //获取天气ID
            weatherLayout.setVisibility(View.INVISIBLE);   //隐藏天气布局
            requestWeather(weatherId);
        }

        //将图片加载进图片显示区
        if (bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else {
            loadBingPic();
        }

        //刷新响应操作
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //响应操作
                requestWeather(weatherId);
            }
        });
    }

    //从服务器上获取天气信息
    public void requestWeather(final String weatherId){
        //获取天气信息地址
        String address = "https://free-api.heweather.com/v5/weather?city=" +
                weatherId + "&key=001be178c1a54af7971a895bebff259e";
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();  //获取数据信息
                final Weather weather = Utility.handleWeatherResponse(responseText);
//                LogUtil.d("故障测试---", weather.forecastList.size() + "");
//                for (int i = 0; i < weather.forecastList.size(); i++) {
//                    LogUtil.d("预报数据日期-------", weather.forecastList.get(i).date);
//                    LogUtil.d("预报数据最大温度---", weather.forecastList.get(i).temprature.max);
//                    LogUtil.d("预报数据最小温度---", weather.forecastList.get(i).temprature.min);
//                    LogUtil.d("预报数据天气状况---", weather.forecastList.get(i).more.info.toString());
//                }
                LogUtil.d("故障测试---", responseText + weather.status);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            //获取数据成功
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText); //将数据放入缓存
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        //加载图片
        loadBingPic();
    }

    //显示天气信息
    private void showWeatherInfo(Weather weather){
        if (weather != null && "ok".equals(weather.status)){
            String cityName = weather.basic.cityName;   //城市名称
            String updateTime = weather.basic.update.updateTime.split(" ")[1];  //更新时间
            String degree = weather.now.temperature + "℃";   //温度
            String weatherInfo = weather.now.more.info;     //天气状况
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);

            //预报天气布局信心
            forcastLayout.removeAllViews();
            //LogUtil.d("故障测试---", "遍历天气预报");
            for (Forecast forecast : weather.forecastList){
                View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                        forcastLayout, false);
                TextView dateText = (TextView)view.findViewById(R.id.date_text);
                TextView infoText = (TextView)view.findViewById(R.id.info_text);
                TextView maxText = (TextView)view.findViewById(R.id.max_text);
                TextView minText = (TextView)view.findViewById(R.id.min_text);
                dateText.setText(forecast.date);
                infoText.setText(forecast.more.info);
                maxText.setText(forecast.temprature.max);
                minText.setText(forecast.temprature.min);
                forcastLayout.addView(view);
            }

            if (weather.aqi != null){
                //如果空气质量状况信息不为空
                aqiText.setText(weather.aqi.city.aqi);
                pm25Text.setText(weather.aqi.city.pm25);
            }

            //建议小贴士
            String comfort = "舒适度：" + weather.suggestion.comfort.info;
            //LogUtil.d("故障测试---", comfort);
            String carWash = "洗车指数：" + weather.suggestion.carWarsh.info;
            //LogUtil.d("故障测试---", carWash);
            String sport = "运动指数：" + weather.suggestion.sport.info;
            //LogUtil.d("故障测试---", sport);

            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);
            weatherLayout.setVisibility(View.VISIBLE);
            //启动定时自动更新服务
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        }else {
            Toast.makeText(WeatherActivity.this, "获取天气失败！",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                    //获取图片信息
                final String bingPic = response.body().string();
                //将图片存入缓冲区
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                //将图片加载显示
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
}
