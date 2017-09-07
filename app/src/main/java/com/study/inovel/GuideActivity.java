package com.study.inovel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dnw on 2017/6/21.
 */

public class GuideActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{
    private ViewPager vp;
    private GuideViewPaperAdapter vpAdapter;
    private List<View> views;
    private Button button;

    // 引导图片资源
    private static final int[] pics = { R.drawable.guide1, R.drawable.guide2,R.drawable.guide3, R.drawable.guide4};
    // 底部小点图片
    private ImageView[] dots;
    // 记录当前选中位置
    private int currentIndex;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpaper_point);
        //首次启动标记
        SharedPreferences preferences=getSharedPreferences("first_pref",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean("isFirstIn",false);
        editor.commit();

        initView();
        views = new ArrayList<View>();
        // 为引导图片提供布局参数
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        // 初始化引导图片列表
        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            iv.setImageResource(pics[i]);
            views.add(iv);
        }
        // 初始化Adapter
        vpAdapter = new GuideViewPaperAdapter(views);
        vp.setAdapter(vpAdapter);
        // 绑定回调
        vp.setOnPageChangeListener(this);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(GuideActivity.this,MainActivity.class);
                startActivity(intent);
                GuideActivity.this.finish();
            }
        });
    }
    private void initView() {
        button = (Button) findViewById(R.id.button);
        vp = (ViewPager) findViewById(R.id.viewpager);
        // 初始化底部小圆点
        initDots();
    }

    private void initDots() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

        dots = new ImageView[pics.length];

        // 循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            //View android.view.ViewGroup.getChildAt(int index)：
            //Returns the view at the specified position in the group.
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(false);// 都设为灰色
            dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
            //View中的setTag(Onbect)表示给View添加一个格外的数据，以后可以用getTag()将这个数据取出来。
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(true);// 设置为红色，即选中状态
    }
    /**
     * 改变当前引导小点颜色ؿ
     */
    private void setCurDot(int position) {
        if (position < 0 || position > pics.length - 1 || currentIndex == position) {
            return;
        }
        System.out.println("position="+position);
        dots[position].setEnabled(true);
        //此时的currentIndex指的是上一个圆点
        System.out.println("currentIndex="+currentIndex);
        dots[currentIndex].setEnabled(false);
        //现在的currentIndex指的是当前的小圆点
        currentIndex = position;
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
// 设置底部小点选中状态
        setCurDot(position);
        if (position == 3) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


}
