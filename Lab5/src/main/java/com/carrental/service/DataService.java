package com.carrental.service;

import com.carrental.model.Car;
import com.carrental.model.RegUser;
import com.carrental.model.Rental;


import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ApplicationScoped
public class DataService implements Serializable {

    private List<RegUser> users = new ArrayList<>();

    private List<Car> cars = new ArrayList<>();

    private List<Rental> rentals = new ArrayList<>();

    private Long carIdCounter = 1L;
    private Long rentalIdCounter = 1L;

    public DataService() {
        users.add(new RegUser("Иванов", "ivanov@mail.ru", "8-999-111-1111", "123", "manager"));
        users.add(new RegUser("Петров", "petrov@mail.ru", "8-999-222-2222", "123", "client"));
        users.add(new RegUser("Сидоров", "sidorov@mail.ru", "8-999-333-3333", "123", "client"));

        cars.add(new Car(carIdCounter++, "Toyota Camry", "Белый", "SN-001", "новый", 3000));
        cars.add(new Car(carIdCounter++, "BMW X5", "Черный", "SN-002", "новый", 7000));
        cars.add(new Car(carIdCounter++, "Kia Rio", "Серый", "SN-003", "после ремонта", 1500));
        cars.add(new Car(carIdCounter++, "Hyundai Solaris", "Синий", "SN-004", "новый", 2000));
    }

    public RegUser login(String email, String password) {
        for (RegUser user : users) {
            if (user.getEmail().equals(email) && 
                user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public boolean emailExists(String email) {
        for (RegUser user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    public void registerUser(RegUser user) {
        user.setRole("client"); 
        users.add(user);
    }

    public List<Car> getAllCars() { return cars; }

    public List<Car> getAvailableCars() {
        List<Car> available = new ArrayList<>();
        for (Car car : cars) {
            if (car.isAvailable()) available.add(car);
        }
        return available;
    }

    public Car getCarById(Long id) {
        for (Car car : cars) {
            if (car.getId().equals(id)) return car;
        }
        return null;
    }

    public void addCar(Car car) {
        car.setId(carIdCounter++);
        cars.add(car);
    }

    public void updateCar(Car updated) {
        for (int i = 0; i < cars.size(); i++) {
            if (cars.get(i).getId().equals(updated.getId())) {
                cars.set(i, updated);
                return;
            }
        }
    }

    public void deleteCar(Long id) {
        cars.removeIf(car -> car.getId().equals(id));
    }

    public List<Rental> getAllRentals() { return rentals; }

    public List<Rental> getRentalsByClient(RegUser client) {
        List<Rental> result = new ArrayList<>();
        for (Rental rental : rentals) {
            if (rental.getClient().getEmail().equals(client.getEmail())) {
                result.add(rental);
            }
        }
        return result;
    }

    public void addRental(Rental rental) {
        rental.setId(rentalIdCounter++);
        rental.getCar().setAvailable(false); // машина занята
        rentals.add(rental);
    }

    public void deleteRental(Long id) {
        for (Rental rental : rentals) {
            if (rental.getId().equals(id)) {
                rental.getCar().setAvailable(true); // машина снова свободна
                rentals.remove(rental);
                return;
            }
        }
    }
}