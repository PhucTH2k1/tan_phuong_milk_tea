package com.tanphuong.milktea.drink.data.model;

import java.io.Serializable;
import java.util.List;

public class MilkTea implements Serializable {
    private String id;
    private String name;
    private String describe;
    private String recipe;
    private int laborCost;
    private String coverImage;
    private List<RealIngredient> ingredients;

    private int totalCost = 0;

    public MilkTea() {
    }

    public MilkTea(String id, String name, String describe, String recipe, int laborCost, String coverImage, List<RealIngredient> ingredients) {
        this.id = id;
        this.name = name;
        this.describe = describe;
        this.recipe = recipe;
        this.laborCost = laborCost;
        this.coverImage = coverImage;
        this.ingredients = ingredients;
        calculateCost();
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

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public int getLaborCost() {
        return laborCost;
    }

    public void setLaborCost(int laborCost) {
        this.laborCost = laborCost;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public List<RealIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RealIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public int getTotalCost() {
        return totalCost;
    }

    private void calculateCost() {
        // Tính tổng tiền trà sữa
        int ingredientCosts = 0;
        for (RealIngredient ingredient : ingredients) {
            ingredientCosts += ingredient.calculateCost();
        }
        totalCost = laborCost + ingredientCosts;
    }
}
