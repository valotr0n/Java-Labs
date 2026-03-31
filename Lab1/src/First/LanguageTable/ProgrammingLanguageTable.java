package First.LanguageTable;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.*;
import javafx.util.converter.IntegerStringConverter;


public class ProgrammingLanguageTable extends Application {

    @Override
    public void start(Stage window) {
        HBox controls = new HBox(10);
        BorderPane root = new BorderPane();
        TableView<Language> tableView = new TableView<>();
        tableView.setEditable(true);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Language, String> columnName = new TableColumn<>("Язык");
        columnName.setCellValueFactory(data -> data.getValue().nameProperty());
        columnName.setCellFactory(TextFieldTableCell.forTableColumn());
        columnName.setOnEditCommit(event -> {
            event.getRowValue().nameProperty().set(event.getNewValue());
        });
        CheckBox showName = new CheckBox("Язык");
        showName.setSelected(true);
        columnName.visibleProperty().bind(showName.selectedProperty());

        TableColumn<Language, String> columnAuthor = new TableColumn<>("Автор");
        columnAuthor.setCellValueFactory(data -> data.getValue().authorProperty());
        columnAuthor.setCellFactory(TextFieldTableCell.forTableColumn());
        columnAuthor.setOnEditCommit(event -> {
            event.getRowValue().authorProperty().set(event.getNewValue());
        });
        CheckBox showAuthor = new CheckBox("Автор");
        showAuthor.setSelected(true);
        columnAuthor.visibleProperty().bind(showAuthor.selectedProperty());


        TableColumn<Language, Integer> columnYear = new TableColumn<>("Год");
        columnYear.setCellValueFactory(data -> data.getValue().yearProperty().asObject());
        columnYear.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        columnYear.setOnEditCommit(event -> {
            event.getRowValue().yearProperty().set(event.getNewValue());
        });
        CheckBox showYear = new CheckBox("Год");
        showYear.setSelected(true);
        columnYear.visibleProperty().bind(showYear.selectedProperty());

        

        tableView.getColumns().add(columnName);
        tableView.getColumns().add(columnAuthor);
        tableView.getColumns().add(columnYear);

        ObservableList<Language> data = FXCollections.observableArrayList(
            new Language("Java", "Джеймс Гослинг", 1995),
            new Language("C++", "Бьёрн Страуструп", 1983),
            new Language("C", "Денис Ритчи", 1972),
            new Language("Python", "Гвидо ван Россум", 1991)
        );
        tableView.setItems(data);
        
        Button addBuuton = new Button("Добавить");
        addBuuton.setOnAction(event -> {
            data.add(new Language("Новый язык", "Автор", 0000));
        });
        
        controls.getChildren().addAll(addBuuton,showName,showAuthor,showYear);
        root.setCenter(tableView);
        root.setBottom(controls);
        Scene view = new Scene(root,800,600);
        window.setScene(view);
        window.setTitle("Programming Language Table");
        window.show();
    }

    public static void main(String[] args) {
        launch(ProgrammingLanguageTable.class);
    }
}
