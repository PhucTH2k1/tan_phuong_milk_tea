package com.tanphuong.milktea.bill.data.model;

import com.tanphuong.milktea.R;

public enum BillStatus {
    CANCELLED, ON_GOING, SHIPPER_FINDING, COMPLETED;

    public String title() {
        switch (this) {
            case CANCELLED:
                return "Đã huỷ";
            case ON_GOING:
                return "Đang giao";
            case SHIPPER_FINDING:
                return "Đang tìm shipper";
            default:
                return "Hoàn thành";
        }
    }

    public int colorRes() {
        switch (this) {
            case CANCELLED:
                return R.color.Red;
            case ON_GOING:
                return R.color.Blue;
            case SHIPPER_FINDING:
                return R.color.Gray;
            default:
                return R.color.Green;
        }
    }

    public static BillStatus fromString(String text) {
        for (BillStatus status : BillStatus.values()) {
            if (status.name().equalsIgnoreCase(text)) {
                return status;
            }
        }
        return COMPLETED;
    }
}
