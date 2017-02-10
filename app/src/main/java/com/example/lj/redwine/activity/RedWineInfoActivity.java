package com.example.lj.redwine.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lj.redwine.R;

import com.example.lj.redwine.custom_widget.pagerSlidingTabStrip.PagerSlidingTabStrip;
import com.example.lj.redwine.fragment.RedwineCommentFragment;
import com.example.lj.redwine.fragment.RedwineDetailFragment;

public class RedWineInfoActivity extends FragmentActivity implements View.OnClickListener{
    int redwine_id;
    Intent intent;
    ViewPager viewPager;
    PagerSlidingTabStrip tabs;
    LinearLayout back_layout;//后退布局
    TextView text_back;//后退文本


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_wine_info);
        intent = getIntent();
        redwine_id = intent.getIntExtra("redwine_id", 0);
        InitView();//初始化布局
    }


    private void InitView() {

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new myPagerAdapter(getSupportFragmentManager()));

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);
        tabs.setUnderlineColorResource(R.color.white);
        tabs.setIndicatorColorResource(R.color.main_color);
        tabs.setIndicatorHeight(8);
        tabs.setTextSize(25);
        tabs.setTextColorResource(R.color.midblack);
        tabs.setBackgroundResource(R.color.white);
        tabs.setDividerColorResource(R.color.white);
        back_layout = (LinearLayout) findViewById(R.id.back_layout);


        text_back = (TextView) findViewById(R.id.text_back);
        text_back.setText("商品详情");

        //添加事件监听器
        back_layout.setOnClickListener(this);

    }

    class myPagerAdapter extends FragmentPagerAdapter {
        String[] title = { "商品", "评论" };
        RedwineDetailFragment redwineDetailFragment;
        RedwineCommentFragment redwineCommentFragment;
        public myPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt("redwine_id", redwine_id);
            switch (position) {
                case 0:
                    redwineDetailFragment = new RedwineDetailFragment();
                    redwineDetailFragment.setArguments(bundle);
                    return redwineDetailFragment;
                case 1:
                    redwineCommentFragment = new RedwineCommentFragment();
                    redwineCommentFragment.setArguments(bundle);
                    return redwineCommentFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {

            return title.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_layout:
                RedWineInfoActivity.this.finish();
                break;

        }
    }
}
