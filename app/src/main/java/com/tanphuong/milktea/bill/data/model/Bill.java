package com.tanphuong.milktea.bill.data.model;

import com.tanphuong.milktea.authorization.data.model.User;
import com.tanphuong.milktea.drink.data.model.MilkTea;
import com.tanphuong.milktea.drink.data.model.MilkTeaOrder;
import com.tanphuong.milktea.shipment.data.model.Shipper;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Bill implements Serializable {
    public static final int SHIP_COST = 18000;

    private String id;
    private List<MilkTeaOrder> orders;
    private User user;
    private Shipper shipper;
    private PaymentMethod paymentMethod;
    private BillStatus status;
    private Date date;

    private int totalPrice = 0;

    public Bill() {
    }

    public Bill(String id, List<MilkTeaOrder> orders, User user, Shipper shipper, PaymentMethod paymentMethod, BillStatus status, Date date) {
        this.id = id;
        this.orders = orders;
        this.user = user;
        this.shipper = shipper;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.date = date;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<MilkTeaOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<MilkTeaOrder> orders) {
        this.orders = orders;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Shipper getShipper() {
        return shipper;
    }

    public void setShipper(Shipper shipper) {
        this.shipper = shipper;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public String getSummaryMilkTeas() {
        StringBuilder sb = new StringBuilder();
        for (MilkTeaOrder order : orders) {
            sb.append(order.getMilkTea().getName()).append(", ");
        }
        return sb.toString();
    }

    public void calculateTotalPrice() {
        int total = 0;
        for (MilkTeaOrder order : orders) {
            total += order.getTotalCost();
        }
        totalPrice = total + SHIP_COST;
    }
}
