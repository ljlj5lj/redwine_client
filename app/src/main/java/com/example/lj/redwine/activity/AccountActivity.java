package com.example.lj.redwine.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.fragment.PersonFragment;
import com.example.lj.redwine.javabean.User;
import com.example.lj.redwine.util.BitmapUtil;
import com.example.lj.redwine.util.ToastUtil;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import cn.smssdk.SMSSDK;


public class AccountActivity extends Activity implements View.OnClickListener{
    User user;
    ConstantClass constantClass;
    RequestQueue requestQueue;//请求队列
    SharedPreferences sharedPreferences;//存储类
    Intent intent;//意图跳转
    LinearLayout back_layout;//后退布局
    LinearLayout password_layout;//密码布局
    RelativeLayout picture_layout;//头像布局
    RelativeLayout username_layout;//用户名布局
    RelativeLayout phone_layout;//手机布局
    RelativeLayout credit_card_layout;//银行卡布局
    Button log_out;//登出按钮

    TextView text_back;//后退文本
    TextView username_text;//用户名文本
    TextView phone_text;//电话文本
    ImageView picture_img;//头像
    String phone_number;//电话号码
    String user_name;//用户名
    final BitmapUtil bitmapUtil = new BitmapUtil(AccountActivity.this);
    private File headFile;
    private static final int USERNAME = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        requestData();
        InitView();

    }

    private void requestData() {//请求用户个人信息
        requestQueue = Volley.newRequestQueue(getBaseContext());
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt("id", 0);
        String url = constantClass.getHttp_prefix()+"/user/findUserById?id="+id;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                user = JSON.parseObject(s, User.class);
                phone_number = user.getTelephone();
                phone_text.setText(phone_number);
                user_name = user.getUsername();
                username_text.setText(user_name);
                if (user.getAvatar() != null){
                    ImageRequest imageRequest = new ImageRequest(constantClass.getHttp_prefix()+"/user_avatar/"+user.getAvatar(),
                            new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap response) {
                                    picture_img.setImageBitmap(response);
                                }
                            }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            picture_img.setImageResource(R.drawable.cat);
                        }
                    });
                    requestQueue.add(imageRequest);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(getBaseContext(), "网络出了点问题");
            }
        });
        requestQueue.add(request);

    }

    private void InitView() {
        //初始化布局
        back_layout = (LinearLayout) findViewById(R.id.back_layout);
        text_back = (TextView) findViewById(R.id.text_back);
        text_back.setText("账号管理");
        password_layout = (LinearLayout) findViewById(R.id.password_layout);
        picture_layout = (RelativeLayout) findViewById(R.id.picture_layout);
        username_layout = (RelativeLayout) findViewById(R.id.username_layout);
        phone_layout = (RelativeLayout) findViewById(R.id.phone_layout);
        phone_text = (TextView) findViewById(R.id.phone_text);
        credit_card_layout = (RelativeLayout) findViewById(R.id.credit_card_layout);
        log_out = (Button) findViewById(R.id.log_out);
        username_text = (TextView) findViewById(R.id.username_text);
        picture_img = (ImageView) findViewById(R.id.picture_img);

        //添加事件监听器
        back_layout.setOnClickListener(this);
        password_layout.setOnClickListener(this);
        picture_layout.setOnClickListener(this);
        username_layout.setOnClickListener(this);
        phone_layout.setOnClickListener(this);
        credit_card_layout.setOnClickListener(this);
        log_out.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_layout:
                intent = new Intent(AccountActivity.this, PersonFragment.class);
                setResult(RESULT_CANCELED,intent);
                AccountActivity.this.finish();
                break;
            case R.id.password_layout:
                editPasswordMethod();//选择修改密码方式
                break;
            case R.id.picture_layout:
                selectPic();//选择头像
                break;
            case R.id.username_layout:
                intent = new Intent(getBaseContext(), UsernameActivity.class);
                startActivityForResult(intent, USERNAME);
                break;
            case R.id.phone_layout:
                break;
            case R.id.credit_card_layout:
                break;
            case R.id.log_out:
                sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
//                intent = new Intent(AccountActivity.this, PersonFragment.class);
//                setResult(RESULT_OK,intent);
                AccountActivity.this.finish();
                ToastUtil.show(getBaseContext(), "已成功登出");
                break;
        }
    }

    private void selectPic() {//选择获取图片方式
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
        builder.setTitle("头像选择");
        builder.setNegativeButton("相册选取", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                bitmapUtil.selectPicFromLocal();
            }
        });
        builder.setPositiveButton("相机拍照", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
                if (sdCardExist) {
                    bitmapUtil.selectPicFromCamera();
                }
            }
        }).show();
    }

    private void editPasswordMethod() {//选择修改密码方式
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
        //设置下拉列表的标题
        builder.setTitle("选择修改密码方式");
        //设置下拉列表显示的数据
        final String [] methods = {"短信验证", "通过旧密码"};
        //设置下拉列表选择项
        builder.setItems(methods, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        intent = new Intent(getBaseContext(), SmsPasswordActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        Toast.makeText(getBaseContext(), "通过旧密码", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//回调图片
        switch (requestCode) {
            case BitmapUtil.activity_result_camera_with_data://拍照
                try {
                    if (bitmapUtil.tempFile != null) {
                        bitmapUtil.cropImageByCamera();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case BitmapUtil.activity_result_crop_image_with_data://截图回调结果
                try {
                    if (bitmapUtil.tempFile != null) {
                        headFile = bitmapUtil.tempFile;
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(headFile));
                        uploadImage(bitmap);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case USERNAME:
                if (resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    String username = bundle.getString("username");
                    username_text.setText(username);
                } else if (requestCode == RESULT_CANCELED) {

                }

                break;
        }
    }
    private String getStringImage(Bitmap bitmap) {//将图片转为字符串
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);//压缩图片
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String encodeImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodeImage;
    }

    private void uploadImage(final Bitmap bitmap) {//上传图片
        requestQueue = Volley.newRequestQueue(getBaseContext());
        int id = sharedPreferences.getInt("id", 0);
        String url = constantClass.getHttp_prefix() + "/user/uploadAvatar?id="+id;
        String imageString = getStringImage(bitmap);
        String imageName = phone_number + "_" + System.currentTimeMillis();
        Map<String, String> map = new HashMap<String, String>();
        map.put("imageString", imageString);
        map.put("imageName", imageName);
        JSONObject jsonObject = new  JSONObject(map);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                picture_img.setImageBitmap(bitmap);
                ToastUtil.show(getBaseContext(), "上传头像成功");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(getBaseContext(), "网络出现点小问题");
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

}
