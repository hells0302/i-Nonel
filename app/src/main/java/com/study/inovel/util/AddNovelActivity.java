package com.study.inovel.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.study.inovel.R;
import com.study.inovel.db.DatabaseUtil;

/**
 * Created by dnw on 2017/4/1.
 */
public class AddNovelActivity extends AppCompatActivity {
    private Spinner spinner;
    private EditText et;
    private String selectNovelSite;
    private DatabaseUtil databaseUtil;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_novel);

        initView();
        //initSpinner();
    }
    private void initView()
    {
        setSupportActionBar((Toolbar) findViewById(R.id.addNovel_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spinner=(Spinner)findViewById(R.id.spinner);
        et=(EditText)findViewById(R.id.novel_name);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectNovelSite=(String)spinner.getSelectedItem();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectNovelSite="";
            }
        });
        databaseUtil=DatabaseUtil.getInstance(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
    public void add(View view)
    {

        if(et.getText().toString().equals(""))
        {
            Toast.makeText(AddNovelActivity.this,"小说名不能为空",Toast.LENGTH_SHORT).show();
        }else
        {
            if(selectNovelSite.equals("")||selectNovelSite.equals("选择小说首发网站"))
            {
                Toast.makeText(AddNovelActivity.this,"请选择小说首发网站",Toast.LENGTH_SHORT).show();
            }else if(selectNovelSite.equals("起点"))
            {
                if (databaseUtil.isExist(et.getText().toString().trim()))
                {
                    Toast.makeText(AddNovelActivity.this,"此小说已经添加过",Toast.LENGTH_SHORT).show();
                }else
                {
                    //此处未添加检测小说是否为本网站首发
                    if(databaseUtil.addLinkToDatabase(et.getText().toString().trim(),Constant.QIDIANLINK+et.getText().toString().trim()))
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddNovelActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                                et.setText("");
                            }
                        });
                    }
                }


                }

            }
        }

    }
