package tasks;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class TicTacToeServlet extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
    
        int[] board = new int[9]; 
        session.setAttribute("board", board);
        session.setAttribute("status", "playing");
        
        req.setAttribute("board", board);
        req.setAttribute("message", "Ваш ход! Выберите клетку.");
        req.getRequestDispatcher("tictactoe.jsp").forward(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        int[] board = (int[]) session.getAttribute("board");
        String status = (String) session.getAttribute("status");
        String message = "";

        if (board == null || "finished".equals(status)) {
            resp.sendRedirect("TicTacToe");
            return;
        }

        // Ход игрока
        int cell = Integer.parseInt(req.getParameter("cell"));
        if (board[cell] != 0) {
            message = "Клетка занята! Выберите другую.";
        } else {
            board[cell] = 1;

            if (checkWin(board, 1)) {
                message = "Вы победили!";
                status = "finished";
            } else if (isBoardFull(board)) {
                message = "Ничья!";
                status = "finished";
            } else {
                // Ход компьютера
                int compMove = getComputerMove(board);
                board[compMove] = 2;

                if (checkWin(board, 2)) {
                    message = "Компьютер победил!";
                    status = "finished";
                } else if (isBoardFull(board)) {
                    message = "Ничья!";
                    status = "finished";
                } else {
                    message = "Ваш ход!";
                }
            }
        }

        session.setAttribute("board", board);
        session.setAttribute("status", status);
        req.setAttribute("board", board);
        req.setAttribute("message", message);
        req.setAttribute("status", status);
        req.getRequestDispatcher("tictactoe.jsp").forward(req, resp);
    }

    private boolean checkWin(int[] b, int player) {
        int[][] wins = {
            {0,1,2}, {3,4,5}, {6,7,8}, 
            {0,3,6}, {1,4,7}, {2,5,8},
            {0,4,8}, {2,4,6}         
        };
        for (int[] w : wins) {
            if (b[w[0]] == player && b[w[1]] == player && b[w[2]] == player)
                return true;
        }
        return false;
    }

    private boolean isBoardFull(int[] b) {
        for (int cell : b) if (cell == 0) return false;
        return true;
    }

    private int getComputerMove(int[] board) {
        for (int i = 0; i < 9; i++) {
            if (board[i] == 0) {
                board[i] = 2;
                if (checkWin(board, 2)) { board[i] = 0; return i; }
                board[i] = 0;
            }
        }
        for (int i = 0; i < 9; i++) {
            if (board[i] == 0) {
                board[i] = 1;
                if (checkWin(board, 1)) { board[i] = 0; return i; }
                board[i] = 0;
            }
        }
        if (board[4] == 0) return 4;
        for (int i = 0; i < 9; i++) {
            if (board[i] == 0) return i;
        }
        return 0;
    }
}