package tasks;

import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.*;

@WebServlet("/Session")
public class SessionServlet extends HttpServlet{

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession();

        out.println("Id сессии - " + session.getId());
        out.println("Время создания -" + new java.util.Date(session.getCreationTime()));
        out.println("Текущее время" + new java.util.Date());

        Integer count = (Integer) session.getAttribute("count");
        if (count == null) count = 0;
        count ++;
        session.setAttribute("count", count);
        out.println("Количество запрсов: " + count);
    }
}