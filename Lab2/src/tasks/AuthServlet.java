package tasks;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.*;
import java.security.MessageDigest;

import javax.xml.parsers.*;
import org.w3c.dom.*;


@WebServlet("/Auth")
public class AuthServlet extends HttpServlet{
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        PrintWriter out = resp.getWriter();
        out.println("Логин - " + login);
        out.println("Пароль - " + password);

        HttpSession session = req.getSession();
        Integer attempts = (Integer) session.getAttribute("attempts");

        if (attempts == null) {
            attempts = 0;
        }
        if (attempts >= 3) {
            out.println("Вход запрещен, вы использовали 3 попытки входа");
            return;
        }
        
        try {
            File xmlFile = new File(getServletContext().getRealPath("/users.xml"));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            NodeList users = doc.getElementsByTagName("user");

            for (int i = 0; i < users.getLength(); i++) {
                Element user = (Element) users.item(i);
                String xmlLogin = user.getElementsByTagName("login").item(0).getTextContent();
                String xmlPassword = user.getElementsByTagName("password").item(0).getTextContent();

                if (login.equals(xmlLogin) && hashPassword(password).equals(xmlPassword)) {
                    session.setAttribute("attempts", 0);
                    out.println("Сходится!" + new java.util.Date());
                }
                else {
                    attempts++;
                    session.setAttribute("attempts", attempts);
                    out.println("Не сходится... Попытка: " + attempts);
                }
            }
        }
        catch (Exception e) {
            out.println("Ошибка чтения XML: " + e.getMessage());
        }
    }

    public String hashPassword(String password) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hash = messageDigest.digest(password.getBytes("UTF-8"));
        StringBuilder hexHash = new StringBuilder();
        for (byte b: hash) {
            hexHash.append(String.format("%02x", b));
        }
        return hexHash.toString();
    }
}
