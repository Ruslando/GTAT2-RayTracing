package main;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import main.shapes.*;
import main.shader.Material;
import main.util.Matrix4;
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

        double[][] testinput = new double[4][4];
        testinput[0][0] = 1;
        testinput[1][0] = 1;
        testinput[2][0] = 1;
        testinput[3][0] = 1;

        Matrix4 test = new Matrix4(testinput);
        Matrix4 testTransposed = test.getTransposed();


        Camera camera = new Camera(new Vector3(5,0,-7), new Vector3(0,0,0));
        Material standardMaterial = new Material(new Vector3(1,0,0), 0.1, 0.5);
        Material standardMaterial2 = new Material(new Vector3(0,1,0), 0.1, 0.5);
        Material transparentMaterial1 = new Material(1.3);
        Material transparentMaterial2 = new Material(1.7);

        Shape sphere = new Sphere(3, new Vector3(0,0,0) , transparentMaterial1);
        Shape sphere2 = new Sphere(1, new Vector3(0,0, 0) , transparentMaterial2);
        Shape sphere3 = new Sphere(2, new Vector3(0,3, 6) , standardMaterial2);
        //Shape sphere3 = new Sphere(2, new Vector3(0,0,0) , transparentMaterial2);
        //Shape sphere2 = new QuadricSphere(material);

        /*Quadric zylinder = new Zylinder(1);
        Quadric zylinder2 = new CustomQuadric(1,0,1,0,0,0,0,0,0,Math.sqrt(1) * -1, material);
        Differenz v = new Differenz(zylinder2, zylinder);
        Quadric zylinder3 = new CustomQuadric(1,1,0,0,0,0,0,0,0,Math.sqrt(1) * -1, material);
        Vereinigung v2 = new Vereinigung(v, zylinder3);*/



        //Differenz v = new Differenz(zylinder, sphere);

        Scene scene = new Scene();
        scene.addShape(sphere);
        scene.addShape(sphere2);
        scene.addShape(sphere3);

        scene.addLight(new Light(new Vector3(0,0,-100), 1, new Vector3(255,255,255), false));
        // scene.addLight(new Light(new Vector3(0,10,-10), 1, new Vector3(255,255,255), false));

        RayTracer rt = new RayTracer(camera, scene, this);
        rt.trace();

    }

    public void writePixel(int x, int y, int argb){
        PixelWriter pw = gc.getPixelWriter();
        pw.setArgb(x,y,argb);
    }

}
