package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Drug on 2017/6/16.
 * 基础类，包含城市名，天气ID，更新日期
 */

public class Basic {

    @SerializedName("city")
    public String cityName;     //城市名

    @SerializedName("id")
    public String weatherId;    //天气ID

    public Update update;   //更新日期

    public class Update{
        @SerializedName("loc")
        public String updateTime;   //更新日期
    }
}
