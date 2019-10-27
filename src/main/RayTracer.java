package main;

import main.shapes.Shape;
import main.util.Intersection;
import main.util.Vector3;

public class RayTracer {

    private Camera camera;
    private Scene scene;
    private OutputController output;

    public RayTracer(Camera camera, Scene scene, OutputController output){
        this.camera = camera;
        this.scene = scene;
        this.output = output;
    }

    public void trace() {
        for (int i = 0; i < Main.HEIGHT; i++){      //Loop every pixel
            for(int j = 0; j < Main.WIDTH; j++){

                Vector3 tracedir = camera.getRayDirection(j, i);


                Intersection intersection = null;
                double distance = Double.MAX_VALUE;

                /* Loops through every object that is in the scene. Only the intersection that is nearest to the screen
                is drawn onto the canvas. If the ray does not hit anything, the intersection field stays null.
                 */
                for(Shape shapes: scene.getScene()){
                    Intersection inter = shapes.intersect(camera.getWorldposition(), tracedir);

                    if(inter.getNearestIntersection() < distance){
                        distance = inter.getNearestIntersection();
                        intersection = inter;
                    }
                }

                /* Currently takes the shape color and draws it on the canvas*/
                if(intersection != null){
                    output.writePixel(j,i, intersection.getShape().getColor());
                }
            }
        }
    }

}
