<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Крестики-нолики</title>
    <style>
        table { border-collapse: collapse; margin: 20px auto; }
        td { width: 80px; height: 80px; text-align: center; font-size: 36px;
             border: 3px solid #333; cursor: pointer; }
        td:hover { background: #f0f0f0; }
        .taken { cursor: default; background: #eee; }
        h2 { text-align: center; }
        .msg { text-align: center; font-size: 20px; margin: 10px; }
        .btn { display: block; margin: 10px auto; padding: 10px 20px; font-size: 16px; }
    </style>
</head>
<body>
    <h2>Крестики-нолики</h2>
    <p class="msg"><%= request.getAttribute("message") %></p>

    <%
        int[] board = (int[]) request.getAttribute("board");
        String status = (String) session.getAttribute("status");
        String[] symbols = {"", "X", "O"};
    %>

    <table>
    <% for (int row = 0; row < 3; row++) { %>
        <tr>
        <% for (int col = 0; col < 3; col++) {
            int idx = row * 3 + col;
            int val = board[idx];
            String symbol = symbols[val];
        %>
            <td class="<%= val != 0 ? "taken" : "" %>">
            <% if (val == 0 && !"finished".equals(status)) { %>
                <form method="post" action="TicTacToe">
                    <input type="hidden" name="cell" value="<%= idx %>">
                    <input type="submit" value=" " style="width:70px;height:70px;font-size:36px;border:none;background:transparent;cursor:pointer;">
                </form>
            <% } else { %>
                <%= symbol %>
            <% } %>
            </td>
        <% } %>
        </tr>
    <% } %>
    </table>

    <a href="TicTacToe"><button class="btn">Новая игра</button></a>
    <a href="index.jsp"><button class="btn">На главную</button></a>
</body>
</html>