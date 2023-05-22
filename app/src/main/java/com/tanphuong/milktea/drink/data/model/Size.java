package com.tanphuong.milktea.drink.data.model;

public enum Size {
    SMALL, MEDIUM, LARGE;

    public String title() {
        switch (this) {
            case SMALL:
                return "Nhỏ";
            case MEDIUM:
                return "Vừa";
            default:
                return "Lớn";
        }
    }
}
