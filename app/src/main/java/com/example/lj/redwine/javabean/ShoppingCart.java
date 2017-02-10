package com.example.lj.redwine.javabean;

/**
 * Created by Administrator on 2016/11/28 0028.
 */
public class ShoppingCart {

    private Integer num = 1;//数量

    private Boolean checked = false;//是否被选中

    private Integer shopping_cart_id;//购物车id

    private User user;//一个用户对应多个收藏

    private Redwine redwine;//一支红酒对应过个收藏

    private Integer user_id;//用户id(外键)

    private Integer redwine_id;//红酒id(外键)

    public Integer getShopping_cart_id() {
        return shopping_cart_id;
    }

    public void setShopping_cart_id(Integer shopping_cart_id) {
        this.shopping_cart_id = shopping_cart_id;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Redwine getRedwine() {
        return redwine;
    }

    public void setRedwine(Redwine redwine) {
        this.redwine = redwine;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getRedwine_id() {
        return redwine_id;
    }

    public void setRedwine_id(Integer redwine_id) {
        this.redwine_id = redwine_id;
    }
}
