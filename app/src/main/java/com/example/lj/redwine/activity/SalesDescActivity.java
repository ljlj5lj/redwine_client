package com.example.lj.redwine.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.adapter.RedwineAdapter;
import com.example.lj.redwine.cache.LruImageCache;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.custom_widget.recyclerView.RefreshRecyclerView;
import com.example.lj.redwine.javabean.Redwine;
import com.example.lj.redwine.util.ToastUtil;

import java.util.List;

public class SalesDescActivity extends Activity {
    LinearLayout back_layout;
    TextView text_back;
    ConstantClass constantClass;
    SwipeRefreshLayout red_wine_refresh_layout;//下拉刷新
    RefreshRecyclerView red_wine_list_view;//新产品列表
    RedwineAdapter redwineAdapter;//红酒适配器
    List<Redwine> redwineList;//红酒列表
    Intent intent;
    //图片缓存
    RequestQueue requestQueue;

    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_desc);
        initView();//初始化控件
        requestData();
        initListener();//初始化监听器
    }

    private void initListener() {
        red_wine_list_view.setOnLoadMoreListener(new RefreshRecyclerView.OnLoadMoreListener() {
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

        red_wine_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        red_wine_refresh_layout.setRefreshing(false);
                        red_wine_list_view.setLoadMoreEnable(true);
                        requestData();
                        red_wine_list_view.notifyData();
                    }
                }, 2000);
            }
        });
    }

    private void initView() {
        text_back = (TextView) findViewById(R.id.text_back);
        text_back.setText("热销红酒");
        red_wine_refresh_layout = (SwipeRefreshLayout) findViewById(R.id.red_wine_refresh_layout);
        red_wine_refresh_layout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        red_wine_list_view = (RefreshRecyclerView) findViewById(R.id.red_wine_list_view);
        red_wine_list_view.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        red_wine_list_view.setLoadMoreEnable(true);
        red_wine_list_view.setFooterResource(R.layout.load_more_layout);
        back_layout = (LinearLayout) findViewById(R.id.back_layout);
        back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalesDescActivity.this.finish();
            }
        });
    }

    private void requestMoreData() {
        int id = redwineAdapter.getItemCount();
        requestQueue = Volley.newRequestQueue(getBaseContext());
        String url = constantClass.getHttp_prefix()+"/redwine/listRedwineOrderBySales?id="+id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                redwineList = JSON.parseArray(s, Redwine.class);
                if (redwineList.size() == 3){
                    redwineAdapter.addItem(redwineList);
                    red_wine_list_view.notifyData();
                } else if (redwineList != null && redwineList.size() > 0 && redwineList.size() < 3){
                    redwineAdapter.addItem(redwineList);
                    red_wine_list_view.notifyData();
                    red_wine_list_view.setLoadMoreEnable(false);
                    ToastUtil.show(getBaseContext(), "数据已经加载完");
                } else if (redwineList.size() < 1) {
                    red_wine_list_view.setLoadMoreEnable(false);
                    ToastUtil.show(getBaseContext(), "数据已经加载完");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(getBaseContext(), "网络出了点问题");
            }
        });
        requestQueue.add(stringRequest);
    }

    private void requestData() {
        int id = 0;
        requestQueue = Volley.newRequestQueue(getBaseContext());
        String url = constantClass.getHttp_prefix()+"/redwine/listRedwineOrderBySales?id="+id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                redwineList = JSON.parseArray(s, Redwine.class);
                if (redwineList.size() < 3) {
                    red_wine_list_view.setLoadMoreEnable(false);
                    ToastUtil.show(getBaseContext(), "只有"+redwineList.size()+"条数据");
                }
                redwineAdapter = new RedwineAdapter(getBaseContext(), redwineList);
                red_wine_list_view.setAdapter(redwineAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(getBaseContext(), "网络出了点问题");
            }
        });
        requestQueue.add(stringRequest);
    }
}
