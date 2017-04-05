package com.study.inovel;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.study.inovel.about.About;
import com.study.inovel.adapter.UpdateAdapter;
import com.study.inovel.bean.Book;
import com.study.inovel.db.DatabaseUtil;
import com.study.inovel.service.CacheService;
import com.study.inovel.settings.SettingsPreferenceActivity;
import com.study.inovel.util.AddNovelActivity;
import com.study.inovel.util.Constant;
import com.study.inovel.util.DelNovelActivity;
import com.study.inovel.util.HtmlParserUtil;
import com.study.inovel.util.NetworkState;

import java.net.URL;
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
                //隐藏processBar
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
        databaseUtil=DatabaseUtil.getInstance(this);
        mNavigationView.setNavigationItemSelectedListener(this);
        startCacheService();
    }

    /**
     * 开启服务
     */
    private void startCacheService()
    {
        Intent intentStart=new Intent(this, CacheService.class);
        startService(intentStart);
    }

    /**
     * 初始化
     */
    private void initView()
    {

        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        mNavigationView=(NavigationView)findViewById(R.id.nav_view);
        //启动toolbar代替默认的toolbar
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
        listView=(ListView)findViewById(R.id.listView);
        adapter=new UpdateAdapter(MainActivity.this,list);
        listView.setAdapter(adapter);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.refreshLayout);
        //下拉刷新回调函数
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //调用刷新函数
                updateNovelInfoLink();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("Test",list.get(i).bookName);
                    Uri uri=Uri.parse("https://m.baidu.com/s?from=1086k&word="+list.get(i).bookName);
                    Intent intent1 = new Intent(Intent.ACTION_VIEW,uri);
                        startActivity(intent1);


            }
        });
    }

    /**
     * 每次调用onReasume时刷新
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateNovelInfoLink();
    }

    /**
     * NavigationViewde item点击事件
     * @param item
     * @return
     */

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

    /**
     * 手动更新
     * @param view
     */
    public void update(View view)
    {
        updateNovelInfoLink();
    }

    /**
     * 刷新更新内容
     */
    public void refresh()
    {
        //更新UI
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        //获取数据库中小说链接总数
        int count=databaseUtil.getNovelInfoLinkCount();
        //更新时防止listview重复显示
        if(list.size()>0)
        {
            list.clear();
        }
        //数据库中小说链接总数为0，说明未添加小说
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
            //有网络是更新
            if(NetworkState.networkConnected(MainActivity.this)&&(NetworkState.wifiConnected(MainActivity.this)||NetworkState.mobileDataConnected(MainActivity.this)))
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(list.size()>0)
                        {
                            list.clear();
                        }
                        //获取小说链接地址
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

    /**
     * 根据添加的小说获取小说详情链接地址
     */
    public void updateNovelInfoLink()
    {
        //如果添加的小说数量为0，则表示已删除所有小说，清空数据库缓存
        if(databaseUtil.getNovelLinkCount()==0)
        {
            databaseUtil.delNovelInfoLinkElement();
            refresh();
        }
        //当添加的小说总数和获取到的小说链接总数不同，说明再次添加小说或者删除小说，刷新
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


}
