package lab6.store;

import lab6.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserStore {
    private static final UserStore INSTANCE = new UserStore();

    private final Map<String, User> users = new ConcurrentHashMap<>();

    private UserStore() {
        add(new User("Иван", "Петров", "ivan@example.com", "12345"));
        add(new User("Анна", "Смирнова", "anna@example.com", "qwerty"));
    }

    public static UserStore getInstance() {
        return INSTANCE;
    }

    public synchronized User add(User user) {
        validate(user);
        String key = key(user.getEmail());
        if (users.containsKey(key)) {
            throw new IllegalArgumentException("Пользователь с таким e-mail уже есть");
        }
        User copy = copy(user);
        users.put(key, copy);
        return copy(copy);
    }

    public List<User> all() {
        List<User> result = new ArrayList<>();
        for (User user : users.values()) {
            result.add(copy(user));
        }
        result.sort(Comparator.comparing(User::getEmail));
        return result;
    }

    public Optional<User> find(String email) {
        User user = users.get(key(email));
        return user == null ? Optional.empty() : Optional.of(copy(user));
    }

    public synchronized Optional<User> update(String email, User data) {
        String key = key(email);
        User current = users.get(key);
        if (current == null) {
            return Optional.empty();
        }
        current.setFirstName(valueOrCurrent(data.getFirstName(), current.getFirstName()));
        current.setLastName(valueOrCurrent(data.getLastName(), current.getLastName()));
        current.setPassword(valueOrCurrent(data.getPassword(), current.getPassword()));
        current.setEmail(current.getEmail());
        return Optional.of(copy(current));
    }

    public synchronized boolean delete(String email) {
        return users.remove(key(email)) != null;
    }

    private static void validate(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Не переданы данные пользователя");
        }
        if (isBlank(user.getFirstName())) {
            throw new IllegalArgumentException("Имя обязательно");
        }
        if (isBlank(user.getLastName())) {
            throw new IllegalArgumentException("Фамилия обязательна");
        }
        if (isBlank(user.getEmail()) || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Нужен корректный e-mail");
        }
        if (isBlank(user.getPassword())) {
            throw new IllegalArgumentException("Пароль обязателен");
        }
    }

    private static String key(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String valueOrCurrent(String value, String current) {
        return isBlank(value) ? current : value.trim();
    }

    private static User copy(User source) {
        return new User(source.getFirstName(), source.getLastName(), source.getEmail(), source.getPassword());
    }
}