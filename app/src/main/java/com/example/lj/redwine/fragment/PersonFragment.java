package com.example.lj.redwine.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.activity.AccountActivity;
import com.example.lj.redwine.activity.AddressActivity;
import com.example.lj.redwine.activity.LoginActivity;
import com.example.lj.redwine.activity.MyCollectionActivity;
import com.example.lj.redwine.activity.MyEvaluationActivity;
import com.example.lj.redwine.activity.ShareActivity;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.custom_widget.imageview_round.RoundImageView;
import com.example.lj.redwine.javabean.User;
import com.example.lj.redwine.util.ToastUtil;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonFragment extends Fragment implements View.OnClickListener{
    User user;
    RoundImageView user_avatar;
    TextView user_name;
    ConstantClass constantClass;
    RequestQueue requestQueue;//请求队列
    SharedPreferences sharedPreferences;//存储类
    LinearLayout account_layout;//账号布局
    LinearLayout comment_layout;//我的酒评布局
    LinearLayout collection_layout;//我的收藏布局
    LinearLayout address_layout;//收货地址布局
    LinearLayout share_layout;//分享给朋友布局
    LinearLayout help_layout;//帮助布局
    LinearLayout update_layout;//更新布局
    LinearLayout telephone_layout;//客服电话布局
    TextView service_telephone;//客服电话
    Dialog dialog;//自定义对话框
    View dialog_view;//对话框的布局View
    Intent intent;//意图跳转
    ImageButton dialog_close;//关闭对话框按钮
    Uri uri;
    private static final int LOGIN = 5;
    private static final int LOGON = 6;

    public PersonFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        InitView(view);
        initUserMessage();
        return view;
    }

    private void initUserMessage() { //初始化用户信息
        requestQueue = Volley.newRequestQueue(getContext());
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt("id", 0);
        if (id == 0) {
            user_name.setText("登录/注册");
        } else {
            user_name.setText(sharedPreferences.getString("username", ""));
            ImageRequest imageRequest = new ImageRequest(constantClass.getHttp_prefix() + "/user_avatar/" + sharedPreferences.getString("avatar", ""),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            user_avatar.setImageBitmap(response);
                        }
                    }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    user_avatar.setImageResource(R.drawable.user_avatar);
                }
            });
            requestQueue.add(imageRequest);
         }
    }

    private void requestData() {
        requestQueue = Volley.newRequestQueue(getContext());
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt("id",0);
        if (id == 0) {
            user_name.setText("登录/注册");
            user_avatar.setImageResource(R.drawable.user_avatar);
        } else {
            String url = constantClass.getHttp_prefix()+"/user/findUserById?id="+id;

            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    user = JSON.parseObject(s, User.class);
                    user_name.setText(user.getUsername());
                    if (user.getAvatar() != null){
                        ImageRequest imageRequest = new ImageRequest(constantClass.getHttp_prefix()+"/user_avatar/"+user.getAvatar(),
                                new Response.Listener<Bitmap>() {
                                    @Override
                                    public void onResponse(Bitmap response) {
                                        user_avatar.setImageBitmap(response);
                                    }
                                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                user_avatar.setImageResource(R.drawable.user_avatar);
                            }
                        });
                        requestQueue.add(imageRequest);
                    } else {
                        user_avatar.setImageResource(R.drawable.user_avatar);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    ToastUtil.show(getContext(), "网络出了点问题");
                }
            });
            requestQueue.add(request);
        }
    }

    private void InitView(View view) {
        user_avatar = (RoundImageView) view.findViewById(R.id.user_avatar);
        user_name = (TextView) view.findViewById(R.id.user_name);
        account_layout = (LinearLayout) view.findViewById(R.id.account_layout);
        comment_layout = (LinearLayout) view.findViewById(R.id.comment_layout);
        collection_layout = (LinearLayout) view.findViewById(R.id.collection_layout);
        address_layout = (LinearLayout) view.findViewById(R.id.address_layout);
        share_layout = (LinearLayout) view.findViewById(R.id.share_layout);
        help_layout = (LinearLayout) view.findViewById(R.id.help_layout);
        update_layout = (LinearLayout) view.findViewById(R.id.update_layout);
        telephone_layout = (LinearLayout) view.findViewById(R.id.telephone_layout);
        service_telephone = (TextView) view.findViewById(R.id.service_telephone);
        dialog_view = LayoutInflater.from(getContext()).inflate(R.layout.help_dialog,null);// 通过LayoutInflater找到对话框布局
        dialog = new AlertDialog.Builder(getContext()).create();//创建对话框布局
        dialog.setCanceledOnTouchOutside(false);//点击对话框外的布局不关闭对话框
        dialog_close = (ImageButton) dialog_view.findViewById(R.id.dialog_close);//布局里的关闭按钮

        //添加事件监听器
        account_layout.setOnClickListener(this);
        comment_layout.setOnClickListener(this);
        collection_layout.setOnClickListener(this);
        address_layout.setOnClickListener(this);
        share_layout.setOnClickListener(this);
        help_layout.setOnClickListener(this);
        update_layout.setOnClickListener(this);
        telephone_layout.setOnClickListener(this);
        dialog_close.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        switch (v.getId()){
            case R.id.account_layout:
                if (sharedPreferences.getInt("id", 0) == 0) {
                    intent = new Intent(getContext(), LoginActivity.class);
                    startActivityForResult(intent, LOGIN);
                } else {
                    intent = new Intent(getContext(), AccountActivity.class);
                    startActivityForResult(intent, LOGON);
                }
                break;
            case R.id.dialog_close:
                dialog.dismiss();//关闭对话框
                break;
            case R.id.comment_layout:
                if (sharedPreferences.getInt("id", 0 ) == 0) {
                    intent = new Intent(getContext(), LoginActivity.class);
                    ToastUtil.show(getContext(), "请先登录");
                } else {
                    intent = new Intent(getContext(), MyEvaluationActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.collection_layout:
                if (sharedPreferences.getInt("id", 0 ) == 0) {
                    intent = new Intent(getContext(), LoginActivity.class);
                    ToastUtil.show(getContext(), "请先登录");
                } else {
                    intent = new Intent(getContext(), MyCollectionActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.address_layout:
                if (sharedPreferences.getInt("id", 0 ) == 0) {
                    intent = new Intent(getContext(), LoginActivity.class);
                    ToastUtil.show(getContext(), "请先登录");
                } else {
                    intent = new Intent(getContext(), AddressActivity.class);
                    intent.putExtra("addresses","manage_address");
                }
                startActivity(intent);
                break;
            case R.id.share_layout:
                intent = new Intent(getContext(), ShareActivity.class);
                startActivity(intent);
                break;
            case R.id.help_layout:
                dialog.show();//显示对话框
                dialog.getWindow().setContentView(dialog_view);
                break;
            case R.id.update_layout:
                Toast.makeText(getContext(),"已经为最新版本",Toast.LENGTH_SHORT).show();
                break;
            case R.id.telephone_layout:
                uri = Uri.parse("tel:"+service_telephone.getText());
                intent = new Intent(Intent.ACTION_DIAL,uri);
                startActivity(intent);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN) {
            requestData();
        }
        else if (requestCode == LOGON) {
            requestData();
        }
    }
}
