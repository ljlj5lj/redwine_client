package com.example.lj.redwine.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.javabean.RedwineInCart;
import com.example.lj.redwine.util.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddOrderActivity extends Activity implements View.OnClickListener{
    ConstantClass constantClass;
    RequestQueue requestQueue;//请求队列
    SharedPreferences sharedPreferences;//存储类
    Intent intent;
    List<RedwineInCart> redwineInCartList;
    List<Map<String, Object>> orderItemList;//子订单数据源
    ListView order_item_list;
    TextView text_back;//后退文本
    LinearLayout back_layout;//后退布局
    LinearLayout detail_address_layout;
    RelativeLayout select_address_layout;//选择地址
    LinearLayout purchase_now_layout;//立即够吗
    TextView total_price;//总价
    TextView select_address_text;
    TextView location_address;
    TextView detail_address;
    TextView consignee;
    TextView consignee_phone;
    RadioGroup radioGroup;//单选选项组
    RadioButton alibaba_btn;
    RadioButton wechat_btn;
    RadioButton cash_btn;
    AppCompatSpinner spinner;
    String[] orderItemName;//每个子订单的红酒名
    int[] orderItemNum;//每个子订单的红酒数量
    SimpleAdapter orderItem_adapter;//子订单适配器
    private static final int ADDRESSES = 5;
    int address_id = 0;
    String price;//总价
    String method;//配送方式
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);
        intent = getIntent();
        redwineInCartList = (List<RedwineInCart>) intent.getSerializableExtra("list");
        price = intent.getStringExtra("total_price");
        InitView();
        initOrderItemList();//初始化子订单列表
    }

    private void initOrderItemList() {
        orderItemList = new ArrayList<>();
        orderItemName = new String[redwineInCartList.size()];
        orderItemNum = new int[redwineInCartList.size()];
        for (int i = 0 ; i < redwineInCartList.size() ; i++){
            orderItemName[i] = redwineInCartList.get(i).getRed_wine_name();
            orderItemNum[i] = redwineInCartList.get(i).getRed_wine_num();
        }

        orderItem_adapter = new SimpleAdapter(getBaseContext(), getOrderItemData(), R.layout.order_item_list_item, new String[]{"orderItemName","orderItemNum"}, new int[]{R.id.redwine_name, R.id.redwine_num});
        order_item_list.setAdapter(orderItem_adapter);
    }


    private void InitView() {
        //初始化布局
        back_layout = (LinearLayout) findViewById(R.id.back_layout);
        text_back = (TextView) findViewById(R.id.text_back);
        text_back.setText("确认订单");
        select_address_layout = (RelativeLayout) findViewById(R.id.select_address_layout);
        location_address = (TextView) findViewById(R.id.location_address);
        detail_address = (TextView) findViewById(R.id.detail_address);
        select_address_text = (TextView) findViewById(R.id.select_address_text);
        consignee = (TextView) findViewById(R.id.consignee);
        consignee_phone = (TextView) findViewById(R.id.consignee_phone);
        detail_address_layout = (LinearLayout) findViewById(R.id.detail_address_layout);
        order_item_list = (ListView) findViewById(R.id.order_item_list);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        alibaba_btn = (RadioButton) findViewById(R.id.alibaba_btn);
        cash_btn = (RadioButton) findViewById(R.id.cash_btn);
        wechat_btn = (RadioButton) findViewById(R.id.wechat_btn);
        purchase_now_layout = (LinearLayout) findViewById(R.id.purchase_now_layout);
        spinner = (AppCompatSpinner) findViewById(R.id.spinner);
        total_price = (TextView) findViewById(R.id.total_price);
        total_price.setText(price);

        //添加事件监听器
        back_layout.setOnClickListener(this);
        select_address_layout.setOnClickListener(this);
        purchase_now_layout.setOnClickListener(this);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                method = getResources().getStringArray(R.array.deliver_method)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_layout:
                AddOrderActivity.this.finish();
                break;
            case R.id.select_address_layout:
                intent = new Intent(AddOrderActivity.this, AddressActivity.class);
                intent.putExtra("addresses","select_address");
                startActivityForResult(intent, ADDRESSES);
                break;
            case R.id.purchase_now_layout:
                purchaseNow();
                break;
        }
    }

    private void purchaseNow() {
        if (address_id == 0){
            ToastUtil.show(getBaseContext(),"请先选择收货地址");
        } else {
            requestQueue = Volley.newRequestQueue(getBaseContext());
            sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            String url = constantClass.getHttp_prefix()+"/orders/addOrders";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    int order_id = Integer.valueOf(s);
                    addOrderItem(order_id);
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
                    map.put("total_price", total_price.getText().toString());
                    map.put("user_id", String.valueOf(sharedPreferences.getInt("id", 0)));
                    map.put("address_id",String.valueOf(address_id));
                    map.put("order_status_id",String.valueOf(2));
                    map.put("deliver_time", method);
                    return map;
                }
            };
            requestQueue.add(stringRequest);
        }
    }

    private void addOrderItem(final int order_id) {
        for (int i = 0 ; i < redwineInCartList.size() ; i++){
            requestQueue = Volley.newRequestQueue(getBaseContext());
            sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            String url = constantClass.getHttp_prefix()+"/orderItem/addOrderItem";
            Map<String, String> map = new HashMap<String, String>();
            map.put("order_id", String.valueOf(order_id));
            map.put("quantity", String.valueOf(redwineInCartList.get(i).getRed_wine_num()));
            map.put("redwine_id", String.valueOf(redwineInCartList.get(i).getRed_wine_id()));
            JSONObject jsonObject = new  JSONObject(map);
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    ToastUtil.show(getBaseContext(), "网络出现点小问题");
                }
            });
            requestQueue.add(jsonRequest);
        }
        requestQueue.start();
        AddOrderActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADDRESSES) {
            if (resultCode == RESULT_OK){
                select_address_text.setVisibility(View.GONE);
                detail_address_layout.setVisibility(View.VISIBLE);
                Bundle bundle = data.getExtras();
                String location_address_text = bundle.getString("location_address");
                location_address.setText(location_address_text);
                String detail_address_text = bundle.getString("detail_address");
                detail_address.setText(detail_address_text);
                String consignee_text = bundle.getString("consignee");
                consignee.setText(consignee_text);
                String consignee_phone_text = bundle.getString("consignee_phone");
                consignee_phone.setText(consignee_phone_text);
                address_id = bundle.getInt("address_id");
            }else if (requestCode == RESULT_CANCELED) {

            }

        }
    }


    public List<Map<String,Object>> getOrderItemData() {
        for (int i = 0; i < orderItemName.length; i++){
            Map<String, Object> map = new HashMap<>();
            map.put("orderItemName",orderItemName[i]);
            map.put("orderItemNum",orderItemNum[i]);
            orderItemList.add(map);
        }
        return orderItemList;
    }

}
