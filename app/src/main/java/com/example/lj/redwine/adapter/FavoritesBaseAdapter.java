package com.example.lj.redwine.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.cache.LruImageCache;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.javabean.Favorites;
import com.example.lj.redwine.util.ToastUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/11/22 0022.
 */
public class FavoritesBaseAdapter extends BaseAdapter {
    ConstantClass constantClass;
    SharedPreferences sharedPreferences;
    public List<Favorites> getFavoritesList() {
        return favoritesList;
    }

    public void setFavoritesList(List<Favorites> favoritesList) {
        this.favoritesList = favoritesList;
    }

    private List<Favorites> favoritesList;
    private Context context;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private LruImageCache lruImageCache;

    public FavoritesBaseAdapter(Context context, List<Favorites> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
        requestQueue = Volley.newRequestQueue(context);
        lruImageCache = LruImageCache.instance();
        imageLoader = new ImageLoader(requestQueue, lruImageCache);
    }
    @Override
    public int getCount() {
        return (favoritesList == null) ? 0 : favoritesList.size();
    }

    @Override
    public Object getItem(int position) {
        return favoritesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.my_collection_list_item, null);
            viewHolder = new ViewHolder();
            initView(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.redwine_name.setText(favoritesList.get(position).getRedwine().getRedwine_name());
        viewHolder.redwine_price.setText(favoritesList.get(position).getRedwine().getPrice().toString());
        viewHolder.redwine_sales.setText(favoritesList.get(position).getRedwine().getSales().toString());
        String imgUrl = constantClass.getHttp_prefix()+"/redwine_img/"+favoritesList.get(position).getRedwine().getPicture();
        if (!favoritesList.get(position).getRedwine().getPicture().toString().equals("")){
            viewHolder.redwine_img.setImageUrl(imgUrl, imageLoader);
            viewHolder.redwine_img.setDefaultImageResId(R.drawable.loading);
            viewHolder.redwine_img.setErrorImageResId(R.drawable.error_p);
        }
        viewHolder.add_shopping_cart.setOnClickListener(new View.OnClickListener() {//添加红酒至购物车
            @Override
            public void onClick(final View v) {
                sharedPreferences = v.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                int user_id = sharedPreferences.getInt("id", 0 );
                requestQueue = Volley.newRequestQueue(v.getContext());
                String url = constantClass.getHttp_prefix()+"/shoppingCart/addShoppingCart?user_id="+user_id+"&redwine_id="+favoritesList.get(position).getRedwine().getRedwine_id();
                StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (s.equals("success")){
                            ToastUtil.show(v.getContext(), "已添加至购物车");
                        } else if (s.equals("fail")){
                            ToastUtil.show(v.getContext(), "加入购物车失败");
                        }else if (s.equals("exist")) {
                            ToastUtil.show(v.getContext(), "该红酒已在购物车");
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
        return convertView;
    }

    private void initView(ViewHolder viewHolder, View convertView) {
        viewHolder.redwine_img = (NetworkImageView) convertView.findViewById(R.id.redwine_img);
        viewHolder.redwine_name = (TextView) convertView.findViewById(R.id.redwine_name);
        viewHolder.redwine_price = (TextView) convertView.findViewById(R.id.redwine_price);
        viewHolder.redwine_sales = (TextView) convertView.findViewById(R.id.redwine_sales);
        viewHolder.add_shopping_cart = (ImageView) convertView.findViewById(R.id.add_shopping_cart);
    }

    static class ViewHolder {
        NetworkImageView redwine_img;
        TextView redwine_name;
        TextView redwine_price;
        TextView redwine_sales;
        ImageView add_shopping_cart;
    }
}
