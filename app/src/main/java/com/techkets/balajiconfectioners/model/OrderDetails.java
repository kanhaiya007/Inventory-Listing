package com.techkets.balajiconfectioners.model;

public class OrderDetails {
    private ItemDetails itemDetails;
    private int orderQuantity;

    public OrderDetails(ItemDetails itemDetails, int orderQuantity) {
        this.itemDetails = itemDetails;
        this.orderQuantity = orderQuantity;
    }

    public OrderDetails() {

    }

    public ItemDetails getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(ItemDetails itemDetails) {
        this.itemDetails = itemDetails;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }
}
