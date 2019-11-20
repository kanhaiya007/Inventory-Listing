package com.techkets.balajiconfectioners.model;

public class Category {
    private String categorName;

    public Category() {

    }

    public Category(String categorName) {
        this.categorName = categorName;
    }

    public String getCategorName() {
        return categorName;
    }

    public void setCategorName(String categorName) {
        this.categorName = categorName;
    }
}
