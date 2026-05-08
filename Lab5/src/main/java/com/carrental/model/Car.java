package com.carrental.model;

import java.io.Serializable;

public class Car implements Serializable {

    private Long id;
    private String name;
    private String color;
    private String serialNumber;
    private String condition; 
    private double pricePerDay;
    private boolean available; 

    public Car() {}

    public Car(Long id, String name, String color, String serialNumber,
               String condition, double pricePerDay) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.serialNumber = serialNumber;
        this.condition = condition;
        this.pricePerDay = pricePerDay;
        this.available = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}