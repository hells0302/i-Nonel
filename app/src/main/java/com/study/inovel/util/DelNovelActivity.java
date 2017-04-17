package com.study.inovel.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.study.inovel.R;
import com.study.inovel.adapter.DelNovelAdapter;
import com.study.inovel.db.DatabaseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dnw on 2017/4/2.
 */
public class DelNovelActivity  extends AppCompatActivity{
    private DatabaseUtil databaseUtil;
    private List<String> listNovel;
    private DelNovelAdapter delNovelAdapter;
    private ListView listDel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_del_novel);
        setSupportActionBar((Toolbar) findViewById(R.id.delNovel_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initData();
    }
    private void initView()
    {
        listDel=(ListView)findViewById(R.id.del_list);
    }
    private void initData()
    {
        listNovel=new ArrayList<>();
        databaseUtil=DatabaseUtil.getInstance(this);
        listNovel=databaseUtil.getNovelNameElement();
        delNovelAdapter=new DelNovelAdapter(DelNovelActivity.this,listNovel,databaseUtil);
        listDel.setAdapter(delNovelAdapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
