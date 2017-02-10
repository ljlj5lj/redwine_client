package com.example.lj.redwine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.util.ToastUtil;


import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 注册电话验证
 */
public class RegisterTelFragment extends Fragment implements View.OnClickListener{
    ConstantClass constantClass;
    RequestQueue requestQueue;//请求数据队列
    Intent intent;
    Bundle bundle;
    private static final int SMSDDK_HANDLER = 1;  //短信回调
    EditText register_tel_edit;//注册手机号码编辑框
    Button btn_register_code;//获取验证码

    public RegisterTelFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_tel, container, false);
        requestQueue = Volley.newRequestQueue(getActivity());
        register_tel_edit = (EditText) view.findViewById(R.id.register_tel_edit);
        btn_register_code = (Button) view.findViewById(R.id.btn_register_code);
        btn_register_code.setOnClickListener(this);
        initSMSSDK();//初始化短信SDK
        return view;
    }

    private void initSMSSDK() {
        SMSSDK.initSDK(getContext(), "1637c47884246", "bc75e3d5d667114fe7328d42acc67d05");
        EventHandler eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message message = new Message();
                message.arg1 = event;
                message.arg2 = result;
                message.obj = data;
                message.what = SMSDDK_HANDLER;
                handler.sendMessage(message);
            }
        };
        // 注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what){
                case SMSDDK_HANDLER:
                    int event = message.arg1;
                    int result = message.arg2;
                    Object data = message.obj;
                    if (result == SMSSDK.RESULT_COMPLETE){ //回调完成
                        if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {//已发送验证码
                            Toast.makeText(getContext(), "验证码已经发出", Toast.LENGTH_SHORT).show();
                            intent = new Intent();
                            intent.putExtra("tel", register_tel_edit.getText().toString());
                            intent.setAction("send_code_count");
                            getActivity().sendBroadcast(intent);
                        }
                    }else {
                        Toast.makeText(getContext(), "发送验证码失败", Toast.LENGTH_SHORT).show();
                        ((Throwable) data).printStackTrace();
                    }
                    break;
            }
        }
    };
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register_code:
                validatePhoneNumber();
                break;
        }
    }

    private void validatePhoneNumber() {//校验输入的电话号码
        if (TextUtils.isEmpty(register_tel_edit.getText())){
            ToastUtil.show(getContext(), "手机号码不能为空");
            register_tel_edit.getText().clear();
        }
        else if (!TextUtils.isDigitsOnly(register_tel_edit.getText())) {
            ToastUtil.show(getContext(), "手机号码必须为纯数字");
            register_tel_edit.getText().clear();
        }
        else if (register_tel_edit.getText().toString().length() < 11) {
            ToastUtil.show(getContext(), "手机号码不能少于11位");
            register_tel_edit.getText().clear();
        } else {
            requestData();//请求服务器判断手机号码是否已经注册
        }
    }

    private void requestData() {
        String url = constantClass.getHttp_prefix()+"/user/findTelephone.action?telephone="+register_tel_edit.getText().toString();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("exsit")) {
                    ToastUtil.show(getContext(), "该手机号码已被注册");
                } else if (s.equals("no_exsit")){ //手机号码不存在
                    intent = new Intent();
                    intent.putExtra("tel", register_tel_edit.getText().toString());
                    intent.setAction("send_code_count");
                    getActivity().sendBroadcast(intent);
                    //发送验证码
                    SMSSDK.getVerificationCode("86", register_tel_edit.getText().toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(getContext(), "网络出现点小问题");
            }
        });
        requestQueue.add(request);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }
}
