package com.tanphuong.milktea.drink.data.model;

public enum SugarGauge {
    LESS, NORMAL, MUCH;

    public SugarGauge nextGauge() {
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
