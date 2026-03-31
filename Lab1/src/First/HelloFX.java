package First;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class HelloFX extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        Label text = new Label("Hello");
        text.setFont(new Font(32));
        root.setCenter(text);
        Scene scene = new Scene(root, 400, 150);

        stage.setTitle("First javaFX program");
        stage.setScene(scene);
        stage.show();
    }
}
