package com.study.inovel.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.study.inovel.R;

/**
 * Created by dnw on 2017/4/2.
 */
public class About extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_about);
        initView();
    }

    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.addNovel_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
