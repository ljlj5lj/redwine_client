package com.example.lj.redwine.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.example.lj.redwine.util.ToastUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/11/26 0026.
 */
public class RedwineAdapter extends RecyclerView.Adapter<RedwineAdapter.ViewHolder> {
    int redwine_id;
    Intent intent;
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

    public RedwineAdapter(Context context, List<Redwine> redwineList) {
        this.context = context;
        this.redwineList = redwineList;
        requestQueue = Volley.newRequestQueue(context);
        lruImageCache = LruImageCache.instance();
        imageLoader = new ImageLoader(requestQueue, lruImageCache);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.red_wine_list_item, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.redwine_name.setText(redwineList.get(position).getRedwine_name());
        holder.redwine_vintage.setText(redwineList.get(position).getVintage().toString());
        holder.redwine_price.setText(redwineList.get(position).getPrice().toString());
        holder. redwine_sales.setText(redwineList.get(position).getSales().toString());
        holder.redwine_description.setText(redwineList.get(position).getDescription());
        String imgUrl = constantClass.getHttp_prefix()+"/redwine_img/"+redwineList.get(position).getPicture();
        if (!redwineList.get(position).getPicture().toString().equals("")){
            holder.redwine_img.setImageUrl(imgUrl, imageLoader);
            holder.redwine_img.setDefaultImageResId(R.drawable.loading);
            holder.redwine_img.setErrorImageResId(R.drawable.error_p);
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
        return (redwineList == null || redwineList.size() == 0) ? 0 : redwineList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView redwine_img;
        TextView redwine_name;
        TextView redwine_vintage;
        TextView redwine_price;
        TextView redwine_sales;
        TextView redwine_description;
        public ViewHolder(View itemView) {
            super(itemView);
            redwine_img = (NetworkImageView) itemView.findViewById(R.id.redwine_img);
            redwine_name = (TextView) itemView.findViewById(R.id.redwine_name);
            redwine_vintage = (TextView) itemView.findViewById(R.id.redwine_vintage);
            redwine_price = (TextView) itemView.findViewById(R.id.redwine_price);
            redwine_sales = (TextView) itemView.findViewById(R.id.redwine_sales);
            redwine_description = (TextView) itemView.findViewById(R.id.redwine_description);
        }
    }
}
