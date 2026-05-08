package com.carrental.bean;

import com.carrental.model.RegUser;
import com.carrental.service.DataService;

import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@RequestScoped
public class RegBean implements Serializable {

    @Inject
    private DataService dataService;

    private RegUser newUser = new RegUser();
    private String confirmPassword;
    private String errorMessage;

    // Кнопка "Зарегистрироваться"
    public String register() {
        // Проверка что пароли совпадают
        if (!newUser.getPassword().equals(confirmPassword)) {
            errorMessage = "Пароли не совпадают";
            return null;
        }

        // Проверка что email не занят
        if (dataService.emailExists(newUser.getEmail())) {
            errorMessage = "Пользователь с таким email уже существует";
            return null;
        }

        dataService.registerUser(newUser);
        return "/pages/login?faces-redirect=true";
    }

    public RegUser getNewUser() { return newUser; }
    public void setNewUser(RegUser newUser) { this.newUser = newUser; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}