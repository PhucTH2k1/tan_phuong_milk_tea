package com.tanphuong.milktea.drink.data.model;

public enum IceGauge {
    LESS, NORMAL, MUCH;

    public IceGauge nextGauge() {
        switch (this) {
            case LESS:
                return NORMAL;
            case NORMAL:
                return MUCH;
            default:
                return LESS;
        }
    }

    public String title() {
        switch (this) {
            case LESS:
                return "Ít";
            case NORMAL:
                return "Trung bình";
            default:
                return "Nhiều";
        }
    }
}
