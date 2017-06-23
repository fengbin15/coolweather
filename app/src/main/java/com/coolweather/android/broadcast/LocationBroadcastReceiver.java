package com.coolweather.android.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.coolweather.android.service.AutoLocationService;

/**
 * 启动定位服务
 */
public class LocationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //启动服务
        Intent locIntent = new Intent(context, AutoLocationService.class);
        context.startService(locIntent);
    }
}
