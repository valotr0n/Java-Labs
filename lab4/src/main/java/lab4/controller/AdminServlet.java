package lab4.controller;

import lab4.util.PasswordUtil;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin", "/admin/*"})
public class AdminServlet extends HttpServlet {

    @Resource(name = "jdbc/postgres")
    private DataSource ds;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if ("delete".equals(action)) {
            deleteUser(req, resp);
            return;
        }
        showAdminPanel(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if ("addUser".equals(action)) {
            addUser(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin");
        }
    }

    private void showAdminPanel(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<String[]> users = new ArrayList<>();

        try (Connection con = ds.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery("SELECT id, login, role FROM users ORDER BY id")) {
            while (rs.next()) {
                users.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("login"),
                    rs.getString("role")
                });
            }
        } catch (SQLException e) {
            req.setAttribute("error", "Ошибка БД: " + e.getMessage());
        }

        req.setAttribute("users", users);
        req.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(req, resp);
    }

    private void addUser(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String login    = req.getParameter("login");
        String password = req.getParameter("password");
        String role     = req.getParameter("role");

        try (Connection con = ds.getConnection()) {
            PreparedStatement pst = con.prepareStatement(
                "INSERT INTO users(login, password_hash, role) VALUES (?, ?, ?)");
            pst.setString(1, login);
            pst.setString(2, PasswordUtil.hash(password));
            pst.setString(3, role);
            pst.executeUpdate();
            pst.close();
        } catch (SQLException e) {

        }
        resp.sendRedirect(req.getContextPath() + "/admin");
    }

    private void deleteUser(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        int id = Integer.parseInt(req.getParameter("id"));
        try (Connection con = ds.getConnection()) {
            PreparedStatement pst = con.prepareStatement("DELETE FROM users WHERE id = ?");
            pst.setInt(1, id);
            pst.executeUpdate();
            pst.close();
        } catch (SQLException e) {
        }
        resp.sendRedirect(req.getContextPath() + "/admin");
    }
}
