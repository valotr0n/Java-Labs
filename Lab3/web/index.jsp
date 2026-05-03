<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, tasks.Music" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Музыкальная библиотека</title>
</head>
<body>
    <h2>Музыкальная библиотека</h2>

    <%-- Фильтр по жанру --%>
    <form method="get" action="Music">
        <input type="text" name="genre" placeholder="Фильтр по жанру">
        <input type="submit" value="Найти">
        <a href="Music">Сбросить</a>
    </form>

    <%-- Таблица песен --%>
    <table border="1">
        <tr>
            <th>Название</th>
            <th>Исполнитель</th>
            <th>Жанр</th>
            <th>Год</th>
            <th>Действие</th>
        </tr>
        <%
            List<Music> songs = (List<Music>) request.getAttribute("songs");
            if (songs != null) {
                for (Music song : songs) {
        %>
        <tr>
            <td><%= song.getTitle() %></td>
            <td><%= song.getArtist() %></td>
            <td><%= song.getGenre() %></td>
            <td><%= song.getYear() %></td>
            <td>
                <form method="post" action="Music">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="title" value="<%= song.getTitle() %>">
                    <input type="submit" value="Удалить">
                </form>
            </td>
        </tr>
        <%
                }
            }
        %>
    </table>

    <%-- Форма добавления --%>
    <h3>Добавить песню</h3>
    <form method="post" action="Music">
        <input type="hidden" name="action" value="add">
        <input type="text" name="title" placeholder="Название"><br>
        <input type="text" name="artist" placeholder="Исполнитель"><br>
        <input type="text" name="genre" placeholder="Жанр"><br>
        <input type="number" name="year" placeholder="Год"><br>
        <input type="submit" value="Добавить">
    </form>
</body>
</html>