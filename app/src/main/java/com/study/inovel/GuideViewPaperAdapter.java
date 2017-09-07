package com.study.inovel;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import java.util.List;

/**
 * Created by dnw on 2017/6/21.
 */

public class GuideViewPaperAdapter extends PagerAdapter {
    private List<View> views;
    public GuideViewPaperAdapter(List<View> views) {
        this.views=views;
    }
    //删除界面
    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView(views.get(arg1));
    }
    @Override
    public int getCount() {
        if(views!=null)
        {
            return views.size();
        }
        return 0;
    }
    @Override
    public void finishUpdate(View arg0) {
        // TODO Auto-generated method stub

    }
    @Override
    public Object instantiateItem(View container, int position) {
        ((ViewPager)container).addView(views.get(position),0);
        return views.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==object);
    }
}
