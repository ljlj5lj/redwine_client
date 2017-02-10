package com.example.lj.redwine.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
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
import com.example.lj.redwine.adapter.OrderItemAdapter;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.javabean.Evaluation;
import com.example.lj.redwine.javabean.OrderItem;
import com.example.lj.redwine.javabean.RedwineItem;
import com.example.lj.redwine.util.ToastUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluationActivity extends Activity implements View.OnClickListener{
    SharedPreferences sharedPreferences;//存储类
    ConstantClass constantClass;
    RequestQueue requestQueue;//请求队列
    Intent intent;
    int order_id;//订单id
    LinearLayout back_layout;//后退布局
    TextView text_back;//后退文本
    RatingBar grade;
    EditText edit_content;
    LinearLayout submit_comment;
    AppCompatSpinner spinner;
    List<OrderItem> orderItemList;
    List<RedwineItem> redwineItemList;
    ArrayAdapter<RedwineItem> redwineItemAdapter;
    int rid;//红酒id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        intent = getIntent();
        order_id = intent.getIntExtra("order_id", 0);
        initView();
        initSpinner();
    }

    private void initSpinner() {
        redwineItemList.clear();
        requestQueue = Volley.newRequestQueue(getBaseContext());
        String url = constantClass.getHttp_prefix()+"/orderItem/listOrderItemsByOrderId?id="+order_id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                orderItemList = JSON.parseArray(s, OrderItem.class);
                for (int i = 0 ; i < orderItemList.size() ; i++) {
                    RedwineItem redwineItem = new RedwineItem(orderItemList.get(i).getRedwine().getRedwine_id(),orderItemList.get(i).getRedwine().getRedwine_name());
                    redwineItemList.add(redwineItem);
                }
                redwineItemAdapter = new ArrayAdapter<RedwineItem>(getBaseContext(), android.R.layout.simple_spinner_item, redwineItemList);
                spinner.setAdapter(redwineItemAdapter);
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
        back_layout = (LinearLayout) findViewById(R.id.back_layout);
        text_back = (TextView) findViewById(R.id.text_back);
        text_back.setText("添加评价");
        grade = (RatingBar) findViewById(R.id.grade);
        edit_content = (EditText) findViewById(R.id.edit_content);
        submit_comment = (LinearLayout) findViewById(R.id.submit_comment);
        spinner = (AppCompatSpinner) findViewById(R.id.spinner);
        redwineItemList = new ArrayList<RedwineItem>();

        //添加事件监听器
        submit_comment.setOnClickListener(this);
        back_layout.setOnClickListener(this);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rid = ((RedwineItem)spinner.getSelectedItem()).getRedwineId();
                ToastUtil.show(getBaseContext(), String.valueOf(rid));
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
                EvaluationActivity.this.finish();
                break;
            case R.id.submit_comment:
                submitEvaluation();
                break;
        }

    }

    private void submitEvaluation() {
        if (TextUtils.isEmpty(edit_content.getText())) {
            ToastUtil.show(getBaseContext(),"请留下你的宝贵意见");
        } else {
            requestQueue = Volley.newRequestQueue(getBaseContext());
            sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            final int user_id = sharedPreferences.getInt("id", 0);
            String url = constantClass.getHttp_prefix() + "/evaluation/addEvaluation";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    if (s.equals("success")) {
                        ToastUtil.show(getBaseContext(),"评价成功");
                        EvaluationActivity.this.finish();
                    } else if (s.equals("fail")) {
                        edit_content.getText().clear();
                        ToastUtil.show(getBaseContext(), "评价失败,请重新评价");
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
                    map.put("content", edit_content.getText().toString());
                    map.put("grade", String.valueOf(grade.getRating()));
                    map.put("user_id", String.valueOf(user_id));
                    map.put("redwine_id", String.valueOf(rid));
                    return map;
                }
            };
            requestQueue.add(stringRequest);
        }

    }
}
