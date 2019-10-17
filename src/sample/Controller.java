package sample;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Controller {

    @FXML
    private Canvas canvas;

    private GraphicsContext gc;

    public void drawShapeTest(){

        canvas = new Canvas(Main.WIDTH, Main.HEIGHT);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        gc.strokeLine(40,10,10,40);
    }
}
