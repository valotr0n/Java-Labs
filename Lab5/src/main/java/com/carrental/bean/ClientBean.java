package com.carrental.bean;

import com.carrental.model.Car;
import com.carrental.model.Rental;
import com.carrental.model.RegUser;
import com.carrental.service.DataService;

import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class ClientBean implements Serializable {

    @Inject
    private DataService dataService;

    @Inject
    private AuthBean authBean;

    // Выбранная машина для аренды
    private Car selectedCar;
    private int rentDays = 1;
    private String errorMessage;

    // Список аренд текущего клиента
    public List<Rental> getMyRentals() {
        RegUser currentUser = authBean.getCurrentUser();
        if (currentUser == null) return null;
        return dataService.getRentalsByClient(currentUser);
    }

    // Список свободных машин
    public List<Car> getAvailableCars() {
        return dataService.getAvailableCars();
    }

    // Кнопка "Выбрать машину"
    public String selectCar(Car car) {
        selectedCar = car;
        rentDays = 1;
        return "/pages/rent?faces-redirect=true";
    }

    // Кнопка "Арендовать"
    public String rentCar() {
        if (rentDays <= 0) {
            errorMessage = "Количество дней должно быть больше 0";
            return null;
        }

        RegUser currentUser = authBean.getCurrentUser();
        Rental rental = new Rental(null, selectedCar, currentUser, rentDays);
        dataService.addRental(rental);
        selectedCar = null;
        rentDays = 1;
        return "/pages/client?faces-redirect=true";
    }

    // Кнопка "Вернуть машину"
    public String returnCar(Long rentalId) {
        dataService.deleteRental(rentalId);
        return null;
    }

    // Подсчёт общей суммы аренды
    public double getTotalPrice() {
        if (selectedCar == null) return 0;
        return selectedCar.getPricePerDay() * rentDays;
    }

    public Car getSelectedCar() { return selectedCar; }
    public void setSelectedCar(Car car) { this.selectedCar = car; }

    public int getRentDays() { return rentDays; }
    public void setRentDays(int rentDays) { this.rentDays = rentDays; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}