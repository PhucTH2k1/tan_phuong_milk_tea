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

    public static IceGauge fromString(String text) {
        for (IceGauge gauge : IceGauge.values()) {
            if (gauge.name().equalsIgnoreCase(text)) {
                return gauge;
            }
        }
        return NORMAL;
    }
}
