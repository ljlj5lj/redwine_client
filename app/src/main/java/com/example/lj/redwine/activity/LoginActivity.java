package com.example.lj.redwine.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lj.redwine.R;
import com.example.lj.redwine.fragment.AccountLoginFragment;
import com.example.lj.redwine.fragment.OrderFragment;
import com.example.lj.redwine.fragment.PersonFragment;
import com.example.lj.redwine.fragment.TelephoneLoginFragment;

import cn.smssdk.SMSSDK;

public class LoginActivity extends FragmentActivity implements View.OnClickListener{
    IntentFilter intentFilter;
    Intent intent;
    Intent intent1;
    FrameLayout login_frame_layout;//帧布局
    FragmentManager fragmentManager;//碎片管理
    FragmentTransaction fragmentTransaction;//碎片事务
    LinearLayout back_layout;//后退布局
    LinearLayout right_layout;//注册布局
    LinearLayout tel_login_layout;//手机登录布局
    LinearLayout account_login_layout;//账号登录布局
    TextView tel_login_text;//手机登录文本
    TextView account_login_text;//账号登录文本
    TextView right_text;//注册文本
    TextView text_back;//后退文本
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InitView();

    }

    private void InitView() {
        //初始化控件
        back_layout = (LinearLayout) findViewById(R.id.back_layout);
        right_layout = (LinearLayout) findViewById(R.id.right_layout);
        right_layout.setClickable(true);
        tel_login_layout = (LinearLayout) findViewById(R.id.tel_login_layout);
        account_login_layout = (LinearLayout) findViewById(R.id.account_login_layout);
        tel_login_text = (TextView) findViewById(R.id.tel_login_text);
        account_login_text = (TextView) findViewById(R.id.account_login_text);
        right_text = (TextView) findViewById(R.id.right_text);
        right_text.setText("立即注册");
        text_back = (TextView) findViewById(R.id.text_back);
        text_back.setText("用户登录");
        login_frame_layout = (FrameLayout) findViewById(R.id.login_frame_layout);

        //添加事件监听器
        back_layout.setOnClickListener(this);
        right_layout.setOnClickListener(this);
        tel_login_layout.setOnClickListener(this);
        account_login_layout.setOnClickListener(this);

        //初始化进去的验证码登录页面
        fragmentManager = this.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.login_frame_layout, new TelephoneLoginFragment());
        fragmentTransaction.commit();
        tel_login_text.setTextColor(getResources().getColor(R.color.main_color));
        account_login_layout.setBackgroundColor(getResources().getColor(R.color.lightgrey));
    }

    @Override
    public void onClick(View v) {
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.back_layout:
//                intent = new Intent(LoginActivity.this, PersonFragment.class);
//                setResult(RESULT_CANCELED,intent);
                LoginActivity.this.finish();
                break;
            case R.id.right_layout://注册
                SMSSDK.unregisterAllEventHandler();//注销登录页面的所有消息接收
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.account_login_layout://账号登录
                fragmentTransaction.replace(R.id.login_frame_layout, new AccountLoginFragment());
                tel_login_text.setTextColor(getResources().getColor(R.color.grey));
                account_login_text.setTextColor(getResources().getColor(R.color.main_color));
                account_login_layout.setBackgroundColor(getResources().getColor(R.color.white));
                tel_login_layout.setBackgroundColor(getResources().getColor(R.color.lightgrey));
                break;
            case R.id.tel_login_layout://验证码登录
                fragmentTransaction.replace(R.id.login_frame_layout, new TelephoneLoginFragment());
                account_login_text.setTextColor(getResources().getColor(R.color.grey));
                tel_login_text.setTextColor(getResources().getColor(R.color.main_color));
                tel_login_layout.setBackgroundColor(getResources().getColor(R.color.white));
                account_login_layout.setBackgroundColor(getResources().getColor(R.color.lightgrey));
                break;
        }
        fragmentTransaction.commit();
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {//广播接收关闭当前ACTIVITY
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(this);
            ((Activity)context).finish();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        intentFilter = new IntentFilter();
        intentFilter.addAction("close_login_activity");//关闭登录activity
        registerReceiver(this.broadcastReceiver, intentFilter);
    }

}
