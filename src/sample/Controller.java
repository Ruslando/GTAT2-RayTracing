package sample;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Controller {

    @FXML
    public Canvas canvas;

    public GraphicsContext gc;


    @FXML
    private void initialize(){
        canvas.setWidth(Main.WIDTH);
        canvas.setHeight(Main.HEIGHT);
        gc = canvas.getGraphicsContext2D();
        fillCanvas(gc);
    }

    public void fillCanvas(GraphicsContext gc){
        gc.setFill(Color.BLUE);
        gc.fillRect(0,0,canvas.getWidth(), canvas.getHeight());
    }

}
