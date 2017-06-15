package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Drug on 2017/6/15.
 * 创建县类，包括县ID，县名：countyName,天气ID：weatherId，城市ID：cityId.
 */

public class County extends DataSupport {
    private int id;     //县id
    private String countyName;   //县名
    private String weatherId;     //天气id
    private int cityId;      //市ID

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
