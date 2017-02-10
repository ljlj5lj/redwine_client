package com.example.lj.redwine.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lj.redwine.R;
import com.example.lj.redwine.fragment.RegisterCodeFragment;
import com.example.lj.redwine.fragment.RegisterPwdFragment;
import com.example.lj.redwine.fragment.RegisterTelFragment;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends FragmentActivity implements View.OnClickListener{
    private static final int SMSDDK_HANDLER = 1;  //短信回调
    String tel;//接收到的手机号码
    Bundle bundle;//数据传输
    IntentFilter intentFilter1,intentFilter2,intentFilter3;//意图拦截
    LinearLayout back_layout;//后退布局
    LinearLayout right_layout;//注册布局
    TextView register_tel_text;//注册电话文本
    TextView register_code_text;//注册验证码文本
    TextView register_pwd_text;//注册密码文本
    TextView right_text;//倒计时文本
    TextView text_back;//后退文本
    FragmentManager fragmentManager;//碎片管理
    FragmentTransaction fragmentTransaction;//碎片事务
    FrameLayout register_frame_layout;//注册帧布局
    TimeCount timeCount;//倒计时
    RegisterCodeFragment registerCodeFragment;//注册验证码碎片
    RegisterPwdFragment registerPwdFragment;//注册密码碎片
    FirstBroadCast firstBroadCast;//第一个广播
    SecondBroadCast secondBroadCast;//第二个广播
    ThirdBroadCast thirdBroadCast;//第三个广播
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        InitView();
        initSMSSDK();//初始化短信SDK
    }

    private void initSMSSDK() {
//        SMSSDK.initSDK(getBaseContext(), "1637c47884246", "bc75e3d5d667114fe7328d42acc67d05");
    }

    private void InitView() {
        //初始化控件
        back_layout = (LinearLayout) findViewById(R.id.back_layout);
        right_layout = (LinearLayout) findViewById(R.id.right_layout);
        right_text = (TextView) findViewById(R.id.right_text);
        text_back = (TextView) findViewById(R.id.text_back);
        text_back.setText("注册");
        register_tel_text = (TextView) findViewById(R.id.register_tel_text);
        register_code_text = (TextView) findViewById(R.id.register_code_text);
        register_pwd_text = (TextView) findViewById(R.id.register_pwd_text);
        register_frame_layout = (FrameLayout) findViewById(R.id.register_frame_layout);

        //添加事件监听器
        back_layout.setOnClickListener(this);
        right_layout.setOnClickListener(this);

        //初始化注册第一步的页面
        fragmentManager = this.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.register_frame_layout, new RegisterTelFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_layout://退出注册
                RegisterActivity.this.finish();
//                intent = new Intent();
//                intent.setAction("exit_register");
//                getBaseContext().sendBroadcast(intent);
                break;
            case R.id.right_layout://再次获取验证码
                timeCount = new TimeCount(30000, 1000);
                timeCount.start();
                SMSSDK.getVerificationCode("86", tel);
                break;
        }
    }

    class FirstBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            tel = intent.getStringExtra("tel");//已发短信的手机号码
            fragmentTransaction = fragmentManager.beginTransaction();
            if (intent.getAction().equals("send_code_count")){
                //初始化注册验证码Fragment
                registerCodeFragment = new RegisterCodeFragment();
                //传递手机号码参数
                bundle = new Bundle();
                bundle.putString("tel",tel);
                registerCodeFragment.setArguments(bundle);
                //跳转至注册验证码页面
                fragmentTransaction.replace(R.id.register_frame_layout, registerCodeFragment);
                //设置注册步骤栏颜色
                register_tel_text.setTextColor(getResources().getColor(R.color.grey));
                register_code_text.setTextColor(getResources().getColor(R.color.main_color));
                //设置短信接收倒计时
                timeCount = new TimeCount(30000, 1000);
                timeCount.start();
            }
            unregisterReceiver(firstBroadCast);
            fragmentTransaction.commit();
        }
    }
    class SecondBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            tel = intent.getStringExtra("tel");//已发短信的手机号码

            fragmentTransaction = fragmentManager.beginTransaction();
            if (intent.getAction().equals("setting_pwd")){
                //初始化注册密码Fragment
                registerPwdFragment = new RegisterPwdFragment();
                //传递手机号码参数
                bundle = new Bundle();
                bundle.putString("te",tel);
                registerPwdFragment.setArguments(bundle);
                //跳转至密码设置页面
                fragmentTransaction.replace(R.id.register_frame_layout, registerPwdFragment);
                //设置注册步骤栏颜色
                register_code_text.setTextColor(getResources().getColor(R.color.grey));
                register_pwd_text.setTextColor(getResources().getColor(R.color.main_color));
                //停止计时,并设置倒计时布局不可按
                timeCount.cancel();
                timeCount = null;
                right_text.setText("");
                right_layout.setClickable(false);
            }

            unregisterReceiver(secondBroadCast);
            fragmentTransaction.commit();
        }
    }
    class ThirdBroadCast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("exit_register")) {
                ((Activity)context).finish();
            }
            unregisterReceiver(thirdBroadCast);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        firstBroadCast = new FirstBroadCast();
        intentFilter1 = new IntentFilter();
        intentFilter1.addAction("send_code_count");//发送验证码倒计时
        registerReceiver(firstBroadCast, intentFilter1);

        secondBroadCast = new SecondBroadCast();
        intentFilter2 = new IntentFilter();
        intentFilter2.addAction("setting_pwd");//转向密码设置
        registerReceiver(secondBroadCast, intentFilter2);

        thirdBroadCast = new ThirdBroadCast();
        intentFilter3 = new IntentFilter();
        intentFilter3.addAction("exit_register");//退出注册
        registerReceiver(thirdBroadCast, intentFilter3);
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

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            right_text.setText(millisUntilFinished / 1000+"秒后重新发送");//设置倒数
            right_layout.setClickable(false);//设置布局不可按
        }

        @Override
        public void onFinish() {
            right_text.setText("重发验证码");
            right_layout.setClickable(true);//是指布局可按
        }
    }
}
