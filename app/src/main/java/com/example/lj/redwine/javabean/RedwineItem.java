package com.example.lj.redwine.javabean;

/**
 * Created by Administrator on 2016/12/7 0007.
 */
public class RedwineItem {
    private int redwineId;
    private String redwineName;

    public int getRedwineId() {
        return redwineId;
    }

    public void setRedwineId(int redwineId) {
        this.redwineId = redwineId;
    }

    public String getRedwineName() {
        return redwineName;
    }

    public void setRedwineName(String redwineName) {
        this.redwineName = redwineName;
    }

    @Override
    public String toString() {
        return redwineName;
    }

    public RedwineItem(int redwineId, String redwineName) {
        this.redwineId = redwineId;
        this.redwineName = redwineName;
    }

    public RedwineItem() {
        this.redwineId = 0;
        this.redwineName = "";
    }
}
