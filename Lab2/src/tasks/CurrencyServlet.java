package tasks;

import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.*;
import java.net.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

@WebServlet("/Currency")
public class CurrencyServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String currency = req.getParameter("currency");
        String from = req.getParameter("from");
        String to = req.getParameter("to");

        out.println("Валюта: " + currency);
        out.println("От: " + from);
        out.println("До: " + to);

        String[] fromParts = from.split("-");
        String fromFormatted = fromParts[2] + "/" + fromParts[1] + "/" + fromParts[0];
        String[] toParts = to.split("-");
        String toFormatted = toParts[2] + "/" + toParts[1] + "/" + toParts[0];
        
        String urlStr = "http://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=" + fromFormatted + "&date_req2=" + toFormatted + "&VAL_NM_RQ=" + currency;
        
        out.println("URL запрос: " + urlStr); 

        try { 
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(con.getInputStream());
            NodeList records = doc.getElementsByTagName("Record");

            out.println("<table border='1'>");
            out.println("<tr><th>Дата</th><th>Курс</th></tr>");

            for (int i = 0; i < records.getLength(); i++) {
                Element record = (Element) records.item(i);
                String date = record.getAttribute("Date");
                String value = record.getElementsByTagName("Value").item(0).getTextContent();
                out.println("<tr><td>" + date + "</td><td>" + value + "</td></tr>");
            }
            out.println("</table>");
        }
        catch (Exception e) {
            out.println("Ошибка" + e.getMessage());
        }
    }
}