package com.example.lj.redwine.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.activity.RedWineInfoActivity;
import com.example.lj.redwine.cache.LruImageCache;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.javabean.Redwine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/11/24 0024.
 */
public class NewProductAdapter extends RecyclerView.Adapter<NewProductAdapter.ViewHolder>{
    Intent intent;
    int redwine_id;
    ConstantClass constantClass;
    private List<Redwine> redwineList;//红酒列表
    private Context context;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private LruImageCache lruImageCache;

    // 添加多个项(自定义方法)

    public void addItem(List<Redwine> list) {
        if (null != list && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                redwineList.add(list.get(i));
            }
        }
    }

    public NewProductAdapter(Context context, List<Redwine> redwineList){
        this.context = context;
        this.redwineList = redwineList;
        requestQueue = Volley.newRequestQueue(context);
        lruImageCache = LruImageCache.instance();
        imageLoader = new ImageLoader(requestQueue, lruImageCache);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.new_product_list_item, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.red_wine_name.setText(redwineList.get(position).getRedwine_name());
        holder.red_wine_vintage.setText(redwineList.get(position).getVintage().toString());
        holder.red_wine_price.setText(redwineList.get(position).getPrice().toString());
        holder. red_wine_sales.setText(redwineList.get(position).getSales().toString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        holder.red_wine_register_time.setText(simpleDateFormat.format(redwineList.get(position).getRegister_date()));
        String imgUrl = constantClass.getHttp_prefix()+"/redwine_img/"+redwineList.get(position).getPicture();
        if (!redwineList.get(position).getPicture().toString().equals("")){
            holder.red_wine_img.setImageUrl(imgUrl, imageLoader);
            holder.red_wine_img.setDefaultImageResId(R.drawable.loading);
            holder.red_wine_img.setErrorImageResId(R.drawable.error_p);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redwine_id = redwineList.get(position).getRedwine_id();
                intent = new Intent(v.getContext(), RedWineInfoActivity.class);
                intent.putExtra("redwine_id", redwine_id);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (redwineList == null || redwineList.size() == 0) ? 0 :redwineList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView red_wine_img;
        TextView red_wine_name;
        TextView red_wine_vintage;
        TextView red_wine_price;
        TextView red_wine_sales;
        TextView red_wine_register_time;

        public ViewHolder(View itemView) {
            super(itemView);
            red_wine_img = (NetworkImageView) itemView.findViewById(R.id.red_wine_img);
            red_wine_name = (TextView) itemView.findViewById(R.id.red_wine_name);
            red_wine_vintage = (TextView) itemView.findViewById(R.id.red_wine_vintage);
            red_wine_price = (TextView) itemView.findViewById(R.id.red_wine_price);
            red_wine_sales = (TextView) itemView.findViewById(R.id.red_wine_sales);
            red_wine_register_time = (TextView) itemView.findViewById(R.id.red_wine_register_time);
        }
    }
}
