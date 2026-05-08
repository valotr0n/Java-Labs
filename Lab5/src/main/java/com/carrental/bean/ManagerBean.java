package com.carrental.bean;

import com.carrental.model.Car;
import com.carrental.model.Rental;
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
public class ManagerBean implements Serializable {

    @Inject
    private DataService dataService;

    // Для добавления/редактирования машины
    private Car selectedCar = new Car();
    private boolean editMode = false;
    private String errorMessage;

    // Список всех машин
    public List<Car> getAllCars() {
        return dataService.getAllCars();
    }

    // Список всех аренд
    public List<Rental> getAllRentals() {
        return dataService.getAllRentals();
    }

    // Кнопка "Добавить машину"
    public String addCar() {
        if (selectedCar.getName() == null || selectedCar.getName().isEmpty()) {
            errorMessage = "Введите название автомобиля";
            return null;
        }
        dataService.addCar(selectedCar);
        selectedCar = new Car();
        errorMessage = null;
        return null;
    }

    // Кнопка "Редактировать"
    public String editCar(Car car) {
        selectedCar = car;
        editMode = true;
        return null;
    }

    // Кнопка "Сохранить изменения"
    public String saveCar() {
        dataService.updateCar(selectedCar);
        selectedCar = new Car();
        editMode = false;
        return null;
    }

    // Кнопка "Удалить"
    public String deleteCar(Long id) {
        dataService.deleteCar(id);
        return null;
    }

    // Кнопка "Отмена"
    public String cancel() {
        selectedCar = new Car();
        editMode = false;
        errorMessage = null;
        return null;
    }

    public Car getSelectedCar() { return selectedCar; }
    public void setSelectedCar(Car car) { this.selectedCar = car; }

    public boolean isEditMode() { return editMode; }
    public void setEditMode(boolean editMode) { this.editMode = editMode; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}