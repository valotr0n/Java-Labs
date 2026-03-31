package First.ChartsSceneBuilder;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Controller {

    @FXML private TextField xMinField;
    @FXML private TextField xMaxField;
    @FXML private TextField stepField;
    @FXML private TextField widthField;
    @FXML private LineChart<Number, Number> chart;
    @FXML private ComboBox<String> functionCombo;
    @FXML private RadioButton showRadio;
    @FXML private RadioButton hideRadio;

    private static final List<String> FUNCTIONS = List.of(
        "y(x)=sin(x)",
        "y(x)=cos(x)",
        "y(x)=cos(x)-2*sin(x)",
        "y(x)=sin(x*x)",
        "y(x)=tan(x)",
        "y(x)=x*x"
    );

    // Функция -> серия, отображаемая на графике
    private final Map<String, XYChart.Series<Number, Number>> seriesMap = new LinkedHashMap<>();
    // Функция -> настройки [xMin, xMax, step, width]
    private final Map<String, double[]> settingsMap = new LinkedHashMap<>();

    @FXML
    public void initialize() {
        functionCombo.getItems().addAll(FUNCTIONS);
        functionCombo.setValue(FUNCTIONS.get(0));
        functionCombo.setOnAction(e -> loadSettings());
    }

    private void loadSettings() {
        String func = functionCombo.getValue();
        if (settingsMap.containsKey(func)) {
            double[] s = settingsMap.get(func);
            xMinField.setText(String.valueOf(s[0]));
            xMaxField.setText(String.valueOf(s[1]));
            stepField.setText(String.valueOf(s[2]));
            widthField.setText(String.valueOf((int) s[3]));
        }
        boolean visible = seriesMap.containsKey(func);
        showRadio.setSelected(visible);
        hideRadio.setSelected(!visible);
    }

    @FXML
    private void buildChart() {
        String funcName = functionCombo.getValue();
        double xMin   = Double.parseDouble(xMinField.getText());
        double xMax   = Double.parseDouble(xMaxField.getText());
        double step   = Double.parseDouble(stepField.getText());
        int    width  = Integer.parseInt(widthField.getText());
        boolean visible = showRadio.isSelected();

        settingsMap.put(funcName, new double[]{xMin, xMax, step, width});

        chart.getData().removeIf(s -> s.getName().equals(funcName));
        seriesMap.remove(funcName);

        if (!visible) return;


        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(funcName);
        for (double x = xMin; x <= xMax + step / 2; x += step) {
            double y = evaluate(funcName, x);
            if (Double.isFinite(y))
                series.getData().add(new XYChart.Data<>(x, y));
        }

        chart.getData().add(series);
        seriesMap.put(funcName, series);

        chart.applyCss();
        chart.layout();
        int idx = chart.getData().indexOf(series);
        var node = chart.lookup(".default-color" + (idx % 8) + ".chart-series-line");
        if (node != null)
            node.setStyle("-fx-stroke-width: " + width + "px;");
    }

    private double evaluate(String funcName, double x) {
        return switch (funcName) {
            case "y(x)=sin(x)"          -> Math.sin(x);
            case "y(x)=cos(x)"          -> Math.cos(x);
            case "y(x)=cos(x)-2*sin(x)" -> Math.cos(x) - 2 * Math.sin(x);
            case "y(x)=sin(x*x)"        -> Math.sin(x * x);
            case "y(x)=tan(x)"          -> Math.tan(x);
            case "y(x)=x*x"             -> x * x;
            default                      -> 0;
        };
    }
}
