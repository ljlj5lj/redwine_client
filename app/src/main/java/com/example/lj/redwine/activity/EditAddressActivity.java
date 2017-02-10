package com.example.lj.redwine.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.custom_widget.edittext_clear.ClearEditText;
import com.example.lj.redwine.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

public class EditAddressActivity extends Activity implements View.OnClickListener{
    ConstantClass constantClass;
    RequestQueue requestQueue;//请求队列
    SharedPreferences sharedPreferences;//存储类
    RadioGroup radioGroup;//单选选项组
    RadioButton radioButton;//单选按钮
    Intent intent;//意图跳转
    ClearEditText edit_detail_address;//详细编辑框
    ClearEditText edit_name;//姓名编辑框
    ClearEditText edit_phone;//手机编辑框
    ClearEditText edit_location_address;//定位地址
    LinearLayout location_layout;//定位布局
    LinearLayout right_layout;//保存布局
    LinearLayout back_layout;//后退布局
    TextView text_back;//后退文本
    ImageView right_img;
    String address_id;//地址id
    private static final int LOCATION = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        intent = getIntent();
        address_id = intent.getStringExtra("address_id");
        initView();

    }
    private void initView() {
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        back_layout = (LinearLayout) findViewById(R.id.back_layout);
        back_layout.setOnClickListener(this);
        right_layout = (LinearLayout) findViewById(R.id.right_layout);
        right_layout.setClickable(true);
        right_layout.setOnClickListener(this);
        location_layout = (LinearLayout) findViewById(R.id.location_layout);
        location_layout.setOnClickListener(this);
        if (intent.getStringExtra("gender").equals("先生")){
            radioGroup.check(R.id.radioMale);
        } else if (intent.getStringExtra("gender").equals("女士")){
            radioGroup.check(R.id.radioFemale);
        }

        edit_name = (ClearEditText) findViewById(R.id.edit_name);
        edit_name.setText(intent.getStringExtra("consignee"));
        edit_detail_address = (ClearEditText) findViewById(R.id.edit_detail_address);
        edit_detail_address.setText(intent.getStringExtra("detail_address"));
        edit_location_address = (ClearEditText) findViewById(R.id.edit_location_address);
        edit_location_address.setText(intent.getStringExtra("location_address"));
        edit_phone = (ClearEditText) findViewById(R.id.edit_phone);
        edit_phone.setText(intent.getStringExtra("consignee_phone"));
        text_back = (TextView) findViewById(R.id.text_back);
        text_back.setText("修改收货地址");
        right_img = (ImageView) findViewById(R.id.right_img);
        right_img.setBackgroundResource(R.drawable.correct);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_layout:
                EditAddressActivity.this.finish();
                break;
            case R.id.right_layout:
                validateAddress();//校验输入的地址信息
                break;
            case R.id.location_layout:
                intent = new Intent(getBaseContext(), MapListActivity.class);
                intent.putExtra("address","edit_address");
                startActivityForResult(intent, LOCATION);
                break;
        }
    }
    private void validateAddress() {
        if (TextUtils.isEmpty(edit_name.getText())){
            ToastUtil.show(getBaseContext(),"收货人不能为空");
            edit_name.getText().clear();
        }
        else if (TextUtils.isEmpty(edit_location_address.getText())) {
            ToastUtil.show(getBaseContext(), "定位地址不能为空");
            edit_location_address.getText().clear();
        }
        else if (TextUtils.isEmpty(edit_phone.getText())){
            ToastUtil.show(getBaseContext(),"收货电话不能为空");
            edit_phone.getText().clear();
        }
        else if (!TextUtils.isDigitsOnly(edit_phone.getText())){
            ToastUtil.show(getBaseContext(),"收货电话格式不对");
            edit_phone.getText().clear();
        }
        else if (TextUtils.isEmpty(edit_detail_address.getText())){
            ToastUtil.show(getBaseContext(),"详细地址不能为空");
            edit_detail_address.getText().clear();
        }
        else if (edit_phone.getText().toString().length() < 11 || edit_phone.getText().toString().length() > 11) {
            ToastUtil.show(getBaseContext(),"收货电话长度必须为11位");
            edit_phone.getText().clear();
        } else {
            requestData();
        }
    }

    private void requestData() {//提交地址信息
        requestQueue = Volley.newRequestQueue(getBaseContext());
        String url = constantClass.getHttp_prefix()+"/address/editAddress";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("success")) {
                    EditAddressActivity.this.finish();
                    ToastUtil.show(getBaseContext(), "修改地址成功");
                } else if (s.equals("fail")) {
                    ToastUtil.show(getBaseContext(), "修改失败，请重新输入");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(getBaseContext(), "网络出了点问题");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                radioButton = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                Map<String, String> map = new HashMap<String, String>();
                map.put("gender", radioButton.getText().toString());
                map.put("consignee", edit_name.getText().toString());
                map.put("consignee_phone",edit_phone.getText().toString());
                map.put("location_address",edit_location_address.getText().toString());
                map.put("detail_address",edit_detail_address.getText().toString());
                map.put("address_id",address_id);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCATION) {
            if (resultCode == RESULT_OK){
                Bundle bundle = data.getExtras();
                String location_address = bundle.getString("location_address");
                edit_location_address.setText(location_address);
            }else if (requestCode == RESULT_CANCELED) {

            }

        }
    }
}
