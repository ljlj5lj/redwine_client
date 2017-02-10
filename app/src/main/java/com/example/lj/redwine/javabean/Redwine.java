package com.example.lj.redwine.javabean;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/11/18 0018.
 */
public class Redwine {
    private Integer redwine_id;//红酒id

    private String redwine_name;//红酒名

    private Float price;//红酒价格

    private String description;//红酒描述

    private String introduction;//红酒介绍

    private String origin;//红酒来源

    private Integer vintage;//红酒酿造年份

    private Float alcohol;//红酒酒精度数

    private Integer capacity;//红酒容量

    private String picture;//红酒图片

    private Integer sales;//红酒销量

    private Date register_date;//上架日期

    private List<Favorites> favoritesList;//一支红酒对应多个收藏

    private List<Evaluation> evaluationList;//一支红酒对应多个评价

    private List<OrderItem> orderItemList;//一支红酒对应多个子订单

    private Integer redwine_type_id;//红酒类型id(外键)

    private RedwineType redwine_type;//一个类型对应多支红酒

    public Integer getRedwine_id() {
        return redwine_id;
    }

    public void setRedwine_id(Integer redwine_id) {
        this.redwine_id = redwine_id;
    }

    public String getRedwine_name() {
        return redwine_name;
    }

    public void setRedwine_name(String redwine_name) {
        this.redwine_name = redwine_name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Integer getVintage() {
        return vintage;
    }

    public void setVintage(Integer vintage) {
        this.vintage = vintage;
    }

    public Float getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(Float alcohol) {
        this.alcohol = alcohol;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public Date getRegister_date() {
        return register_date;
    }

    public void setRegister_date(Date register_date) {
        this.register_date = register_date;
    }

    public List<Favorites> getFavoritesList() {
        return favoritesList;
    }

    public void setFavoritesList(List<Favorites> favoritesList) {
        this.favoritesList = favoritesList;
    }

    public List<Evaluation> getEvaluationList() {
        return evaluationList;
    }

    public void setEvaluationList(List<Evaluation> evaluationList) {
        this.evaluationList = evaluationList;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public Integer getRedwine_type_id() {
        return redwine_type_id;
    }

    public void setRedwine_type_id(Integer redwine_type_id) {
        this.redwine_type_id = redwine_type_id;
    }

    public RedwineType getRedwine_type() {
        return redwine_type;
    }

    public void setRedwine_type(RedwineType redwine_type) {
        this.redwine_type = redwine_type;
    }
}
