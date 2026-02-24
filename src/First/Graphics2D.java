package First;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.*;
import java.util.Random;

public class Graphics2D extends Application{
    public final double WIDTH = 800;
    public final double HEIGHT = 600;
    private ShapeType curShapeType;
    private Shape shape;
    private Group root = new Group();
    private double frameX;
    private double frameY;
    private double frameW;
    private double frameH;
    private Rectangle frame = new Rectangle();
    private Random random = new Random();

    enum ShapeType {
        RECTANGLE, CIRCLE, ELLIPSE, LINE
    }

    @Override
    public void start(Stage window) {
        createRandomShape();
        frame.setFill(Color.TRANSPARENT);
        frame.setStroke(Color.BLACK);
        frame.setStrokeWidth(5);
        frame.setOpacity(0.5);
        frame.getStrokeDashArray().addAll(10.0,10.0);
        frameX = WIDTH / 2-100;
        frameY = HEIGHT / 2-100;
        frameH = 200;
        frameW = 200;

        root.getChildren().addAll(frame, shape);
        updateView();

        Scene view = new Scene(root,WIDTH,HEIGHT);
        window.setTitle("Second Task");
        window.setScene(view);
        window.show();
        root.requestFocus();

        view.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP:
                    frameY -= 10;
                    break;
                case DOWN:
                    frameY += 10;
                    break;
                case LEFT:
                    frameX -= 10;
                    break;
                case RIGHT:
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
    }

    private Shape createRandomShape() {
        int type = random.nextInt(4);
        switch (type) {
            case 0 -> {
                curShapeType = ShapeType.RECTANGLE;
                shape = new Rectangle(200,200);
            }
            case 1 -> {
                curShapeType = ShapeType.CIRCLE;
                shape = new Circle(100);
            }
            case 2 -> {
                curShapeType = ShapeType.ELLIPSE;
                shape = new Ellipse(150,100);
            }
            case 3 -> {
                curShapeType = ShapeType.LINE;
                shape = new Line(50,50,100,100);
            }
            
            default -> throw new IllegalStateException();
        }
        shape.setFill(Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble()));
        shape.setStroke(Color.BLACK);
        shape.setStrokeWidth(10);

        return shape;
    }
    private void updateView() {
        frame.setX(frameX);
        frame.setY(frameY);
        frame.setWidth(frameW);
        frame.setHeight(frameH);

        switch (curShapeType) {
            case RECTANGLE -> {
                Rectangle rectangle = (Rectangle) shape;
                rectangle.setX(frameX);
                rectangle.setY(frameY);
                rectangle.setWidth(frameW);
                rectangle.setHeight(frameH);
            }
            case CIRCLE -> {
                Circle circle = (Circle) shape;
                circle.setCenterX(frameX + frameW / 2);
                circle.setCenterY(frameY + frameH / 2);
                circle.setRadius(Math.min(frameW, frameH) / 2);
            }
            case ELLIPSE -> {
                Ellipse ellipse = (Ellipse) shape;
                ellipse.setCenterX(frameX + frameW / 2);
                ellipse.setCenterY(frameY + frameH / 2);
                ellipse.setRadiusX(frameW / 2);
                ellipse.setRadiusY(frameH / 2);
            }
            case LINE -> {
                Line line = (Line) shape;
                line.setStartX(frameX);
                line.setStartY(frameY);
                line.setEndX(frameX + frameW);
                line.setEndY(frameY + frameH);
            }
        }
    }



    public static void main(String[] args) {
        launch(Graphics2D.class);
    }

}
