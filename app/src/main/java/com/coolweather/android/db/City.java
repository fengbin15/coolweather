package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Drug on 2017/6/15.
 * 创建一个市类，包括，市ID，市名字cityName，市编码cityCode,省ID：provinceId
 */

public class City extends DataSupport {
    private int id;   //市id
    private String cityName;   //市名
    private int cityCode;     //市编码
    private int provinceId;    //省id

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getProvinceId() {
        return provinceId;
    }
}
