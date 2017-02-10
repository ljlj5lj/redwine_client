package com.example.lj.redwine.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.activity.AccountActivity;
import com.example.lj.redwine.activity.AddOrderActivity;
import com.example.lj.redwine.activity.ChatClientActivity;
import com.example.lj.redwine.activity.LoginActivity;
import com.example.lj.redwine.activity.MyCollectionActivity;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.javabean.Redwine;
import com.example.lj.redwine.javabean.RedwineInCart;
import com.example.lj.redwine.javabean.ShoppingCart;
import com.example.lj.redwine.util.ToastUtil;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RedwineDetailFragment extends Fragment implements View.OnClickListener{
    Redwine redwine;
    int redwine_id;//红酒id
    Intent intent;//意图
    ConstantClass constantClass;
    RequestQueue requestQueue;//请求队列
    SharedPreferences sharedPreferences;//存储类
    ImageView redwine_img;//红酒图片
    TextView redwine_name;//红酒名
    TextView redwine_price;//红酒价格
    TextView redwine_description;//红酒描述
    TextView redwine_origin;//红酒来源
    TextView redwine_vintage;//红酒酿造日期
    TextView redwine_alcohol;//红酒酒精度
    TextView redwine_capacity;//红酒容量
    TextView redwine_date;//红酒上架日期
    TextView redwine_introduction;//红酒简介
    RelativeLayout question_ask;//问题咨询
    LinearLayout contact_customer_service_layout;//在线咨询布局
    LinearLayout add_collection_layout;//加入收藏布局
    LinearLayout add_shopping_cart_layout;//添加至购物车布局
    LinearLayout purchase_now_layout;//立即购买布局
    List<RedwineInCart> redwineInCartList = new ArrayList<RedwineInCart>();
    public RedwineDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_redwine_detail, container, false);
        redwine_id = this.getArguments().getInt("redwine_id");
        requestData();//请求红酒信息
        InitView(view);//初始化布局
        return view;
    }

    private void requestData() {
        requestQueue = Volley.newRequestQueue(getContext());
        String url = constantClass.getHttp_prefix()+"/redwine/queryRedwineById?id="+redwine_id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                redwine = JSON.parseObject(s, Redwine.class);
                redwineInfoSet(redwine);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(getContext(), "网络出了点问题");
            }
        });
        requestQueue.add(stringRequest);
    }

    private void redwineInfoSet(Redwine redwine) {
        redwine_name.setText(redwine.getRedwine_name());
        redwine_price.setText(redwine.getPrice().toString());
        redwine_description.setText(redwine.getDescription());
        redwine_origin.setText(redwine.getOrigin());
        redwine_vintage.setText(redwine.getVintage().toString());
        redwine_alcohol.setText(redwine.getAlcohol().toString());
        redwine_capacity.setText(redwine.getCapacity().toString());
        redwine_introduction.setText(redwine.getIntroduction());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        redwine_date.setText(simpleDateFormat.format(redwine.getRegister_date()));
        ImageRequest imageRequest = new ImageRequest(constantClass.getHttp_prefix()+"/redwine_img/"+redwine.getPicture(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        redwine_img.setImageBitmap(response);
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                redwine_img.setImageResource(R.drawable.cat);
            }
        });
        requestQueue.add(imageRequest);

    }

    private void InitView(View view) {
        redwine_img = (ImageView) view.findViewById(R.id.redwine_img);
        redwine_name = (TextView) view.findViewById(R.id.redwine_name);
        redwine_price = (TextView) view.findViewById(R.id.redwine_price);
        redwine_description = (TextView) view.findViewById(R.id.redwine_description);
        redwine_origin = (TextView) view.findViewById(R.id.redwine_origin);
        redwine_vintage = (TextView) view.findViewById(R.id.redwine_vintage);
        redwine_alcohol = (TextView) view.findViewById(R.id.redwine_alcohol);
        redwine_capacity = (TextView) view.findViewById(R.id.redwine_capacity);
        redwine_introduction = (TextView) view.findViewById(R.id.redwine_introduce);
        redwine_date = (TextView) view.findViewById(R.id.redwine_date);
        contact_customer_service_layout = (LinearLayout) view.findViewById(R.id.contact_customer_service_layout);
        add_collection_layout = (LinearLayout) view.findViewById(R.id.add_collection_layout);
        add_shopping_cart_layout = (LinearLayout) view.findViewById(R.id.add_shopping_cart_layout);
        purchase_now_layout = (LinearLayout) view.findViewById(R.id.purchase_now_layout);
        question_ask = (RelativeLayout) view.findViewById(R.id.question_ask);


        //添加事件监听器
        contact_customer_service_layout.setOnClickListener(this);
        add_collection_layout.setOnClickListener(this);
        add_shopping_cart_layout.setOnClickListener(this);
        purchase_now_layout.setOnClickListener(this);
        question_ask.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        switch (v.getId()) {
            case R.id.contact_customer_service_layout:
                if (sharedPreferences.getInt("id", 0 ) == 0) {
                    intent = new Intent(getContext(), LoginActivity.class);
                    ToastUtil.show(getContext(), "请先登录");
                } else {
                    intent = new Intent(getContext(), ChatClientActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.add_collection_layout:
                if (sharedPreferences.getInt("id", 0 ) == 0) {
                    intent = new Intent(getContext(), LoginActivity.class);
                    ToastUtil.show(getContext(), "请先登录");
                    startActivity(intent);
                } else {
                    addCollection();
                }
                break;
            case R.id.add_shopping_cart_layout:
                if (sharedPreferences.getInt("id", 0 ) == 0) {
                    intent = new Intent(getContext(), LoginActivity.class);
                    ToastUtil.show(getContext(), "请先登录");
                    startActivity(intent);
                } else {
                    addShoppingCart();
                }
                break;
            case R.id.purchase_now_layout:
                if (sharedPreferences.getInt("id", 0 ) == 0) {
                    intent = new Intent(getContext(), LoginActivity.class);
                    ToastUtil.show(getContext(), "请先登录");
                    startActivity(intent);
                } else {
                    purchaseNow();
                }
                break;
            case R.id.question_ask:
                ToastUtil.show(getContext(), "问题咨询");
                break;
        }
    }

    private void purchaseNow() {
        redwineInCartList.clear();
        RedwineInCart redwineInCart = new RedwineInCart(redwine_name.getText().toString(),1,redwine_id);
        redwineInCartList.add(redwineInCart);
        intent = new Intent(getContext(), AddOrderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", (Serializable) redwineInCartList);
        intent.putExtra("total_price", redwine_price.getText().toString());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void addShoppingCart() {
        int user_id = sharedPreferences.getInt("id", 0 );
        requestQueue = Volley.newRequestQueue(getContext());
        String url = constantClass.getHttp_prefix()+"/shoppingCart/addShoppingCart?user_id="+user_id+"&redwine_id="+redwine_id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("success")){
                    ToastUtil.show(getContext(), "已添加至购物车");
                } else if (s.equals("fail")){
                    ToastUtil.show(getContext(), "加入购物车失败");
                } else if (s.equals("exist")) {
                    ToastUtil.show(getContext(), "该红酒已在购物车");
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

    private void addCollection() {
        int user_id = sharedPreferences.getInt("id", 0 );
        requestQueue = Volley.newRequestQueue(getContext());
        String url = constantClass.getHttp_prefix()+"/favorites/addFavorites?user_id="+user_id+"&redwine_id="+redwine_id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("success")){
                    ToastUtil.show(getContext(), "已加入收藏");
                } else if (s.equals("fail")){
                    ToastUtil.show(getContext(), "加入收藏失败");
                }
                else if (s.equals("exist")) {
                    ToastUtil.show(getContext(), "该红酒已在收藏");
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

}
