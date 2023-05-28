package com.tanphuong.milktea.drink.data.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class RealIngredient extends Ingredient implements Serializable {
    private float quantity;

    public RealIngredient() {
    }

    public RealIngredient(String id, String name, String unit, int pricePerUnit, float quantity) {
        super(id, name, unit, pricePerUnit);
        this.quantity = quantity;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public int calculateCost() {
        return (int) (quantity * getPricePerUnit());
    }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }
}
