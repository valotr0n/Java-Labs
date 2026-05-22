package lab4.controller;

import lab4.util.DBUtil;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.*;
import java.sql.*;


@WebServlet(name = "ProductServlet", urlPatterns = {"/products", "/products/*"})
public class ProductServlet extends HttpServlet {

    @Resource(name = "jdbc/postgres")
    private DataSource ds;

    @Override
    public void init() throws ServletException {
        try (Connection con = ds.getConnection()) {
            DBUtil.initIfNeeded(con);
        } catch (SQLException e) {
            throw new ServletException("Ошибка инициализации БД: " + e.getMessage(), e);
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        String action = req.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "edit":   showEditForm(req, resp); break;
            case "delete": deleteProduct(req, resp); break;
            default:       listProducts(req, resp);
        }
    }

    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "add":    addProduct(req, resp);    break;
            case "update": updateProduct(req, resp); break;
            default:       resp.sendRedirect(req.getContextPath() + "/products");
        }
    }


    private void listProducts(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        PrintWriter out = resp.getWriter();
        printHeader(out, "Список товаров");

        out.println("<h1>Список товаров</h1>");
        out.println("<a href='?action=edit'>+ Добавить товар</a><br><br>");

        try (Connection con = ds.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(
                 "SELECT p.id, p.name, p.price, c.name AS cat, m.name AS mfr " +
                 "FROM product p " +
                 "LEFT JOIN category c ON p.category_id = c.id " +
                 "LEFT JOIN manufacturer m ON p.manufacturer_id = m.id " +
                 "ORDER BY p.id")) {

            out.println("<table border='1' cellpadding='5' cellspacing='0'>");
            out.println("<tr><th>ID</th><th>Название</th><th>Цена</th>" +
                        "<th>Категория</th><th>Производитель</th><th>Действия</th></tr>");

            while (rs.next()) {
                int id = rs.getInt("id");
                out.println("<tr>");
                out.println("<td>" + id + "</td>");
                out.println("<td>" + rs.getString("name") + "</td>");
                out.println("<td>" + rs.getDouble("price") + "</td>");
                out.println("<td>" + rs.getString("cat") + "</td>");
                out.println("<td>" + rs.getString("mfr") + "</td>");
                out.println("<td>" +
                    "<a href='?action=edit&id=" + id + "'>Изменить</a> | " +
                    "<a href='?action=delete&id=" + id + "' " +
                    "onclick=\"return confirm('Удалить?')\">Удалить</a>" +
                    "</td>");
                out.println("</tr>");
            }
            out.println("</table>");

        } catch (SQLException e) {
            out.println("<p style='color:red'>Ошибка: " + e.getMessage() + "</p>");
        }

        printFooter(out);
    }


    private void showEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        PrintWriter out = resp.getWriter();
        String idParam = req.getParameter("id");
        boolean isEdit = (idParam != null && !idParam.isEmpty());

        printHeader(out, isEdit ? "Редактировать товар" : "Добавить товар");
        out.println("<h1>" + (isEdit ? "Редактировать" : "Добавить") + " товар</h1>");

        String name = "";
        double price = 0;
        int categoryId = 0, manufacturerId = 0;

        if (isEdit) {
            try (Connection con = ds.getConnection()) {
              
                PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM product WHERE id = ?");
                pst.setInt(1, Integer.parseInt(idParam));
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    name           = rs.getString("name");
                    price          = rs.getDouble("price");
                    categoryId     = rs.getInt("category_id");
                    manufacturerId = rs.getInt("manufacturer_id");
                }
                rs.close(); pst.close();
            } catch (SQLException e) {
                out.println("<p style='color:red'>Ошибка: " + e.getMessage() + "</p>");
            }
        }

        String formAction = req.getContextPath() + "/products";
        out.println("<form method='post' action='" + formAction + "'>");
        if (isEdit) {
            out.println("<input type='hidden' name='id' value='" + idParam + "'>");
            out.println("<input type='hidden' name='action' value='update'>");
        } else {
            out.println("<input type='hidden' name='action' value='add'>");
        }

        out.println("<table cellpadding='5'>");
        out.println("<tr><td>Название:</td><td><input name='name' value='" + name + "' required></td></tr>");
        out.println("<tr><td>Цена:</td><td><input name='price' type='number' step='0.01' value='" + price + "' required></td></tr>");

        out.println("<tr><td>Категория:</td><td>");
        out.println(buildSelect("category_id", "category", categoryId, req));
        out.println("</td></tr>");

        out.println("<tr><td>Производитель:</td><td>");
        out.println(buildSelect("manufacturer_id", "manufacturer", manufacturerId, req));
        out.println("</td></tr>");

        out.println("</table>");
        out.println("<br><button type='submit'>" + (isEdit ? "Сохранить" : "Добавить") + "</button>");
        out.println(" <a href='" + req.getContextPath() + "/products'>Отмена</a>");
        out.println("</form>");

        printFooter(out);
    }


    private String buildSelect(String fieldName, String table, int selectedId,
                               HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        sb.append("<select name='").append(fieldName).append("'>");
        sb.append("<option value='0'>-- выберите --</option>");
        try (Connection con = ds.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery("SELECT id, name FROM " + table + " ORDER BY name")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String selected = (id == selectedId) ? " selected" : "";
                sb.append("<option value='").append(id).append("'").append(selected).append(">")
                  .append(rs.getString("name")).append("</option>");
            }
        } catch (SQLException e) {
            sb.append("<option>Ошибка загрузки</option>");
        }
        sb.append("</select>");
        return sb.toString();
    }

    private void addProduct(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String name   = req.getParameter("name");
        double price  = Double.parseDouble(req.getParameter("price"));
        int catId     = Integer.parseInt(req.getParameter("category_id"));
        int mfrId     = Integer.parseInt(req.getParameter("manufacturer_id"));

        try (Connection con = ds.getConnection()) {
            PreparedStatement pst = con.prepareStatement(
                "INSERT INTO product(name, price, category_id, manufacturer_id) VALUES (?, ?, ?, ?)");
            pst.setString(1, name);
            pst.setDouble(2, price);
            pst.setInt(3, catId == 0 ? Types.NULL : catId);
            pst.setInt(4, mfrId == 0 ? Types.NULL : mfrId);
            pst.executeUpdate();
            pst.close();
        } catch (SQLException e) {
            resp.getWriter().println("Ошибка: " + e.getMessage());
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/products");
    }

    private void updateProduct(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        int    id    = Integer.parseInt(req.getParameter("id"));
        String name  = req.getParameter("name");
        double price = Double.parseDouble(req.getParameter("price"));
        int catId    = Integer.parseInt(req.getParameter("category_id"));
        int mfrId    = Integer.parseInt(req.getParameter("manufacturer_id"));

        try (Connection con = ds.getConnection()) {
            PreparedStatement pst = con.prepareStatement(
                "UPDATE product SET name=?, price=?, category_id=?, manufacturer_id=? WHERE id=?");
            pst.setString(1, name);
            pst.setDouble(2, price);
            pst.setInt(3, catId);
            pst.setInt(4, mfrId);
            pst.setInt(5, id);
            pst.executeUpdate();
            pst.close();
        } catch (SQLException e) {
            resp.getWriter().println("Ошибка: " + e.getMessage());
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/products");
    }

    
    private void deleteProduct(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        int id = Integer.parseInt(req.getParameter("id"));

        try (Connection con = ds.getConnection()) {
            
            PreparedStatement pst1 = con.prepareStatement(
                "DELETE FROM warehouse WHERE product_id = ?");
            pst1.setInt(1, id);
            pst1.executeUpdate();
            pst1.close();

            PreparedStatement pst2 = con.prepareStatement(
                "DELETE FROM product WHERE id = ?");
            pst2.setInt(1, id);
            pst2.executeUpdate();
            pst2.close();
        } catch (SQLException e) {
            resp.getWriter().println("Ошибка: " + e.getMessage());
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/products");
    }

    private void printHeader(PrintWriter out, String title) {
        out.println("<!DOCTYPE html><html><head>");
        out.println("<meta charset='UTF-8'><title>" + title + "</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
        out.println("table { border-collapse: collapse; }");
        out.println("th { background: #3377ff; color: white; padding: 6px 12px; }");
        out.println("td { padding: 5px 10px; }");
        out.println("a { color: #3377ff; }");
        out.println("nav a { margin-right: 15px; font-weight: bold; }");
        out.println("</style></head><body>");
        out.println("<nav>");
        out.println("<a href='../index.jsp'>Главная</a>");
        out.println("<a href='../products'>Товары (JDBC)</a>");
        out.println("<a href='../products-jpa'>Товары (JPA)</a>");
        out.println("<a href='../admin'>Администратор</a>");
        out.println("</nav><hr>");
    }

    private void printFooter(PrintWriter out) {
        out.println("</body></html>");
    }
}
