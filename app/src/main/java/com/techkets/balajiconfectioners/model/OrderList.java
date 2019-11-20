package com.techkets.balajiconfectioners.model;

import java.util.ArrayList;
import java.util.List;

public class OrderList {
    private List<OrderDetails> orderDetailsList = new ArrayList<>();
    private String username;
    private String userMob;
    private String userAddress;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public OrderList(List<OrderDetails> orderDetailsList, String username, String userMob, String userAddress, String userId) {
        this.orderDetailsList = orderDetailsList;
        this.username = username;
        this.userMob = userMob;
        this.userAddress = userAddress;
        this.userId = userId;
    }

    public OrderList() {
    }

    public List<OrderDetails> getOrderDetailsList() {
        return orderDetailsList;
    }

    public void setOrderDetailsList(List<OrderDetails> orderDetailsList) {
        this.orderDetailsList = orderDetailsList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserMob() {
        return userMob;
    }

    public void setUserMob(String userMob) {
        this.userMob = userMob;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }
}
