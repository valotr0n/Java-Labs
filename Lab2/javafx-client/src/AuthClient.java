import java.io.*;
import java.net.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AuthClient extends Application{

    @Override
    public void start(Stage stage) {
        TextField loginField = new TextField();
        loginField.setPromptText("Логин");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Пароль");

        ToggleGroup methGroup = new ToggleGroup();
        RadioButton getButton = new RadioButton("GET");
        getButton.setToggleGroup(methGroup);
        getButton.setSelected(true);
        RadioButton postButton = new RadioButton("POST");
        postButton.setToggleGroup(methGroup);

        Button sendButton = new Button("Войти");
        TextArea responseArea = new TextArea();
        responseArea.setEditable(false);

        sendButton.setOnAction(event -> {
            String login = loginField.getText();
            String password = passwordField.getText();

            RadioButton selected = (RadioButton) methGroup.getSelectedToggle();
            String method = selected.getText();

            try {
                String response = sendRequest(login,password,method);
                responseArea.setText(response);
            }
            catch (Exception e) {
                responseArea.setText("Ошибка: " + e.getMessage());
            }
        });

        VBox root = new VBox(10, loginField, passwordField, getButton, postButton, sendButton, responseArea);
        
        stage.setTitle("Auth");
        stage.setScene(new Scene(root, 400, 300));
        stage.show();
    }

    public String sendRequest(String login, String password, String method) throws Exception{
        String urlStr = "http://localhost:8080/Lab2/Auth?login=" + login + "&password=" + password;
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line).append("\n");
        }
        in.close();
        return response.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
