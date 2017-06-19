package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Drug on 2017/6/16.
 * 天气预报类，包括日期date， 天气状况， 温度变化
 */

public class Forecast {

    public String date;    //日期
    @SerializedName("cond")
    public More more;   //更多信息
    @SerializedName("tmp")
    public Temprature temprature;   //温度

    public class More{
        @SerializedName("txt_d")
        public String info;   //天气状况信息
    }

    public class Temprature{
        public String max;   //最大温度
        public String min;   //最小温度
    }

}
