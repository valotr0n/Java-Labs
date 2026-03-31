package First.ChartsSceneBuilder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Charts extends Application {

    @Override
    public void start(Stage window) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chart-view.fxml"));
        Parent root = loader.load();

        Scene view = new Scene(root, 900, 600);
        window.setTitle("Charts");
        window.setScene(view);
        window.show();
    }

    public static void main(String[] args) {
        launch(Charts.class);
    }
}
