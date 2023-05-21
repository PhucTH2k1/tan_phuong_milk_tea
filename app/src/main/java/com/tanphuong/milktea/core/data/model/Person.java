package com.tanphuong.milktea.core.data.model;

public class Person {
    private String id;
    private String phoneNumber;
    private String avatar;

    public Person() {
    }

    public Person(String id, String phoneNumber, String avatar) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
