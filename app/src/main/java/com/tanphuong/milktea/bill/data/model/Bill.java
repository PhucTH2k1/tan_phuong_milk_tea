package com.tanphuong.milktea.bill.data.model;

import com.tanphuong.milktea.authorization.data.model.User;
import com.tanphuong.milktea.drink.data.model.MilkTeaOrder;
import com.tanphuong.milktea.shipment.data.model.Shipper;

import java.util.Date;
import java.util.List;

public class Bill {
    private String id;
    private List<MilkTeaOrder> orders;
    private User user;
    private Shipper shipper;
    private PaymentMethod paymentMethod;
    private BillStatus status;
    private Date date;

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
}
