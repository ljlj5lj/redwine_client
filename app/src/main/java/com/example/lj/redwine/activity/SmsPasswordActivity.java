package com.example.lj.redwine.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.custom_widget.edittext_clear.ClearEditText;
import com.example.lj.redwine.util.ToastUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class SmsPasswordActivity extends Activity implements View.OnClickListener{
    RequestQueue requestQueue;//请求数据队列
    ConstantClass constantClass;
    SharedPreferences sharedPreferences;//存储类
    LinearLayout back_layout;//后退布局
    Button btn_password;//修改按钮
    Button btn_code;//获取验证码按钮
    ClearEditText edit_sms_password;//修改的密码
    ClearEditText edit_phone_code;//验证码
    TextView text_back;//后退文本
    TimeCount timeCount;//倒时
    private static final int SMSDDK_HANDLER = 3;  //短信回调

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_password);
        initView();
        initSMSSDK();//初始化短信SDK
    }

    private void initSMSSDK() {
        SMSSDK.initSDK(getBaseContext(), "1637c47884246", "bc75e3d5d667114fe7328d42acc67d05");
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
                            requestData();//提交修改密码表单
                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {//已发送验证码
                            Toast.makeText(getBaseContext(), "验证码已经发出", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getBaseContext(), "回调失败", Toast.LENGTH_SHORT).show();
                        ((Throwable) data).printStackTrace();
                    }
                    break;
            }
        }
    };

    private void initView() {
        //初始化布局
        back_layout = (LinearLayout) this.findViewById(R.id.back_layout);
        text_back = (TextView) this.findViewById(R.id.text_back);
        text_back.setText("重置密码--短信验证");
        edit_sms_password = (ClearEditText) this.findViewById(R.id.edit_sms_password);
        edit_phone_code = (ClearEditText) this.findViewById(R.id.edit_phone_code);
        btn_password = (Button) this.findViewById(R.id.btn_password);
        btn_code = (Button) this.findViewById(R.id.btn_code);

        //添加事件监听
        back_layout.setOnClickListener(this);
        btn_password.setOnClickListener(this);
        btn_code.setOnClickListener(this);
    }

    private void validateCode() {//校验输入的密码和验证码
        if (TextUtils.isEmpty(edit_sms_password.getText())){
            ToastUtil.show(getBaseContext(), "密码不能为空");
            edit_sms_password.getText().clear();
        }
        else if (edit_sms_password.getText().toString().length() > 12 || edit_sms_password.getText().toString().length() < 6) {
            ToastUtil.show(getBaseContext(), "密码长度必须为6-12位");
            edit_sms_password.getText().clear();
        }
        else if (TextUtils.isEmpty(edit_phone_code.getText())) {
            Toast.makeText(getBaseContext(), "验证不能为空", Toast.LENGTH_SHORT).show();
        }
        else if (!TextUtils.isDigitsOnly(edit_phone_code.getText())) {
            Toast.makeText(getBaseContext(), "验证码必须为纯数字", Toast.LENGTH_SHORT).show();
        } else {
            sharedPreferences = getBaseContext().getSharedPreferences("user", Context.MODE_PRIVATE);
            SMSSDK.submitVerificationCode("86", sharedPreferences.getString("telephone", "").toString() ,edit_phone_code.getText().toString());
        }
    }

    private void requestData() {
        requestQueue = Volley.newRequestQueue(getBaseContext());
        sharedPreferences = getBaseContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt("id", 0);
        String url = constantClass.getHttp_prefix()+"/user/updatePassword?id="+id;
        Map<String, String> map = new HashMap<String, String>();
        map.put("password", edit_sms_password.getText().toString());
        JSONObject jsonObject = new  JSONObject(map);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                ToastUtil.show(getBaseContext(), "密码重置成功");
                SmsPasswordActivity.this.finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(getBaseContext(), "网络出现点小问题");
            }
        });
        requestQueue.add(jsonRequest);
    }
    private void validatePassword() {//校验输入的密码
        if (TextUtils.isEmpty(edit_sms_password.getText())){
            ToastUtil.show(getBaseContext(), "密码不能为空");
            edit_sms_password.getText().clear();
        }
        else if (edit_sms_password.getText().toString().length() > 12 || edit_sms_password.getText().toString().length() < 6) {
            ToastUtil.show(getBaseContext(), "密码长度必须为6-12位");
            edit_sms_password.getText().clear();
        } else {
            sharedPreferences = getBaseContext().getSharedPreferences("user", Context.MODE_PRIVATE);
            timeCount = new TimeCount(60000, 1000);//60秒重发短信倒计时
            timeCount.start();
            //发送验证码
            SMSSDK.getVerificationCode("86", sharedPreferences.getString("telephone", "").toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_password:
                validateCode();
                break;
            case R.id.btn_code:
                validatePassword();
                break;
            case R.id.back_layout:
                SmsPasswordActivity.this.finish();
                break;
        }
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
