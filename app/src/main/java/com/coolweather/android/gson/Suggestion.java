package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Drug on 2017/6/16.
 * 创建建议类，包含舒适度，洗车和运动三个方面
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;   //舒适度信息
    @SerializedName("cw")
    public CarWash carWarsh;    //洗车建议

    public Sport sport;      //运动建议

    //舒适度提示类
    public class Comfort{
        @SerializedName("txt")
        public String info;    //提示信息
    }
    //洗车提示类
    public class CarWash{
        @SerializedName("txt")
        public String info;    //提示信息
    }
    //运动提示类
    public class Sport{
        @SerializedName("txt")
        public String info;    //提示信息
    }

}
