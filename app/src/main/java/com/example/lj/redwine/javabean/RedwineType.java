package com.example.lj.redwine.javabean;

import java.util.List;

/**
 * Created by Administrator on 2016/11/18 0018.
 */
public class RedwineType {
    private Integer redwine_type_id;//红酒类型id

    private String type_name;//类型名

    private List<Redwine> redwineList;//一个类型对应多支红酒

    public Integer getRedwine_type_id() {
        return redwine_type_id;
    }

    public void setRedwine_type_id(Integer redwine_type_id) {
        this.redwine_type_id = redwine_type_id;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public List<Redwine> getRedwineList() {
        return redwineList;
    }

    public void setRedwineList(List<Redwine> redwineList) {
        this.redwineList = redwineList;
    }
}
