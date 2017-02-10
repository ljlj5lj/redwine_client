package com.example.lj.redwine.javabean;

import java.util.List;

/**
 * Created by Administrator on 2016/11/18 0018.
 */
public class OrderStatus {
    private Integer order_status_id;//订单状态

    private String status_name;//状态名

    private List<Orders> ordersList;//一个订单状态对应多个订单

    public Integer getOrder_status_id() {
        return order_status_id;
    }

    public void setOrder_status_id(Integer order_status_id) {
        this.order_status_id = order_status_id;
    }

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    public List<Orders> getOrdersList() {
        return ordersList;
    }

    public void setOrdersList(List<Orders> ordersList) {
        this.ordersList = ordersList;
    }
}
