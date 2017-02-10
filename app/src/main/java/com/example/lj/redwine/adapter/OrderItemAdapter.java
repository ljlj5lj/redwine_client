package com.example.lj.redwine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lj.redwine.R;
import com.example.lj.redwine.javabean.OrderItem;

import java.util.List;

/**
 * Created by Administrator on 2016/12/2 0002.
 */
public class OrderItemAdapter extends BaseAdapter{
    private List<OrderItem> orderItemList;
    private Context context;

    public OrderItemAdapter(Context context,List<OrderItem> orderItemList){
        this.context = context;
        this.orderItemList = orderItemList;
    }

    @Override
    public int getCount() {
        return (orderItemList == null) ? 0 : orderItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.order_item_list_item, null);
            viewHolder = new ViewHolder();
            initView(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.redwine_name.setText(orderItemList.get(position).getRedwine().getRedwine_name());
        viewHolder.redwine_num.setText(orderItemList.get(position).getQuantity().toString());
        return convertView;
    }

    private void initView(ViewHolder viewHolder, View convertView) {
        viewHolder.redwine_name = (TextView) convertView.findViewById(R.id.redwine_name);
        viewHolder.redwine_num = (TextView) convertView.findViewById(R.id.redwine_num);
    }

    static class ViewHolder {
        TextView redwine_name;
        TextView redwine_num;
    }
}
