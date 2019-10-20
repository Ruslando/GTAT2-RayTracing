package sample;

import sample.shapes.Shape;
import util.Intersection;

public class RayTracer {

    private Camera camera;
    private Scene scene;
    private Output output;

    public RayTracer(Camera camera, Scene scene, Output output){
        this.camera = camera;
        this.scene = scene;
        this.output = output;
    }

    public void trace() {
        for (int i = 0; i < Main.HEIGHT; i++){
            for(int j = 0; j < Main.WIDTH; j++){
                double u = camera.getL() + (camera.getR() - camera.getL()) * (j + 0.5) / Main.WIDTH;
                double v = camera.getT() + (camera.getB() - camera.getT()) * (i + 0.5) / Main.HEIGHT;

                Vector3 s = camera.getU().scalarmultiplication(u)
                        .add(camera.getUP().scalarmultiplication(v))
                        .add(camera.getW_d_negated());

                Vector3 tracedir = s.normalize();


                Intersection intersection = null;
                double distance = Double.MAX_VALUE;

                for(Shape shapes: scene.getScene()){
                    Intersection inter = shapes.intersect(camera.getWorldposition(), tracedir);

                    if(inter.getNearestIntersection() < distance){
                        distance = inter.getNearestIntersection();
                        intersection = inter;
                    }
                }

                if(intersection != null){
                    output.writePixel(j,i, intersection.getShape().getColor());
                }
            }
        }
    }

}
