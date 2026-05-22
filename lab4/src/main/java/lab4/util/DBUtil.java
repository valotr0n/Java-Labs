package lab4.util;

import java.sql.*;


public class DBUtil {

    public static void initIfNeeded(Connection con) throws SQLException {
        if (!tableExists(con, "category")) {
            createSchema(con);
            insertSampleData(con);
        }
    }

    private static boolean tableExists(Connection con, String tableName) throws SQLException {
        DatabaseMetaData meta = con.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, tableName.toLowerCase(), new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    private static void createSchema(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS category (" +
                "  id SERIAL PRIMARY KEY," +
                "  name VARCHAR(255) NOT NULL," +
                "  description VARCHAR(500)" +
                ")"
            );

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS manufacturer (" +
                "  id SERIAL PRIMARY KEY," +
                "  name VARCHAR(255) NOT NULL," +
                "  country VARCHAR(100)" +
                ")"
            );

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS product (" +
                "  id SERIAL PRIMARY KEY," +
                "  name VARCHAR(255) NOT NULL," +
                "  price DOUBLE PRECISION," +
                "  category_id INT REFERENCES category(id)," +
                "  manufacturer_id INT REFERENCES manufacturer(id)" +
                ")"
            );

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS warehouse (" +
                "  id SERIAL PRIMARY KEY," +
                "  product_id INT REFERENCES product(id)," +
                "  quantity INT," +
                "  location VARCHAR(255)" +
                ")"
            );

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                "  id SERIAL PRIMARY KEY," +
                "  login VARCHAR(100) NOT NULL UNIQUE," +
                "  password_hash VARCHAR(64) NOT NULL," +
                "  role VARCHAR(20) NOT NULL" +
                ")"
            );
        }
        System.out.println("[DBUtil] Схема БД создана.");
    }

    private static void insertSampleData(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.executeUpdate("INSERT INTO category(name, description) VALUES ('Комплектующие ПК', 'Процессоры, видеокарты и т.д.')");
            st.executeUpdate("INSERT INTO category(name, description) VALUES ('Сетевое оборудование', 'Роутеры, коммутаторы')");
            st.executeUpdate("INSERT INTO category(name, description) VALUES ('Периферия', 'Мыши, клавиатуры, мониторы')");

            st.executeUpdate("INSERT INTO manufacturer(name, country) VALUES ('ASUS', 'Тайвань')");
            st.executeUpdate("INSERT INTO manufacturer(name, country) VALUES ('Intel', 'США')");
            st.executeUpdate("INSERT INTO manufacturer(name, country) VALUES ('Logitech', 'Швейцария')");

            st.executeUpdate("INSERT INTO product(name, price, category_id, manufacturer_id) VALUES ('Процессор Core i7', 29999.0, 1, 2)");
            st.executeUpdate("INSERT INTO product(name, price, category_id, manufacturer_id) VALUES ('Роутер RT-AX88U', 15999.0, 2, 1)");
            st.executeUpdate("INSERT INTO product(name, price, category_id, manufacturer_id) VALUES ('Мышь MX Master 3', 5999.0, 3, 3)");

            st.executeUpdate("INSERT INTO warehouse(product_id, quantity, location) VALUES (1, 10, 'Склад А, полка 1')");
            st.executeUpdate("INSERT INTO warehouse(product_id, quantity, location) VALUES (2, 5, 'Склад А, полка 2')");
            st.executeUpdate("INSERT INTO warehouse(product_id, quantity, location) VALUES (3, 20, 'Склад Б, полка 1')");

            String adminHash = PasswordUtil.hash("admin123");
            String userHash  = PasswordUtil.hash("user123");
            st.executeUpdate("INSERT INTO users(login, password_hash, role) VALUES ('admin', '" + adminHash + "', 'ADMIN')");
            st.executeUpdate("INSERT INTO users(login, password_hash, role) VALUES ('user', '"  + userHash  + "', 'USER')");
        }
        System.out.println("[DBUtil] Тестовые данные добавлены.");
    }
}
