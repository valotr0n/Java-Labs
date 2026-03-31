package First.View3d;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Malecule3dView extends Application {

    private List<Atom> currentAtoms = new ArrayList<>();
    private Group moleculeGroup = new Group();
    private Rotate rotateX = new Rotate(-20, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(-20, Rotate.Y_AXIS);
    private SubScene subScene;
    private PerspectiveCamera camera;

    private double mouseStartX;
    private double mouseStartY;
    private double startAngleX;
    private double startAngleY;

    private final Map<String, Color> customColors = new HashMap<>();

    @Override
    public void start(Stage window) {
        BorderPane root = new BorderPane();

        Group world = new Group(moleculeGroup);
        world.getTransforms().addAll(rotateX, rotateY);

        subScene = new SubScene(world, 600, 600, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BLACK);

        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000);
        camera.setTranslateZ(-500);
        subScene.setCamera(camera);

        root.setCenter(subScene);

        Button openButton = new Button("Открыть .xyz");
        Button saveButton = new Button("Сохранить картинку");
        Slider scaleSlider = new Slider();
        Label scaleLabel = new Label("Масштаб");

        scaleSlider.setMin(0.3);
        scaleSlider.setMax(2.5);
        scaleSlider.setValue(1.0);

        scaleSlider.valueProperty().addListener((obs, oldV, newV) -> {
            double s = newV.doubleValue();
            moleculeGroup.setScaleX(s);
            moleculeGroup.setScaleY(s);
            moleculeGroup.setScaleZ(s);
        });

        Label colorLabel = new Label("Цвет элемента (для выбранного символа):");
        TextField elementField = new TextField();
        elementField.setPromptText("например: H, C, O...");
        ColorPicker colorPicker = new ColorPicker(Color.LIGHTBLUE);
        Button applyColorBtn = new Button("Применить цвет");

        applyColorBtn.setOnAction(e -> {
            String el = elementField.getText();
            if (el == null) return;
            el = el.trim();
            if (el.isEmpty()) return;

            customColors.put(el.toUpperCase(Locale.ROOT), colorPicker.getValue());
            if (currentAtoms != null && !currentAtoms.isEmpty()) {
                renderMolecule(currentAtoms);
            }
        });

        VBox leftBar = new VBox(8,openButton,saveButton,scaleLabel,scaleSlider,new Separator(),colorLabel,elementField,colorPicker, applyColorBtn);
        leftBar.setPrefWidth(240);
        leftBar.setPadding(new Insets(8));
        root.setLeft(leftBar);

        openButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите .xyz файл");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XYZ files", "*.xyz"));
            File file = fileChooser.showOpenDialog(window);
            if (file == null) {
                return;
            }
            try {
                List<Atom> atoms = readXyz(file);
                currentAtoms = atoms;
                renderMolecule(currentAtoms);
            } catch (IOException e) {
                showError("Ошибка чтения xyz", e.getMessage());
                e.printStackTrace();
            }
        });

        saveButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить картинку");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PNG files", "*.png"),
                    new FileChooser.ExtensionFilter("JPG files", "*.jpg"),
                    new FileChooser.ExtensionFilter("GIF files", "*.gif")
            );
            fileChooser.setInitialFileName("molecule.png");
            File file = fileChooser.showSaveDialog(window);
            if (file == null) {
                return;
            }

            String name = file.getName().toLowerCase(Locale.ROOT);
            String format;
            if (name.endsWith(".png")) format = "png";
            else if (name.endsWith(".jpg")) format = "jpg";
            else if (name.endsWith(".gif")) format = "gif";
            else {
                format = "png";
                file = new File(file.getParentFile(), file.getName() + ".png");
            }

            WritableImage image = root.snapshot(null, null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), format, file);
            } catch (IOException e) {
                showError("Ошибка сохранения", e.getMessage());
                e.printStackTrace();
            }
        });

        subScene.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            mouseStartX = e.getSceneX();
            mouseStartY = e.getSceneY();
            startAngleX = rotateX.getAngle();
            startAngleY = rotateY.getAngle();
        });

        subScene.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            double dx = e.getSceneX() - mouseStartX;
            double dy = e.getSceneY() - mouseStartY;

            rotateY.setAngle(startAngleY + dx * 0.6);
            rotateX.setAngle(startAngleX - dy * 0.6);
        });

        Scene view = new Scene(root, 800, 600);
        window.setTitle("3d Molecule Viewer");
        window.setScene(view);
        window.show();
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(msg == null ? "" : msg);
        alert.showAndWait();
    }

    private List<Atom> readXyz(File file) throws IOException {
        List<Atom> atoms = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String firstLine = reader.readLine();
            if (firstLine == null) {
                throw new IOException("Файл пуст");
            }
            int expectedCount;
            try {
                expectedCount = Integer.parseInt(firstLine.trim());
            } catch (NumberFormatException e) {
                throw new IOException("Первая строка должна быть числом");
            }

            reader.readLine();

            String line;
            int lineNumber = 2;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                String parts[] = trimmed.split("\\s+");
                if (parts.length < 4) {
                    throw new IOException("Некорректная строка: " + lineNumber);
                }

                String element = parts[0];
                double x;
                double y;
                double z;
                try {
                    x = Double.parseDouble(parts[1]);
                    y = Double.parseDouble(parts[2]);
                    z = Double.parseDouble(parts[3]);
                } catch (NumberFormatException e) {
                    throw new IOException("Некорректные координаты в строке: " + lineNumber);
                }

                atoms.add(new Atom(element, x, y, z));
            }

            if (atoms.size() != expectedCount) {
                throw new IOException("Ожидалось атомов: " + expectedCount + ", прочитано: " + atoms.size());
            }
        }
        return atoms;
    }

    private void renderMolecule(List<Atom> atoms) {
        moleculeGroup.getChildren().clear();
        if (atoms == null || atoms.isEmpty()) return;

        double cx = atoms.stream().mapToDouble(a -> a.x).average().orElse(0);
        double cy = atoms.stream().mapToDouble(a -> a.y).average().orElse(0);
        double cz = atoms.stream().mapToDouble(a -> a.z).average().orElse(0);

        double scale = 45.0;

        List<Point3D> points = new ArrayList<>();
        List<String> elements = new ArrayList<>();

        for (Atom a : atoms) {
            double x = (a.x - cx) * scale;
            double y = -(a.y - cy) * scale;
            double z = (a.z - cz) * scale;

            Point3D p = new Point3D(x, y, z);
            points.add(p);
            elements.add(a.element == null ? "" : a.element);

            Sphere sphere = new Sphere(getAtomRadius(a.element));
            sphere.setTranslateX(x);
            sphere.setTranslateY(y);
            sphere.setTranslateZ(z);

            Color col = getColorForElement(a.element);
            sphere.setMaterial(new PhongMaterial(col));

            moleculeGroup.getChildren().add(sphere);
        }

        double threshold = 1.8;

        for (int i = 0; i < atoms.size(); i++) {
            for (int j = i + 1; j < atoms.size(); j++) {
                double d = atoms.get(i).point().distance(atoms.get(j).point());
                if (d <= threshold) {
                    Node bond = createBond(points.get(i), points.get(j), 2.0);
                    moleculeGroup.getChildren().add(bond);
                }
            }
        }
    }

    private Color getColorForElement(String el) {
        if (el == null) return Color.LIGHTBLUE;
        String key = el.toUpperCase(Locale.ROOT);
        if (customColors.containsKey(key)) {
            return customColors.get(key);
        }
        return getAtomColor(el);
    }

    private Node createBond(Point3D p1, Point3D p2, double radius) {
        Point3D diff = p2.subtract(p1);
        double h = diff.magnitude();

        Cylinder c = new Cylinder(radius, h);
        c.setMaterial(new PhongMaterial(Color.LIGHTGRAY));

        Point3D mid = p1.midpoint(p2);
        c.setTranslateX(mid.getX());
        c.setTranslateY(mid.getY());
        c.setTranslateZ(mid.getZ());

        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D axis = yAxis.crossProduct(diff);
        double angle = Math.toDegrees(Math.acos(yAxis.normalize().dotProduct(diff.normalize())));

        if (axis.magnitude() > 1e-6 && !Double.isNaN(angle)) {
            c.getTransforms().add(new Rotate(angle, axis));
        } else if (diff.getY() < 0) {
            c.getTransforms().add(new Rotate(180, Rotate.X_AXIS));
        }

        return c;
    }

    private Color getAtomColor(String el) {
        return switch (el == null ? "" : el.toUpperCase(Locale.ROOT)) {
            case "H" -> Color.WHITE;
            case "C" -> Color.GREY;
            case "O" -> Color.RED;
            case "N" -> Color.DEEPSKYBLUE;
            case "S" -> Color.YELLOW;
            default -> Color.LIGHTBLUE;
        };
    }

    private double getAtomRadius(String el) {
        return switch (el == null ? "" : el.toUpperCase(Locale.ROOT)) {
            case "H" -> 7;
            case "C", "N", "O" -> 10;
            case "S" -> 12;
            default -> 9;
        };
    }

    public static void main(String[] args) {
        launch(Malecule3dView.class);
    }
}