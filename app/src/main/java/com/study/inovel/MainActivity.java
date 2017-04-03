package com.study.inovel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.study.inovel.about.About;
import com.study.inovel.adapter.UpdateAdapter;
import com.study.inovel.bean.Book;
import com.study.inovel.db.DatabaseUtil;
import com.study.inovel.settings.SettingsPreferenceActivity;
import com.study.inovel.util.AddNovelActivity;
import com.study.inovel.util.Constant;
import com.study.inovel.util.DelNovelActivity;
import com.study.inovel.util.HtmlParserUtil;
import com.study.inovel.util.NetworkState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ListView listView;
    private UpdateAdapter adapter;
    private ProgressBar pb;
    private NavigationView mNavigationView;
    private DrawerLayout drawerLayout;
    private DatabaseUtil databaseUtil;
    List<Book> list=new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0x123)
            {
                Toast.makeText(MainActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);
        initView();
        adapter=new UpdateAdapter(MainActivity.this,list);
        listView.setAdapter(adapter);
        databaseUtil=DatabaseUtil.getInstance(this);
        mNavigationView.setNavigationItemSelectedListener(this);

    }
    private void initView()
    {
        pb=(ProgressBar)findViewById(R.id.pb);
        listView=(ListView)findViewById(R.id.listView);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        mNavigationView=(NavigationView)findViewById(R.id.nav_view);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(toolbar!=null)
            toolbar.setNavigationIcon(R.drawable.list);
        if(toolbar!=null)
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateNovelInfoLink();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNovelInfoLink();
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch(item.getItemId())
        {
            case R.id.nav_home:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_change_theme:
                drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        SharedPreferences sp =  getSharedPreferences("user_settings",MODE_PRIVATE);
                        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                                == Configuration.UI_MODE_NIGHT_YES) {
                            sp.edit().putInt("theme", 0).apply();
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        } else {
                            sp.edit().putInt("theme", 1).apply();
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        }
                        getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
                        recreate();
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {

                    }
                });
                break;
            case R.id.nav_settings:
                startActivity(new Intent(MainActivity.this,SettingsPreferenceActivity.class));
                break;
            case R.id.nav_add:
                startActivity(new Intent(MainActivity.this,AddNovelActivity.class));
                break;
            case R.id.nav_del:
                startActivity(new Intent(MainActivity.this,DelNovelActivity.class));
                break;
            case R.id.nav_about:
                startActivity(new Intent(MainActivity.this,About.class));
                break;
        }
        return true;
    }

    public void update(View view)
    {
        updateNovelInfoLink();
    }
    public void refresh()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        int count=databaseUtil.getNovelInfoLinkCount();
        if(list.size()>0)
        {
            list.clear();
        }
        if(count==0)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                }
            });
            Toast.makeText(MainActivity.this,"还未添加小说，请先添加小说",Toast.LENGTH_SHORT).show();
        }else
        {
            if(NetworkState.networkConnected(MainActivity.this)&&(NetworkState.wifiConnected(MainActivity.this)||NetworkState.mobileDataConnected(MainActivity.this)))
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(list.size()>0)
                        {
                            list.clear();
                        }
                        List<String> list12= databaseUtil.getNovelInfoLinkElement();
                        for(int i=0;i<list12.size();i++)
                        {
                            Book book=HtmlParserUtil.getUpdateInfo(list12.get(i));
                            list.add(book);
                        }
                        handler.sendEmptyMessage(0x123);
                    }
                }).start();

            }else
            {
                Toast.makeText(MainActivity.this,"无网络连接，请检查...",Toast.LENGTH_SHORT).show();
            }
        }


    }
    public void updateNovelInfoLink()
    {
        if(databaseUtil.getNovelLinkCount()==0)
        {
            databaseUtil.delNovelInfoLinkElement();
            refresh();
        }
        if(databaseUtil.getNovelInfoLinkCount()!=databaseUtil.getNovelLinkCount())
        {
            databaseUtil.delNovelInfoLinkElement();
            if(NetworkState.networkConnected(MainActivity.this)&&(NetworkState.wifiConnected(MainActivity.this)||NetworkState.mobileDataConnected(MainActivity.this)))
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String,String> map;
                        Iterator iterator;
                        List<Map<String,String>> listLinks=databaseUtil.getNovelLinkElement();
                        for(int i=0;i<listLinks.size();i++){
                            map=listLinks.get(i);
                            iterator=map.keySet().iterator();
                            String name=(String)iterator.next();
                            String tmpUrl1=map.get(name);
                            String tmpUrl2=HtmlParserUtil.getNovelLink(tmpUrl1);
                            databaseUtil.addLinkToNovelInfoLink(name,"http:"+tmpUrl2);
                        }
                    refresh();
                    }
                }).start();

            }
        }else
        {
            refresh();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_MOVE:

                break;
        }
        return true;
    }
}
