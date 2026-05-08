package com.carrental.model;

import java.io.Serializable;

public class Rental implements Serializable {

    private Long id;
    private Car car;
    private RegUser client;
    private int days;       
    private double totalPrice; 

    public Rental() {}

    public Rental(Long id, Car car, RegUser client, int days) {
        this.id = id;
        this.car = car;
        this.client = client;
        this.days = days;
        this.totalPrice = car.getPricePerDay() * days; 
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }

    public RegUser getClient() { return client; }
    public void setClient(RegUser client) { this.client = client; }

    public int getDays() { return days; }
    public void setDays(int days) { 
        this.days = days;
        if (this.car != null) {
            this.totalPrice = this.car.getPricePerDay() * days;
        }
    }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}