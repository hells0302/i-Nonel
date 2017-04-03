package com.study.inovel.settings;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.study.inovel.R;

/**
 * Created by dnw on 2017/4/1.
 */
public class SettingsPreferenceActivity  extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_setting);
        initView();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        PrefFragment prefFragment = new PrefFragment();
        transaction.add(R.id.frameLayout, prefFragment);
        transaction.commit();
    }
    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.setting_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
