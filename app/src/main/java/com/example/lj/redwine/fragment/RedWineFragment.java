package com.example.lj.redwine.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.activity.AddressActivity;
import com.example.lj.redwine.activity.LoginActivity;
import com.example.lj.redwine.activity.RedwineTypeActivity;
import com.example.lj.redwine.activity.SearchActivity;
import com.example.lj.redwine.activity.ShoppingCartActivity;
import com.example.lj.redwine.adapter.RedwineAdapter;
import com.example.lj.redwine.cache.LruImageCache;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.custom_widget.recyclerView.RefreshRecyclerView;
import com.example.lj.redwine.javabean.Redwine;
import com.example.lj.redwine.javabean.RedwineType;
import com.example.lj.redwine.javabean.ShoppingCart;
import com.example.lj.redwine.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RedWineFragment extends Fragment implements View.OnClickListener{
    SharedPreferences sharedPreferences;
    ConstantClass constantClass;
    SwipeRefreshLayout red_wine_refresh_layout;//下拉刷新
    RefreshRecyclerView red_wine_list_view;//新产品列表
    RedwineAdapter redwineAdapter;//红酒适配器
    List<Redwine> redwineList;//红酒列表
    Intent intent;
    EditText search_edit;//搜索框
    LinearLayout shopping_cart_layout;
    LinearLayout search_layout;
    //布局控件
    GridView classification_gridView;//红酒分类网格布局
    //数据源
    private int[] classification_icon;//红酒分类图标集合
    private String[] classification_text;//红酒分类图标名称集合
    private List<Map<String, Object>> classificationList;//红酒分类列数据源
    ImageView red_wine_introduce;//红酒广告介绍
    //适配器
    SimpleAdapter classification_adapter;//分类适配器
    //图片缓存
    LruImageCache lruImageCache;
    RequestQueue requestQueue;
    ImageLoader imageLoader;
    private Handler handler = new Handler();

    public RedWineFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_red_wine, container, false);
        initView(view);//初始化控件
        requestData();//请求红酒数据
        initIntroduce();//初始化红酒介绍广告
        initClassification();//初始化红酒分类
        initListener();//初始化监听器
        return view;
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
                        initIntroduce();
                        requestData();
                        red_wine_list_view.notifyData();
                    }
                }, 2000);
            }
        });
    }

    private void requestMoreData() {
        int id = redwineAdapter.getItemCount();
        requestQueue = Volley.newRequestQueue(getContext());
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
                    ToastUtil.show(getContext(), "数据已经加载完");
                } else if (redwineList.size() < 1) {
                    red_wine_list_view.setLoadMoreEnable(false);
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
        String url = constantClass.getHttp_prefix()+"/redwine/listRedwineOrderBySales?id="+id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                redwineList = JSON.parseArray(s, Redwine.class);
                if (redwineList.size() < 3) {
                    red_wine_list_view.setLoadMoreEnable(false);
                    ToastUtil.show(getContext(), "只有"+redwineList.size()+"条数据");
                }
                redwineAdapter = new RedwineAdapter(getContext(), redwineList);
                red_wine_list_view.setAdapter(redwineAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(getContext(), "网络出了点问题");
            }
        });
        requestQueue.add(stringRequest);
    }

    //初始化红酒介绍广告
    private void initIntroduce() {
        lruImageCache = LruImageCache.instance();
        requestQueue = Volley.newRequestQueue(getContext());
        imageLoader = new ImageLoader(requestQueue, lruImageCache);
        ImageRequest imageRequest = new ImageRequest(constantClass.getHttp_prefix()+"/advertisement/index.jpg",
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        red_wine_introduce.setImageBitmap(response);
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                red_wine_introduce.setImageResource(R.drawable.loading_error);
            }
        });
        requestQueue.add(imageRequest);
    }

    //初始化红酒分类
    private void initClassification() {
        classificationList = new ArrayList<>();
        classification_icon = new int[]{R.drawable.redwine_dry, R.drawable.champagne, R.drawable.sherry, R.drawable.ice_wine};
        classification_text = new String[]{"干红", "香槟", "白葡萄酒", "冰酒"};
        classification_adapter = new SimpleAdapter(getActivity(), getData(), R.layout.classification_item,
                new String[]{"classification_icon", "classification_text"}, new int[]{R.id.classification_icon, R.id.classification_text});
        classification_gridView.setAdapter(classification_adapter);
        classification_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(getContext(), RedwineTypeActivity.class);
                intent.putExtra("type_id", classification_adapter.getItemId(position));//类型id
                intent.putExtra("type_name",classification_text[position]);//类型名
                startActivity(intent);
            }
        });
    }

    //初始化控件
    private void initView(View view) {
        red_wine_refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.red_wine_refresh_layout);
        red_wine_refresh_layout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        red_wine_list_view = (RefreshRecyclerView) view.findViewById(R.id.red_wine_list_view);
        red_wine_list_view.setLayoutManager(new LinearLayoutManager(getContext()));
        red_wine_list_view.setLoadMoreEnable(true);
        red_wine_list_view.setFooterResource(R.layout.load_more_layout);
        search_edit = (EditText) view.findViewById(R.id.search_edit);
        search_layout = (LinearLayout) view.findViewById(R.id.search_layout);
        shopping_cart_layout = (LinearLayout) view.findViewById(R.id.shopping_cart_layout);
        classification_gridView = (GridView) view.findViewById(R.id.classification_gridView);
        red_wine_introduce = (ImageView) view.findViewById(R.id.red_wine_introduce);

        //添加事件监听器
        search_layout.setOnClickListener(this);
        shopping_cart_layout.setOnClickListener(this);

    }
    //获取红酒分类图片和文字
    public List<Map<String, Object>> getData() {
        for (int i = 0; i<classification_icon.length; i++){
            Map<String, Object> map = new HashMap<>();
            map.put("classification_icon", classification_icon[i]);
            map.put("classification_text", classification_text[i]);
            classificationList.add(map);
        }
        return classificationList;
    }

    @Override
    public void onClick(View v) {
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        switch (v.getId()) {
            case R.id.search_layout:
                if (search_edit.getText().toString().equals("") || search_edit.getText().toString() == null) {
                    ToastUtil.show(getContext(), "搜索内容不能为空");
                } else {
                    intent = new Intent(getContext(), SearchActivity.class);
                    intent.putExtra("search_text", search_edit.getText().toString());
                    startActivity(intent);
                    search_edit.getText().clear();
                }
                break;
            case R.id.shopping_cart_layout:
                if (sharedPreferences.getInt("id", 0 ) == 0) {
                    intent = new Intent(getContext(), LoginActivity.class);
                    ToastUtil.show(getContext(), "请先登录");
                } else {
                    intent = new Intent(getContext(), ShoppingCartActivity.class);
                }
                startActivity(intent);
                break;
        }
    }
}
