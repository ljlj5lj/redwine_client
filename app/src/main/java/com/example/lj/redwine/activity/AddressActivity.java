package com.example.lj.redwine.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.adapter.AddressBaseAdapter;
import com.example.lj.redwine.adapter.FavoritesBaseAdapter;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.javabean.Address;
import com.example.lj.redwine.javabean.Favorites;
import com.example.lj.redwine.util.ToastUtil;

import java.util.List;

public class AddressActivity extends Activity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener,AdapterView.OnItemClickListener{
    AddressBaseAdapter addressBaseAdapter;//地址适配器
    List<Address> addressList;//我的收藏数据源
    ConstantClass constantClass;
    RequestQueue requestQueue;//请求队列
    SharedPreferences sharedPreferences;//存储类
    SwipeRefreshLayout address_refresh_layout;//下拉刷新控件
    ListView address_list_view;//地址列表
    LinearLayout back_layout;//返回布局
    FloatingActionButton fab;//浮动添加按钮
    TextView text_back;//返回文本
    Intent intent;//意图跳转
    String addresses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        intent = getIntent();
        addresses = intent.getStringExtra("addresses");
        requestData();//请求地址列表
        InitView();

    }

    private void requestData() {
        requestQueue = Volley.newRequestQueue(getBaseContext());
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt("id", 0);
        String url = constantClass.getHttp_prefix()+"/address/listAddressByUserId?id="+id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                addressList = JSON.parseArray(s, Address.class);
                addressBaseAdapter = new AddressBaseAdapter(getBaseContext(), addressList);
                address_list_view.setAdapter(addressBaseAdapter);
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
        fab = (FloatingActionButton) findViewById(R.id.fab);
        back_layout = (LinearLayout) findViewById(R.id.back_layout);
        text_back = (TextView) findViewById(R.id.text_back);
        text_back.setText("收货地址管理");
        address_list_view = (ListView) findViewById(R.id.address_list_view);
        address_refresh_layout = (SwipeRefreshLayout) findViewById(R.id.address_refresh_layout);
        //设置下拉刷新控件颜色
        address_refresh_layout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        //添加事件监听器
        back_layout.setOnClickListener(this);
        fab.setOnClickListener(this);
        address_refresh_layout.setOnRefreshListener(this);
        address_list_view.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                if (addressBaseAdapter.getCount() > 3) {
                    ToastUtil.show(getBaseContext(), "最多只能添加4条地址");
                } else {
                    addressBaseAdapter.getCount();
                    intent = new Intent(getBaseContext(), AddAddressActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.back_layout:
                if (addresses.equals("select_address")){
                    intent = new Intent(AddressActivity.this, AddOrderActivity.class);
                    setResult(RESULT_CANCELED,intent);
                }
                AddressActivity.this.finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestData();
                addressBaseAdapter.notifyDataSetChanged();
                address_refresh_layout.setRefreshing(false);
            }
        },1000);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (addresses.equals("select_address")) {
            intent = new Intent(AddressActivity.this, AddOrderActivity.class);
            intent.putExtra("location_address", addressList.get(position).getLocation_address());
            intent.putExtra("detail_address", addressList.get(position).getDetail_address());
            intent.putExtra("consignee",addressList.get(position).getConsignee());
            intent.putExtra("consignee_phone", addressList.get(position).getConsignee_phone());
            intent.putExtra("address_id",addressList.get(position).getAddress_id());
            setResult(RESULT_OK,intent);
            AddressActivity.this.finish();
        }
    }
}
