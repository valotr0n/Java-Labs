import java.net.URL;
import java.net.HttpURLConnection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.chart.*;;

public class CurrencyClient extends Application{
    
    @Override
    public void start(Stage stage) {
        ComboBox<String> currencyBox = new ComboBox<>();
        currencyBox.getItems().addAll("R01235", "R01239", "R01375");
        currencyBox.setValue("R01235");

        TextField fromField = new TextField();
        fromField.setPromptText("От (дд/мм/гггг)");
        TextField toField = new TextField();
        toField.setPromptText("До (дд/мм/гггг)");

        Button loadButton = new Button("Загрузить");

        TableView<CurrencyRecord> table = new TableView<>();
        TableColumn<CurrencyRecord, String> dateCol = new TableColumn<>("Дата");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDate()));
        TableColumn<CurrencyRecord, String> valueCol = new TableColumn<>("Курс");
        valueCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getValue()));
        table.getColumns().addAll(dateCol, valueCol);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Дата");
        yAxis.setLabel("Курс");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Динамика");
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        loadButton.setOnAction(event -> {
            String currency = currencyBox.getValue();
            String from = fromField.getText().replace(".","/");
            String to = toField.getText().replace(".", "/");

            String urlStr = "http://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=" + from + "&date_req2=" + to + "&VAL_NM_RQ=" + currency;

            table.getItems().clear();

            try {
                URL url = new URL(urlStr);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(con.getInputStream());
                NodeList records = doc.getElementsByTagName("Record");

                for (int i = 0; i < records.getLength(); i++) {
                    Element record = (Element) records.item(i);
                    String date = record.getAttribute("Date");
                    String value = record.getElementsByTagName("Value").item(0).getTextContent();
                    table.getItems().add(new CurrencyRecord(date,value));
                    series.getData().add(new XYChart.Data<>(date, Double.parseDouble(value.replace(",", "."))));
                }
                chart.getData().clear();
                chart.getData().add(series);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });

        VBox root = new VBox(10, currencyBox, fromField, toField, loadButton, table,chart);
        stage.setTitle("Currency");
        stage.setScene(new Scene(root, 600,500));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

