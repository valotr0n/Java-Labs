package First;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.io.File;

public class ImagePallet extends Application {
    private ToggleGroup toggleGroup;
    private Pane canvas;

    @Override
    public void start(Stage window) {
        BorderPane root = new BorderPane();
        toggleGroup = new ToggleGroup();
        VBox pallete = new VBox(10);
        pallete.setPrefWidth(120);
        pallete.setStyle("-fx-padding: 10; -fx-background-color: white;");
        
        Button saveButton = new Button("Save");
        pallete.getChildren().add(createImageBut("/img/img1.jpg"));
        pallete.getChildren().add(createImageBut("/img/img2.jpg"));
        pallete.getChildren().add(createImageBut("/img/img3.jpg"));
        pallete.getChildren().add(createImageBut("/img/img4.jpg"));


        HBox hbox = new HBox(saveButton);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        root.setLeft(pallete);
        root.setBottom(hbox);

        canvas = new Pane();
        canvas.setPrefSize(800, 600);
        canvas.setStyle("-fx-background-color: white; -fx-border-color: black");
        root.setCenter(canvas);

        canvas.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                Toggle selected = toggleGroup.getSelectedToggle();

                if (selected != null) {
                    Image img = (Image) selected.getUserData();
                    ImageView imageView = new ImageView(img);
                    imageView.setFitWidth(64);
                    imageView.setFitHeight(64);
                    imageView.setPreserveRatio(true);

                    imageView.setLayoutX(event.getX() - 32);
                    imageView.setLayoutY(event.getY() - 32);

                    canvas.getChildren().add(imageView);
                }
            }
        });

        saveButton.setOnAction(event -> {
            WritableImage snapshot = canvas.snapshot(new SnapshotParameters(), null);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Png Image", "*.png"));

            File file = fileChooser.showSaveDialog(window);

            if (file != null) {
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
                    System.out.println("Успешно сохранено: " + file.getAbsolutePath());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        Scene view = new Scene(root, 800, 600);
        window.setTitle("First Task");
        window.setScene(view);
        window.show();
    }

    public final ToggleButton createImageBut(String path) {
        Image img = new Image(getClass().getResourceAsStream(path));

        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(64);
        imageView.setFitHeight(64);
        imageView.setPreserveRatio(true);

        ToggleButton tgButton = new ToggleButton();
        tgButton.setGraphic(imageView);
        tgButton.setToggleGroup(toggleGroup);
        tgButton.setUserData(img);

        return tgButton;
    }

    public static void main(String[] args) {
        launch(ImagePallet.class);
    }

    
}
