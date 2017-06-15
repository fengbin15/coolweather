package com.coolweather.android.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Drug on 2017/6/15.
 * 该类完成了将与服务器的交互功能
 */

public class HttpUtil {
    //定义静态方法完成对服务器的访问
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();   //创建服务器客户端
        Request request = new Request.Builder().url(address).build();  //创建请求
        client.newCall(request).enqueue(callback);
    }
}
