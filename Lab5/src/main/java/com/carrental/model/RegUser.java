package com.carrental.model;

public class RegUser extends User {

    public RegUser() {}

    public RegUser(String name, String email, String phone, 
                   String password, String role) {
        super(name, email, phone, password, role);
    }
}