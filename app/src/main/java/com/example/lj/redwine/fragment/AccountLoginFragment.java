package com.example.lj.redwine.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.activity.SmsPasswordActivity;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.custom_widget.edittext_clear.ClearEditText;
import com.example.lj.redwine.custom_widget.switch_button.SwitchButton;
import com.example.lj.redwine.javabean.User;
import com.example.lj.redwine.util.ToastUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountLoginFragment extends Fragment implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    ConstantClass constantClass;
    RequestQueue requestQueue;//请求队列
    SharedPreferences sharedPreferences;//存储类
    Intent intent;//意图
    SwitchButton password_switch;//密码可视开关
    ClearEditText edit_phone_account;//账号编辑框
    ClearEditText edit_phone_password;//密码编辑框
    Button btn_login;//登录按钮
    TextView forget_pwd_text;//忘记密码
    User user;
    public AccountLoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_login, container, false);
        initView(view);//初始化控件
        return view;
    }

    private void initView(View view) {
        password_switch = (SwitchButton) view.findViewById(R.id.password_switch);
        edit_phone_account = (ClearEditText) view.findViewById(R.id.edit_phone_account);
        edit_phone_password = (ClearEditText) view.findViewById(R.id.edit_phone_password);
        btn_login = (Button) view.findViewById(R.id.btn_login);
        forget_pwd_text = (TextView) view.findViewById(R.id.forget_pwd_text);

        //添加事件监听器
        btn_login.setOnClickListener(this);
        forget_pwd_text.setOnClickListener(this);
        password_switch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                validateAccount();//校验输入的用户名和密码
                break;
            case R.id.forget_pwd_text:
                intent = new Intent(getContext(), SmsPasswordActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void validateAccount() {
        if (TextUtils.isEmpty(edit_phone_account.getText())){
            Toast.makeText(getContext(), "用户名不能为空", Toast.LENGTH_SHORT).show();
            edit_phone_account.getText().clear();
        }
        else if (TextUtils.isEmpty(edit_phone_password.getText())){
            Toast.makeText(getContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
            edit_phone_password.getText().clear();
        }
        else if (edit_phone_password.getText().toString().length() < 4 || edit_phone_password.getText().toString().length() > 11) {
            Toast.makeText(getContext(), "密码长度必须在4至11位", Toast.LENGTH_SHORT).show();
            edit_phone_password.getText().clear();
        } else {
            requestData();
        }
    }

    private void requestData() {
        requestQueue = Volley.newRequestQueue(getContext());
        String url = constantClass.getHttp_prefix()+"/user/accountLogin";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("error")){
                    ToastUtil.show(getContext(), "用户名和密码不匹配");
                    edit_phone_password.setText("");
                    edit_phone_account.setText("");
                } else {
                    user = JSON.parseObject(s, User.class);
                    sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("id", user.getUser_id());
                    editor.putString("telephone", user.getTelephone());
                    editor.putString("username", user.getUsername());
                    editor.putString("avatar", user.getAvatar());
                    editor.commit();
                    intent = new Intent();
                    intent.setAction("close_login_activity");
                    getActivity().sendBroadcast(intent);
                    ToastUtil.show(getContext(), "登录成功");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(getContext(), "网络出了点问题");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("username", edit_phone_account.getText().toString());
                map.put("password", edit_phone_password.getText().toString());
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {//密码可视开关
        if (isChecked) {
            edit_phone_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            edit_phone_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);
        }
    }
}
