package com.example.lj.redwine.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.custom_widget.edittext_clear.ClearEditText;
import com.example.lj.redwine.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

public class UsernameActivity extends Activity implements View.OnClickListener{
    Intent intent;
    ConstantClass constantClass;
    RequestQueue requestQueue;//请求队列
    SharedPreferences sharedPreferences;//存储类
    LinearLayout back_layout;//后退布局
    LinearLayout right_layout;//保存布局
    ImageView right_img;//保存用户名
    TextView text_back;//后退文本
    ClearEditText edit_username;//修改用户名编辑框
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);
        InitView();
    }

    private void InitView() {
        //初始化控件
        back_layout = (LinearLayout) findViewById(R.id.back_layout);
        right_layout = (LinearLayout) findViewById(R.id.right_layout);
        right_layout.setClickable(true);
        text_back = (TextView) findViewById(R.id.text_back);
        text_back.setText("修改用户名");
        right_img = (ImageView) findViewById(R.id.right_img);
        right_img.setBackgroundResource(R.drawable.correct);
        edit_username = (ClearEditText) findViewById(R.id.edit_username);

        //添加事件监听器
        back_layout.setOnClickListener(this);
        right_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_layout:
                intent = new Intent(UsernameActivity.this, AccountActivity.class);
                setResult(RESULT_CANCELED,intent);
                UsernameActivity.this.finish();
                break;
            case R.id.right_layout:
                validateUsername();
                break;
        }
    }

    private void validateUsername() {//校验用户名输入正确性
        if (TextUtils.isEmpty(edit_username.getText())){
            Toast.makeText(getBaseContext(), "用户名不能为空", Toast.LENGTH_SHORT).show();
            edit_username.getText().clear();
        }
        else if (edit_username.getText().toString().length() < 4 || edit_username.getText().toString().length() > 10) {
            Toast.makeText(getBaseContext(), "用户名长度必须在4-10字符之间", Toast.LENGTH_SHORT).show();
            edit_username.getText().clear();
        } else {
            requestData();
        }
    }

    private void requestData() {//校验用户名是否存在,及更新用户名
        requestQueue = Volley.newRequestQueue(getBaseContext());
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt("id", 0);
        String url = constantClass.getHttp_prefix() + "/user/updateUsername?id="+id;
        StringRequest request = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("success")){
                    intent = new Intent(UsernameActivity.this, AccountActivity.class);
                    intent.putExtra("username", edit_username.getText().toString());
                    setResult(RESULT_OK,intent);
                    UsernameActivity.this.finish();
                    ToastUtil.show(getBaseContext(), "用户名修改成功");
                } else if (s.equals("exsit")){
                    edit_username.setText("");
                    ToastUtil.show(getBaseContext(), "该用户已存在,请重新填写");
                } else if (s.equals("fail")) {
                    edit_username.setText("");
                    ToastUtil.show(getBaseContext(), "用户名修改失败");
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
                map.put("username", edit_username.getText().toString());
                return map;
            }
        };
        requestQueue.add(request);
    }
}
