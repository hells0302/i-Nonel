package com.study.inovel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

/**
 * Created by dnw on 2017/6/21.
 */

public class WelcomeActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    LinearLayout ll;
    private boolean isFirstIn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ll=(LinearLayout)findViewById(R.id.wel_ll);
        ScaleAnimation sa=new ScaleAnimation(0,1f,0,1f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        sa.setDuration(500);
        LayoutAnimationController lac=new LayoutAnimationController(sa,0.5f);
        lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
        ll.setLayoutAnimation(lac);
        SharedPreferences preferences=getSharedPreferences("first_pref",MODE_PRIVATE);
        isFirstIn=preferences.getBoolean("isFirstIn",true);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if(isFirstIn)
                {
                    intent=new Intent(WelcomeActivity.this,GuideActivity.class);
                }
                else
                {
                    intent=new Intent(WelcomeActivity.this,MainActivity.class);
                }
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        },1500);
    }
}
