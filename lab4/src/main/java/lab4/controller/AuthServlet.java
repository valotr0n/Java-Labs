package lab4.controller;

import lab4.util.PasswordUtil;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;

@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/logout"})
public class AuthServlet extends HttpServlet {

    @Resource(name = "jdbc/postgres")
    private DataSource ds;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String uri = req.getRequestURI();
        if (uri.endsWith("/logout")) {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
        } else {
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String login    = req.getParameter("login");
        String password = req.getParameter("password");

        try (Connection con = ds.getConnection()) {
            PreparedStatement pst = con.prepareStatement(
                "SELECT password_hash, role FROM users WHERE login = ?");
            pst.setString(1, login);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String role       = rs.getString("role");

                if (PasswordUtil.check(password, storedHash)) {
                    HttpSession session = req.getSession(true);
                    session.setAttribute("login", login);
                    session.setAttribute("role", role);

                    if ("ADMIN".equals(role)) {
                        resp.sendRedirect(req.getContextPath() + "/admin");
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/products");
                    }
                    return;
                }
            }
            rs.close(); pst.close();
        } catch (SQLException e) {
            req.setAttribute("error", "Ошибка БД: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }

        req.setAttribute("error", "Неверный логин или пароль");
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }
}
