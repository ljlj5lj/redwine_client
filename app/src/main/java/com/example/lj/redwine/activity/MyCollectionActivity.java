package com.example.lj.redwine.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.adapter.FavoritesBaseAdapter;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.javabean.Favorites;
import com.example.lj.redwine.util.ToastUtil;

import java.util.List;

public class MyCollectionActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener,SwipeRefreshLayout.OnRefreshListener{
    SwipeRefreshLayout my_collection_refresh_layout;//下拉刷新控件
    Intent intent;
    List<Favorites> favoritesList;//我的收藏数据源
    ConstantClass constantClass;
    RequestQueue requestQueue;//请求队列
    SharedPreferences sharedPreferences;//存储类
    LinearLayout back_layout;//后退布局
    TextView text_back;//后退文本
    ListView my_collection_list_view;//我的收藏列表
    FavoritesBaseAdapter favoritesBaseAdapter;//我的收藏适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);
        requestData();
        InitView();//初始化布局
    }

    private void requestData() {
        requestQueue = Volley.newRequestQueue(getBaseContext());
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt("id", 0);
        String url = constantClass.getHttp_prefix()+"/favorites/listFavoritesByUserId?id="+id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                favoritesList = JSON.parseArray(s, Favorites.class);
                favoritesBaseAdapter = new FavoritesBaseAdapter(getBaseContext(), favoritesList);
                my_collection_list_view.setAdapter(favoritesBaseAdapter);
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
        my_collection_list_view = (ListView) this.findViewById(R.id.my_collection_list_view);
        my_collection_refresh_layout = (SwipeRefreshLayout) findViewById(R.id.my_collection_refresh_layout);
        //设置下拉刷新控件颜色
        my_collection_refresh_layout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        //添加事件监听器
        back_layout.setOnClickListener(this);
        my_collection_list_view.setOnItemClickListener(this);
        my_collection_list_view.setOnItemLongClickListener(this);
        my_collection_refresh_layout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_layout:
                MyCollectionActivity.this.finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int redwine_id = (favoritesList.get(position).getRedwine().getRedwine_id());
        intent = new Intent(MyCollectionActivity.this, RedWineInfoActivity.class);
        intent.putExtra("redwine_id", redwine_id);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否删除该收藏");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestQueue = Volley.newRequestQueue(getBaseContext());
                String url = constantClass.getHttp_prefix()+"/favorites/deleteFavorites?id="+favoritesList.get(position).getFavorites_id();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (s.equals("success")) {
                            ToastUtil.show(getBaseContext(), "删除成功");
                        } else if (s.equals("fail")) {
                            ToastUtil.show(getBaseContext(), "删除失败");
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
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
        return true;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestData();
                favoritesBaseAdapter.notifyDataSetChanged();
                my_collection_refresh_layout.setRefreshing(false);
            }
        },1000);
    }
}
