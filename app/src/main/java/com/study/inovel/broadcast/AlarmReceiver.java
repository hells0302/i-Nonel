package com.study.inovel.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.study.inovel.service.CacheService;

/**
 * Created by dnw on 2017/4/3.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            //时间到后再次启动service
            Intent i=new Intent(context, CacheService.class);
            context.startService(i);
    }
}
