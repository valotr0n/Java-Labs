package First;

import java.io.File;
import javax.imageio.ImageIO;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.*;

public class GraphicEditor extends Application{

    private BorderPane root;
    private Pane canvas;
    private ToggleGroup toggleGroup;
    private TextField thinesField;
    private ComboBox<String> lineType;
    private TextField imageSizeWidth;
    private TextField imageSizeHeight;
    private ColorPicker strokeColorPicker;
    private ColorPicker fillColorPicker;
    private Rectangle frame = new Rectangle();
    private Shape curShape;
    private String shapeType;
    private double frameX;
    private double frameY;
    private double frameW;
    private double frameH;
    private double getShapeMinX(Shape s) {
        return s.getBoundsInParent().getMinX();
    }
    private double getShapeMinY(Shape s) {
        return s.getBoundsInParent().getMinY();
    }
    private double getShapeWidth(Shape s) {
        return s.getBoundsInParent().getWidth();
    }
    private double getShapeHeight(Shape s) {
        return s.getBoundsInParent().getHeight();
    }


    @Override
    public void start(Stage window) {
        root = new BorderPane();
        canvas = new Pane();
        canvas.setPrefSize(600,400);
        toggleGroup = new ToggleGroup();
        root.setCenter(canvas);

        VBox palleteBox = new VBox(10);
        palleteBox.setPrefWidth(120);
        palleteBox.setStyle("-fx-padding: 10; -fx-background-color: white;");
        palleteBox.setAlignment(Pos.TOP_CENTER);

        Menu menuFile = new Menu("Файл");
        Menu menuInstruction = new Menu("Помощь");
        MenuItem savItem = new MenuItem("Сохранить");
        MenuItem exitItem = new MenuItem("Выйти");
        menuFile.getItems().addAll(savItem,exitItem);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menuFile,menuInstruction);
        savItem.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Png", "*.png"));
            File file = fileChooser.showSaveDialog(root.getScene().getWindow());
            if (file != null) {
                try {
                    int w = (int) getDoubleValue(imageSizeWidth, 800);
                    int h = (int) getDoubleValue(imageSizeHeight, 400);
                    SnapshotParameters param = new SnapshotParameters();
                    param.setFill(Color.WHITE);

                    double sx = w / canvas.getWidth();
                    double sy = h / canvas.getHeight();
                    param.setTransform(new javafx.scene.transform.Scale(sx,sy));
                    WritableImage wImage = new WritableImage(w,h);
                    canvas.snapshot(param, wImage);
                    ImageIO.write(javafx.embed.swing.SwingFXUtils.fromFXImage(wImage, null), "png", file);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        exitItem.setOnAction(event -> {
            System.exit(0);
        });
        root.setTop(menuBar);
        

        Label strokeColorLabel = new Label("Цвет Контура");
        Label fillColorLabel = new Label("Цвет Заливки");
        Label strokeThiknesLabel = new Label("Толщина");
        Label lineTypeLabel = new Label("Тип");
        Label imageSizeWidthLabel = new Label("Ширина");
        Label imageSizeHeightLable = new Label("Высота");
        
        Line lineIcon = new Line(0,0,15,15);
        Circle circleIcon = new Circle(8);
        Ellipse ellipseIcon = new Ellipse(10,6);
        Rectangle rectangleIcon = new Rectangle(15,12);

        ToggleButton LineBut = creaIconButton(toggleGroup, lineIcon);
        LineBut.setUserData("LINE");
        ToggleButton CircleBut = creaIconButton(toggleGroup, circleIcon);
        CircleBut.setUserData("CIRCLE");
        ToggleButton EllipseBut = creaIconButton(toggleGroup, ellipseIcon);
        EllipseBut.setUserData("ELLIPSE");
        ToggleButton RectangleBut = creaIconButton(toggleGroup, rectangleIcon);
        RectangleBut.setUserData("RECTANGLE");
        palleteBox.getChildren().addAll(LineBut, CircleBut, EllipseBut, RectangleBut);

        TitledPane palletePane = new TitledPane("Палитра", palleteBox);
        palletePane.setCollapsible(false);

        strokeColorPicker = new ColorPicker(Color.BLACK);
        fillColorPicker = new ColorPicker(Color.TRANSPARENT);
        GridPane colorsGrid = new GridPane();
        colorsGrid.setHgap(8);
        colorsGrid.setVgap(8);
        colorsGrid.addRow(0, strokeColorLabel, strokeColorPicker);
        colorsGrid.addRow(1, fillColorLabel, fillColorPicker);

        TitledPane colorsPane = new TitledPane("Цвета", colorsGrid);
        colorsPane.setCollapsible(false);
        colorsPane.setMaxWidth(Double.MAX_VALUE);

        thinesField = new TextField("1.0");
        thinesField.setPrefColumnCount(5);

        lineType = new ComboBox<>();
        lineType.getItems().addAll("Solid", "Dash", "Dot");
        lineType.setValue("Solid");

        GridPane strokeGrid = new GridPane();
        strokeGrid.setHgap(8);
        strokeGrid.setVgap(8);
        strokeGrid.addRow(0, strokeThiknesLabel, thinesField);
        strokeGrid.addRow(1, lineTypeLabel, lineType);

        TitledPane strokePane = new TitledPane("Контур", strokeGrid);
        strokePane.setCollapsible(false);
        strokePane.setMaxWidth(Double.MAX_VALUE);

        imageSizeWidth = new TextField();
        imageSizeWidth.setPrefColumnCount(5);

        imageSizeHeight = new TextField();
        imageSizeHeight.setPrefColumnCount(5);

        GridPane imageSize = new GridPane();
        imageSize.setHgap(8);
        imageSize.setVgap(8);
        imageSize.addRow(0, imageSizeWidthLabel, imageSizeWidth);
        imageSize.addRow(1, imageSizeHeightLable, imageSizeHeight);

        TitledPane imageSizePane = new TitledPane("Размер", imageSize);
        imageSizePane.setCollapsible(false);
        imageSizePane.setMaxWidth(Double.MAX_VALUE);

        VBox leftCol = new VBox(); 
        leftCol.getChildren().add(palletePane);

        VBox rightCol = new VBox();
        rightCol.getChildren().addAll(colorsPane, strokePane, imageSizePane);
        rightCol.setFillWidth(true);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(leftCol,rightCol);
        hBox.setAlignment(Pos.TOP_CENTER);
        
        VBox sidebar = new VBox(hBox);
        sidebar.setStyle("-fx-padding: 10; -fx-background-color: #f5f5f5;");
        root.setRight(sidebar);

        canvas.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.SECONDARY) {
                return;
            }
            if (toggleGroup.getSelectedToggle() == null) {
                return;
            }
            Shape newShape = createShape(event.getX(), event.getY());
            if (newShape != null) {
                canvas.getChildren().add(newShape);
                curShape = newShape;
                shapeType = (String) toggleGroup.getSelectedToggle().getUserData();
                frameX = getShapeMinX(curShape);
                frameY = getShapeMinY(curShape);
                frameW = getShapeWidth(curShape);
                frameH = getShapeHeight(curShape);
                updateView();
            }
        });

        strokeColorPicker.setOnAction(event ->{
            applyStyleToCurrnet();
        });
        fillColorPicker.setOnAction(event ->{
            applyStyleToCurrnet();
        });
        lineType.setOnAction(event ->{
            applyStyleToCurrnet();
        });
        thinesField.setOnAction(event ->{
            applyStyleToCurrnet();
        });

        Scene view = new Scene(root, 800, 400);
        window.setTitle("Graphic Editor");
        window.setScene(view);
        window.show();

        view.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W:
                    frameY -= 10;
                    break;
                case S:
                    frameY += 10;
                    break;
                case A:
                    frameX -= 10;
                    break;
                case D:
                    frameX += 10;
                    break;
                case PLUS, ADD, EQUALS:
                    frameH += 10;
                    break;
                case MINUS, SUBTRACT:
                    frameH -= 10;
                    break;
                case GREATER, PERIOD:
                    frameW += 10;
                    break;
                case LESS, COMMA:
                    frameW -= 10;
                    break;
                default:
                    return;
            }
            updateView();
        });
        canvas.requestFocus();
    }

    private ToggleButton creaIconButton(ToggleGroup group, Shape shape) {
        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setToggleGroup(group);
        shape.setFill(Color.TRANSPARENT);
        shape.setStroke(Color.BLACK);
        shape.setStrokeWidth(2);
        toggleButton.setGraphic(shape);
        toggleButton.setPrefHeight(50);
        toggleButton.setAlignment(Pos.CENTER_LEFT);
        toggleButton.setGraphicTextGap(10);
        return toggleButton;
    }
    private Shape createShape(double x, double y) {
        Toggle selected = toggleGroup.getSelectedToggle();
        if (selected == null) {
            return null;
        }
        Shape shape = null;
        String type = (String) selected.getUserData();
        double w = getDoubleValue(imageSizeWidth, 50);
        double h = getDoubleValue(imageSizeHeight, 50);

        switch (type) {
            case "LINE":
                shape = new Line(x,y,x+w,y+h);
                break;
            case "CIRCLE":
                shape = new Circle(x,y,w/2);
                break;
            case "ELLIPSE":
                shape = new Ellipse(x,y,w/2,h/2);
                break;
            case "RECTANGLE":
                shape = new Rectangle(x,y,w,h);
                break;
            default:
                break;
        }

        if (shape != null) {
            shape.setStroke(strokeColorPicker.getValue());
            double thickness = getDoubleValue(thinesField, 1.0);
            shape.setStrokeWidth(thickness);

            if(!(shape instanceof Line)) {
                shape.setFill(fillColorPicker.getValue());
            }
            applyLineStyle(shape, lineType.getValue());
        }
        return shape;
    }

    private void applyStyleToCurrnet() {
        curShape.setStroke(strokeColorPicker.getValue());
        curShape.setStrokeWidth(getDoubleValue(thinesField, 1.0));
        applyLineStyle(curShape, lineType.getValue());

        if (!(curShape instanceof Line)) {
            curShape.setFill(fillColorPicker.getValue());
        }
    }

    private double getDoubleValue(TextField field, double defaultValue) {
        try {
            return Double.parseDouble(field.getText());
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    private void applyLineStyle(Shape shape, String style) {
        shape.getStrokeDashArray().clear();
        if ("Dash".equals(style)) {
            shape.getStrokeDashArray().addAll(10.0,10.0);
        }
        if ("Dot".equals(style)) {
            shape.getStrokeDashArray().addAll(2.0, 10.0);
        }
    }

    private void updateView() {
        
        if(!canvas.getChildren().contains(frame)) {
            frame.setFill(Color.TRANSPARENT);
            frame.setStroke(Color.BLACK);
            frame.setStrokeWidth(2);
            canvas.getChildren().add(frame);
        }

        frame.setX(frameX);
        frame.setY(frameY);
        frame.setWidth(frameW);
        frame.setHeight(frameH);
        switch (shapeType) {
            case "RECTANGLE":
                Rectangle r = (Rectangle) curShape;
                r.setX(frameX);
                r.setY(frameY);
                r.setWidth(frameW);
                r.setHeight(frameH);
                break;
            case "CIRCLE":
                Circle c = (Circle) curShape;
                c.setCenterX(frameX + frameW / 2);
                c.setCenterY(frameY + frameH / 2);
                c.setRadius(Math.min(frameW, frameH) / 2);
                break;
            case "ELLIPSE":
                Ellipse e = (Ellipse) curShape;
                e.setCenterX(frameX + frameW / 2);
                e.setCenterY(frameY + frameH / 2);
                e.setRadiusX(frameW / 2);
                e.setRadiusY(frameH / 2);
                break;
            case "LINE":
                Line l = (Line) curShape;
                l.setStartX(frameX);
                l.setStartY(frameY);
                l.setEndX(frameX + frameW);
                l.setEndY(frameY + frameH);
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) {
        launch(GraphicEditor.class);
    }
}
