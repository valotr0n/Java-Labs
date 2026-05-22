<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Вход</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 60px auto; max-width: 360px; }
        input { width: 100%; padding: 8px; margin: 6px 0 14px; box-sizing: border-box; }
        button { width: 100%; padding: 10px; background: #3377ff; color: white;
                 border: none; cursor: pointer; font-size: 15px; border-radius: 4px; }
        .error { color: red; }
        h2 { text-align: center; }
    </style>
</head>
<body>
<h2>Вход в систему</h2>

<% String error = (String) request.getAttribute("error");
   if (error != null) { %>
    <p class="error"><%= error %></p>
<% } %>

<form method="post" action="${pageContext.request.contextPath}/login">
    <label>Логин:</label>
    <input type="text" name="login" required autofocus>
    <label>Пароль:</label>
    <input type="password" name="password" required>
    <button type="submit">Войти</button>
</form>

<p style="text-align:center; margin-top:15px;">
    <a href="${pageContext.request.contextPath}/index.jsp">← На главную</a>
</p>

<p style="font-size:12px; color:#999; text-align:center; margin-top:20px;">
    Тестовые пользователи:<br>
    admin / admin123 (администратор)<br>
    user / user123 (пользователь)
</p>
</body>
</html>
