package com.example.lj.redwine.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;


import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.custom_widget.switch_button.SwitchButton;
import com.example.lj.redwine.util.ToastUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * 注册密码设置及确定登录
 */
public class RegisterPwdFragment extends Fragment implements CompoundButton.OnCheckedChangeListener,View.OnClickListener{
    RequestQueue requestQueue;//请求数据队列
    ConstantClass  constantClass;
    Intent intent;
    EditText register_pwd_edit;//注册设置密码
    SwitchButton password_switch;//密码可视开关
    Button btn_register_pwd;//注册按钮
    String tel;

    public RegisterPwdFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_pwd, container, false);
        initView(view);//初始化控件
        tel = getArguments().getString("te");
        return view;
    }

    private void initView(View view) {
        //初始化布局
        register_pwd_edit = (EditText) view.findViewById(R.id.register_pwd_edit);
        password_switch = (SwitchButton) view.findViewById(R.id.password_switch);
        btn_register_pwd = (Button) view.findViewById(R.id.btn_register_pwd);

        //添加事件监听器
        btn_register_pwd.setOnClickListener(this);
        password_switch.setOnCheckedChangeListener(this);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            register_pwd_edit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            register_pwd_edit.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register_pwd://注册新用户
                validatePassword();//校验输入的密码
                break;
        }
    }

    private void requestData() {
        requestQueue = Volley.newRequestQueue(getActivity());
        String url = constantClass.getHttp_prefix()+"/user/addUser.action";
        Map<String, String> map = new HashMap<String, String>();
        map.put("telephone", tel);
        map.put("password", register_pwd_edit.getText().toString());
        JSONObject jsonObject = new  JSONObject(map);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                intent = new Intent();
                intent.setAction("exit_register");
                getActivity().sendBroadcast(intent);
                ToastUtil.show(getContext(), "注册成功,请先登录");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(getContext(), "网络出现点小问题");
            }
        });
        requestQueue.add(jsonRequest);
        requestQueue.start();
    }
    private void validatePassword() {//校验输入的密码
        if (TextUtils.isEmpty(register_pwd_edit.getText())){
            ToastUtil.show(getContext(), "密码不能为空");
            register_pwd_edit.getText().clear();
        }
        else if (register_pwd_edit.getText().toString().length() > 12 || register_pwd_edit.getText().toString().length() < 6) {
            ToastUtil.show(getContext(), "密码长度必须为6-12位");
            register_pwd_edit.getText().clear();
        } else {
            requestData();//提交注册表单
        }
    }
}
