package main;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import main.shapes.Sphere;
import main.util.Vector3;

public class OutputController {

    @FXML
    public Canvas canvas;

    private GraphicsContext gc;


    @FXML
    private void initialize(){
        canvas.setWidth(Main.WIDTH);
        canvas.setHeight(Main.HEIGHT);
        gc = canvas.getGraphicsContext2D();

        Camera camera = new Camera(new Vector3(0,0,-5), new Vector3(0,0,0));

        Scene scene = new Scene();
        scene.addShape(new Sphere(1, new Vector3(0,0,0), Color.GREEN));

        RayTracer rt = new RayTracer(camera, scene, this);
        rt.trace();

    }

    public void fillCanvas(GraphicsContext gc){
        gc.setFill(Color.BLUE);
        gc.fillRect(0,0,canvas.getWidth(), canvas.getHeight());
    }

    public void writePixel(int x, int y, Color c){
        PixelWriter pw = gc.getPixelWriter();
        pw.setColor(x,y,c);
    }

}
