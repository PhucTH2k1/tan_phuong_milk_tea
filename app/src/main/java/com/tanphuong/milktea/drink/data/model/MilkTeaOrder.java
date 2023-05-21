package com.tanphuong.milktea.drink.data.model;

import java.util.List;

public class MilkTeaOrder {
    private String id;
    private MilkTea milkTea;
    private List<RealIngredient> toppings;
    private String note;
    private IceGauge iceGauge;
    private SugarGauge sugarGauge;
    private Size size;
    private int quantity;

    private int totalCost;

    public MilkTeaOrder() {
    }

    public MilkTeaOrder(String id, MilkTea milkTea, List<RealIngredient> toppings, String note, IceGauge iceGauge, SugarGauge sugarGauge, Size size, int quantity) {
        this.id = id;
        this.milkTea = milkTea;
        this.toppings = toppings;
        this.note = note;
        this.iceGauge = iceGauge;
        this.sugarGauge = sugarGauge;
        this.size = size;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MilkTea getMilkTea() {
        return milkTea;
    }

    public void setMilkTea(MilkTea milkTea) {
        this.milkTea = milkTea;
    }

    public List<RealIngredient> getToppings() {
        return toppings;
    }

    public void setToppings(List<RealIngredient> toppings) {
        this.toppings = toppings;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public IceGauge getIceGauge() {
        return iceGauge;
    }

    public void setIceGauge(IceGauge iceGauge) {
        this.iceGauge = iceGauge;
    }

    public SugarGauge getSugarGauge() {
        return sugarGauge;
    }

    public void setSugarGauge(SugarGauge sugarGauge) {
        this.sugarGauge = sugarGauge;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void calculateCost() {
        int cost = 0;
        for (RealIngredient topping : toppings) {
            cost += topping.calculateCost();
        }
        totalCost = cost + milkTea.getTotalCost();
    }
}
