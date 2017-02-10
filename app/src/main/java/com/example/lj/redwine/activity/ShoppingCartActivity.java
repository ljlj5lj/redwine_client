package com.example.lj.redwine.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.example.lj.redwine.adapter.ShoppingCartBaseAdapter;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.javabean.Favorites;
import com.example.lj.redwine.javabean.RedwineInCart;
import com.example.lj.redwine.javabean.ShoppingCart;
import com.example.lj.redwine.util.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends Activity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener,AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener,SwipeRefreshLayout.OnRefreshListener{
    SwipeRefreshLayout shopping_cart_refresh_layout;//下拉刷新控件
    Intent intent;
    ConstantClass constantClass;
    RequestQueue requestQueue;//请求队列
    SharedPreferences sharedPreferences;//存储类
    List<ShoppingCart> shoppingCartList;//购物车数据源
    ListView shopping_cart_list_view; //购物车列表
    LinearLayout back_layout; //后退布局
    LinearLayout right_layout; //清空购物车布局
    TextView right_text; //清空购物车文本
    TextView text_back; //后退文本
    CheckBox shopping_cart_allcheck;//全选
    TextView total_price;//总价
    LinearLayout purchase_now_layout;//立即购买
    ShoppingCartBaseAdapter shoppingCartBaseAdapter;
    List<RedwineInCart> redwineInCartList = new ArrayList<RedwineInCart>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        requestData();
        InitView();
    }

    private void requestData() {
        requestQueue = Volley.newRequestQueue(getBaseContext());
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt("id", 0);
        String url = constantClass.getHttp_prefix()+"/shoppingCart/listShoppingCartByUserId?id="+id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                shoppingCartList = JSON.parseArray(s, ShoppingCart.class);
                shoppingCartBaseAdapter = new ShoppingCartBaseAdapter(getBaseContext(),handler, shoppingCartList);
                shopping_cart_list_view.setAdapter(shoppingCartBaseAdapter);
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
        back_layout = (LinearLayout) findViewById(R.id.back_layout);
        right_layout = (LinearLayout) findViewById(R.id.right_layout);
        right_layout.setClickable(true);
        text_back = (TextView) findViewById(R.id.text_back);
        text_back.setText("我的购物车");
        right_text = (TextView) findViewById(R.id.right_text);
        right_text.setText("操作");
        shopping_cart_allcheck = (CheckBox) findViewById(R.id.shopping_cart_allcheck);
        total_price = (TextView) findViewById(R.id.total_price);
        purchase_now_layout = (LinearLayout) findViewById(R.id.purchase_now_layout);
        shopping_cart_list_view = (ListView) findViewById(R.id.shopping_cart_list_view);
        shopping_cart_refresh_layout = (SwipeRefreshLayout) findViewById(R.id.shopping_cart_refresh_layout);
        //设置下拉刷新控件颜色
        shopping_cart_refresh_layout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

//        添加事件监听器
        back_layout.setOnClickListener(this);
        right_layout.setOnClickListener(this);
        purchase_now_layout.setOnClickListener(this);
        shopping_cart_allcheck.setOnCheckedChangeListener(this);
        shopping_cart_list_view.setOnItemClickListener(this);
        shopping_cart_list_view.setOnItemLongClickListener(this);
        shopping_cart_refresh_layout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_layout:
                ShoppingCartActivity.this.finish();
                break;
            case R.id.right_layout:
                shoppingCartBaseAdapter.flage = !shoppingCartBaseAdapter.flage;
                if (shoppingCartBaseAdapter.flage) {
                    right_text.setText("取消");
                }else {
                    right_text.setText("操作");
                }
                shoppingCartBaseAdapter.notifyDataSetChanged();
                break;
            case R.id.purchase_now_layout:
                redwineInCartList.clear();
                for (ShoppingCart shoppingCart : shoppingCartList) {
                    if (shoppingCart.getChecked()) {
                        RedwineInCart redwineInCart = new RedwineInCart(shoppingCart.getRedwine().getRedwine_name(),shoppingCart.getNum(),shoppingCart.getRedwine().getRedwine_id());
                        redwineInCartList.add(redwineInCart);
                    }
                }
                if (redwineInCartList.size() < 1){
                    ToastUtil.show(getBaseContext(),"请勾选相应红酒");
                } else {
                    intent = new Intent(ShoppingCartActivity.this, AddOrderActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", (Serializable) redwineInCartList);
                    intent.putExtra("total_price", total_price.getText());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        float all_price = 0f;//总价
        if (isChecked){
            shoppingCartBaseAdapter.allCheck = true;
            for (ShoppingCart shoppingCart : shoppingCartList) {
                all_price = all_price + shoppingCart.getNum()*shoppingCart.getRedwine().getPrice();
                total_price.setText(String.valueOf(all_price));
            }
        }else {
            shoppingCartBaseAdapter.allCheck = false;
            total_price.setText("0");
        }
        shoppingCartBaseAdapter.notifyDataSetChanged();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            float price = (float) msg.obj;
            if (msg.what == 10){
                if (price > 0){
                    total_price.setText(String.valueOf(price));
                } else {
                    total_price.setText("0");
                }
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int redwine_id = (shoppingCartList.get(position).getRedwine().getRedwine_id());
        intent = new Intent(ShoppingCartActivity.this, RedWineInfoActivity.class);
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
                String url = constantClass.getHttp_prefix()+"/shoppingCart/deleteShoppingCart?id="+shoppingCartList.get(position).getShopping_cart_id();
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
                shoppingCartBaseAdapter.allCheck = false;
                total_price.setText("0");
                requestData();
                shoppingCartBaseAdapter.notifyDataSetChanged();
                shopping_cart_refresh_layout.setRefreshing(false);
            }
        },1000);
    }
}
