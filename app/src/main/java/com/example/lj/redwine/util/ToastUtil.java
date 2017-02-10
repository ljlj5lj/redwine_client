package com.example.lj.redwine.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/10/2 0002.
 */
public class ToastUtil {
    private static Toast toast;
    public static void show(Context context, String msg){
        if(toast == null){
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }else{
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}
