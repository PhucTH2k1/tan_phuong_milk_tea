package com.tanphuong.milktea.drink.data.model;

import java.io.Serializable;

public class Ingredient implements Serializable {
    private String id;
    private String name;
    private String unit;
    private int pricePerUnit;

    public Ingredient() {
    }

    public Ingredient(String id, String name, String unit, int pricePerUnit) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.pricePerUnit = pricePerUnit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(int pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
}
