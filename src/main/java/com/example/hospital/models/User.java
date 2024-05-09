package com.example.hospital.models;

import java.sql.Date;

public  class User {
    private  int id;

    public User(int id, String name, String email, String phone, String address, Date dob, String bloodGroup, String type) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dob = dob;
        this.bloodGroup = bloodGroup;
        this.type = type;
    }

    public User(String name, String email,String password, String phone, String address, Date dob, String bloodGroup, String type) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.dob = dob;
        this.bloodGroup = bloodGroup;
        this.type = type;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private  String password;

    private  String type;
    private  String name;
    private  String email;
    private Date dob;
    private  String phone;
    private String bloodGroup;
    private String address;

    public User(int id, String password, String type, String name, String email, Date dob, String phone, String bloodGroup, String address) {
        this.id = id;
        this.password = password;
        this.type = type;
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
        this.address = address;
    }

    public User(int id, String name, String email, String phone, String address, Date dob, String bloodGroup) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dob = dob;
        this.bloodGroup = bloodGroup;
    }
    // Define user properties and constructor here
}

