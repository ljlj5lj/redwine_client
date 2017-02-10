package com.example.lj.redwine.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.adapter.FavoritesBaseAdapter;
import com.example.lj.redwine.adapter.MyEvaluationBaseAdapter;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.javabean.Evaluation;
import com.example.lj.redwine.javabean.Favorites;
import com.example.lj.redwine.util.ToastUtil;

import java.util.List;

public class MyEvaluationActivity extends Activity implements View.OnClickListener,AdapterView.OnItemLongClickListener,SwipeRefreshLayout.OnRefreshListener{
    SwipeRefreshLayout my_evaluation_refresh_layout;//下拉刷新控件
    Intent intent;
    List<Evaluation> evaluationList;//我的酒评数据源
    ConstantClass constantClass;
    RequestQueue requestQueue;//请求队列
    SharedPreferences sharedPreferences;//存储类
    LinearLayout back_layout;//后退布局
    TextView text_back;//后退文本
    ListView my_evaluation_list_view;//我的酒评列表
    MyEvaluationBaseAdapter myEvaluationBaseAdapter;//我的酒评适配器
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_evaluation);
        requestData();
        InitView();//初始化布局
    }
    private void requestData() {
        requestQueue = Volley.newRequestQueue(getBaseContext());
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt("id", 0);
        String url = constantClass.getHttp_prefix()+"/evaluation/listEvaluationByUserId?id="+id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                evaluationList = JSON.parseArray(s, Evaluation.class);
                myEvaluationBaseAdapter = new MyEvaluationBaseAdapter(getBaseContext(), evaluationList);
                my_evaluation_list_view.setAdapter(myEvaluationBaseAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(getBaseContext(), "网络出了点问题");
            }
        });
        requestQueue.add(stringRequest);
    }
    private void InitView() {
        back_layout = (LinearLayout) this.findViewById(R.id.back_layout);
        text_back = (TextView) findViewById(R.id.text_back);
        text_back.setText("我的收藏");
        my_evaluation_list_view = (ListView) this.findViewById(R.id.my_evaluation_list_view);
        my_evaluation_refresh_layout = (SwipeRefreshLayout) findViewById(R.id.my_evaluation_refresh_layout);
        //设置下拉刷新控件颜色
        my_evaluation_refresh_layout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        //添加事件监听器
        back_layout.setOnClickListener(this);
        my_evaluation_list_view.setOnItemLongClickListener(this);
        my_evaluation_refresh_layout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_layout:
                MyEvaluationActivity.this.finish();
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestData();
                myEvaluationBaseAdapter.notifyDataSetChanged();
                my_evaluation_refresh_layout.setRefreshing(false);
            }
        },1000);
    }
}
