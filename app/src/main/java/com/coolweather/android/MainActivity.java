package com.coolweather.android;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.coolweather.android.gson.Weather;
import com.coolweather.android.service.AutoLocationService;
import com.coolweather.android.util.Utility;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //日志标签
    String TAG = "MainActivity";

    //获取主活动布局
    FrameLayout mainLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //判断权限是否开启
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.
                permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.
                permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.
                permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }else{
            //启动定位服务
            Intent autoIntent = new Intent(this, AutoLocationService.class);
            startService(autoIntent);
        }

        //检查缓冲区中是否存在天气数据，若存在则直接读取，否则继续
        SharedPreferences mainPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String mWeatherString = mainPrefs.getString("weather", null);
        if (mWeatherString != null){
            //解析天气数据
            Weather mWeather = Utility.handleWeatherResponse(mWeatherString);
            String weatherId = mWeather.basic.weatherId;
            Intent mIntent = new Intent(this, WeatherActivity.class);
            mIntent.putExtra("weather_id", weatherId);
            startActivity(mIntent);
            finish();
        }

        //获取按钮实例并添加点击事件
        mainLayout = (FrameLayout)findViewById(R.id.main_layout);
        Button manualWeatherButton = (Button)findViewById(R.id.manual_weather_button);
        Button autoWeatherButton = (Button)findViewById(R.id.auto_weather_button);
        //添加事件
        manualWeatherButton.setOnClickListener(this);
        autoWeatherButton.setOnClickListener(this);
    }

    //动态添加碎片
    private void replaceFragment(Fragment fragment){
        FragmentManager manager = getSupportFragmentManager();   //碎片管理器
        FragmentTransaction transaction = manager.beginTransaction(); //开启事务
        transaction.replace(R.id.main_layout, fragment);
        //提交事务
        transaction.commit();
    }

    //重启应用
    private void restartApplication() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0){
                    for (int result : grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this, "必须同意权限才能使用本程序！",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    //启动定位服务
                    Intent autoIntent = new Intent(this, AutoLocationService.class);
                    startService(autoIntent);
                }else {
                    Toast.makeText(this, "发生未知错误！", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.manual_weather_button:
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(this).edit();
                editor.putString("weather", null);
                editor.apply();
                replaceFragment(new ChooseAreaFragment());
                break;
            case R.id.auto_weather_button:
                replaceFragment(new AutoShowWeatherFragment());
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, AutoLocationService.class);
        stopService(intent);
    }
}
