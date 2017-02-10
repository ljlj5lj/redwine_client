package com.example.lj.redwine.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.activity.AccountActivity;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.custom_widget.edittext_clear.ClearEditText;
import com.example.lj.redwine.javabean.User;
import com.example.lj.redwine.util.ToastUtil;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 手机验证码登录
 */
public class TelephoneLoginFragment extends Fragment implements View.OnClickListener{
    SharedPreferences sharedPreferences;//存储类
    RequestQueue requestQueue;//请求数据队列
    ConstantClass constantClass;
    private static final int CODE_ING = 1;   //已发送，倒计时
    private static final int CODE_REPEAT = 2;  //重新发送
    private static final int SMSDDK_HANDLER = 3;  //短信回调
    Intent intent;//意图跳转
    User user;
    Button btn_login;//登录按钮
    Button btn_code;//获取验证码按钮
    TextView tip_text;//提示信息
    TimeCount timeCount;//倒时
    ClearEditText edit_phone_number;//手机号码
    ClearEditText edit_phone_code;//验证码

    public TelephoneLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_telephone_login, container, false);
        initView(view);//初始化控件
        initSMSSDK();//初始化短信SDK
        return view;
    }

    private void initView(View view) {
        btn_code = (Button) view.findViewById(R.id.btn_code);
        btn_login = (Button) view.findViewById(R.id.btn_login);
        edit_phone_number = (ClearEditText) view.findViewById(R.id.edit_phone_number);
        edit_phone_code = (ClearEditText) view.findViewById(R.id.edit_phone_code);
        tip_text = (TextView) view.findViewById(R.id.tip_text);

        //添加事件监听器
        btn_login.setOnClickListener(this);
        btn_code.setOnClickListener(this);
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
                      if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){//验证码验证成功
                          //连接后台服务器
                          requestData_login();
                      } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {//已发送验证码
                          Toast.makeText(getContext(), "验证码已经发出", Toast.LENGTH_SHORT).show();
                      }
                  }else {
                      Toast.makeText(getContext(), "回调失败", Toast.LENGTH_SHORT).show();
                      ((Throwable) data).printStackTrace();
                  }
                  break;
          }
      }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_code:
                validatePhoneNumber();//校验输入的电话号码
                break;
            case R.id.btn_login:
                validateCode();//校验输入的验证码
                break;
        }
    }

    private void validateCode() {
        if (TextUtils.isEmpty(edit_phone_number.getText())){
            Toast.makeText(getContext(), "手机号码不能为空", Toast.LENGTH_SHORT).show();
        }
        else if (!TextUtils.isDigitsOnly(edit_phone_number.getText())) {
            Toast.makeText(getContext(), "手机号码必须为纯数字", Toast.LENGTH_SHORT).show();
        }
        else if (edit_phone_number.getText().toString().length() < 11 || edit_phone_number.getText().toString().length() > 11) {
            Toast.makeText(getContext(), "手机号码必须为11位", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(edit_phone_code.getText())) {
            Toast.makeText(getContext(), "验证不能为空", Toast.LENGTH_SHORT).show();
        }
        else if (!TextUtils.isDigitsOnly(edit_phone_code.getText())) {
            Toast.makeText(getContext(), "验证码必须为纯数字", Toast.LENGTH_SHORT).show();
        }
        else {
            SMSSDK.submitVerificationCode("86", edit_phone_number.getText().toString() ,edit_phone_code.getText().toString());
        }
    }

    private void requestData_login() {
        requestQueue = Volley.newRequestQueue(getActivity());
        String url = constantClass.getHttp_prefix()+"/user/telephoneLogin?telephone="+edit_phone_number.getText().toString();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(getContext(), "网络出了点问题");
            }
        });
        requestQueue.add(request);
    }

    private void validatePhoneNumber() {//校验输入的电话号码
        if (TextUtils.isEmpty(edit_phone_number.getText())){
            Toast.makeText(getContext(), "手机号码不能为空", Toast.LENGTH_SHORT).show();
            edit_phone_number.getText().clear();
        }
        else if (!TextUtils.isDigitsOnly(edit_phone_number.getText())) {
            Toast.makeText(getContext(), "手机号码必须为纯数字", Toast.LENGTH_SHORT).show();
            edit_phone_number.getText().clear();
        }
        else if (edit_phone_number.getText().toString().length() < 11 || edit_phone_number.getText().toString().length() > 11) {
            Toast.makeText(getContext(), "手机号码必须为11位", Toast.LENGTH_SHORT).show();
            edit_phone_number.getText().clear();
        } else {
            requestData();
        }
    }



    private void requestData() {//校验手机号码是否已经注册
        requestQueue = Volley.newRequestQueue(getActivity());
        String url = constantClass.getHttp_prefix()+"/user/findTelephone.action?telephone="+edit_phone_number.getText().toString();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("exsit")) {//手机号码已存在,直接发送验证码
                    tip_text.setTextColor(getResources().getColor(R.color.white));
                    tip_text.setText("");
                    timeCount = new TimeCount(60000, 1000);
                    timeCount.start();
                    SMSSDK.getVerificationCode("86", edit_phone_number.getText().toString());
                } else if (s.equals("no_exsit")){ //手机号码不存在
                    tip_text.setTextColor(getResources().getColor(R.color.royalblue));
                    tip_text.setText("该手机号还未被注册,请先注册");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                tip_text.setTextColor(getResources().getColor(R.color.royalblue));
                tip_text.setText("网络出了点问题");
            }
        });
        requestQueue.add(request);
    }

    class TimeCount extends CountDownTimer {//自定义短信发送倒计时
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btn_code.setTextSize(14);
            btn_code.setText(millisUntilFinished / 1000+"秒后重新发送");
            btn_code.setClickable(false);
            btn_code.setBackgroundResource(R.drawable.btn_code_sending);
        }

        @Override
        public void onFinish() {
            btn_code.setText("获取验证码");
            btn_code.setBackgroundResource(R.drawable.btn_code);
            btn_code.setClickable(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timeCount != null){
            timeCount.cancel();
            timeCount = null;
        }
        SMSSDK.unregisterAllEventHandler();
    }
}
