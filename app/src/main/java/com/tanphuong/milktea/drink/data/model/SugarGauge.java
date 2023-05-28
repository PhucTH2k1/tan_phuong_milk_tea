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

    public static SugarGauge fromString(String text) {
        for (SugarGauge gauge : SugarGauge.values()) {
            if (gauge.name().equalsIgnoreCase(text)) {
                return gauge;
            }
        }
        return NORMAL;
    }
}
