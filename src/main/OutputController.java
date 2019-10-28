package main;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import main.shapes.Sphere;
import main.util.Material;
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

        Camera camera = new Camera(new Vector3(0,10,-10), new Vector3(0,0,0));

        Scene scene = new Scene();
        scene.addShape(new Sphere(2, new Vector3(0,0,0), new Material(new Vector3(0,1,0))));
        scene.addShape(new Sphere(0.5, new Vector3(5,3,1), new Material(new Vector3(0,1,0))));
        scene.addShape(new Sphere(1, new Vector3(-3,-3,-3), new Material(new Vector3(0,1,0))));
        scene.addLight(new Light(new Vector3(0,10,-10), 100, new Vector3(255,255,255), true));
        // scene.addLight(new Light(new Vector3(0,10,-10), 1, new Vector3(255,255,255), false));

        RayTracer rt = new RayTracer(camera, scene, this);
        rt.trace();

    }

    public void writePixel(int x, int y, int argb){
        PixelWriter pw = gc.getPixelWriter();
        pw.setArgb(x,y,argb);
    }

}
