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
@SessionScoped
public class AuthBean implements Serializable {

    @Inject
    private DataService dataService;

    // Текущий залогиненный пользователь
    private RegUser currentUser;

    // Поля формы авторизации
    private String email;
    private String password;
    private String errorMessage;

    // Кнопка "Войти"
    public String login() {
        RegUser user = dataService.login(email, password);
        if (user != null) {
            currentUser = user;
            // Перенаправляем в зависимости от роли
            if ("manager".equals(user.getRole())) {
                return "/pages/manager?faces-redirect=true";
            } else {
                return "/pages/client?faces-redirect=true";
            }
        }
        errorMessage = "Неверный email или пароль";
        return null;
    }

    // Кнопка "Выйти"
    public String logout() {
        currentUser = null;
        email = null;
        password = null;
        return "/pages/login?faces-redirect=true";
    }

    // Проверка залогинен ли пользователь
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public RegUser getCurrentUser() { return currentUser; }
    public void setCurrentUser(RegUser currentUser) { this.currentUser = currentUser; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}