package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Drug on 2017/6/16.
 *
 * 当前天气类Now，包括温度tmp和天气信息comd
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;    //温度

    @SerializedName("cond")
    public More more;     //更多信息

    public class More{
        @SerializedName("txt")
        public String info;   //天气信息
    }
}
