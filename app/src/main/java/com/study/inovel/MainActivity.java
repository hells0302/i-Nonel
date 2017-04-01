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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.study.inovel.adapter.UpdateAdapter;
import com.study.inovel.bean.Book;
import com.study.inovel.db.DatabaseUtil;
import com.study.inovel.settings.SettingsPreferenceActivity;
import com.study.inovel.util.AddNovelActivity;
import com.study.inovel.util.Constant;
import com.study.inovel.util.HtmlParserUtil;
import com.study.inovel.util.NetworkState;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ListView listView;
    private UpdateAdapter adapter;
    private ProgressBar pb;
    private NavigationView mNavigationView;
    private DrawerLayout drawerLayout;
    private DatabaseUtil databaseUtil;
    List<Book> list=new ArrayList<>();
    private Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0x123)
            {
                Toast.makeText(MainActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                if(pb.getVisibility()==View.VISIBLE)
                {
                    pb.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);
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
        adapter=new UpdateAdapter(MainActivity.this,list);
        listView.setAdapter(adapter);
        databaseUtil=DatabaseUtil.getInstance(this);
        mNavigationView.setNavigationItemSelectedListener(this);
//        if(list.size()==0)
//        {
//            refresh();
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        refresh();
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
        }
        return true;
    }

    public void update(View view)
    {
        refresh();
    }
    public void refresh()
    {
        int count=databaseUtil.getNovelLinkCount();
        if(list.size()>0)
        {
            list.clear();
        }
        if(count==0)
        {
            Toast.makeText(MainActivity.this,"还未添加小说，请先添加小说",Toast.LENGTH_SHORT).show();
        }else
        {
            if(NetworkState.networkConnected(MainActivity.this))
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(list.size()>0)
                        {
                            list.clear();
                        }
                        if(pb.getVisibility()==View.GONE)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pb.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                        List<String> list1=databaseUtil.getNovelLinkElement();
                        for(int i=0;i<list1.size();i++)
                        {
                            String tmp= HtmlParserUtil.getNovelLink(list1.get(i));
                            Book book=HtmlParserUtil.getUpdateInfo("http:"+tmp);
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
}
