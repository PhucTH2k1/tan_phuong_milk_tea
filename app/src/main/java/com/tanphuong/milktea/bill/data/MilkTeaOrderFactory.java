package com.tanphuong.milktea.bill.data;

import com.tanphuong.milktea.drink.data.model.MilkTeaOrder;

import java.util.ArrayList;
import java.util.List;

public final class MilkTeaOrderFactory {
    private static final List<MilkTeaOrder> orders = new ArrayList<>();

    public static void addOrder(MilkTeaOrder order) {
        order.setId((orders.size() + 1) + "");
        order.calculateCost();
        orders.add(order);
    }

    public static boolean removeOrder(MilkTeaOrder order) {
        return orders.remove(order);
    }

    public static List<MilkTeaOrder> showCart() {
        return orders;
    }
}
