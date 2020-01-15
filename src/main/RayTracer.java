package main;

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

                Vector3 ray1Direction = camera.getRayDirection(j+0.25, i+0.25);
                Vector3 ray2Direction = camera.getRayDirection(j+0.75, i+0.75);
                int argb;

                Ray ray1 = new Ray(camera.getWorldposition(), ray1Direction, scene, 1, 6);
                Ray ray2 = new Ray(camera.getWorldposition(), ray2Direction, scene,1 , 6);
                Vector3 ray1Result = ray1.shootRay().normalizedToColor();
                Vector3 ray2Result = ray2.shootRay().normalizedToColor();

                Vector3 outputColor;

                double similaritySum = Math.abs((ray1Result.getX() + ray1Result.getY() + ray1Result.getZ()) - (ray2Result.getX() + ray2Result.getY() + ray2Result.getZ()));

                if(similaritySum < 30) { // Farbwerte ähnlich genug
                    outputColor = ray1Result.add(ray2Result).scalarmultiplication(1./2.);
                }
                else {
                    Vector3 ray3Direction = camera.getRayDirection(j+0.5, i+0.5);
                    Ray ray3 = new Ray(camera.getWorldposition(), ray3Direction, scene);
                    Vector3 ray3Result = ray3.shootRay().normalizedToColor();
                    outputColor = ray1Result.add(ray2Result).add(ray3Result).scalarmultiplication(1./3.);
                }

                argb = (0xff << 24) | (Math.max(0, Math.min(255, (int) outputColor.getX())) << 16) | (Math.max(0, Math.min(255, (int) outputColor.getY())) << 8) | (Math.max(0, Math.min(255, (int) outputColor.getZ())));
                output.writePixel(j,i, argb);

            }
        }
    }

}
