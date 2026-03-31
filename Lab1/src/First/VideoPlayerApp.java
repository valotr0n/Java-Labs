package First;

import java.io.File;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.media.*;
import javafx.util.Duration;

public class VideoPlayerApp extends Application{

    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private Button playerButton;
    private Slider time;
    private Slider volume; 
    private Label fileNameLabel;
    private Label totalTimeLabel;
    private Label currentTimeLabel;

    @Override
    public void start(Stage window) {
        BorderPane root = new BorderPane();

        Menu file = new Menu("Файл");
        MenuItem open = new MenuItem("Открыть");
        MenuBar menu = new MenuBar();
        file.getItems().add(open);
        menu.getMenus().add(file);
        root.setTop(menu);

        fileNameLabel = new Label();
        totalTimeLabel = new Label("00:00");
        currentTimeLabel = new Label("00:00");
        mediaView = new MediaView();
        StackPane centerPane = new StackPane(mediaView,fileNameLabel);
        centerPane.setAlignment(fileNameLabel, Pos.TOP_CENTER);
        root.setCenter(centerPane);
        mediaView.fitWidthProperty().bind(centerPane.widthProperty());
        mediaView.fitHeightProperty().bind(centerPane.heightProperty());

        open.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Video Files", "*.mp4"));
            File selectedFile = fileChooser.showOpenDialog(window);
            if (selectedFile != null) {
                openVideo(selectedFile);
            }
            fileNameLabel.setText(selectedFile.getName());
        });


        playerButton = new Button("||");
        time = new Slider();
        volume = new Slider();
        volume.setMin(0);
        volume.setMax(1);
        volume.setValue(0.5);
        volume.setPrefWidth(120);
        time.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(time, Priority.ALWAYS);
        HBox bottomBar = new HBox(10,playerButton,currentTimeLabel,time,totalTimeLabel,volume);
        bottomBar.setPadding(new Insets(8));
        time.setMaxWidth(Double.MAX_VALUE);
        root.setBottom(bottomBar);


        playerButton.setOnAction(event -> {
            MediaPlayer.Status status = mediaPlayer.getStatus();
            if (status == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playerButton.setText(">");
            }
            else {
                mediaPlayer.play();
                playerButton.setText("||");
            }
        });

        Scene view = new Scene(root,800,600);
        window.setScene(view);
        window.setTitle("Video Player");
        window.show();
    }

    private void openVideo(File file) {
        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.volumeProperty().bind(volume.valueProperty());
        mediaPlayer.play();

        mediaPlayer.setOnReady(() -> {
            time.setMax(mediaPlayer.getTotalDuration().toSeconds());
            totalTimeLabel.setText(formatTime(mediaPlayer.getTotalDuration()));
        });

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!time.isValueChanging()) {
                time.setValue(newTime.toSeconds());
            }
            currentTimeLabel.setText(formatTime(newTime));
        });

        time.valueChangingProperty().addListener((obs,wasChanging,isChanging) -> {
            if(!isChanging) {
                mediaPlayer.seek(Duration.seconds(time.getValue()));
            }
        });

        time.setOnMouseReleased(event -> {
            mediaPlayer.seek(Duration.seconds(time.getValue()));
        });
    }

    private String formatTime(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) duration.toSeconds() % 60;
        return String.format("%02d:%02d", minutes,seconds);
    }
    public static void main(String[] args) {
        launch(VideoPlayerApp.class);
    }
}
