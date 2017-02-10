package com.example.lj.redwine.javabean;

import java.util.Date;

/**
 * Created by Administrator on 2016/11/18 0018.
 */
public class Evaluation {
    private Integer evaluation_id;//评价id

    private String content;//评价内容

    private Float grade;//评价等级

    private Date evaluation_date;//评论日期

    private User user;//一个用户对应多条评价

    private Redwine redwine;//一支红酒对用多条评价

    private Integer user_id;//用户id(外键)

    private Integer redwine_id;//红酒id(外键)

    public Integer getEvaluation_id() {
        return evaluation_id;
    }

    public void setEvaluation_id(Integer evaluation_id) {
        this.evaluation_id = evaluation_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Float getGrade() {
        return grade;
    }

    public void setGrade(Float grade) {
        this.grade = grade;
    }

    public Date getEvaluation_date() {
        return evaluation_date;
    }

    public void setEvaluation_date(Date evaluation_date) {
        this.evaluation_date = evaluation_date;
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
