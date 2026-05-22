<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Панель администратора</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        table { border-collapse: collapse; }
        th { background: #e74c3c; color: white; padding: 6px 16px; }
        td { padding: 5px 14px; border-bottom: 1px solid #eee; }
        input, select { padding: 5px; margin: 3px; }
        button { padding: 6px 14px; background: #e74c3c; color: white; border: none;
                 cursor: pointer; border-radius: 4px; }
        nav a { margin-right: 15px; font-weight: bold; color: #e74c3c; text-decoration: none; }
        .section { margin-top: 30px; padding: 15px; border: 1px solid #ddd; border-radius: 6px; }
    </style>
</head>
<body>
<nav>
    <a href="${pageContext.request.contextPath}/index.jsp">Главная</a>
    <a href="${pageContext.request.contextPath}/products">Товары (JDBC)</a>
    <a href="${pageContext.request.contextPath}/products-jpa">Товары (JPA)</a>
    <a href="${pageContext.request.contextPath}/logout">Выйти</a>
</nav>
<hr>
<h1>Панель администратора</h1>
<p>Вы вошли как: <b>${sessionScope.login}</b></p>

<%-- Вывод ошибок --%>
<% String err = (String) request.getAttribute("error");
   if (err != null) { %>
    <p style="color:red"><%= err %></p>
<% } %>

<%-- Список пользователей --%>
<div class="section">
    <h2>Пользователи</h2>
    <table border="0">
        <tr><th>ID</th><th>Логин</th><th>Роль</th><th>Действие</th></tr>
        <%
            List<String[]> users = (List<String[]>) request.getAttribute("users");
            if (users != null) {
                for (String[] u : users) {
        %>
        <tr>
            <td><%= u[0] %></td>
            <td><%= u[1] %></td>
            <td><%= u[2] %></td>
            <td>
                <a href="${pageContext.request.contextPath}/admin?action=delete&id=<%= u[0] %>"
                   onclick="return confirm('Удалить пользователя <%= u[1] %>?')">Удалить</a>
            </td>
        </tr>
        <% } } %>
    </table>
</div>

<%-- Добавление нового пользователя --%>
<div class="section">
    <h2>Добавить пользователя</h2>
    <form method="post" action="${pageContext.request.contextPath}/admin">
        <input type="hidden" name="action" value="addUser">
        Логин: <input type="text" name="login" required>
        Пароль: <input type="password" name="password" required>
        Роль:
        <select name="role">
            <option value="USER">USER</option>
            <option value="ADMIN">ADMIN</option>
        </select>
        <button type="submit">Добавить</button>
    </form>
</div>

<%-- Настройки соединения (отображение текущих параметров из context.xml) --%>
<div class="section">
    <h2>Настройки БД (из context.xml)</h2>
    <p>URL: <code>jdbc:postgresql://127.0.0.1:5432/shop</code></p>
    <p>Пользователь: <code>postgres</code></p>
    <p><em>Для изменения параметров отредактируйте
        <b>webapp/META-INF/context.xml</b> и перезапустите сервер.</em></p>
</div>

</body>
</html>
