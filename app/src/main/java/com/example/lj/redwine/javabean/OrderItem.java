package com.example.lj.redwine.javabean;

/**
 * Created by Administrator on 2016/11/18 0018.
 */
public class OrderItem {
    private Integer order_item_id;//子订单id

    private Integer quantity;//单品数量

    private Redwine redwine;//一支红酒对应多个子订单

    private Orders orders;//一个订单对应过个子订单

    private Integer redwine_id;//红酒id(外键)

    private Integer order_id;//订单id(外键)

    public Integer getOrder_item_id() {
        return order_item_id;
    }

    public void setOrder_item_id(Integer order_item_id) {
        this.order_item_id = order_item_id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Redwine getRedwine() {
        return redwine;
    }

    public void setRedwine(Redwine redwine) {
        this.redwine = redwine;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public Integer getRedwine_id() {
        return redwine_id;
    }

    public void setRedwine_id(Integer redwine_id) {
        this.redwine_id = redwine_id;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }
}
