package com.example.lj.redwine.javabean;

/**
 * Created by Administrator on 2016/11/18 0018.
 */
public class Favorites {
    private Integer favorites_id;//我的收藏id

    private User user;//一个用户对应多个收藏

    private Redwine redwine;//一支红酒对应过个收藏

    private Integer user_id;//用户id(外键)

    private Integer redwine_id;//红酒id(外键)

    public Integer getFavorites_id() {
        return favorites_id;
    }

    public void setFavorites_id(Integer favorites_id) {
        this.favorites_id = favorites_id;
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
