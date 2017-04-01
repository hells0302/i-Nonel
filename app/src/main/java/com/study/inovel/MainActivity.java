package com.study.inovel;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.study.inovel.adapter.UpdateAdapter;
import com.study.inovel.bean.Book;
import com.study.inovel.util.Constant;
import com.study.inovel.util.HtmlParserUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private ListView listView;
    private UpdateAdapter adapter;
    private ProgressBar pb;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        pb=(ProgressBar)findViewById(R.id.pb);
        listView=(ListView)findViewById(R.id.listView);
        adapter=new UpdateAdapter(MainActivity.this,list);
        listView.setAdapter(adapter);
    }
    public void update(View view)
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
                String url= Constant.QIDIANLINK+"圣墟";
                String tmp= HtmlParserUtil.getNovelLink(url);
                Book book=HtmlParserUtil.getUpdateInfo("http:"+tmp);
                list.add(book);
                String url1= Constant.QIDIANLINK+"大主宰";
                String tmp1= HtmlParserUtil.getNovelLink(url1);
                Book book1=HtmlParserUtil.getUpdateInfo("http:"+tmp1);
                list.add(book1);
                String url4= Constant.QIDIANLINK+"武炼巅峰";
                String tmp4= HtmlParserUtil.getNovelLink(url4);
                Book book4=HtmlParserUtil.getUpdateInfo("http:"+tmp4);
                list.add(book4);
                String url3= Constant.QIDIANLINK+"武神天下";
                String tmp3= HtmlParserUtil.getNovelLink(url3);
                Book book3=HtmlParserUtil.getUpdateInfo("http:"+tmp3);
                list.add(book3);
                String url2= Constant.QIDIANLINK+"无敌天下";
                String tmp2= HtmlParserUtil.getNovelLink(url2);
                Book book2=HtmlParserUtil.getUpdateInfo("http:"+tmp2);
                list.add(book2);

                handler.sendEmptyMessage(0x123);
            }
        }).start();
    }
}
