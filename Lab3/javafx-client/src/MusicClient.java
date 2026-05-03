package client;

import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class MusicClient extends Application {

    private TableView<MusicRecord> table = new TableView<>();
    private ObservableList<MusicRecord> data = FXCollections.observableArrayList();
    private TextField filterField = new TextField();
    private TextField titleField = new TextField();
    private TextField artistField = new TextField();
    private TextField genreField = new TextField();
    private TextField yearField = new TextField();
    private Label statusLabel = new Label();

    private static final String BASE_URL = "http://localhost:8080/lab3/Music";

    @Override
    public void start(Stage stage) {
        // Таблица
        TableColumn<MusicRecord, String> titleCol = new TableColumn<>("Название");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<MusicRecord, String> artistCol = new TableColumn<>("Исполнитель");
        artistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));
        TableColumn<MusicRecord, String> genreCol = new TableColumn<>("Жанр");
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        TableColumn<MusicRecord, String> yearCol = new TableColumn<>("Год");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));

        table.getColumns().addAll(titleCol, artistCol, genreCol, yearCol);
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Фильтр
        filterField.setPromptText("Жанр для фильтра");
        Button filterBtn = new Button("Найти");
        Button resetBtn = new Button("Сбросить");
        filterBtn.setOnAction(e -> loadSongs(filterField.getText()));
        resetBtn.setOnAction(e -> { filterField.clear(); loadSongs(""); });
        HBox filterBox = new HBox(5, filterField, filterBtn, resetBtn);

        // Форма добавления
        titleField.setPromptText("Название");
        artistField.setPromptText("Исполнитель");
        genreField.setPromptText("Жанр");
        yearField.setPromptText("Год");
        Button addBtn = new Button("Добавить");
        addBtn.setOnAction(e -> addSong());
        HBox addBox = new HBox(5, titleField, artistField, genreField, yearField, addBtn);

        // Удаление
        Button deleteBtn = new Button("Удалить выбранное");
        deleteBtn.setOnAction(e -> deleteSong());

        VBox root = new VBox(10,
            new Label("Фильтр по жанру:"), filterBox,
            table,
            deleteBtn,
            new Label("Добавить песню:"), addBox,
            statusLabel
        );
        root.setPadding(new Insets(10));

        stage.setScene(new Scene(root, 800, 500));
        stage.setTitle("Музыкальная библиотека");
        stage.show();

        loadSongs("");
    }

    private void loadSongs(String genre) {
        data.clear();
        try {
            String url = BASE_URL + (genre.isEmpty() ? "" : "?genre=" + URLEncoder.encode(genre, "UTF-8"));
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) sb.append(line);
            in.close();

            String html = sb.toString();
            String[] rows = html.split("<tr>");
            for (int i = 2; i < rows.length; i++) { 
                String row = rows[i];
                String[] cells = row.split("<td>");
                if (cells.length >= 5) {
                    String title  = cells[1].split("</td>")[0].trim();
                    String artist = cells[2].split("</td>")[0].trim();
                    String gn     = cells[3].split("</td>")[0].trim();
                    String year   = cells[4].split("</td>")[0].trim();
                    data.add(new MusicRecord(title, artist, gn, year));
                }
            }
            statusLabel.setText("Загружено: " + data.size() + " песен");
        } catch (Exception e) {
            statusLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    private void addSong() {
        try {
            String params = "action=add"
                + "&title=" + URLEncoder.encode(titleField.getText(), "UTF-8")
                + "&artist=" + URLEncoder.encode(artistField.getText(), "UTF-8")
                + "&genre=" + URLEncoder.encode(genreField.getText(), "UTF-8")
                + "&year=" + URLEncoder.encode(yearField.getText(), "UTF-8");

            HttpURLConnection con = (HttpURLConnection) new URL(BASE_URL).openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setConnectTimeout(5000);

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(params.getBytes(StandardCharsets.UTF_8));
            }

            con.getResponseCode();
            titleField.clear(); artistField.clear();
            genreField.clear(); yearField.clear();
            loadSongs("");
            statusLabel.setText("Песня добавлена!");
        } catch (Exception e) {
            statusLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    private void deleteSong() {
        MusicRecord selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Выберите песню для удаления!");
            return;
        }
        try {
            String params = "action=delete&title=" + URLEncoder.encode(selected.getTitle(), "UTF-8");
            HttpURLConnection con = (HttpURLConnection) new URL(BASE_URL).openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setConnectTimeout(5000);

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(params.getBytes(StandardCharsets.UTF_8));
            }

            con.getResponseCode();
            loadSongs("");
            statusLabel.setText("Песня удалена!");
        } catch (Exception e) {
            statusLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    public static void main(String[] args) { launch(args); }
}