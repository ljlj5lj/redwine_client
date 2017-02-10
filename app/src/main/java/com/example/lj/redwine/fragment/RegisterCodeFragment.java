package com.example.lj.redwine.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.lj.redwine.R;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 注册验证码
 */
public class RegisterCodeFragment extends Fragment implements View.OnClickListener{
    private static final int SMSDDK_HANDLER = 3;  //短信回调
    String tel;
    Intent intent;
    EditText register_code_edit;//注册验证码编辑框
    Button btn_register_next;//下一步

    public RegisterCodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_code, container, false);
        register_code_edit = (EditText) view.findViewById(R.id.register_code_edit);
        btn_register_next = (Button) view.findViewById(R.id.btn_register_next);
        //事件监听器
        btn_register_next.setOnClickListener(this);
        tel = getArguments().getString("tel");
        registerSMSSDK();
        return view;
    }

    private void registerSMSSDK() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId() ){
            case R.id.btn_register_next:
                //验证手机短信正确性
                SMSSDK.submitVerificationCode("86", tel, register_code_edit.getText().toString());
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
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
                            Toast.makeText(getContext(), "验证成功", Toast.LENGTH_SHORT).show();
                            //跳转到设置密码页面
                            intent = new Intent();
                            intent.putExtra("tel", tel);
                            intent.setAction("setting_pwd");
                            getActivity().sendBroadcast(intent);
                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {//已发送验证码
                            Toast.makeText(getContext(), "验证码已经发出", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getContext(), "验证码不正确", Toast.LENGTH_SHORT).show();
                        ((Throwable) data).printStackTrace();
                    }
                    break;
            }
        }
    };

}
