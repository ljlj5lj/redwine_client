package com.example.lj.redwine.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.activity.LoginActivity;
import com.example.lj.redwine.adapter.OrderAdatper;
import com.example.lj.redwine.adapter.RedwineAdapter;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.custom_widget.recyclerView.RefreshRecyclerView;
import com.example.lj.redwine.javabean.Orders;
import com.example.lj.redwine.javabean.Redwine;
import com.example.lj.redwine.util.ToastUtil;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment{
    SharedPreferences sharedPreferences;
    ConstantClass constantClass;
    RefreshRecyclerView order_list_view;//新产品列表
    OrderAdatper orderAdatper;//订单适配器
    List<Orders> ordersList;//红酒列表
    Intent intent;
    RequestQueue requestQueue;

    SwipeRefreshLayout order_refresh_layout;//下拉刷新控件
    private Handler handler = new Handler();
    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        initView(view);//初始化控件
        requestData();//请求红酒数据
        initListener();//初始化监听器


        return view;
    }

    private void initListener() {
        order_list_view.setOnLoadMoreListener(new RefreshRecyclerView.OnLoadMoreListener() {
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

        order_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        order_refresh_layout.setRefreshing(false);
                        order_list_view.setLoadMoreEnable(true);
                        requestData();
                        order_list_view.notifyData();
                    }
                }, 2000);
            }
        });
    }

    private void requestMoreData() {
        int id = orderAdatper.getItemCount();
        requestQueue = Volley.newRequestQueue(getContext());
        String url = constantClass.getHttp_prefix()+"/orders/listOrderByUserId?id="+id+"&user_id="+sharedPreferences.getInt("id",0);
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                ordersList = JSON.parseArray(s, Orders.class);
                if (ordersList.size() == 3){
                    orderAdatper.addItem(ordersList);
                    order_list_view.notifyData();
                } else if (ordersList != null && ordersList.size() > 0 && ordersList.size() < 3){
                    orderAdatper.addItem(ordersList);
                    order_list_view.notifyData();
                    order_list_view.setLoadMoreEnable(false);
                    ToastUtil.show(getContext(), "数据已经加载完");
                } else if (ordersList.size() < 1) {
                    order_list_view.setLoadMoreEnable(false);
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
        requestQueue = Volley.newRequestQueue(getContext());
        String url = constantClass.getHttp_prefix()+"/orders/listOrderByUserId?id=0&user_id="+sharedPreferences.getInt("id",0);
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                ordersList = JSON.parseArray(s, Orders.class);
                if (ordersList.size() < 3) {
                    order_list_view.setLoadMoreEnable(false);
                    ToastUtil.show(getContext(), "只有"+ordersList.size()+"条数据");
                }
                orderAdatper = new OrderAdatper(getContext(), ordersList);
                order_list_view.setAdapter(orderAdatper);
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
        order_refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.order_refresh_layout);
        //设置下拉刷新控件颜色
        order_refresh_layout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        //添加刷新事件监听器
        order_list_view = (RefreshRecyclerView) view.findViewById(R.id.order_list_view);
        order_list_view.setLayoutManager(new LinearLayoutManager(getContext()));
        order_list_view.setLoadMoreEnable(true);
        order_list_view.setFooterResource(R.layout.load_more_layout);
    }

}
