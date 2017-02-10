package com.example.lj.redwine.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.activity.EvaluationActivity;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.javabean.Favorites;
import com.example.lj.redwine.javabean.OrderItem;
import com.example.lj.redwine.javabean.Orders;
import com.example.lj.redwine.javabean.Redwine;
import com.example.lj.redwine.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2016/12/2 0002.
 */
public class OrderAdatper extends RecyclerView.Adapter<OrderAdatper.ViewHolder> {
    SharedPreferences sharedPreferences;
    RequestQueue requestQueue;
    Intent intent;
    ConstantClass constantClass;
    private List<OrderItem> orderItemList;
    OrderItemAdapter orderItemAdapter;
    private List<Orders> ordersList;
    private Context context;
    // 添加多个项(自定义方法)
    public void addItem(List<Orders> list) {
        if (null != list && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ordersList.add(list.get(i));
            }
        }
    }

    public OrderAdatper(Context context, List<Orders> ordersList) {
        this.context = context;
        this.ordersList = ordersList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.order_status_text.setText(ordersList.get(position).getOrder_status().getStatus_name());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        holder.order_time_text.setText(simpleDateFormat.format(ordersList.get(position).getOrder_time()));
        holder.order_price_text.setText(ordersList.get(position).getTotal_price().toString());
        holder.btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(v.getContext(), EvaluationActivity.class);
                intent.putExtra("order_id", ordersList.get(position).getOrder_id());
                v.getContext().startActivity(intent);
            }
        });

        requestQueue = Volley.newRequestQueue(holder.itemView.getContext());
        sharedPreferences = holder.itemView.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String url = constantClass.getHttp_prefix()+"/orderItem/listOrderItemsByOrderId?id="+ordersList.get(position).getOrder_id();
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                orderItemList = JSON.parseArray(s, OrderItem.class);
                orderItemAdapter = new OrderItemAdapter(holder.itemView.getContext(), orderItemList);
                holder.order_item_list_view.setAdapter(orderItemAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(holder.itemView.getContext(), "网络出了点问题");
            }
        });
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return (ordersList == null || ordersList.size() == 0) ? 0 : ordersList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView order_status_text;
        TextView order_time_text;
        ListView order_item_list_view;
        TextView order_price_text;
        Button btn_comment;
        public ViewHolder(View itemView) {
            super(itemView);
            order_status_text = (TextView) itemView.findViewById(R.id.order_status_text);
            order_time_text = (TextView) itemView.findViewById(R.id.order_time_text);
            order_item_list_view = (ListView) itemView.findViewById(R.id.order_item_list_view);
            order_price_text = (TextView) itemView.findViewById(R.id.order_price_text);
            btn_comment = (Button) itemView.findViewById(R.id.btn_comment);
        }
    }
}
