package com.example.lj.redwine.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import com.example.lj.redwine.R;
import com.example.lj.redwine.constant.ConstantClass;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class ChatClientActivity extends Activity implements View.OnClickListener{
    SharedPreferences sharedPreferences;//存储类
    private TextView text_back;
    private LinearLayout back_layout;
    private ScrollView svChat;
    private EditText etDetails;
    private EditText message;
    private Button send;
    private WebSocketClient client;// 连接客户端
    private Draft draft;// 连接协议
    String web_socket_address;
    ConstantClass constantClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_client);
        initView();
        WebSocketImpl.DEBUG = true;
        System.setProperty("java.net.preferIPv6Addresses", "false");
        System.setProperty("java.net.preferIPv4Stack", "true");
        initWebSocket();
    }

    private void initWebSocket() {
        try {
            sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            String address = web_socket_address + sharedPreferences.getString("username","");
            client = new WebSocketClient(new URI(address), draft) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            etDetails.append("已经连接到服务器\n");
                            back_layout.setEnabled(true);
                            send.setEnabled(true);
                        }
                    });
                }

                @Override
                public void onMessage(final String s) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            etDetails.append(s + "\n");
                        }
                    });
                }

                @Override
                public void onClose(final int i,final String s, boolean b) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            etDetails.append("断开服务器连接\n");
                            send.setEnabled(false);
                        }
                    });
                }

                @Override
                public void onError(final Exception e) {
                    etDetails.append("连接发生了异常\n");
                    send.setEnabled(false);
                }
            };
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        //初始化控件
        text_back = (TextView) findViewById(R.id.text_back);
        text_back.setText("在线咨询");
        back_layout = (LinearLayout) findViewById(R.id.back_layout);
        svChat = (ScrollView) findViewById(R.id.svChat);
        etDetails = (EditText) findViewById(R.id.etDetails);
        message = (EditText) findViewById(R.id.message);
        send = (Button) findViewById(R.id.send);
        draft = new Draft_17();
        web_socket_address = constantClass.getWeb_socket_prefix();
        //添加事件监听器
        back_layout.setOnClickListener(this);
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_layout:
                if (client != null) {
                    client.close();
                    ChatClientActivity.this.finish();
                }
                break;
            case R.id.send:
                try {
                    if (client != null) {
                        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                        client.send(sharedPreferences.getString("username","")+":" + message.getText().toString().trim());
                        svChat.post(new Runnable() {
                            @Override
                            public void run() {
                                svChat.fullScroll(View.FOCUS_DOWN);
                                message.setText("");
                                message.requestFocus();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client != null) {
            client.close();
        }
    }
}
