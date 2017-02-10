package com.example.lj.redwine.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.adapter.NewProductAdapter;
import com.example.lj.redwine.adapter.SearchBaseAdapter;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.custom_widget.recyclerView.RefreshRecyclerView;
import com.example.lj.redwine.javabean.Redwine;
import com.example.lj.redwine.util.ToastUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends Activity implements View.OnClickListener{
    String search_text;
    Intent intent;
    ConstantClass constantClass;
    RequestQueue requestQueue;//请求队列
    RefreshRecyclerView search_list_view;//查找列表
    List<Redwine> redwineList;//红酒列表
    SearchBaseAdapter searchBaseAdapter;//搜索适配器
    LinearLayout back_layout; //后退布局
    TextView text_back; //后退文本
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        intent = getIntent();
        search_text = intent.getStringExtra("search_text");
        initView();
        requestData();//请求红酒数据
        initListener();
    }

    private void initListener() {
        search_list_view.setOnLoadMoreListener(new RefreshRecyclerView.OnLoadMoreListener() {
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
    }

    private void requestMoreData() {
        int id = searchBaseAdapter.getItemCount();
        requestQueue = Volley.newRequestQueue(getBaseContext());
        String url =  constantClass.getHttp_prefix()+"/redwine/listRedwineOrderById?id="+id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                redwineList = JSON.parseArray(s, Redwine.class);
                if (redwineList.size() == 4) {
                    searchBaseAdapter.addItem(redwineList);
                    search_list_view.notifyData();
                } else if (redwineList != null && redwineList.size() > 0 && redwineList.size() < 4){
                    searchBaseAdapter.addItem(redwineList);
                    search_list_view.notifyData();
                    search_list_view.setLoadMoreEnable(false);
                    ToastUtil.show(getBaseContext(), "数据已经加载完");
                } else if (redwineList.size() < 1) {
                    search_list_view.setLoadMoreEnable(false);
                    ToastUtil.show(getBaseContext(), "数据已经加载完");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(getBaseContext(), "网络出了点问题");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("name",search_text.toString());
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void requestData() {
        int id = 0;
        requestQueue = Volley.newRequestQueue(getBaseContext());
        String url = constantClass.getHttp_prefix()+"/redwine/listRedwineOrderById?id="+id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("[]")){
                    ToastUtil.show(getBaseContext(), "没有匹配到相应结果");
                } else {
                    redwineList = JSON.parseArray(s, Redwine.class);
                    if (redwineList.size() < 4){
                        search_list_view.setLoadMoreEnable(false);
                        ToastUtil.show(getBaseContext(), "只有"+redwineList.size()+"条数据");
                    }
                    searchBaseAdapter = new SearchBaseAdapter(getBaseContext(), redwineList);
                    search_list_view.setAdapter(searchBaseAdapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(getBaseContext(), "网络出了点问题");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("name",search_text.toString());
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void initView() {
        back_layout = (LinearLayout) this.findViewById(R.id.back_layout);
        text_back = (TextView) this.findViewById(R.id.text_back);
        text_back.setText("搜索结果");
        search_list_view = (RefreshRecyclerView) this.findViewById(R.id.search_list_view);
        search_list_view.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        search_list_view.setLoadMoreEnable(true);
        search_list_view.setFooterResource(R.layout.load_more_layout);

        //添加事件监听器
        back_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           case  R.id.back_layout:
            SearchActivity.this.finish();
               break;
        }
    }
}
