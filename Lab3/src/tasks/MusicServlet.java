package tasks;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MusicServlet extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String genre = req.getParameter("genre");
        List<Music> songs = loadSongs();
        List<Music> filtered = new ArrayList<>();

        if (genre != null && !genre.isEmpty()) {
            for (Music song : songs) {
                if (song.getGenre().equalsIgnoreCase(genre)) {
                    filtered.add(song);
                }
            }
        } else {
            filtered = songs;
        }

        req.setAttribute("songs", filtered);
        RequestDispatcher dispatcher = req.getRequestDispatcher("index.jsp");
        dispatcher.forward(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        if (action.equals("add")) {
            String title = req.getParameter("title");
            String artist = req.getParameter("artist");
            String genre = req.getParameter("genre");
            int year = Integer.parseInt(req.getParameter("year"));
            saveSong(new Music(title, artist, genre, year));
        } else if (action.equals("delete")) {
            String title = req.getParameter("title");
            removeSong(title);
        }

        resp.sendRedirect("Music");
    }

    private List<Music> loadSongs() {
        List<Music> songs = new ArrayList<>();
        try {
            DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dFactory.newDocumentBuilder();
            String path = getServletContext().getRealPath("/music.xml");
            Document doc = docBuilder.parse(new File(path));
            doc.getDocumentElement().normalize();

            NodeList nodes = doc.getElementsByTagName("song");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                NodeList children = node.getChildNodes();
                String title = "", artist = "", genre = "";
                int year = 0;
                for (int j = 0; j < children.getLength(); j++) {
                    Node child = children.item(j);
                    switch (child.getNodeName()) {
                        case "title":  title  = child.getTextContent().trim(); break;
                        case "artist": artist = child.getTextContent().trim(); break;
                        case "genre":  genre  = child.getTextContent().trim(); break;
                        case "year":   year   = Integer.parseInt(child.getTextContent().trim()); break;
                    }
                }
                songs.add(new Music(title, artist, genre, year));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songs;
    }

    private void saveSong(Music song) {
        try {
            DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
            String path = getServletContext().getRealPath("/music.xml");
            Document doc = dBuilder.parse(new File(path));

            org.w3c.dom.Element songEl = doc.createElement("song");

            org.w3c.dom.Element titleEl = doc.createElement("title");
            titleEl.setTextContent(song.getTitle());
            songEl.appendChild(titleEl);

            org.w3c.dom.Element artistEl = doc.createElement("artist");
            artistEl.setTextContent(song.getArtist());
            songEl.appendChild(artistEl);

            org.w3c.dom.Element genreEl = doc.createElement("genre");
            genreEl.setTextContent(song.getGenre());
            songEl.appendChild(genreEl);

            org.w3c.dom.Element yearEl = doc.createElement("year");
            yearEl.setTextContent(String.valueOf(song.getYear()));
            songEl.appendChild(yearEl);

            doc.getDocumentElement().appendChild(songEl);
            saveDocument(doc, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeSong(String title) {
        try {
            DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
            String path = getServletContext().getRealPath("/music.xml");
            Document doc = dBuilder.parse(new File(path));
            doc.getDocumentElement().normalize();

            NodeList nodes = doc.getElementsByTagName("song");
            Node toRemove = null;

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                NodeList children = node.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    Node child = children.item(j);
                    if (child.getNodeName().equals("title") &&
                        child.getTextContent().trim().equals(title.trim())) {
                        toRemove = node;
                        break;
                    }
                }
                if (toRemove != null) break;
            }

            if (toRemove != null) {
                doc.getDocumentElement().removeChild(toRemove);
                saveDocument(doc, path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveDocument(Document doc, String path) {
        try {
            javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = tf.newTransformer();
            javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(doc);
            javax.xml.transform.stream.StreamResult result =
                new javax.xml.transform.stream.StreamResult(new File(path));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}