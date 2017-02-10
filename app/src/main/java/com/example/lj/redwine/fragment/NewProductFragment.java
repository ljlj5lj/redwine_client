package com.example.lj.redwine.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.app.Fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;


import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.activity.NewProductDescActivity;
import com.example.lj.redwine.activity.SalesDescActivity;
import com.example.lj.redwine.adapter.AdvertisementAdapter;
import com.example.lj.redwine.adapter.FavoritesBaseAdapter;
import com.example.lj.redwine.adapter.NewProductAdapter;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.custom_widget.recyclerView.RefreshRecyclerView;
import com.example.lj.redwine.custom_widget.viewpager_autoscroll.AutoScrollViewPager;
import com.example.lj.redwine.custom_widget.viewpager_autoscroll.CirclePageIndicator;
import com.example.lj.redwine.javabean.Favorites;
import com.example.lj.redwine.javabean.Redwine;
import com.example.lj.redwine.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewProductFragment extends Fragment implements View.OnClickListener{
    Intent intent;
    LinearLayout time_layout;//限时抢购
    LinearLayout sales_layout;//热销商品
    LinearLayout news_layout;//最新红酒
    LinearLayout load_more;//加载布局
    ConstantClass constantClass;
    SwipeRefreshLayout new_product_refresh_layout;//下拉刷新
    RefreshRecyclerView new_product_list_view;//新产品列表
    RequestQueue requestQueue;//请求队列
    NewProductAdapter newProductAdapter;//新品适配器
    List<Redwine> redwineList;//红酒列表
    AutoScrollViewPager auto_scroll_viewpager; // 自动轮播ViewPager
    CirclePageIndicator circle_indicator; // 圆圈指示器
    RelativeLayout advertisement_layout; // 广告栏布局
    ArrayList<ImageView> imageList; // 广告图片列表
    // 图片资源ID
    private final int[] imageIds = {R.drawable.redwine_1, R.drawable.redwine_2, R.drawable.redwine_3, R.drawable.redwine_4};
    public NewProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_product, container, false);
        initView(view); // 初始化控件
        requestData();//请求红酒数据
        initData(); // 初始化数据
        initListener();//初始化监听器
        return view;
    }

    private void initListener() {
        new_product_list_view.setOnLoadMoreListener(new RefreshRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMoreListener() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestMoreData();
                    }
                }, 2000);
            }
        });

        new_product_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new_product_refresh_layout.setRefreshing(false);
                        new_product_list_view.setLoadMoreEnable(true);
                        requestData();
                        new_product_list_view.notifyData();
                    }
                }, 2000);
            }
        });
    }

    private void requestMoreData() {
        int id = newProductAdapter.getItemCount();
        requestQueue = Volley.newRequestQueue(getContext());
        String url = constantClass.getHttp_prefix()+"/redwine/listRedwineOrderByTime?id="+id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                redwineList = JSON.parseArray(s, Redwine.class);
                if (redwineList.size() == 2){
                    newProductAdapter.addItem(redwineList);
                    new_product_list_view.notifyData();
                } else if (redwineList != null && redwineList.size() > 0 && redwineList.size() < 2){
                    newProductAdapter.addItem(redwineList);
                    new_product_list_view.notifyData();
                    new_product_list_view.setLoadMoreEnable(false);
                    ToastUtil.show(getContext(), "数据已经加载完");
                } else if (redwineList.size() < 1) {
                    new_product_list_view.setLoadMoreEnable(false);
                    ToastUtil.show(getContext(), "数据已经加载完");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(getContext(), "网络出了点问题");
            }
        });
        requestQueue.add(stringRequest);
    }

    private void requestData() {
        int id = 0;
        requestQueue = Volley.newRequestQueue(getContext());
        String url = constantClass.getHttp_prefix()+"/redwine/listRedwineOrderByTime?id="+id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                redwineList = JSON.parseArray(s, Redwine.class);
                if (redwineList.size() < 2) {
                    new_product_list_view.setLoadMoreEnable(false);
                    ToastUtil.show(getContext(), "只有"+redwineList.size()+"条数据");
                }
                newProductAdapter = new NewProductAdapter(getContext(), redwineList);
                new_product_list_view.setAdapter(newProductAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(getContext(), "网络出了点问题");
            }
        });
        requestQueue.add(stringRequest);
    }

    private void initView(View view) {
        time_layout = (LinearLayout) view.findViewById(R.id.time_layout);
        sales_layout = (LinearLayout) view.findViewById(R.id.sales_layout);
        news_layout = (LinearLayout) view.findViewById(R.id.news_layout);

        auto_scroll_viewpager = (AutoScrollViewPager) view.findViewById(R.id.auto_scroll_viewpager);
        circle_indicator = (CirclePageIndicator) view.findViewById(R.id.circle_indicator);
        advertisement_layout = (RelativeLayout) view.findViewById(R.id.advertisement_layout);
        new_product_refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.new_product_refresh_layout);
        new_product_refresh_layout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        new_product_list_view = (RefreshRecyclerView) view.findViewById(R.id.new_product_list_view);
        new_product_list_view.setLayoutManager(new LinearLayoutManager(getContext()));
        new_product_list_view.setLoadMoreEnable(true);
        new_product_list_view.setFooterResource(R.layout.load_more_layout);

        time_layout.setOnClickListener(this);
        sales_layout.setOnClickListener(this);
        news_layout.setOnClickListener(this);

    }


    private void initData() {//初始化广告页面
        imageList = new ArrayList<ImageView>();
        for (int i = 0; i < imageIds.length; i++) { //将图片资源放进ImageView里面
            ImageView image = new ImageView(getActivity());
            image.setBackgroundResource(imageIds[i]);
            imageList.add(image);
        }

        auto_scroll_viewpager.setAdapter(new AdvertisementAdapter(getContext(),imageList)); // 自动轮播viewpager设置适配器
        auto_scroll_viewpager.setInterval(3000);//设置广告页面切换时间为3000毫秒
        auto_scroll_viewpager.startAutoScroll();//开启自动轮播
        circle_indicator.setViewPager(auto_scroll_viewpager);//将圆圈指示器导入viewpager中
        circle_indicator.setSnap(true);
    }
    private Handler handler = new Handler();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sales_layout:
                intent = new Intent(getContext(), SalesDescActivity.class);
                startActivity(intent);
                break;
            case R.id.news_layout:
                intent = new Intent(getContext(), NewProductDescActivity.class);
                startActivity(intent);
                break;
            case R.id.time_layout:
                ToastUtil.show(getContext(), "敬请期待");
                break;
        }
    }
}
