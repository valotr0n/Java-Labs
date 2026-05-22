<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Интернет-магазин</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .card { display: inline-block; border: 1px solid #ccc; border-radius: 8px;
                padding: 20px 30px; margin: 10px; text-align: center; text-decoration: none;
                color: #333; transition: box-shadow 0.2s; }
        .card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
    </style>
</head>
<body>
<h1>Интернет-магазин — Лабораторная работа 4</h1>
<p>JDBC и JPA</p>
<hr>

<%
    String login = (String)(session.getAttribute("login"));
    String role  = (String)(session.getAttribute("role"));
    if (login != null) {
%>
    <p>Вы вошли как: <b><%= login %></b> [<%= role %>]
       &nbsp;<a href="logout">Выйти</a></p>
<% } else { %>
    <p><a href="login">Войти</a></p>
<% } %>

<a class="card" href="products">
    <h2>Товары (JDBC)</h2>
    <p>Задания 1, 2</p>
</a>
<a class="card" href="products-jpa">
    <h2> Товары (JPA)</h2>
    <p>Задание 3</p>
</a>
<a class="card" href="admin">
    <h2>Администратор</h2>
    <p>Задание 4</p>
</a>
</body>
</html>
