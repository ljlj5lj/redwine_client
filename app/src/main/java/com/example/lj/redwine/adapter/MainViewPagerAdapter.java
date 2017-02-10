package com.example.lj.redwine.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by L.J on 2016/8/3.
 */
public class MainViewPagerAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> list;
    public MainViewPagerAdapter(FragmentManager fragmentManager ,ArrayList<Fragment> list){
        super(fragmentManager);
        setList(list);
    }
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void setList(ArrayList<Fragment> list) {
        if (list == null) {
            this.list = new ArrayList<Fragment>();
        } else {
            this.list = list;
        }
    }
}
