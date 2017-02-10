package com.example.lj.redwine.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.activity.AddAddressActivity;
import com.example.lj.redwine.activity.EditAddressActivity;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.javabean.Address;
import com.example.lj.redwine.util.ToastUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/11/23 0023.
 */
public class AddressBaseAdapter extends BaseAdapter{
    Intent intent;
    ConstantClass constantClass;
    RequestQueue requestQueue;//请求队列
    List<Address> addressList;//地址列表
    Context context;

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public AddressBaseAdapter(Context context, List<Address> addressList) {
        this.context = context;
        this.addressList = addressList;
    }
    @Override
    public int getCount() {
        return (addressList == null) ? 0 : addressList.size();
    }

    @Override
    public Object getItem(int position) {
        return addressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.address_list_item, null);
            viewHolder = new ViewHolder();
            initView(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.consignee_text.setText(addressList.get(position).getConsignee());
        viewHolder.gender_text.setText(addressList.get(position).getGender());
        viewHolder.location_address_text.setText(addressList.get(position).getLocation_address());
        viewHolder.consignee_phone_text.setText(addressList.get(position).getConsignee_phone());
        viewHolder.detail_address_text.setText(addressList.get(position).getDetail_address());
        viewHolder.edit_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(v.getContext(), EditAddressActivity.class);
                intent.putExtra("consignee",addressList.get(position).getConsignee());
                intent.putExtra("gender",addressList.get(position).getGender());
                intent.putExtra("location_address",addressList.get(position).getLocation_address());
                intent.putExtra("consignee_phone",addressList.get(position).getConsignee_phone());
                intent.putExtra("detail_address",addressList.get(position).getDetail_address());
                intent.putExtra("address_id",addressList.get(position).getAddress_id().toString());
                v.getContext().startActivity(intent);
            }
        });
        viewHolder.delete_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("是否删除该地址");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestQueue = Volley.newRequestQueue(v.getContext());
                        String url = constantClass.getHttp_prefix()+"/address/deleteAddress?id="+addressList.get(position).getAddress_id();
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                if (s.equals("success")) {
                                    ToastUtil.show(v.getContext(), "删除成功");
                                } else if (s.equals("fail")) {
                                    ToastUtil.show(v.getContext(), "删除失败");
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                ToastUtil.show(v.getContext(), "网络出了点问题");
                            }
                        });
                        requestQueue.add(stringRequest);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
        return convertView;
    }

    private void initView(ViewHolder viewHolder, View convertView) {//初始化布局
        viewHolder.consignee_text = (TextView) convertView.findViewById(R.id.consignee_text);
        viewHolder.gender_text = (TextView) convertView.findViewById(R.id.gender_text);
        viewHolder.location_address_text = (TextView) convertView.findViewById(R.id.location_address_text);
        viewHolder.consignee_phone_text = (TextView) convertView.findViewById(R.id.consignee_phone_text);
        viewHolder.detail_address_text = (TextView) convertView.findViewById(R.id.detail_address_text);
        viewHolder.edit_address = (ImageView) convertView.findViewById(R.id.edit_address);
        viewHolder.delete_address = (ImageView) convertView.findViewById(R.id.delete_address);
    }

    static class ViewHolder {
        TextView consignee_text;//收货人
        TextView gender_text;//性别
        TextView location_address_text;//定位地址
        TextView consignee_phone_text;//收货人电话
        TextView detail_address_text;//详细地址
        ImageView edit_address;//修改地址
        ImageView delete_address;//删除地址
    }
}
