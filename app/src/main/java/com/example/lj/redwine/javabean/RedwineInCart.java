package com.example.lj.redwine.javabean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/29 0029.
 */
public class RedwineInCart implements Serializable {
    private String red_wine_name;
    private int red_wine_num;
    private int red_wine_id;

    public String getRed_wine_name() {
        return red_wine_name;
    }

    public void setRed_wine_name(String red_wine_name) {
        this.red_wine_name = red_wine_name;
    }

    public int getRed_wine_num() {
        return red_wine_num;
    }

    public void setRed_wine_num(int red_wine_num) {
        this.red_wine_num = red_wine_num;
    }

    public int getRed_wine_id() {
        return red_wine_id;
    }

    public void setRed_wine_id(int red_wine_id) {
        this.red_wine_id = red_wine_id;
    }

    public RedwineInCart(String red_wine_name, int red_wine_num, int red_wine_id) {
        this.red_wine_name = red_wine_name;
        this.red_wine_num = red_wine_num;
        this.red_wine_id = red_wine_id;
    }
}
