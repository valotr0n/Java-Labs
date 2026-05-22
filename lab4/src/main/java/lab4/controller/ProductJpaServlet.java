package lab4.controller;

import lab4.model.Category;
import lab4.model.Manufacturer;
import lab4.model.Product;

import javax.persistence.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;

@WebServlet(name = "ProductJpaServlet", urlPatterns = {"/products-jpa", "/products-jpa/*"})
public class ProductJpaServlet extends HttpServlet {

    private EntityManagerFactory emFactory;

    @Override
    public void init() throws ServletException {
        emFactory = Persistence.createEntityManagerFactory("shopPU");
    }

    @Override
    public void destroy() {
        if (emFactory != null && emFactory.isOpen()) emFactory.close();
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
            default:       resp.sendRedirect(req.getContextPath() + "/products-jpa");
        }
    }

    private void listProducts(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        PrintWriter out = resp.getWriter();
        printHeader(out, "Товары (JPA)");
        out.println("<h1>Список товаров (JPA)</h1>");
        out.println("<a href='?action=edit'>+ Добавить товар</a><br><br>");

        EntityManager em = emFactory.createEntityManager();
        try {
            List<Product> products = em.createQuery(
                "SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.manufacturer",
                Product.class).getResultList();

            out.println("<table border='1' cellpadding='5' cellspacing='0'>");
            out.println("<tr><th>ID</th><th>Название</th><th>Цена</th>" +
                        "<th>Категория</th><th>Производитель</th><th>Действия</th></tr>");

            for (Product p : products) {
                out.println("<tr>");
                out.println("<td>" + p.getId() + "</td>");
                out.println("<td>" + p.getName() + "</td>");
                out.println("<td>" + p.getPrice() + "</td>");
                out.println("<td>" + (p.getCategory() != null ? p.getCategory().getName() : "-") + "</td>");
                out.println("<td>" + (p.getManufacturer() != null ? p.getManufacturer().getName() : "-") + "</td>");
                out.println("<td>" +
                    "<a href='?action=edit&id=" + p.getId() + "'>Изменить</a> | " +
                    "<a href='?action=delete&id=" + p.getId() + "' " +
                    "onclick=\"return confirm('Удалить?')\">Удалить</a>" +
                    "</td>");
                out.println("</tr>");
            }
            out.println("</table>");
        } finally {
            em.close();
        }
        printFooter(out);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        PrintWriter out = resp.getWriter();
        String idParam = req.getParameter("id");
        boolean isEdit = (idParam != null && !idParam.isEmpty());

        printHeader(out, isEdit ? "Редактировать (JPA)" : "Добавить (JPA)");
        out.println("<h1>" + (isEdit ? "Редактировать" : "Добавить") + " товар (JPA)</h1>");

        EntityManager em = emFactory.createEntityManager();
        Product p = null;
        if (isEdit) {
            p = em.find(Product.class, Integer.parseInt(idParam));
        }

        List<Category>     cats = em.createQuery("SELECT c FROM Category c ORDER BY c.name", Category.class).getResultList();
        List<Manufacturer> mfrs = em.createQuery("SELECT m FROM Manufacturer m ORDER BY m.name", Manufacturer.class).getResultList();

        String formAction = req.getContextPath() + "/products-jpa";
        out.println("<form method='post' action='" + formAction + "'>");
        if (isEdit) {
            out.println("<input type='hidden' name='id' value='" + idParam + "'>");
            out.println("<input type='hidden' name='action' value='update'>");
        } else {
            out.println("<input type='hidden' name='action' value='add'>");
        }

        String pName  = (p != null) ? p.getName() : "";
        double pPrice = (p != null) ? p.getPrice() : 0;
        int    pCatId = (p != null && p.getCategory() != null) ? p.getCategory().getId() : 0;
        int    pMfrId = (p != null && p.getManufacturer() != null) ? p.getManufacturer().getId() : 0;

        out.println("<table cellpadding='5'>");
        out.println("<tr><td>Название:</td><td><input name='name' value='" + pName + "' required></td></tr>");
        out.println("<tr><td>Цена:</td><td><input name='price' type='number' step='0.01' value='" + pPrice + "' required></td></tr>");

        out.println("<tr><td>Категория:</td><td><select name='category_id'>");
        out.println("<option value='0'>-- выберите --</option>");
        for (Category c : cats) {
            String sel = (c.getId() == pCatId) ? " selected" : "";
            out.println("<option value='" + c.getId() + "'" + sel + ">" + c.getName() + "</option>");
        }
        out.println("</select></td></tr>");

        out.println("<tr><td>Производитель:</td><td><select name='manufacturer_id'>");
        out.println("<option value='0'>-- выберите --</option>");
        for (Manufacturer m : mfrs) {
            String sel = (m.getId() == pMfrId) ? " selected" : "";
            out.println("<option value='" + m.getId() + "'" + sel + ">" + m.getName() + "</option>");
        }
        out.println("</select></td></tr>");

        out.println("</table>");
        out.println("<br><button type='submit'>" + (isEdit ? "Сохранить" : "Добавить") + "</button>");
        out.println(" <a href='" + req.getContextPath() + "/products-jpa'>Отмена</a>");
        out.println("</form>");

        em.close();
        printFooter(out);
    }

    private void addProduct(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        EntityManager em = emFactory.createEntityManager();
        try {
            Product p = new Product();
            p.setName(req.getParameter("name"));
            p.setPrice(Double.parseDouble(req.getParameter("price")));

            int catId = Integer.parseInt(req.getParameter("category_id"));
            int mfrId = Integer.parseInt(req.getParameter("manufacturer_id"));
            if (catId > 0) p.setCategory(em.find(Category.class, catId));
            if (mfrId > 0) p.setManufacturer(em.find(Manufacturer.class, mfrId));

            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            resp.getWriter().println("Ошибка: " + e.getMessage());
            return;
        } finally {
            em.close();
        }
        resp.sendRedirect(req.getContextPath() + "/products-jpa");
    }

    private void updateProduct(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        EntityManager em = emFactory.createEntityManager();
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            Product p = em.find(Product.class, id);
            if (p == null) { resp.sendRedirect(req.getContextPath() + "/products-jpa"); return; }

            p.setName(req.getParameter("name"));
            p.setPrice(Double.parseDouble(req.getParameter("price")));

            int catId = Integer.parseInt(req.getParameter("category_id"));
            int mfrId = Integer.parseInt(req.getParameter("manufacturer_id"));
            if (catId > 0) p.setCategory(em.find(Category.class, catId));
            if (mfrId > 0) p.setManufacturer(em.find(Manufacturer.class, mfrId));

            em.getTransaction().begin();
            em.merge(p);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            resp.getWriter().println("Ошибка: " + e.getMessage());
            return;
        } finally {
            em.close();
        }
        resp.sendRedirect(req.getContextPath() + "/products-jpa");
    }

    private void deleteProduct(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        EntityManager em = emFactory.createEntityManager();
        try {
            int id = Integer.parseInt(req.getParameter("id"));

            em.getTransaction().begin();
            em.createQuery("DELETE FROM Warehouse w WHERE w.product.id = :id")
              .setParameter("id", id)
              .executeUpdate();
            Product p = em.find(Product.class, id);
            if (p != null) em.remove(p);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            resp.getWriter().println("Ошибка: " + e.getMessage());
            return;
        } finally {
            em.close();
        }
        resp.sendRedirect(req.getContextPath() + "/products-jpa");
    }

    private void printHeader(PrintWriter out, String title) {
        out.println("<!DOCTYPE html><html><head>");
        out.println("<meta charset='UTF-8'><title>" + title + "</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
        out.println("table { border-collapse: collapse; }");
        out.println("th { background: #22aa55; color: white; padding: 6px 12px; }");
        out.println("td { padding: 5px 10px; }");
        out.println("a { color: #22aa55; }");
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
