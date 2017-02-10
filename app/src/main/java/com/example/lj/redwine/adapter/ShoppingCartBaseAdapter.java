package com.example.lj.redwine.adapter;

import android.content.Context;
import android.media.Image;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.cache.LruImageCache;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.javabean.ShoppingCart;
import com.example.lj.redwine.util.ToastUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/11/28 0028.
 */
public class ShoppingCartBaseAdapter extends BaseAdapter {
    public boolean flage = false;//操作
    public boolean allCheck = false;//全选
    ConstantClass constantClass;
    private Context context;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private LruImageCache lruImageCache;
    private List<ShoppingCart> shoppingCartList;
    private Handler handler;
    public ShoppingCartBaseAdapter(Context context,Handler handler, List<ShoppingCart> shoppingCartList) {
        this.context = context;
        this.handler = handler;
        this.shoppingCartList = shoppingCartList;
        requestQueue = Volley.newRequestQueue(context);
        lruImageCache = LruImageCache.instance();
        imageLoader = new ImageLoader(requestQueue, lruImageCache);
    }

    @Override
    public int getCount() {
        return (shoppingCartList == null) ? 0 : shoppingCartList.size();
    }

    @Override
    public Object getItem(int position) {
        return shoppingCartList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.shopping_cart_list_item, null);
            viewHolder = new ViewHolder();
            initView(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        init(viewHolder, position);
        viewHolder.redwine_name.setText(shoppingCartList.get(position).getRedwine().getRedwine_name());
        viewHolder.redwine_price.setText(shoppingCartList.get(position).getRedwine().getPrice().toString());
        viewHolder.redwine_vintage.setText(shoppingCartList.get(position).getRedwine().getVintage().toString());
        String imgUrl = constantClass.getHttp_prefix()+"/redwine_img/"+shoppingCartList.get(position).getRedwine().getPicture();
        if (!shoppingCartList.get(position).getRedwine().getPicture().toString().equals("")){
            viewHolder.redwine_img.setImageUrl(imgUrl, imageLoader);
            viewHolder.redwine_img.setDefaultImageResId(R.drawable.loading);
            viewHolder.redwine_img.setErrorImageResId(R.drawable.error_p);
        }
        if (flage) {
            viewHolder.shopping_cart_checkbox.setVisibility(View.VISIBLE);
        } else {
            viewHolder.shopping_cart_checkbox.setVisibility(View.GONE);
        }
        if (allCheck){
            viewHolder.shopping_cart_checkbox.setChecked(true);
            for (int i = 0 ; i < shoppingCartList.size() ; i++) {
                shoppingCartList.get(position).setChecked(true);
            }
        } else {
            viewHolder.shopping_cart_checkbox.setChecked(false);
            for (int i = 0 ; i < shoppingCartList.size() ; i++) {
                shoppingCartList.get(position).setChecked(false);
            }
        }
        return convertView;
    }

    private void init(final ViewHolder viewHolder, final int position) {
        viewHolder.shopping_cart_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float all_price = 0;
                if (shoppingCartList.get(position).getChecked()){
                    shoppingCartList.get(position).setChecked(false);
                    for (ShoppingCart shoppingCart : shoppingCartList) {
                        if (shoppingCart.getChecked()) {
                            all_price = all_price +shoppingCart.getNum()*shoppingCart.getRedwine().getPrice();
                            handler.sendMessage(handler.obtainMessage(10,all_price));
                        } else {
                            handler.sendMessage(handler.obtainMessage(10, all_price));
                        }
                    }
                } else {
                    shoppingCartList.get(position).setChecked(true);
                    for (ShoppingCart shoppingCart : shoppingCartList) {
                        if (shoppingCart.getChecked()) {
                            all_price = all_price +shoppingCart.getNum()*shoppingCart.getRedwine().getPrice();
                            handler.sendMessage(handler.obtainMessage(10,all_price));
                        }else {
                            handler.sendMessage(handler.obtainMessage(10, all_price));
                        }
                    }
                }
            }
        });

        viewHolder.shopping_cart_jian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float all_price = 0;
                int num = shoppingCartList.get(position).getNum();
                    if (--num < 1) {
                        num++;
                    } else {
                        shoppingCartList.get(position).setNum(num);
                        viewHolder.shopping_cart_number.setText(String.valueOf(num));
                        for (ShoppingCart shoppingCart : shoppingCartList) {
                            if (shoppingCart.getChecked()) {
                                all_price = all_price + shoppingCart.getNum()*shoppingCart.getRedwine().getPrice();
                                handler.sendMessage(handler.obtainMessage(10,all_price));
                            }
                        }
                    }

            }
        });
        viewHolder.shopping_cart_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float all_price = 0;
                int num = shoppingCartList.get(position).getNum();
                    if (++num > 10) {
                        ToastUtil.show(v.getContext(),"不能超过10件");
                        num--;
                    }else {
                        shoppingCartList.get(position).setNum(num);
                        viewHolder.shopping_cart_number.setText(String.valueOf(num));
                        for (ShoppingCart shoppingCart : shoppingCartList) {
                            if (shoppingCart.getChecked()) {
                                all_price = all_price +shoppingCart.getNum()*shoppingCart.getRedwine().getPrice();
                                handler.sendMessage(handler.obtainMessage(10,all_price));
                            }
                        }
                    }
                }

        });
    }

    private void initView(ViewHolder viewHolder, View convertView) {
        viewHolder.redwine_img = (NetworkImageView) convertView.findViewById(R.id.redwine_img);
        viewHolder.redwine_name = (TextView) convertView.findViewById(R.id.redwine_name);
        viewHolder.redwine_price = (TextView) convertView.findViewById(R.id.redwine_price);
        viewHolder.redwine_vintage = (TextView) convertView.findViewById(R.id.redwine_vintage);
        viewHolder.shopping_cart_number = (TextView) convertView.findViewById(R.id.shopping_cart_number);
        viewHolder.shopping_cart_add = (ImageView) convertView.findViewById(R.id.shopping_cart_add);
        viewHolder.shopping_cart_jian = (ImageView) convertView.findViewById(R.id.shopping_cart_jian);
        viewHolder.shopping_cart_checkbox = (CheckBox) convertView.findViewById(R.id.shopping_cart_checkbox);
    }


    static class ViewHolder {
        NetworkImageView redwine_img;
        TextView redwine_name;
        TextView redwine_price;
        TextView redwine_vintage;
        TextView shopping_cart_number;
        ImageView shopping_cart_add;
        ImageView shopping_cart_jian;
        CheckBox shopping_cart_checkbox;
    }
}
