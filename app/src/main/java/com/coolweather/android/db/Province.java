package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Drug on 2017/6/15.
 *
 * 创建一个省类，其中包括省ID，省名字，省编码
 */

public class Province extends DataSupport {
    private int id;     //省ID
    private String provinceName;    //省名
    private int provinceCode;   //省编码

    //get和set方法
    public void setId(int id) {
        this.id = id;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getId() {
        return id;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }
}
