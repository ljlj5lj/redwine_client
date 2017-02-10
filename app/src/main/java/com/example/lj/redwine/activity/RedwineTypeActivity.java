package com.example.lj.redwine.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
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
import com.example.lj.redwine.adapter.RedwineTypeAdapter;
import com.example.lj.redwine.adapter.SearchBaseAdapter;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.custom_widget.recyclerView.RefreshRecyclerView;
import com.example.lj.redwine.javabean.Redwine;
import com.example.lj.redwine.util.ToastUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedwineTypeActivity extends Activity implements View.OnClickListener{
    long type_id;
    String type_name;
    Intent intent;
    ConstantClass constantClass;
    RequestQueue requestQueue;//请求队列
    RefreshRecyclerView redwine_type_list_view;//查找列表
    List<Redwine> redwineList;//红酒列表
    RedwineTypeAdapter redwineTypeAdapter;//红酒类型适配器
    LinearLayout back_layout; //后退布局
    TextView text_back; //后退文本
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redwine_type);
        intent = getIntent();
        type_id = intent.getLongExtra("type_id", 0) + 1;//逻辑加一
        type_name = intent.getStringExtra("type_name");
        initView();
        requestData();//请求红酒数据
        initListener();
    }

    private void initListener() {
        redwine_type_list_view.setOnLoadMoreListener(new RefreshRecyclerView.OnLoadMoreListener() {
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
        int id = redwineTypeAdapter.getItemCount();
        requestQueue = Volley.newRequestQueue(getBaseContext());
        String url =  constantClass.getHttp_prefix()+"/redwine/listRedwineByType?id="+id+"&redwine_type_id="+type_id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                redwineList = JSON.parseArray(s, Redwine.class);
                if (redwineList.size() == 4) {
                    redwineTypeAdapter.addItem(redwineList);
                    redwine_type_list_view.notifyData();
                } else if (redwineList != null && redwineList.size() > 0 && redwineList.size() < 4){
                    redwineTypeAdapter.addItem(redwineList);
                    redwine_type_list_view.notifyData();
                    redwine_type_list_view.setLoadMoreEnable(false);
                    ToastUtil.show(getBaseContext(), "数据已经加载完");
                } else if (redwineList.size() < 1) {
                    redwine_type_list_view.setLoadMoreEnable(false);
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
        requestQueue = Volley.newRequestQueue(getBaseContext());
        String url = constantClass.getHttp_prefix()+"/redwine/listRedwineByType?id="+0+"&redwine_type_id="+type_id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("[]")){
                    ToastUtil.show(getBaseContext(), "没有匹配到相应结果");
                } else {
                    redwineList = JSON.parseArray(s, Redwine.class);
                    if (redwineList.size() < 4){
                        redwine_type_list_view.setLoadMoreEnable(false);
                        ToastUtil.show(getBaseContext(), "只有"+redwineList.size()+"条数据");
                    }
                    redwineTypeAdapter = new RedwineTypeAdapter(getBaseContext(), redwineList);
                    redwine_type_list_view.setAdapter(redwineTypeAdapter);
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

    private void initView() {
        back_layout = (LinearLayout) this.findViewById(R.id.back_layout);
        text_back = (TextView) this.findViewById(R.id.text_back);
        text_back.setText(type_name);
        redwine_type_list_view = (RefreshRecyclerView) this.findViewById(R.id.redwine_type_list_view);
        redwine_type_list_view.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        redwine_type_list_view.setLoadMoreEnable(true);
        redwine_type_list_view.setFooterResource(R.layout.load_more_layout);

        //添加事件监听器
        back_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.back_layout:
                RedwineTypeActivity.this.finish();
                break;
        }
    }
}
