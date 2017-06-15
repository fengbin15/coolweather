package com.coolweather.android.util;

import android.text.TextUtils;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Drug on 2017/6/15.
 * 完成数据解析功能，将省市县的数据解析存入数据库
 */

public class Utility {
    /**
     * 解析和处理服务器返回的数据，完成省级数据的解析并存入数据库
     */
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            //判断返回数据是否为空，若不为空则进行解析
            try {
                JSONArray allProvinces = new JSONArray(response);
                //遍历解析出来的数据，将数据存入数据库
                for (int i = 0; i < allProvinces.length(); i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i); //获取json对象
                    Province province = new Province();  //创建省份对象
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    //存入数据库
                    province.save();
                }
                return true;
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的市级response数据，并存入数据库
     */

    public static boolean handleCityResponse(String response, int provinceId){
        //判断返回的数据是否为空
        if (!TextUtils.isEmpty(response)){
            //不为空，进行解析
            try{
                //从response中获取city数据
                JSONArray allCities = new JSONArray(response);
                //遍历cityObjects读取数据存入数据库
                for (int i = 0; i < allCities.length(); i++){
                    //读取city对象
                    JSONObject cityObject = allCities.getJSONObject(i);
                    //实例化城市对象
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    //存入数据库
                    city.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据并存入服务器
     */
    public static boolean handleCountyResponse(String response, int cityId){
        //判断获取的response是否为空
        if (!TextUtils.isEmpty(response)){
            try {
                //不为空，进行解析
                JSONArray allCounties = new JSONArray(response);
                //遍历数据存入数据库
                for (int i = 0; i < allCounties.length(); i++) {
                    //从Json中获取对象
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    //实例化县级对象
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();   //保存进数据库
                }
                return true;
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
