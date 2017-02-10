package com.example.lj.redwine.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by L.J on 2016/8/11.
 */
public class AdvertisementAdapter extends PagerAdapter { //广告轮播图适配器
    Context context; // 上下文对象
    ArrayList<ImageView> imageList; // 图片列表

    public AdvertisementAdapter(Context context, ArrayList<ImageView> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        if (view == object) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View) object);

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(imageList.get(position % imageList.size()));
        return imageList.get(position % imageList.size());
    }
}
