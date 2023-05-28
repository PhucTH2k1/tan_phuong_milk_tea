package com.tanphuong.milktea.bill.data.model;

public class Card {
    private String cardHolder;
    private String expireDate;
    private String cardNumber;
    private int cvv;

    public Card() {
    }

    public Card(String cardHolder, String expireDate, String cardNumber, int cvv) {
        this.cardHolder = cardHolder;
        this.expireDate = expireDate;
        this.cardNumber = cardNumber;
        this.cvv = cvv;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }
}
