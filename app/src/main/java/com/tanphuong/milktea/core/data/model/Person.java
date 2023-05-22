package com.tanphuong.milktea.core.data.model;

public class Person {
    private String id;
    private String phoneNumber;
    private String email;
    private String avatar;

    public Person() {
    }

    public Person(String id, String phoneNumber, String email, String avatar) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
