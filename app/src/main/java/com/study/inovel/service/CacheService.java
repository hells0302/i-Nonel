package com.study.inovel.service;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.study.inovel.MainActivity;
import com.study.inovel.R;
import com.study.inovel.bean.CacheBook;
import com.study.inovel.broadcast.AlarmReceiver;
import com.study.inovel.db.DatabaseUtil;
import com.study.inovel.util.HtmlParserUtil;
import com.study.inovel.util.NetworkState;

import java.util.List;

/**
 * Created by dnw on 2017/4/3.
 */
public class CacheService extends Service {
    public static final int anMin=60*1000;
    private List<CacheBook> lastList;
    private DatabaseUtil databaseUtil=DatabaseUtil.getInstance(this);
    private NotificationManager nm;
    private int setMin;
    SharedPreferences sharedPreferences;
    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0x234)
            {
                //此处比较两个列表是否相同
                List<CacheBook> listDb=databaseUtil.getNovelCacheElement();
                //循环比较
                if(lastList.size()>0&&listDb.size()>0)
                {
                    for(int i=0;i<lastList.size();i++)
                    {
                        CacheBook book1=lastList.get(i);
                        for(int j=0;j<listDb.size();j++)
                        {
                            CacheBook book2=listDb.get(j);
                            //书名相同之后比较两书的更新时间，不同发出更新通知

                            if(book1.cacheBookName.equals(book2.cacheBookName))
                            {
                                Log.d("test",book1.cacheBookName+">>"+book1.cacheUpdateTitle+">>"+book1.cacheUpdateTime+"  "+book2.cacheBookName+">>"+book2.cacheUpdateTitle+">>"+book2.cacheUpdateTime);
                                if(!book1.cacheUpdateTitle.equals(book2.cacheUpdateTitle))
                                {
                                    //此处发出更新通知
                                    Intent intent=new Intent(CacheService.this, MainActivity.class);
                                    PendingIntent pi=PendingIntent.getActivity(CacheService.this,0,intent,0);
                                    NotificationCompat.Builder builder=new NotificationCompat.Builder(CacheService.this);
                                    builder.setContentTitle(book2.cacheBookName+">>有更新");
                                    builder.setContentText(book2.cacheUpdateTitle+"  "+book2.cacheUpdateTime);
                                    builder.setWhen(System.currentTimeMillis());
                                    builder.setSmallIcon(R.drawable.ic_fiber_new_white_36dp);
                                    builder.setColor(Color.parseColor("#EAA935"));
                                    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.icon));
                                    builder.setAutoCancel(true);
                                    builder.setContentIntent(pi);
                                    //获取设置页的是否震动参数
                                    if(sharedPreferences.getBoolean("vibrator_mode",true))
                                    {
                                        builder.setDefaults(Notification.DEFAULT_VIBRATE);
                                    }
                                    //获取设置页的呼吸灯是否闪烁参数
                                    if(sharedPreferences.getBoolean("light_mode",false))
                                    {
                                        builder.setDefaults(Notification.DEFAULT_LIGHTS);
                                    }
                                    if(sharedPreferences.getBoolean("sound_mode",true))
                                    {
                                        builder.setDefaults(Notification.DEFAULT_SOUND);
                                    }

                                    Notification notification=builder.getNotification();
                                    //注意每一条更新都要通知
                                    nm.notify(j,notification);
                                }
                            }
                        }
                    }
                }
            }
        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("Test","onStartCommand");
        if(sharedPreferences==null)
            sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        if(nm==null)
            nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        setMin=Integer.parseInt(sharedPreferences.getString("time_of_refresh","120"));
        //时间间隔到后执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("test","in Thread");
                cacheRefresh();
                handler.sendEmptyMessage(0x234);
            }

        }).start();

        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        long triggerAtTime= SystemClock.elapsedRealtime()+setMin*anMin;
        Intent i=new Intent(this,AlarmReceiver.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi=PendingIntent.getBroadcast(this,0,i,0);
        int currentVersion=Build.VERSION.SDK_INT;

        if(currentVersion>=19) {
           manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        }else
        {
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        }

        return START_STICKY;
    }
    private void cacheRefresh()
    {
        //更新之前获取上次更新的数据
        lastList=databaseUtil.getNovelCacheElement();
        if(databaseUtil.getNovelInfoLinkCount()>0)
        {
            if(NetworkState.networkConnected(this)&&(NetworkState.wifiConnected(this)||NetworkState.mobileDataConnected(this))) {
                List<String>listLink=databaseUtil.getNovelInfoLinkElement();
                if(databaseUtil.delAllNovelCacheElement())
                    for(int i=0;i<listLink.size();i++)
                    {
                        CacheBook book=HtmlParserUtil.getCacheUpdateInfo(listLink.get(i));
                        if(book!=null)
                            databaseUtil.cacheToNovelCache(book.cacheBookName,book.cacheAuthor,book.cacheUpdateTitle,book.cacheUpdateTime);
                    }

            }
        }

    }

}
